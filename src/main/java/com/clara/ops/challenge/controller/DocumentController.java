package com.clara.ops.challenge.controller;

import com.clara.ops.challenge.model.dto.DocumentDownloadUrlResponse;
import com.clara.ops.challenge.model.dto.DocumentResponseDto;
import com.clara.ops.challenge.model.dto.DocumentSearchFilters;
import com.clara.ops.challenge.model.dto.MetadataDto;
import com.clara.ops.challenge.model.dto.PaginatedDocumentSearchResponse;
import com.clara.ops.challenge.model.dto.UploadDocumentRequest;
import com.clara.ops.challenge.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/document-management")
@RequiredArgsConstructor
@Tag(name = "Document Management", description = "Endpoints para subir y consultar Pdfs")
public class DocumentController {

  private static final byte[] EMPTY_PDF_BYTES =
      "%PDF-1.4\n%\u00e2\u00e3\u00cf\u00d3\n".getBytes(StandardCharsets.UTF_8);

  private final DocumentService documentService;

  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Sube un nuevo PDF a MinIO")
  public DocumentResponseDto uploadDocumentMultipart(
      @RequestParam("user") String user,
      @RequestParam("name") String name,
      @RequestParam("tags") List<String> tags,
      @RequestParam("file") MultipartFile file)
      throws IOException {

    return documentService.uploadDocument(
        user, name, tags, file.getInputStream(), file.getSize(), file.getContentType());
  }

  @PostMapping(value = "/upload", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Sube un nuevo PDF a MinIO (JSON)")
  public DocumentResponseDto uploadDocumentJson(@Valid @RequestBody UploadDocumentRequest request) {
    byte[] contentBytes = EMPTY_PDF_BYTES;
    if (request.getContentBase64() != null && !request.getContentBase64().isBlank()) {
      contentBytes = Base64.getDecoder().decode(request.getContentBase64());
    }

    String contentType =
        request.getContentType() == null
            ? MediaType.APPLICATION_PDF_VALUE
            : request.getContentType();

    return documentService.uploadDocument(
        request.getUser(),
        request.getName(),
        request.getTags(),
        new ByteArrayInputStream(contentBytes),
        contentBytes.length,
        contentType);
  }

  @PostMapping("/search")
  @Operation(summary = "Busca documentos dinámicas por user, nombre o tag")
  public PaginatedDocumentSearchResponse searchDocuments(
      @Valid @RequestBody DocumentSearchFilters filters,
      @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
          @Parameter(description = "Paginación y sorting por defecto")
          Pageable pageable) {

    Page<DocumentResponseDto> page =
        documentService.searchDocuments(
            filters.getUser(), filters.getName(), filters.getTags(), pageable);

    MetadataDto metadata = new MetadataDto();
    metadata.setCurrentPage(pageable.getPageNumber());
    metadata.setItemsPerPage(pageable.getPageSize());
    metadata.setCurrentItems(page.getNumberOfElements());
    metadata.setTotalPages(page.getTotalPages());
    metadata.setTotalItems(page.getTotalElements());

    PaginatedDocumentSearchResponse response = new PaginatedDocumentSearchResponse();
    response.setMetadata(metadata);
    response.setDocuments(page.getContent());
    return response;
  }

  @GetMapping("/download/{documentId}")
  @Operation(summary = "Genera url temporal para descarga desde MinIO")
  public ResponseEntity<DocumentDownloadUrlResponse> getDownloadUrl(@PathVariable UUID documentId) {
    String url = documentService.getDownloadUrl(documentId);
    return ResponseEntity.ok(new DocumentDownloadUrlResponse(url));
  }
}
