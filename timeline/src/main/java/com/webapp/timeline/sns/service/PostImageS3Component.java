package com.webapp.timeline.sns.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.webapp.timeline.config.SuperS3Uploader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;


@Component
public class PostImageS3Component extends SuperS3Uploader {
    private static final Logger logger = LoggerFactory.getLogger(PostImageS3Component.class);

    @Autowired
    public PostImageS3Component(AmazonS3Client amazonS3Client) {
        super(amazonS3Client);
    }


    public String upload(MultipartFile multipartFile, String dirName) throws IOException {
        File uploadFile = super.convert(multipartFile)
                .orElseThrow(() -> new IllegalArgumentException("FAIL : Convert MultipartFile -> File"));

        return upload(uploadFile, dirName);
    }

    private String upload(File uploadFile, String dirName) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss"));
        String fileName = dirName + "/" + timestamp;
        String uploadImageUrl = super.putS3(uploadFile, fileName);
        super.removeNewFile(uploadFile);
        return uploadImageUrl;
    }

}
