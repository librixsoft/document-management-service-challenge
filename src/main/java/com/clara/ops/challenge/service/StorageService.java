package com.clara.ops.challenge.service;

import java.io.InputStream;

public interface StorageService {
  void uploadFile(String path, InputStream inputStream, long size, String contentType);

  String getPresignedUrl(String path);
}
