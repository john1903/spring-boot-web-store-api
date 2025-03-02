package me.jangluzniewicz.webstore.exceptions;

public class AwsException extends RuntimeException {
  public AwsException(String message) {
    super(message);
  }
}
