package com.eatcarefully.backend.service;

import lombok.AllArgsConstructor;
import net.sourceforge.tess4j.ITesseract;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@AllArgsConstructor
public class OCRService {

    private final ITesseract tesseract;
    private final ImagePreprocessingService imagePreprocessingService;

    private final String imageStoragePath = "preprocessed-images";

    private Path createStorageDirectoryIfNotExists() throws Exception {
        Path storagePath = Paths.get(imageStoragePath);
        if (!Files.exists(storagePath)) {
            Files.createDirectories(storagePath);
        }
        return storagePath;
    }

    private String generateFileName(String originalFileName) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String cleanFileName = originalFileName.replaceAll("[^a-zA-Z0-9.]", "_");
        return timestamp + "_" + cleanFileName;
    }

    public ResponseEntity<String> extractTextFromFile(MultipartFile imageFile) {
        try {
            // saving preprocessed images
            Path storagePath = createStorageDirectoryIfNotExists();

            // convert to temporary file for OpenCV processing
            Path tempFile = Files.createTempFile("ocr_", ".png");
            imageFile.transferTo(tempFile.toFile());

            // flag to preprocess image
            boolean shouldPreprocess = true;

            Mat processed;
            if(shouldPreprocess){
                processed = imagePreprocessingService.preprocessImage(tempFile);

                // Save preprocessed image to storage
                String preprocessedFileName = generateFileName(imageFile.getOriginalFilename());
                Path savedImagePath = storagePath.resolve(preprocessedFileName);
                Imgcodecs.imwrite(savedImagePath.toString(), processed);
            } else {
                processed = Imgcodecs.imread(tempFile.toString());
            }

            // Save processed image to temporary file for OCR
            Path processedTempFile = Files.createTempFile("processed_", ".png");
            Imgcodecs.imwrite(processedTempFile.toString(), processed);

            BufferedImage bufferedImage = ImageIO.read(processedTempFile.toFile());

            // perform OCR
            String result = tesseract.doOCR(bufferedImage);

            Files.deleteIfExists(tempFile);
            Files.deleteIfExists(processedTempFile);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error extracting text from image: " + e.getMessage());
        }
    }
}