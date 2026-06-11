package com.example.cvmanager.cv.service;

import com.example.cvmanager.auth.security.AuthenticatedUser;
import com.example.cvmanager.common.exception.BadRequestException;
import com.example.cvmanager.common.exception.NotFoundException;
import com.example.cvmanager.common.security.AdminAccessService;
import com.example.cvmanager.cv.dto.request.CvCreateRequest;
import com.example.cvmanager.cv.dto.request.CvEducationEntryRequest;
import com.example.cvmanager.cv.dto.request.CvLanguageRequest;
import com.example.cvmanager.cv.dto.request.CvLinkRequest;
import com.example.cvmanager.cv.dto.request.CvPersonalDetailsRequest;
import com.example.cvmanager.cv.dto.request.CvSkillRequest;
import com.example.cvmanager.cv.dto.request.CvUpdateRequest;
import com.example.cvmanager.cv.dto.request.CvWorkExperienceEntryRequest;
import com.example.cvmanager.cv.dto.response.CvResponse;
import com.example.cvmanager.cv.mapper.CvMapper;
import com.example.cvmanager.cv.model.Cv;
import com.example.cvmanager.cv.model.CvEducationEntry;
import com.example.cvmanager.cv.model.CvLanguage;
import com.example.cvmanager.cv.model.CvLink;
import com.example.cvmanager.cv.model.CvPersonalDetails;
import com.example.cvmanager.cv.model.CvSkill;
import com.example.cvmanager.cv.model.CvWorkExperienceEntry;
import com.example.cvmanager.cv.repository.CvRepository;
import com.example.cvmanager.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;

