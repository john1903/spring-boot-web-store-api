package me.jangluzniewicz.webstore.exceptions;

public class DeletionNotAllowedException extends RuntimeException {
  public DeletionNotAllowedException(String message) {
    super(message);
  }
}
