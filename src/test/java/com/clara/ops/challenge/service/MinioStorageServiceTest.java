package com.clara.ops.challenge.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.clara.ops.challenge.exception.StorageException;
import io.minio.MinioClient;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MinioStorageServiceTest {

  @Mock private MinioClient minioClient;

  private MinioStorageService storageService;

  @BeforeEach
  void setUp() {
    storageService = new MinioStorageService(minioClient, "bucket");
  }

  @Test
  void uploadFile_callsMinioClient() throws Exception {
    ByteArrayInputStream input =
        new ByteArrayInputStream("data".getBytes(StandardCharsets.UTF_8));

    storageService.uploadFile("user/file.pdf", input, 4L, "application/pdf");

    verify(minioClient).putObject(any());
  }

  @Test
  void uploadFile_whenMinioFails_throwsStorageException() throws Exception {
    doThrow(new RuntimeException("fail")).when(minioClient).putObject(any());

    ByteArrayInputStream input =
        new ByteArrayInputStream("data".getBytes(StandardCharsets.UTF_8));

    assertThatThrownBy(
            () -> storageService.uploadFile("user/file.pdf", input, 4L, "application/pdf"))
        .isInstanceOf(StorageException.class)
        .hasMessageContaining("Ocurrió un error guardando");
  }

  @Test
  void getPresignedUrl_returnsUrl() throws Exception {
    when(minioClient.getPresignedObjectUrl(any())).thenReturn("http://download");

    String url = storageService.getPresignedUrl("user/file.pdf");

    assertThat(url).isEqualTo("http://download");
  }

  @Test
  void getPresignedUrl_whenMinioFails_throwsStorageException() throws Exception {
    when(minioClient.getPresignedObjectUrl(any())).thenThrow(new RuntimeException("fail"));

    assertThatThrownBy(() -> storageService.getPresignedUrl("user/file.pdf"))
        .isInstanceOf(StorageException.class)
        .hasMessageContaining("Ocurrió un error generando");
  }
}
