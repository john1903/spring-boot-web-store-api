package me.jangluzniewicz.webstore.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

public class JwtSuccessHandler implements AuthenticationSuccessHandler {
  private final JwtService jwtService;

  public JwtSuccessHandler(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException {
    List<String> roles =
        authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
    String signedJwt = jwtService.createSignedJwt(authentication.getName(), roles);
    new ObjectMapper().writeValue(response.getWriter(), new JwtSuccessResponse(signedJwt));
  }

  private record JwtSuccessResponse(String token) {}
}
