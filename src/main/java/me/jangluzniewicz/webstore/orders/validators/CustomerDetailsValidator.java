package me.jangluzniewicz.webstore.orders.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import me.jangluzniewicz.webstore.orders.controllers.OrderRequest;

public class CustomerDetailsValidator
    implements ConstraintValidator<CustomerDetailsRequired, OrderRequest> {
  @Override
  public boolean isValid(
      OrderRequest orderRequest, ConstraintValidatorContext constraintValidatorContext) {
    if (orderRequest == null) {
      return true;
    }
    if (orderRequest.getCustomerId() == null) {
      boolean valid = true;
      constraintValidatorContext.disableDefaultConstraintViolation();
      if (orderRequest.getEmail() == null || orderRequest.getEmail().trim().isEmpty()) {
        constraintValidatorContext
            .buildConstraintViolationWithTemplate("Email is required when customerId is null")
            .addPropertyNode("email")
            .addConstraintViolation();
        valid = false;
      }
      if (orderRequest.getPhoneNumber() == null || orderRequest.getPhoneNumber().trim().isEmpty()) {
        constraintValidatorContext
            .buildConstraintViolationWithTemplate(
                "Phone number is required when customerId is null")
            .addPropertyNode("phoneNumber")
            .addConstraintViolation();
        valid = false;
      }
      return valid;
    }
    return true;
  }
}
