package com.eatcarefully.backend.service;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
public class ImagePreprocessingService {

    static {
        nu.pattern.OpenCV.loadLocally();
    }

    public Mat preprocessImage(Path imagePath) {
        Mat original = Imgcodecs.imread(imagePath.toString());
        return preprocessImage(original);
    }

    public Mat preprocessImage(Mat originalImage) {
        // grayscale
        Mat gray = new Mat();
        Imgproc.cvtColor(originalImage, gray, Imgproc.COLOR_BGR2GRAY);

        return gray;

//        // CLAHE
//        Mat contrast = new Mat();
//        Imgproc.createCLAHE(4.0, new Size(16, 16)).apply(gray, contrast);
//
//        return contrast;

//        // global threshold
//        Mat threshold = new Mat();
//        Imgproc.threshold(gray, threshold, 120, 255, Imgproc.THRESH_BINARY);
//
//
//        return threshold;
    }
}