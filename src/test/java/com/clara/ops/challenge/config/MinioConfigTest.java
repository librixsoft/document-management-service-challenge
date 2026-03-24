package com.clara.ops.challenge.config;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.minio.MinioClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class MinioConfigTest {

  @Mock private MinioClient minioClient;

  private TestableMinioConfig config;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    config = new TestableMinioConfig(minioClient);
    ReflectionTestUtils.setField(config, "bucket", "bucket");
  }

  @Test
  void initBucket_createsWhenMissing() throws Exception {
    when(minioClient.bucketExists(any())).thenReturn(false);

    config.initBucket();

    verify(minioClient).makeBucket(any());
  }

  @Test
  void initBucket_skipsWhenExists() throws Exception {
    when(minioClient.bucketExists(any())).thenReturn(true);

    config.initBucket();

    verify(minioClient, never()).makeBucket(any());
  }

  @Test
  void initBucket_whenError_throwsRuntimeException() throws Exception {
    when(minioClient.bucketExists(any())).thenThrow(new RuntimeException("boom"));

    assertThatThrownBy(() -> config.initBucket())
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Fallo al inicializar MinIO");
  }

  static class TestableMinioConfig extends MinioConfig {
    private final MinioClient client;

    TestableMinioConfig(MinioClient client) {
      this.client = client;
    }

    @Override
    public MinioClient minioClient() {
      return client;
    }
  }
}
