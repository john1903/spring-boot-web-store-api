package me.jangluzniewicz.webstore.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import me.jangluzniewicz.webstore.exceptions.JwtException;
import me.jangluzniewicz.webstore.security.services.JwtService;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class JwtFilter extends HttpFilter {
  private final RequestMatcher pathRequestMatcher =
      AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/auth/login");
  private final AuthenticationManager authenticationManager;
  private final AuthenticationFailureHandler failureHandler = new JwtFailureHandler();
  private final JwtSuccessHandler successHandler;

  public JwtFilter(AuthenticationManager authenticationManager, JwtService jwtService) {
    this.authenticationManager = authenticationManager;
    this.successHandler = new JwtSuccessHandler(jwtService);
  }

  private record JwtAuthenticationRequest(String username, String password) {}

  private Authentication attemptAuthentication(HttpServletRequest request) throws IOException {
    if (request.getContentLength() == 0) {
      throw new JwtException("Request body cannot be empty");
    }
    JwtAuthenticationRequest jwtAuthenticationRequest =
        new ObjectMapper().readValue(request.getInputStream(), JwtAuthenticationRequest.class);
    if (jwtAuthenticationRequest.username() == null
        || jwtAuthenticationRequest.password() == null) {
      throw new JwtException("Username and password are required");
    }
    UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(
            jwtAuthenticationRequest.username(), jwtAuthenticationRequest.password());
    return authenticationManager.authenticate(authenticationToken);
  }

  @Override
  protected void doFilter(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    if (pathRequestMatcher.matches(request)) {
      try {
        Authentication authentication = attemptAuthentication(request);
        successHandler.onAuthenticationSuccess(request, response, authentication);
      } catch (AuthenticationException e) {
        failureHandler.onAuthenticationFailure(request, response, e);
      }
    } else {
      chain.doFilter(request, response);
    }
  }
}
