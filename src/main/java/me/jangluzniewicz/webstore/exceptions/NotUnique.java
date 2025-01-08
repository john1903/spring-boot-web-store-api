package me.jangluzniewicz.webstore.exceptions;

public class NotUnique extends RuntimeException {
  public NotUnique(String message) {
    super(message);
  }
}
