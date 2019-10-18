package com.webapp.timeline.sns.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.util.IOUtils;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import java.io.*;
import java.util.*;


@Component("s3Uploader")
public class S3Uploader {

    private static final Logger logger = LoggerFactory.getLogger(S3Uploader.class);

    private final AmazonS3Client amazonS3Client;
    private LinkedHashMap<Integer, String> imageUrlMap;
    private int urlIndex;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Autowired
    public S3Uploader(AmazonS3Client amazonS3Client) {
        this.amazonS3Client = amazonS3Client;
    }

    public LinkedHashMap<Integer, String> getImageUrlMap() {
        return this.imageUrlMap;
    }

    private PutObjectResult upload(String filePath, String dirName, String fileName) throws IOException {
        return upload(new FileInputStream(filePath), dirName, fileName);
    }

    public List<PutObjectResult> upload(MultipartFile[] multipartFiles, String dirName) {
        if(multipartFiles.length == 0)
            return null;

        imageUrlMap = new LinkedHashMap<>();
        urlIndex = 0;
        List<PutObjectResult> putObjectResults = new ArrayList<>();

        Arrays.stream(multipartFiles).filter(multipartFile ->
                !StringUtils.isEmpty(multipartFile.getOriginalFilename()))
                .forEach(multipartFile -> {
                    try {
                        putObjectResults.add(upload(multipartFile.getInputStream(), dirName, multipartFile.getOriginalFilename()));
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                });

        return putObjectResults;
    }

    private PutObjectResult upload(InputStream inputStream, String dirName, String fileName) throws IOException{
        ObjectMetadata objectMetadata = new ObjectMetadata();
        byte[] bytes = IOUtils.toByteArray(inputStream);
        objectMetadata.setContentLength(bytes.length);
        ByteArrayInputStream specifiedInputStream = new ByteArrayInputStream(bytes);
        fileName = dirName + "/" + fileName;

        PutObjectRequest putObjectRequest =
                new PutObjectRequest(bucket, fileName, specifiedInputStream, objectMetadata)
                .withCannedAcl(CannedAccessControlList.PublicReadWrite);

        PutObjectResult putObjectResult =
                amazonS3Client.putObject(putObjectRequest);

        String uploadImageUrl = amazonS3Client.getUrl(bucket, fileName).toString();
        imageUrlMap.put(++urlIndex, uploadImageUrl);

        try {
            inputStream.close();
        } catch(IOException e) {
            e.printStackTrace();
        }

        return putObjectResult;
    }
}

