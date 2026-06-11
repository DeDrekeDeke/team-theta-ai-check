package com.example.cvmanager.cv.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.example.cvmanager.auth.security.AuthenticatedUser;
import com.example.cvmanager.common.exception.BadRequestException;
import com.example.cvmanager.common.security.AdminAccessService;
import com.example.cvmanager.cv.mapper.CvMapper;
import com.example.cvmanager.cv.repository.CvRepository;
import com.example.cvmanager.user.repository.UserRepository;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class CvServiceTest {

    private CvRepository cvRepository;
    private UserRepository userRepository;
    private AuthenticatedUser user;
    private CvService cvService;

    @BeforeEach
    void setUp() {
        cvRepository = mock(CvRepository.class);
        userRepository = mock(UserRepository.class);
        user = new AuthenticatedUser(1L, "alice@example.com", "Alice Student", false);

        cvService = new CvService(
                cvRepository,
                userRepository,
                mock(CvMapper.class),
                //new CvStorageProperties("build/test-uploads"),
                mock(AdminAccessService.class));
    }

    @Test
    void uploadHtmlCvIsDisabledForInvalidOwnerId() {
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> cvService.uploadHtmlCv(user, 0L, "CV", null));

        assertEquals("CV_HTML_UPLOAD_DISABLED", exception.getCode());
        verifyNoInteractions(userRepository, cvRepository);
    }

    @Test
    void uploadHtmlCvIsDisabled() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "alice-cv.html",
                "text/html",
                "<html><body>Alice CV</body></html>".getBytes(StandardCharsets.UTF_8));

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> cvService.uploadHtmlCv(user, 1L, "Alice CV", file));

        assertEquals("CV_HTML_UPLOAD_DISABLED", exception.getCode());
        verifyNoInteractions(userRepository, cvRepository);
    }
}