import java.util.Locale;
import java.util.regex.Pattern;
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
    //private final CvStorageProperties storageProperties;
    private final AdminAccessService adminAccessService;

    private static final int MAX_TITLE_LENGTH = 255;
    private static final long MAX_HTML_UPLOAD_BYTES = 1_000_000;
    private static final Pattern UNSAFE_HTML_PATTERN = Pattern.compile(
            "(?is)<\\s*(script|iframe|object|embed|base|form|input|button|textarea|select|option|meta|link)\\b"
                    + "|\\son[a-z0-9_-]+\\s*="
                    + "|\\ssrcdoc\\s*="
                    + "|(?:href|src|xlink:href)\\s*=\\s*(['\"]?)\\s*(javascript:|data:text/html|vbscript:)");

    @Transactional(readOnly = true)
    public List<CvResponse> listCvs(AuthenticatedUser user) {
        return getAllVisibleCvsBasedOnPermission(user).stream()
                .map(cvMapper::toResponse)
                .toList();
    }

    @Transactional
    public void archiveCv(AuthenticatedUser user, Long id) {
        Cv cv = findAuthorizedCv(user, id);
        cv.archive();
        cvRepository.save(cv);
    }

    @Transactional(readOnly = true)
    public CvResponse getCv(AuthenticatedUser user, Long id) {
        return cvMapper.toResponse(findAuthorizedCv(user, id));
    }

    @Transactional(readOnly = true)
    public List<CvResponse> searchCvs(AuthenticatedUser user, String q) {
        if (q == null || q.isBlank()) {
            return listCvs(user);
        }

        return getAllVisibleCvsBasedOnPermission(user).stream()
                .filter(cv -> matchesSearch(cv, q.trim()))
                .map(cvMapper::toResponse)
                .toList();
    }

    @Transactional
    public CvResponse createCv(AuthenticatedUser user, CvCreateRequest request) {
        adminAccessService.requireOwnerOrAdmin(user, request.ownerUserId());
        var owner = userRepository.findById(request.ownerUserId())
                .orElseThrow(() -> new NotFoundException("Owner user not found", "USER_NOT_FOUND"));

        Cv cv = new Cv(owner, request.title());
        cv.setSummary(request.summary());
        applyStructuredFields(cv, request.personalDetails(), request.educationEntries(), request.workExperienceEntries(),
                request.skills(), request.languages(), request.links());
        return cvMapper.toResponse(cvRepository.save(cv));
    }

    @Transactional
    public CvResponse updateCv(AuthenticatedUser user, Long id, CvUpdateRequest request) {
        Cv cv = findAuthorizedCv(user, id);
        cv.setTitle(request.title());
        cv.setSummary(request.summary());
        applyStructuredFields(cv, request.personalDetails(), request.educationEntries(), request.workExperienceEntries(),
                request.skills(), request.languages(), request.links());
        return cvMapper.toResponse(cvRepository.save(cv));
    }

    @Transactional
    public CvResponse uploadHtmlCv(AuthenticatedUser user, Long ownerUserId, String submittedTitle, MultipartFile file) {
        throw new com.example.cvmanager.common.exception.BadRequestException(
                "HTML upload is disabled. Use structured CV fields instead.",
                "CV_HTML_UPLOAD_DISABLED");
    }

    @Transactional(readOnly = true)
    public String getUploadedHtml(AuthenticatedUser user, Long id) {
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

    @Transactional(readOnly = true)
    public Cv findAuthorizedCv(AuthenticatedUser user, Long id) {
        Cv cv = findCv(id);
        adminAccessService.requireOwnerOrAdmin(user, cv.getOwner().getId());
        return cv;
    }

    private String normalizeLegacyText(String value) {
        return value.replace("\r\n", "\n").replace("\r", "\n").trim();
    }

    private Sort updatedAtDescending() {
        return Sort.by(Sort.Direction.DESC, "updatedAt");
    }

    private List<Cv> getAllVisibleCvsBasedOnPermission(AuthenticatedUser user) {
        if (user.admin()) {
            return cvRepository.findByArchivedAtIsNull(updatedAtDescending());
        }
        return getVisibleCvsForOwner(user.userId());
    }

    private List<Cv> getVisibleCvsForOwner(Long ownerId) {
        return cvRepository.findByOwnerIdAndArchivedAtIsNull(ownerId, updatedAtDescending());
    }

    private boolean matchesSearch(Cv cv, String query) {
        String loweredQuery = query.toLowerCase(Locale.ROOT);
        return containsIgnoreCase(cv.getTitle(), loweredQuery)
                || containsIgnoreCase(cv.getOwner().getEmail(), loweredQuery)
                || containsIgnoreCase(cv.getSummary(), loweredQuery);
    }

    private boolean containsIgnoreCase(String value, String loweredQuery) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(loweredQuery);
    }

    private void applyStructuredFields(
            Cv cv,
            CvPersonalDetailsRequest personalDetails,
            List<CvEducationEntryRequest> educationEntries,
            List<CvWorkExperienceEntryRequest> workExperienceEntries,
            List<CvSkillRequest> skills,
            List<CvLanguageRequest> languages,
            List<CvLinkRequest> links) {
        if (personalDetails != null) {
            applyPersonalDetails(cv, personalDetails);
        }
        if (educationEntries != null) {
            replaceEducationEntries(cv, educationEntries);
        }
        if (workExperienceEntries != null) {
            replaceWorkExperienceEntries(cv, workExperienceEntries);
        }
        if (skills != null) {
            replaceSkills(cv, skills);
        }
        if (languages != null) {
            replaceLanguages(cv, languages);
        }
        if (links != null) {
            replaceLinks(cv, links);
        }
    }

    private void applyPersonalDetails(Cv cv, CvPersonalDetailsRequest request) {
        CvPersonalDetails personalDetails = cv.getPersonalDetails();
        if (personalDetails == null) {
            personalDetails = new CvPersonalDetails(cv);
            cv.setPersonalDetails(personalDetails);
        }

        personalDetails.setFullName(request.fullName());
        personalDetails.setEmail(request.email());
        personalDetails.setPhone(request.phone());
        personalDetails.setLocation(request.location());
        personalDetails.setHeadline(request.headline());
    }

    private void replaceEducationEntries(Cv cv, List<CvEducationEntryRequest> requests) {
        cv.getEducationEntries().clear();
        for (CvEducationEntryRequest request : requests) {
            CvEducationEntry entry = new CvEducationEntry(cv, request.institution());
            entry.setDegree(request.degree());
            entry.setFieldOfStudy(request.fieldOfStudy());
            entry.setStartDate(request.startDate());
            entry.setEndDate(request.endDate());
            entry.setDescription(request.description());
            entry.setDisplayOrder(request.displayOrder());
            cv.getEducationEntries().add(entry);
        }
    }

    private void replaceWorkExperienceEntries(Cv cv, List<CvWorkExperienceEntryRequest> requests) {
        cv.getWorkExperienceEntries().clear();
        for (CvWorkExperienceEntryRequest request : requests) {
            CvWorkExperienceEntry entry = new CvWorkExperienceEntry(cv, request.employer(), request.jobTitle());
            entry.setLocation(request.location());
            entry.setStartDate(request.startDate());
            entry.setEndDate(request.endDate());
            entry.setDescription(request.description());
            entry.setDisplayOrder(request.displayOrder());
            cv.getWorkExperienceEntries().add(entry);
        }
    }

    private void replaceSkills(Cv cv, List<CvSkillRequest> requests) {
        cv.getSkills().clear();
        for (CvSkillRequest request : requests) {
            CvSkill skill = new CvSkill(cv, request.name());
            skill.setCategory(request.category());
            skill.setProficiency(request.proficiency());
            skill.setDisplayOrder(request.displayOrder());
            cv.getSkills().add(skill);
        }
    }

    private void replaceLanguages(Cv cv, List<CvLanguageRequest> requests) {
        cv.getLanguages().clear();
        for (CvLanguageRequest request : requests) {
            CvLanguage language = new CvLanguage(cv, request.name());
            language.setProficiency(request.proficiency());
            language.setDisplayOrder(request.displayOrder());
            cv.getLanguages().add(language);
        }
    }

    private void replaceLinks(Cv cv, List<CvLinkRequest> requests) {
        cv.getLinks().clear();
        for (CvLinkRequest request : requests) {
            CvLink link = new CvLink(cv, request.label(), request.url());
            link.setDisplayOrder(request.displayOrder());
            cv.getLinks().add(link);
        }
    }

    private void rejectUnsafeHtml(String html) {
        if (UNSAFE_HTML_PATTERN.matcher(html).find()) {
            throw new BadRequestException("HTML file contains unsafe content", "CV_FILE_UNSAFE");
        }
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
