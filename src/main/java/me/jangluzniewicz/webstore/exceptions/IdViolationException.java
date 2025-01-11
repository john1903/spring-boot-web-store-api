package me.jangluzniewicz.webstore.exceptions;

public class IdViolationException extends RuntimeException {
    public IdViolationException(String message) {
        super(message);
    }
}
