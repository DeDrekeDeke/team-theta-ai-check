package com.example.cvmanager.cv.service;

import com.example.cvmanager.common.exception.BadRequestException;
import com.example.cvmanager.common.exception.NotFoundException;
import com.example.cvmanager.cv.dto.CvCreateRequest;
import com.example.cvmanager.cv.dto.CvResponse;
import com.example.cvmanager.cv.dto.CvUpdateRequest;
import com.example.cvmanager.cv.mapper.CvMapper;
import com.example.cvmanager.cv.model.Cv;
import com.example.cvmanager.cv.repository.CvRepository;
import com.example.cvmanager.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class CvService {

    private final CvRepository cvRepository;
    private final UserRepository userRepository;
    private final CvMapper cvMapper;

    @Transactional(readOnly = true)
    public List<CvResponse> listCvs() {
        return cvRepository.findByArchivedAtIsNull(updatedAtDescending()).stream()
                .map(cvMapper::toResponse)
                .toList();
    }

    @Transactional
    public void archiveCv(Long id) {
        Cv cv = findCv(id);
        cv.archive();
        cvRepository.save(cv);
    }

    @Transactional(readOnly = true)
    public CvResponse getCv(Long id) {
        return cvMapper.toResponse(findCv(id));
    }

    @Transactional(readOnly = true)
    public List<CvResponse> searchCvs(String q) {
        if (q == null || q.isBlank()) {
            return listCvs();
        }

        return cvRepository.search(q.trim()).stream()
                .map(cvMapper::toResponse)
                .toList();
    }

    @Transactional
    public CvResponse createCv(CvCreateRequest request) {
        var owner = userRepository.findById(request.ownerUserId())
                .orElseThrow(() -> new NotFoundException("Owner user not found", "USER_NOT_FOUND"));

        Cv cv = new Cv(owner, request.title());
        cv.setSummary(request.summary());
        return cvMapper.toResponse(cvRepository.save(cv));
    }

    @Transactional
    public CvResponse updateCv(Long id, CvUpdateRequest request) {
        Cv cv = findCv(id);
        cv.setTitle(request.title());
        cv.setSummary(request.summary());
        return cvMapper.toResponse(cvRepository.save(cv));
    }

    @Transactional
    public CvResponse uploadHtmlCv(Long ownerUserId, String submittedTitle, MultipartFile file) {
        throw new com.example.cvmanager.common.exception.BadRequestException(
                "HTML upload is disabled. Use structured CV fields instead.",
                "CV_HTML_UPLOAD_DISABLED");
    }

    @Transactional(readOnly = true)
    public String getUploadedHtml(Long id) {
        throw new NotFoundException("Uploaded CV HTML is no longer available", "CV_HTML_DISABLED");
    }

    public String buildLegacyPreview(String input) {
        String value = input == null ? "" : input;
        String normalized = normalizeLegacyText(value);
        String withoutHeader = removeLegacyHeader(normalized);
        String name = guessLegacyName(withoutHeader);
        String body = convertLegacyLines(withoutHeader);
        // return "<html><body><h1>" + name + "</h1><div>" + body + "</div></body></html>";
        throw new com.example.cvmanager.common.exception.BadRequestException(
                "HTML upload is disabled. Use structured CV fields instead.",
                "CV_HTML_UPLOAD_DISABLED");
    }

    @Transactional(readOnly = true)
    public Cv findCv(Long id) {
        return cvRepository.findByIdAndArchivedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("CV not found", "CV_NOT_FOUND"));
    }

    private String normalizeLegacyText(String value) {
        return value.replace("\r\n", "\n").replace("\r", "\n").trim();
    }

    private Sort updatedAtDescending() {
        return Sort.by(Sort.Direction.DESC, "updatedAt");
    }

    private String removeLegacyHeader(String value) {
        if (value.startsWith("CV:")) {
            return value.substring(3).trim();
        }
        return value;
    }

    private String guessLegacyName(String value) {
        String[] lines = value.split("\n");
        if (lines.length > 0 && !lines[0].isBlank()) {
            return lines[0].trim();
        }
        return "Legacy CV";
    }

    private String convertLegacyLines(String value) {
        return value.replace("\n", "<br>");
    }
}
