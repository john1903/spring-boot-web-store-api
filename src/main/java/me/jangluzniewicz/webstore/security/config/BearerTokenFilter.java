package me.jangluzniewicz.webstore.security.config;

import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import me.jangluzniewicz.webstore.exceptions.JwtException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

public class BearerTokenFilter extends HttpFilter {
  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String BEARER_PREFIX = "Bearer ";
  private final SecurityContextHolderStrategy securityContextHolderStrategy =
      SecurityContextHolder.getContextHolderStrategy();
  private final AuthenticationFailureHandler failureHandler =
      new SimpleUrlAuthenticationFailureHandler();
  private final JwtService jwtService;

  public BearerTokenFilter(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  private void setSecurityContext(SignedJWT signedJWT) {
    Authentication authentication = jwtService.getAuthentication(signedJWT);
    SecurityContext securityContext = securityContextHolderStrategy.createEmptyContext();
    securityContext.setAuthentication(authentication);
  }

  @Override
  protected void doFilter(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
    if (authorizationHeader == null || authorizationHeader.isEmpty()) {
      chain.doFilter(request, response);
    } else {
      String token = authorizationHeader.substring(BEARER_PREFIX.length());
      try {
        SignedJWT signedJWT = SignedJWT.parse(token);
        jwtService.verifyJwt(signedJWT);
        setSecurityContext(signedJWT);
        chain.doFilter(request, response);
      } catch (JwtException e) {
        failureHandler.onAuthenticationFailure(request, response, e);
      } catch (ParseException e) {
        JwtException jwtException = new JwtException("Bearer token could not be parsed");
        failureHandler.onAuthenticationFailure(request, response, jwtException);
      }
    }
  }
}
