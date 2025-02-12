package me.jangluzniewicz.webstore.exceptions;

import org.springframework.security.core.AuthenticationException;

public class JwtException extends AuthenticationException {
  public JwtException(String message) {
    super(message);
  }
}
