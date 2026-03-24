package com.clara.ops.challenge.service;

import com.clara.ops.challenge.exception.StorageException;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MinioStorageService implements StorageService {

  private final MinioClient minioClient;
  private final String bucketName;

  public MinioStorageService(MinioClient minioClient, @Value("${minio.bucket}") String bucketName) {
    this.minioClient = minioClient;
    this.bucketName = bucketName;
  }

  @Override
  public void uploadFile(String path, InputStream inputStream, long size, String contentType) {
    try {
      // PutObject soporta subir streams grandes (-1 significa tamaño desconocido)
      // pero si conocemos el "size", MinIO optimiza internamente en multipart y evita memory-leaks
      // en el Heap.
      // -Xmx50m estará a salvo (Memory constraint met)
      PutObjectArgs args =
          PutObjectArgs.builder().bucket(bucketName).object(path).stream(inputStream, size, -1)
              .contentType(contentType)
              .build();

      minioClient.putObject(args);
    } catch (Exception e) {
      throw new StorageException(
          "Ocurrió un error guardando el archivo físico en S3/MinIO: " + path, e);
    }
  }

  @Override
  public String getPresignedUrl(String path) {
    try {
      // Url temporal válida por 24 horas (descarga)
      GetPresignedObjectUrlArgs args =
          GetPresignedObjectUrlArgs.builder()
              .method(Method.GET)
              .bucket(bucketName)
              .object(path)
              .expiry(24, TimeUnit.HOURS)
              .build();

      return minioClient.getPresignedObjectUrl(args);
    } catch (Exception e) {
      throw new StorageException(
          "Ocurrió un error generando la URL temporal de descarga para: " + path, e);
    }
  }
}
