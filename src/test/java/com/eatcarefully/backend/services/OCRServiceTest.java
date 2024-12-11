package com.eatcarefully.backend.services;

import com.eatcarefully.backend.service.ImagePreprocessingService;
import com.eatcarefully.backend.service.OCRService;
import net.sourceforge.tess4j.ITesseract;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class OCRServiceTest {

    @Mock
    private ITesseract tesseract;

    @Mock
    private ImagePreprocessingService imagePreprocessingService;

    @InjectMocks
    private OCRService ocrService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void Should_ExtractTextFromImageFile() throws Exception {

        MultipartFile mockFile = mock(MultipartFile.class);
        Path tempFile = Files.createTempFile("ocr_", ".png");
        File tempFileAsFile = tempFile.toFile();
        Path processedTempFile = Files.createTempFile("processed_", ".png");
        Mat processedMat = Mat.eye(3, 3, CvType.CV_8UC1);


        doAnswer(invocation -> {
            Files.copy(tempFile, tempFileAsFile.toPath());
            return null;
        }).when(mockFile).transferTo(any(File.class));


        when(imagePreprocessingService.preprocessImage(any(Path.class))).thenReturn(processedMat);

        when(tesseract.doOCR(any(BufferedImage.class))).thenReturn("Extracted text");

        String result = ocrService.extractTextFromFile(mockFile);

        assertNotNull(result);
        assertEquals("Extracted text", result);

        Files.deleteIfExists(tempFile);
        Files.deleteIfExists(processedTempFile);
    }


    @Test
    void Should_ReturnNull_OnProcessingError() throws Exception {

        MultipartFile mockFile = mock(MultipartFile.class);
        doAnswer(invocation -> {
            throw new RuntimeException();
        }).when(mockFile).transferTo(any(File.class));

        String result = ocrService.extractTextFromFile(mockFile);


        assertNull(result);
    }



}
