package com.clara.ops.challenge.model.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class DocumentResponseDto {
  private UUID id;
  private String user;
  private String name;
  private List<String> tags;
  private Integer size;
  private String type;
  private LocalDateTime createdAt;
}
