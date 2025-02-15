package me.jangluzniewicz.webstore.security.interfaces;

import me.jangluzniewicz.webstore.security.models.CustomUser;

public interface ISecurity {
  CustomUser getCurrentUser();
}
