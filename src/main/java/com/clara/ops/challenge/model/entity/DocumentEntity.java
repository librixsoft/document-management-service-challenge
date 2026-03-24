package com.clara.ops.challenge.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
@Entity
@Table(name = "documents")
public class DocumentEntity {

  @Id private UUID id;

  @Column(name = "upload_user", nullable = false)
  private String user;

  @Column(name = "document_name", nullable = false)
  private String documentName;

  // En PostgreSQL esto se mapeará nativamente a TEXT[] gracias a Hibernate 6
  @Column(name = "tags")
  private List<String> tags;

  @Column(name = "minio_path", nullable = false)
  private String minioPath;

  @Column(name = "file_size")
  private Long fileSize;

  @Column(name = "file_type")
  private String fileType;

  @Column(name = "created_at")
  private LocalDateTime createdAt;
}
