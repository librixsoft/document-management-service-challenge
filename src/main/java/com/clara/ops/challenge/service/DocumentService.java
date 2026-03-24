package com.clara.ops.challenge.service;

import com.clara.ops.challenge.exception.DocumentNotFoundException;
import com.clara.ops.challenge.model.dto.DocumentResponseDto;
import com.clara.ops.challenge.model.entity.DocumentEntity;
import com.clara.ops.challenge.repository.DocumentRepository;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DocumentService {

  private final DocumentRepository documentRepository;
  private final StorageService storageService;

  @Transactional
  public DocumentResponseDto uploadDocument(
      String user,
      String documentName,
      List<String> tags,
      InputStream inputStream,
      long size,
      String contentType) {

    UUID docId = UUID.randomUUID();
    String minioPath = user + "/" + documentName;

    // 1. Upload stream to MinIO (avoids loading the whole file in RAM)
    storageService.uploadFile(minioPath, inputStream, size, contentType);

    // 2. Insert metadata to PostgreSQL
    DocumentEntity entity = new DocumentEntity();
    entity.setId(docId);
    entity.setUser(user);
    entity.setDocumentName(documentName);
    entity.setTags(tags);
    entity.setMinioPath(minioPath);
    entity.setFileSize(size);
    entity.setFileType(contentType);
    entity.setCreatedAt(LocalDateTime.now());

    DocumentEntity saved = documentRepository.save(entity);

    return mapToDto(saved);
  }

  @Transactional(readOnly = true)
  public Page<DocumentResponseDto> searchDocuments(
      String user, String documentName, List<String> tags, Pageable pageable) {

    Specification<DocumentEntity> spec =
        com.clara.ops.challenge.repository.DocumentSpecification.byFilters(
            user, documentName, tags);

    Page<DocumentEntity> page = documentRepository.findAll(spec, pageable);
    return page.map(this::mapToDto);
  }

  @Transactional(readOnly = true)
  public String getDownloadUrl(UUID documentId) {
    DocumentEntity entity =
        documentRepository
            .findById(documentId)
            .orElseThrow(
                () -> new DocumentNotFoundException("Document not found with ID: " + documentId));

    return storageService.getPresignedUrl(entity.getMinioPath());
  }

  private DocumentResponseDto mapToDto(DocumentEntity entity) {
    DocumentResponseDto dto = new DocumentResponseDto();
    dto.setId(entity.getId());
    dto.setUser(entity.getUser());
    dto.setDocumentName(entity.getDocumentName());
    dto.setTags(entity.getTags());
    dto.setFileSize(entity.getFileSize());
    dto.setCreatedAt(entity.getCreatedAt());
    return dto;
  }
}
