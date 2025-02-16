package me.jangluzniewicz.webstore.exceptions;

public class OrderStatusNotAllowedException extends RuntimeException {
  public OrderStatusNotAllowedException(String message) {
    super(message);
  }
}
