package me.jangluzniewicz.webstore.utils.integrations.security;

import java.util.Arrays;
import me.jangluzniewicz.webstore.security.models.CustomUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithCustomUserSecurityContextFactory
    implements WithSecurityContextFactory<WithCustomUser> {
  @Override
  public SecurityContext createSecurityContext(WithCustomUser annotation) {
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    CustomUser principal =
        CustomUser.customBuilder()
            .id(annotation.id())
            .username(annotation.username())
            .password(annotation.password())
            .roles(Arrays.asList(annotation.roles()))
            .build();
    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(
            principal, principal.getPassword(), principal.getAuthorities());
    context.setAuthentication(authentication);
    return context;
  }
}
