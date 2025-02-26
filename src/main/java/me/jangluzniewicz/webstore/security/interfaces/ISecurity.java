package me.jangluzniewicz.webstore.security.interfaces;

import me.jangluzniewicz.webstore.security.models.CustomUser;

/** Interface for security-related operations. */
public interface ISecurity {

  /**
   * Retrieves the current authenticated user.
   *
   * @return the current authenticated {@link CustomUser}.
   */
  CustomUser getCurrentUser();
}
