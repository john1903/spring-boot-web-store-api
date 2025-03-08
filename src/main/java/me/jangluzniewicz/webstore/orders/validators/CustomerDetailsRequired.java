package me.jangluzniewicz.webstore.orders.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = CustomerDetailsValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomerDetailsRequired {
  String message() default "When customerId is null, email and phoneNumber must be provided.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
