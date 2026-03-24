package com.clara.ops.challenge.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.clara.ops.challenge.model.dto.DocumentResponseDto;
import com.clara.ops.challenge.service.DocumentService;
import java.time.LocalDateTime;
import java.util.List;
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
    dto.setName("doc1.pdf");
    dto.setTags(List.of("tag1"));
    dto.setSize(123);
    dto.setType(MediaType.APPLICATION_PDF_VALUE);
    dto.setCreatedAt(LocalDateTime.now());

    when(documentService.uploadDocument(
            eq("user1"),
            eq("doc1.pdf"),
            eq(List.of("tag1")),
            any(),
            eq(3L),
            eq(MediaType.APPLICATION_PDF_VALUE)))
        .thenReturn(dto);

    MockMultipartFile file =
        new MockMultipartFile(
            "file", "doc1.pdf", MediaType.APPLICATION_PDF_VALUE, "pdf".getBytes());

    mockMvc
        .perform(
            multipart("/document-management/upload")
                .file(file)
                .param("user", "user1")
                .param("name", "doc1.pdf")
                .param("tags", "tag1"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.user").value("user1"))
        .andExpect(jsonPath("$.name").value("doc1.pdf"));
  }

  @Test
  void uploadDocument_user1Doc2_returnsCreated() throws Exception {
    DocumentResponseDto dto = new DocumentResponseDto();
    dto.setId(UUID.randomUUID());
    dto.setUser("user1");
    dto.setName("doc2.pdf");
    dto.setTags(List.of("tag2"));
    dto.setSize(4);
    dto.setType(MediaType.APPLICATION_PDF_VALUE);
    dto.setCreatedAt(LocalDateTime.now());

    when(documentService.uploadDocument(
            eq("user1"),
            eq("doc2.pdf"),
            eq(List.of("tag2")),
            any(),
            eq(3L),
            eq(MediaType.APPLICATION_PDF_VALUE)))
        .thenReturn(dto);

    MockMultipartFile file =
        new MockMultipartFile(
            "file", "doc2.pdf", MediaType.APPLICATION_PDF_VALUE, "pdf".getBytes());

    mockMvc
        .perform(
            multipart("/document-management/upload")
                .file(file)
                .param("user", "user1")
                .param("name", "doc2.pdf")
                .param("tags", "tag2"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.user").value("user1"))
        .andExpect(jsonPath("$.name").value("doc2.pdf"));
  }

  @Test
  void uploadDocument_user2Doc3_returnsCreated() throws Exception {
    DocumentResponseDto dto = new DocumentResponseDto();
    dto.setId(UUID.randomUUID());
    dto.setUser("user2");
    dto.setName("doc3.pdf");
    dto.setTags(List.of("tag3"));
    dto.setSize(4);
    dto.setType(MediaType.APPLICATION_PDF_VALUE);
    dto.setCreatedAt(LocalDateTime.now());

    when(documentService.uploadDocument(
            eq("user2"),
            eq("doc3.pdf"),
            eq(List.of("tag3")),
            any(),
            eq(3L),
            eq(MediaType.APPLICATION_PDF_VALUE)))
        .thenReturn(dto);

    MockMultipartFile file =
        new MockMultipartFile(
            "file", "doc3.pdf", MediaType.APPLICATION_PDF_VALUE, "pdf".getBytes());

    mockMvc
        .perform(
            multipart("/document-management/upload")
                .file(file)
                .param("user", "user2")
                .param("name", "doc3.pdf")
                .param("tags", "tag3"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.user").value("user2"))
        .andExpect(jsonPath("$.name").value("doc3.pdf"));
  }

  @Test
  void searchDocuments_returnsPage() throws Exception {
    DocumentResponseDto doc1 = new DocumentResponseDto();
    doc1.setId(UUID.randomUUID());
    doc1.setUser("user1");
    doc1.setName("doc1.pdf");
    doc1.setTags(List.of("tag1"));
    doc1.setSize(123);
    doc1.setType(MediaType.APPLICATION_PDF_VALUE);
    doc1.setCreatedAt(LocalDateTime.now());

    DocumentResponseDto doc2 = new DocumentResponseDto();
    doc2.setId(UUID.randomUUID());
    doc2.setUser("user1");
    doc2.setName("doc2.pdf");
    doc2.setTags(List.of("tag2"));
    doc2.setSize(10);
    doc2.setType(MediaType.APPLICATION_PDF_VALUE);
    doc2.setCreatedAt(LocalDateTime.now());

    when(documentService.searchDocuments(eq("user1"), eq(null), eq(null), any()))
        .thenReturn(new PageImpl<>(List.of(doc1, doc2), PageRequest.of(0, 20), 2));

    mockMvc
        .perform(
            post("/document-management/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"user\":\"user1\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.documents[0].user").value("user1"))
        .andExpect(jsonPath("$.documents[0].name").value("doc1.pdf"))
        .andExpect(jsonPath("$.documents[1].name").value("doc2.pdf"))
        .andExpect(jsonPath("$.metadata.totalItems").value(2));
  }

  @Test
  void searchDocuments_user2Doc3_returnsMatch() throws Exception {
    DocumentResponseDto doc3 = new DocumentResponseDto();
    doc3.setId(UUID.randomUUID());
    doc3.setUser("user2");
    doc3.setName("doc3.pdf");
    doc3.setTags(List.of("tag3"));
    doc3.setSize(20);
    doc3.setType(MediaType.APPLICATION_PDF_VALUE);
    doc3.setCreatedAt(LocalDateTime.now());

    when(documentService.searchDocuments(eq("user2"), eq("doc3"), eq(List.of("tag3")), any()))
        .thenReturn(new PageImpl<>(List.of(doc3), PageRequest.of(0, 20), 1));

    mockMvc
        .perform(
            post("/document-management/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"user\":\"user2\",\"name\":\"doc3\",\"tags\":[\"tag3\"]}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.documents[0].user").value("user2"))
        .andExpect(jsonPath("$.documents[0].name").value("doc3.pdf"))
        .andExpect(jsonPath("$.metadata.totalItems").value(1));
  }

  @Test
  void getDownloadUrl_returnsUrl() throws Exception {
    UUID id = UUID.randomUUID();
    when(documentService.getDownloadUrl(id)).thenReturn("http://download");

    mockMvc
        .perform(get("/document-management/download/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.url").value("http://download"));
  }
}
