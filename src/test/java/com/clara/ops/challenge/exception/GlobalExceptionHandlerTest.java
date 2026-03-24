package com.clara.ops.challenge.exception;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class GlobalExceptionHandlerTest {

  private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

  @Test
  void handleNotFound_returns404() {
    ResponseEntity<Map<String, String>> response =
        handler.handleNotFound(new DocumentNotFoundException("missing"));

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody()).containsEntry("error", "missing");
  }

  @Test
  void handleStorage_returns503() {
    ResponseEntity<Map<String, String>> response =
        handler.handleStorage(new StorageException("storage fail", new RuntimeException("boom")));

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
    assertThat(response.getBody()).containsEntry("error", "storage fail");
  }

  @Test
  void handleGeneric_returns500() {
    ResponseEntity<Map<String, String>> response =
        handler.handleGeneric(new RuntimeException("boom"));

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody())
        .containsEntry("error", "Error interno procesando la petición")
        .containsEntry("detail", "boom");
  }
}
