package com.eatcarefully.backend.service;

import lombok.AllArgsConstructor;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

@Service
@AllArgsConstructor
public class OCRService {

    private ITesseract tesseract;

    public ResponseEntity<String> extractTextFromFile(MultipartFile imageFile) {
        try {
            BufferedImage bufferedImage = ImageIO.read(imageFile.getInputStream());

            String result = tesseract.doOCR(bufferedImage);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error etracting text from image: " + e.getMessage());
        }
    }
}
