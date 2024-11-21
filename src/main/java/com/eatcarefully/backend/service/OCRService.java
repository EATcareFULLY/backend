package com.eatcarefully.backend.service;

import lombok.AllArgsConstructor;
import net.sourceforge.tess4j.ITesseract;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@AllArgsConstructor
public class OCRService {

    private final ITesseract tesseract;
    private final ImagePreprocessingService imagePreprocessingService;


    public String extractTextFromFile(MultipartFile imageFile) {
        try {

            // convert to temporary file for OpenCV processing
            Path tempFile = Files.createTempFile("ocr_", ".png");
            imageFile.transferTo(tempFile.toFile());

            Mat processed = imagePreprocessingService.preprocessImage(tempFile);

            // convert to temp file and then BufferedImage for OCR
            Path processedTempFile = Files.createTempFile("processed_", ".png");
            Imgcodecs.imwrite(processedTempFile.toString(), processed);

            BufferedImage bufferedImage = ImageIO.read(processedTempFile.toFile());

            // perform OCR
            String result = tesseract.doOCR(bufferedImage);

            Files.deleteIfExists(tempFile);
            Files.deleteIfExists(processedTempFile);

            return result;

        } catch (Exception e) {
            return null;
        }
    }
}