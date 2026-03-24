package com.clara.ops.challenge.model.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class DocumentResponseDto {
  private UUID id;
  private String user;
  private String documentName;
  private List<String> tags;
  private Long fileSize;
  private LocalDateTime createdAt;
}
