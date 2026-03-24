package com.clara.ops.challenge.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.clara.ops.challenge.exception.DocumentNotFoundException;
import com.clara.ops.challenge.model.dto.DocumentResponseDto;
import com.clara.ops.challenge.model.entity.DocumentEntity;
import com.clara.ops.challenge.repository.DocumentRepository;
import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

  @Mock private DocumentRepository documentRepository;

  @Mock private StorageService storageService;

  private DocumentService documentService;

  @BeforeEach
  void setUp() {
    documentService = new DocumentService(documentRepository, storageService);
  }

  @Test
  void uploadDocument_persistsMetadataAndReturnsDto() {
    UUID docId = UUID.randomUUID();
    DocumentEntity saved = new DocumentEntity();
    saved.setId(docId);
    saved.setUser("user1");
    saved.setDocumentName("file.pdf");
    saved.setTags(List.of("tag1"));
    saved.setMinioPath("user1/file.pdf");
    saved.setFileSize(123L);
    saved.setFileType("application/pdf");
    saved.setCreatedAt(LocalDateTime.now());

    when(documentRepository.save(any(DocumentEntity.class))).thenReturn(saved);

    ByteArrayInputStream inputStream = new ByteArrayInputStream("data".getBytes());

    DocumentResponseDto dto =
        documentService.uploadDocument(
            "user1", "file.pdf", List.of("tag1"), inputStream, 123L, "application/pdf");

    verify(storageService)
        .uploadFile(eq("user1/file.pdf"), any(java.io.InputStream.class), eq(123L), eq("application/pdf"));

    assertThat(dto.getId()).isEqualTo(docId);
    assertThat(dto.getUser()).isEqualTo("user1");
    assertThat(dto.getDocumentName()).isEqualTo("file.pdf");
    assertThat(dto.getTags()).containsExactly("tag1");
    assertThat(dto.getFileSize()).isEqualTo(123L);
    assertThat(dto.getCreatedAt()).isNotNull();
  }

  @Test
  void searchDocuments_mapsEntitiesToDto() {
    DocumentEntity entity = new DocumentEntity();
    entity.setId(UUID.randomUUID());
    entity.setUser("user1");
    entity.setDocumentName("file.pdf");
    entity.setTags(List.of("tag1"));
    entity.setFileSize(123L);
    entity.setCreatedAt(LocalDateTime.now());

    PageImpl<DocumentEntity> page = new PageImpl<>(List.of(entity));
    when(documentRepository.findAll(
            org.mockito.Mockito.<org.springframework.data.jpa.domain.Specification<DocumentEntity>>any(),
            any(org.springframework.data.domain.Pageable.class)))
        .thenReturn(page);

    Page<DocumentResponseDto> result =
        documentService.searchDocuments("user1", null, List.of("tag1"), PageRequest.of(0, 10));

    assertThat(result.getContent()).hasSize(1);
    DocumentResponseDto dto = result.getContent().get(0);
    assertThat(dto.getUser()).isEqualTo("user1");
    assertThat(dto.getDocumentName()).isEqualTo("file.pdf");
  }

  @Test
  void getDownloadUrl_returnsPresignedUrl() {
    UUID docId = UUID.randomUUID();
    DocumentEntity entity = new DocumentEntity();
    entity.setId(docId);
    entity.setMinioPath("user1/file.pdf");

    when(documentRepository.findById(docId)).thenReturn(Optional.of(entity));
    when(storageService.getPresignedUrl("user1/file.pdf")).thenReturn("http://download");

    String url = documentService.getDownloadUrl(docId);

    assertThat(url).isEqualTo("http://download");
  }

  @Test
  void getDownloadUrl_whenNotFound_throwsException() {
    UUID docId = UUID.randomUUID();
    when(documentRepository.findById(docId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> documentService.getDownloadUrl(docId))
        .isInstanceOf(DocumentNotFoundException.class)
        .hasMessageContaining("Document not found");
  }
}
