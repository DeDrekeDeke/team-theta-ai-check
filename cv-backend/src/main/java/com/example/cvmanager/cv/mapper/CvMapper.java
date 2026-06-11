package com.example.cvmanager.cv.mapper;

import com.example.cvmanager.cv.dto.response.CvResponse;
import com.example.cvmanager.cv.dto.response.CvEducationEntryResponse;
import com.example.cvmanager.cv.dto.response.CvLanguageResponse;
import com.example.cvmanager.cv.dto.response.CvLinkResponse;
import com.example.cvmanager.cv.dto.response.CvPersonalDetailsResponse;
import com.example.cvmanager.cv.dto.response.CvSkillResponse;
import com.example.cvmanager.cv.dto.response.CvWorkExperienceEntryResponse;
import com.example.cvmanager.cv.model.Cv;
import com.example.cvmanager.cv.model.CvEducationEntry;
import com.example.cvmanager.cv.model.CvLanguage;
import com.example.cvmanager.cv.model.CvLink;
import com.example.cvmanager.cv.model.CvPersonalDetails;
import com.example.cvmanager.cv.model.CvSkill;
import com.example.cvmanager.cv.model.CvWorkExperienceEntry;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CvMapper {

    public CvResponse toResponse(Cv cv) {
        return new CvResponse(
                cv.getId(),
                cv.getOwner().getId(),
                cv.getOwner().getEmail(),
                cv.getTitle(),
                cv.getSummary(),
                toPersonalDetailsResponse(cv.getPersonalDetails()),
                toEducationEntryResponses(cv.getEducationEntries()),
                toWorkExperienceEntryResponses(cv.getWorkExperienceEntries()),
                toSkillResponses(cv.getSkills()),
                toLanguageResponses(cv.getLanguages()),
                toLinkResponses(cv.getLinks()),
                cv.getCreatedAt(),
                cv.getUpdatedAt(),
                cv.getArchivedAt());
    }

    private CvPersonalDetailsResponse toPersonalDetailsResponse(CvPersonalDetails personalDetails) {
        if (personalDetails == null) {
            return null;
        }

        return new CvPersonalDetailsResponse(
                personalDetails.getId(),
                personalDetails.getFullName(),
                personalDetails.getEmail(),
                personalDetails.getPhone(),
                personalDetails.getLocation(),
                personalDetails.getHeadline());
    }

    private List<CvEducationEntryResponse> toEducationEntryResponses(List<CvEducationEntry> entries) {
        return entries.stream()
                .sorted(Comparator.comparingInt(CvEducationEntry::getDisplayOrder))
                .map(entry -> new CvEducationEntryResponse(
                        entry.getId(),
                        entry.getInstitution(),
                        entry.getDegree(),
                        entry.getFieldOfStudy(),
                        entry.getStartDate(),
                        entry.getEndDate(),
                        entry.getDescription(),
                        entry.getDisplayOrder()))
                .toList();
    }

    private List<CvWorkExperienceEntryResponse> toWorkExperienceEntryResponses(List<CvWorkExperienceEntry> entries) {
        return entries.stream()
                .sorted(Comparator.comparingInt(CvWorkExperienceEntry::getDisplayOrder))
                .map(entry -> new CvWorkExperienceEntryResponse(
                        entry.getId(),
                        entry.getEmployer(),
                        entry.getJobTitle(),
                        entry.getLocation(),
                        entry.getStartDate(),
                        entry.getEndDate(),
                        entry.getDescription(),
                        entry.getDisplayOrder()))
                .toList();
    }

    private List<CvSkillResponse> toSkillResponses(List<CvSkill> skills) {
        return skills.stream()
                .sorted(Comparator.comparingInt(CvSkill::getDisplayOrder))
                .map(skill -> new CvSkillResponse(
                        skill.getId(),
                        skill.getName(),
                        skill.getCategory(),
                        skill.getProficiency(),
                        skill.getDisplayOrder()))
                .toList();
    }

    private List<CvLanguageResponse> toLanguageResponses(List<CvLanguage> languages) {
        return languages.stream()
                .sorted(Comparator.comparingInt(CvLanguage::getDisplayOrder))
                .map(language -> new CvLanguageResponse(
                        language.getId(),
                        language.getName(),
                        language.getProficiency(),
                        language.getDisplayOrder()))
                .toList();
    }

    private List<CvLinkResponse> toLinkResponses(List<CvLink> links) {
        return links.stream()
                .sorted(Comparator.comparingInt(CvLink::getDisplayOrder))
                .map(link -> new CvLinkResponse(
                        link.getId(),
                        link.getLabel(),
                        link.getUrl(),
                        link.getDisplayOrder()))
                .toList();
    }
}
