package com.example.cvmanager.cv.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

import com.example.cvmanager.common.exception.BadRequestException;
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
    private CvService cvService;

    @BeforeEach
    void setUp() {
        cvRepository = mock(CvRepository.class);
        userRepository = mock(UserRepository.class);
        cvService = new CvService(
                cvRepository,
                userRepository,
                mock(CvMapper.class));
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
                () -> cvService.uploadHtmlCv(0L, "CV", file));

        assertEquals("CV_HTML_UPLOAD_DISABLED", exception.getCode());
        verifyNoInteractions(userRepository, cvRepository);
    }
}
