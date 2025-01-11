package me.jangluzniewicz.webstore.exceptions;

public class NotUniqueException extends RuntimeException {
  public NotUniqueException(String message) {
    super(message);
  }
}
