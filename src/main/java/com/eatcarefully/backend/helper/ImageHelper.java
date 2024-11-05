package com.eatcarefully.backend.helper;


import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Service
public class ImageHelper {



    public  String getFileExtension(MultipartFile file) {

        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null && originalFilename.contains(".")) {
            return originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        }
        return null;
    }


    public  Boolean isFileExtensionSupported(MultipartFile file){

        String extension  =getFileExtension(file);


        return (extension != null) && (extension.equals("jpg") || extension.equals("jpeg") || extension.equals("png"));
    }


    public BufferedImage convertMultipartFileToBufferedImage(MultipartFile file) throws IOException{

        BufferedImage image = ImageIO.read(file.getInputStream());
        if (image == null) {
            throw new IOException("File is not an image or format is unsupported.");
        }
        return image;

    }


}
