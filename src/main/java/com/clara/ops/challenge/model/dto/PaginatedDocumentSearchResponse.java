package com.clara.ops.challenge.model.dto;

import java.util.List;
import lombok.Data;

@Data
public class PaginatedDocumentSearchResponse {
  private MetadataDto metadata;
  private List<DocumentResponseDto> documents;
}
