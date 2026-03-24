package com.clara.ops.challenge.model.dto;

import lombok.Data;

@Data
public class MetadataDto {
  private int currentPage;
  private int itemsPerPage;
  private int currentItems;
  private int totalPages;
  private long totalItems;
}
