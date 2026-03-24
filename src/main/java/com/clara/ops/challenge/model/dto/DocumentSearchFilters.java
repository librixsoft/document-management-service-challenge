package com.clara.ops.challenge.model.dto;

import java.util.List;
import lombok.Data;

@Data
public class DocumentSearchFilters {
  private String user;
  private String name;
  private List<String> tags;
}
