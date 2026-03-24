package com.clara.ops.challenge.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.clara.ops.challenge.model.dto.DocumentResponseDto;
import com.clara.ops.challenge.service.DocumentService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DocumentController.class)
class DocumentControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private DocumentService documentService;

  @Test
  void uploadDocument_returnsCreated() throws Exception {
    DocumentResponseDto dto = new DocumentResponseDto();
    dto.setId(UUID.randomUUID());
    dto.setUser("user1");
    dto.setDocumentName("file.pdf");
    dto.setTags(List.of("tag1"));
    dto.setFileSize(123L);
    dto.setCreatedAt(LocalDateTime.now());

    when(documentService.uploadDocument(
            eq("user1"),
            eq("file.pdf"),
            eq(List.of("tag1")),
            any(),
            eq(3L),
            eq(MediaType.APPLICATION_PDF_VALUE)))
        .thenReturn(dto);

    MockMultipartFile file =
        new MockMultipartFile(
            "file", "file.pdf", MediaType.APPLICATION_PDF_VALUE, "pdf".getBytes());

    mockMvc
        .perform(
            multipart("/api/v1/documents/upload")
                .file(file)
                .param("user", "user1")
                .param("tags", "tag1"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.user").value("user1"))
        .andExpect(jsonPath("$.documentName").value("file.pdf"));
  }

  @Test
  void searchDocuments_returnsPage() throws Exception {
    DocumentResponseDto dto = new DocumentResponseDto();
    dto.setId(UUID.randomUUID());
    dto.setUser("user1");
    dto.setDocumentName("file.pdf");
    dto.setTags(List.of("tag1"));
    dto.setFileSize(123L);
    dto.setCreatedAt(LocalDateTime.now());

    when(documentService.searchDocuments(eq("user1"), eq("file"), eq(List.of("tag1")), any()))
        .thenReturn(new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1));

    mockMvc
        .perform(
            get("/api/v1/documents/search")
                .param("user", "user1")
                .param("documentName", "file")
                .param("tags", "tag1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].user").value("user1"))
        .andExpect(jsonPath("$.content[0].documentName").value("file.pdf"));
  }

  @Test
  void getDownloadUrl_returnsUrl() throws Exception {
    UUID id = UUID.randomUUID();
    when(documentService.getDownloadUrl(id)).thenReturn("http://download");

    mockMvc
        .perform(get("/api/v1/documents/{id}/download_url", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.downloadUrl").value("http://download"));
  }
}
