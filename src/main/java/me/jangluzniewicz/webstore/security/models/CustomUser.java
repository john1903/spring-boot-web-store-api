package me.jangluzniewicz.webstore.security.models;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import me.jangluzniewicz.webstore.exceptions.JwtException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

@Getter
public class CustomUser extends User {
  private final Long id;
  private final List<String> roles;

  @Builder(builderMethodName = "customBuilder")
  public CustomUser(
      Long id,
      String username,
      String password,
      List<SimpleGrantedAuthority> authorities,
      List<String> roles) {
    super(username, password != null ? password : "", processAuthorities(authorities, roles));
    this.roles = roles != null ? roles : new ArrayList<>();
    this.id = id;
  }

  private static List<SimpleGrantedAuthority> processAuthorities(
      List<SimpleGrantedAuthority> authorities, List<String> roles) {
    List<SimpleGrantedAuthority> result = authorities != null ? authorities : new ArrayList<>();
    if (roles != null) {
      roles.stream()
          .map(
              role -> {
                if (role.startsWith("ROLE_")) {
                  throw new JwtException(
                      "Role should not start with ROLE_ (it is added automatically)");
                }
                return new SimpleGrantedAuthority("ROLE_" + role);
              })
          .forEach(result::add);
    }
    return result;
  }
}
