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
    saved.setDocumentName("doc1.pdf");
    saved.setTags(List.of("tag1"));
    saved.setMinioPath("user1/doc1.pdf");
    saved.setFileSize(123L);
    saved.setFileType("application/pdf");
    saved.setCreatedAt(LocalDateTime.now());

    when(documentRepository.save(any(DocumentEntity.class))).thenReturn(saved);

    ByteArrayInputStream inputStream = new ByteArrayInputStream("data".getBytes());

    DocumentResponseDto dto =
        documentService.uploadDocument(
            "user1", "doc1.pdf", List.of("tag1"), inputStream, 123L, "application/pdf");

    verify(storageService)
        .uploadFile(
            eq("user1/doc1.pdf"), any(java.io.InputStream.class), eq(123L), eq("application/pdf"));

    assertThat(dto.getId()).isEqualTo(docId);
    assertThat(dto.getUser()).isEqualTo("user1");
    assertThat(dto.getName()).isEqualTo("doc1.pdf");
    assertThat(dto.getTags()).containsExactly("tag1");
    assertThat(dto.getSize()).isEqualTo(123);
    assertThat(dto.getType()).isEqualTo("application/pdf");
    assertThat(dto.getCreatedAt()).isNotNull();
  }

  @Test
  void uploadDocument_user1Doc2_usesExpectedMinioPathFromRequirements() {
    UUID docId = UUID.randomUUID();
    DocumentEntity saved = new DocumentEntity();
    saved.setId(docId);
    saved.setUser("user1");
    saved.setDocumentName("doc2.pdf");
    saved.setTags(List.of("tag2"));
    saved.setMinioPath("user1/doc2.pdf");
    saved.setFileSize(10L);
    saved.setFileType("application/pdf");
    saved.setCreatedAt(LocalDateTime.now());
    when(documentRepository.save(any(DocumentEntity.class))).thenReturn(saved);
    ByteArrayInputStream inputStream = new ByteArrayInputStream("x".getBytes());

    documentService.uploadDocument(
        "user1", "doc2.pdf", List.of("tag2"), inputStream, 10L, "application/pdf");

    verify(storageService)
        .uploadFile(
            eq("user1/doc2.pdf"), any(java.io.InputStream.class), eq(10L), eq("application/pdf"));
  }

  @Test
  void uploadDocument_user2Doc3_usesExpectedMinioPathFromRequirements() {
    UUID docId = UUID.randomUUID();
    DocumentEntity saved = new DocumentEntity();
    saved.setId(docId);
    saved.setUser("user2");
    saved.setDocumentName("doc3.pdf");
    saved.setTags(List.of("tag3"));
    saved.setMinioPath("user2/doc3.pdf");
    saved.setFileSize(20L);
    saved.setFileType("application/pdf");
    saved.setCreatedAt(LocalDateTime.now());
    when(documentRepository.save(any(DocumentEntity.class))).thenReturn(saved);
    ByteArrayInputStream inputStream = new ByteArrayInputStream("y".getBytes());

    documentService.uploadDocument(
        "user2", "doc3.pdf", List.of("tag3"), inputStream, 20L, "application/pdf");

    verify(storageService)
        .uploadFile(
            eq("user2/doc3.pdf"), any(java.io.InputStream.class), eq(20L), eq("application/pdf"));
  }

  @Test
  void searchDocuments_mapsUser1Doc1AndDoc2_asInRequirementsTree() {
    DocumentEntity entity = new DocumentEntity();
    entity.setId(UUID.randomUUID());
    entity.setUser("user1");
    entity.setDocumentName("doc1.pdf");
    entity.setTags(List.of("tag1"));
    entity.setFileSize(123L);
    entity.setCreatedAt(LocalDateTime.now());

    DocumentEntity second = new DocumentEntity();
    second.setId(UUID.randomUUID());
    second.setUser("user1");
    second.setDocumentName("doc2.pdf");
    second.setTags(List.of("tag2"));
    second.setFileSize(10L);
    second.setCreatedAt(LocalDateTime.now());

    PageImpl<DocumentEntity> twoDocs = new PageImpl<>(List.of(entity, second));
    when(documentRepository.findAll(
            org.mockito.Mockito
                .<org.springframework.data.jpa.domain.Specification<DocumentEntity>>any(),
            any(org.springframework.data.domain.Pageable.class)))
        .thenReturn(twoDocs);

    Page<DocumentResponseDto> result =
        documentService.searchDocuments("user1", null, null, PageRequest.of(0, 10));

    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getContent().get(0).getUser()).isEqualTo("user1");
    assertThat(result.getContent().get(0).getName()).isEqualTo("doc1.pdf");
    assertThat(result.getContent().get(1).getName()).isEqualTo("doc2.pdf");
  }

  @Test
  void getDownloadUrl_returnsPresignedUrl() {
    UUID docId = UUID.randomUUID();
    DocumentEntity entity = new DocumentEntity();
    entity.setId(docId);
    entity.setMinioPath("user1/doc1.pdf");

    when(documentRepository.findById(docId)).thenReturn(Optional.of(entity));
    when(storageService.getPresignedUrl("user1/doc1.pdf")).thenReturn("http://download");

    String url = documentService.getDownloadUrl(docId);

    assertThat(url).isEqualTo("http://download");
  }

  @Test
  void getDownloadUrl_forUser2Doc3_usesPathFromRequirementsTree() {
    UUID docId = UUID.randomUUID();
    DocumentEntity entity = new DocumentEntity();
    entity.setId(docId);
    entity.setMinioPath("user2/doc3.pdf");

    when(documentRepository.findById(docId)).thenReturn(Optional.of(entity));
    when(storageService.getPresignedUrl("user2/doc3.pdf")).thenReturn("http://minio/user2/doc3");

    assertThat(documentService.getDownloadUrl(docId)).isEqualTo("http://minio/user2/doc3");
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
