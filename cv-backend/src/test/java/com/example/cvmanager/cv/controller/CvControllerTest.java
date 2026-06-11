package com.example.cvmanager.cv.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.cvmanager.auth.security.JwtService;
import com.example.cvmanager.common.exception.GlobalExceptionHandler;
import com.example.cvmanager.cv.dto.request.CvCreateRequest;
import com.example.cvmanager.cv.service.CvService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CvController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class CvControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CvService cvService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void createCvRejectsBlankTitle() throws Exception {
        mockMvc.perform(post("/api/cvs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "ownerUserId": 1,
                                  "title": "",
                                  "summary": "Student CV summary"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.details[0]", containsString("Title is required")));

        verifyNoInteractions(cvService);
    }

    @Test
    void createCvAcceptsStructuredSections() throws Exception {
        mockMvc.perform(post("/api/cvs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "ownerUserId": 1,
                                  "title": "Alice CV",
                                  "summary": "Student CV summary",
                                  "personalDetails": {
                                    "fullName": "Alice Student",
                                    "email": "alice@example.com",
                                    "phone": "+358 40 123 4567",
                                    "location": "Helsinki",
                                    "headline": "Junior developer"
                                  },
                                  "educationEntries": [
                                    {
                                      "institution": "University of Helsinki",
                                      "degree": "BSc",
                                      "fieldOfStudy": "Computer Science",
                                      "startDate": "2022-09-01",
                                      "endDate": "2025-05-31",
                                      "description": "Software engineering track",
                                      "displayOrder": 0
                                    }
                                  ],
                                  "workExperienceEntries": [
                                    {
                                      "employer": "Example Oy",
                                      "jobTitle": "Intern",
                                      "location": "Espoo",
                                      "startDate": "2024-06-01",
                                      "endDate": "2024-08-31",
                                      "description": "Built internal tools",
                                      "displayOrder": 0
                                    }
                                  ],
                                  "skills": [
                                    {
                                      "name": "Java",
                                      "category": "Backend",
                                      "proficiency": "Intermediate",
                                      "displayOrder": 0
                                    }
                                  ],
                                  "languages": [
                                    {
                                      "name": "English",
                                      "proficiency": "Fluent",
                                      "displayOrder": 0
                                    }
                                  ],
                                  "links": [
                                    {
                                      "label": "Portfolio",
                                      "url": "https://example.com",
                                      "displayOrder": 0
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isCreated());

        verify(cvService).createCv(
                isNull(),
                argThat((CvCreateRequest request) ->
                        request.personalDetails() != null
                                && "Alice Student".equals(request.personalDetails().fullName())
                                && request.educationEntries().size() == 1
                                && request.workExperienceEntries().size() == 1
                                && request.skills().size() == 1
                                && request.languages().size() == 1
                                && request.links().size() == 1));
    }

    @Test
    void createCvRejectsInvalidNestedLinkUrl() throws Exception {
        mockMvc.perform(post("/api/cvs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "ownerUserId": 1,
                                  "title": "Alice CV",
                                  "summary": "Student CV summary",
                                  "links": [
                                    {
                                      "label": "Portfolio",
                                      "url": "not-a-url",
                                      "displayOrder": 0
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.details[0]", containsString("Link URL must be valid")));

        verifyNoInteractions(cvService);
    }

    @Test
    void uploadCvRejectsMissingFileWithValidationResponse() throws Exception {
        mockMvc.perform(multipart("/api/cvs/upload")
                        .param("ownerUserId", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.details[0]").value("file: is required"));

        verifyNoInteractions(cvService);
    }
}
