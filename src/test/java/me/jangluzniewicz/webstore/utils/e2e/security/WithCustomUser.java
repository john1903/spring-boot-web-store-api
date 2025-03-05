package me.jangluzniewicz.webstore.utils.e2e.security;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithCustomUserSecurityContextFactory.class)
public @interface WithCustomUser {
  long id() default 1L;

  String username() default "user";

  String password() default "password";

  String[] roles() default {"CUSTOMER"};
}
