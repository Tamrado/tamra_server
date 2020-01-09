package com.webapp.timeline.sns.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.webapp.timeline.config.SuperS3Uploader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Component
public class ImageS3Uploader extends SuperS3Uploader {
    private static final Logger logger = LoggerFactory.getLogger(ImageS3Uploader.class);

    @Autowired
    public ImageS3Uploader(AmazonS3Client amazonS3Client) {
        super(amazonS3Client);
    }

    public String upload(MultipartFile multipartFile, String dirName) throws IOException {
        logger.info("[ImageS3Uploader] Start to upload image file in post.");

        logger.error(multipartFile.toString());
        String originalFileName = multipartFile.getOriginalFilename();
        logger.error(originalFileName);

        File uploadFile = super.convert(multipartFile)
                                .orElseThrow(() -> new IllegalArgumentException("FAIL : Convert MultipartFile -> File"));

        return upload(uploadFile, dirName,originalFileName);
    }

    public String upload(File uploadFile, String dirName,String originalFileName) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss"));
        String fileName = dirName + "/" + timestamp + originalFileName;
        String uploadImageUrl = super.putS3(uploadFile, fileName);

        super.removeNewFile(uploadFile);

        return uploadImageUrl;
    }


}
