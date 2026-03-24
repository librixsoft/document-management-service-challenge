package com.clara.ops.challenge.exception;

public class DocumentNotFoundException extends RuntimeException {
  public DocumentNotFoundException(String message) {
    super(message);
  }
}
