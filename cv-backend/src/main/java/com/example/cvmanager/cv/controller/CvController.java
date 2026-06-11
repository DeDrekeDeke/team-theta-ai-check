package com.example.cvmanager.cv.controller;

import com.example.cvmanager.auth.security.AuthenticatedUser;
import com.example.cvmanager.cv.dto.CvCreateRequest;
import com.example.cvmanager.cv.dto.CvResponse;
import com.example.cvmanager.cv.dto.CvUpdateRequest;
import com.example.cvmanager.cv.service.CvService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/cvs")
public class CvController {

    private static final String CV_HTML_CONTENT_SECURITY_POLICY =
            "default-src 'none'; img-src data: http: https:; style-src 'unsafe-inline'; font-src data:; sandbox";

    private final CvService cvService;

    public CvController(CvService cvService) {
        this.cvService = cvService;
    }

    @GetMapping
    public List<CvResponse> listCvs(@AuthenticationPrincipal AuthenticatedUser user) {
        return cvService.listCvs(user);
    }

    @GetMapping("/search")
    public List<CvResponse> searchCvs(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam String q) {
        return cvService.searchCvs(user, q);
    }

    @GetMapping("/{id}")
    public CvResponse getCv(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long id) {
        return cvService.getCv(user, id);
    }

    @GetMapping(value = "/{id}/html", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> getCvHtml(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long id) {
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .header("Content-Security-Policy", CV_HTML_CONTENT_SECURITY_POLICY)
                .header("X-Content-Type-Options", "nosniff")
                .body(cvService.getUploadedHtml(user, id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CvResponse createCv(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody CvCreateRequest request) {
        return cvService.createCv(user, request);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public CvResponse uploadCv(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam Long ownerUserId,
            @RequestParam(required = false) String title,
            @RequestParam("file") MultipartFile file) {
        return cvService.uploadHtmlCv(user, ownerUserId, title, file);
    }

    @PostMapping(value = "/legacy-preview", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_HTML_VALUE)
    public String legacyPreview(@RequestBody String input) {
        return cvService.buildLegacyPreview(input);
    }

    @PutMapping("/{id}")
    public CvResponse updateCv(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long id,
            @Valid @RequestBody CvUpdateRequest request) {
        return cvService.updateCv(user, id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void archiveCv(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long id) {
        cvService.archiveCv(user, id);
    }
}
