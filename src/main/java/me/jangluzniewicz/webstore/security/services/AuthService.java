package me.jangluzniewicz.webstore.security.services;

import me.jangluzniewicz.webstore.security.interfaces.ISecurity;
import me.jangluzniewicz.webstore.security.models.CustomUser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements ISecurity {
  @Override
  public CustomUser getCurrentUser() {
    return (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }
}
