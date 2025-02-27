package me.jangluzniewicz.webstore.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import me.jangluzniewicz.webstore.security.models.CustomUser;
import me.jangluzniewicz.webstore.security.services.JwtService;
import org.springframework.http.MediaType;
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
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    List<String> roles =
        authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
    Long id =
        (authentication.getPrincipal() instanceof CustomUser principal) ? principal.getId() : null;
    String signedJwt = jwtService.createSignedJwt(id, authentication.getName(), roles);
    new ObjectMapper().writeValue(response.getWriter(), new JwtSuccessResponse(signedJwt));
  }

  private record JwtSuccessResponse(String token) {}
}
