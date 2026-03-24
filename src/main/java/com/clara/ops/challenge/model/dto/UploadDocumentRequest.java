package com.clara.ops.challenge.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Data;

@Data
public class UploadDocumentRequest {
  @NotBlank private String user;

  @NotBlank private String name;

  @NotEmpty private List<String> tags;

  // Optional: base64 content for JSON uploads (if client wants to send file data)
  private String contentBase64;

  private String contentType;
}
