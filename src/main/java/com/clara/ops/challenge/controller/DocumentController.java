package com.clara.ops.challenge.controller;

import com.clara.ops.challenge.model.dto.DocumentResponseDto;
import com.clara.ops.challenge.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
@Tag(name = "Document Management", description = "Endpoints para subir y consultar Pdfs")
public class DocumentController {

  private final DocumentService documentService;

  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Sube un nuevo PDF a MinIO")
  public DocumentResponseDto uploadDocument(
      @RequestParam("user") String user,
      @RequestParam("tags") List<String> tags,
      @RequestParam("file") MultipartFile file)
      throws IOException {

    return documentService.uploadDocument(
        user,
        file.getOriginalFilename(),
        tags,
        file.getInputStream(),
        file.getSize(),
        file.getContentType());
  }

  @GetMapping("/search")
  @Operation(summary = "Busca documentos dinámicas por user, nombre o tag")
  public Page<DocumentResponseDto> searchDocuments(
      @RequestParam(value = "user", required = false) String user,
      @RequestParam(value = "documentName", required = false) String documentName,
      @RequestParam(value = "tags", required = false) List<String> tags,
      @PageableDefault(size = 10, sort = "createdAt")
          @Parameter(description = "Paginación y sorting por defecto")
          Pageable pageable) {

    return documentService.searchDocuments(user, documentName, tags, pageable);
  }

  @GetMapping("/{id}/download_url")
  @Operation(summary = "Genera url temporal para descarga desde MinIO")
  public ResponseEntity<Map<String, String>> getDownloadUrl(@PathVariable UUID id) {
    String url = documentService.getDownloadUrl(id);
    return ResponseEntity.ok(Map.of("downloadUrl", url));
  }
}
