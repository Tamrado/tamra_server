package com.webapp.timeline.membership.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.webapp.timeline.config.SuperS3Uploader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;

@Component
public class UserImageS3Component extends SuperS3Uploader {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${cloud.aws.bucket}")
    private String bucket;

    public UserImageS3Component(AmazonS3Client amazonS3Client) {
        super(amazonS3Client);
    }

    public String upload(MultipartFile multipartFile, String userName, HttpServletResponse response) throws IOException {
        response.setStatus(200);
        if(multipartFile != null) {
            File uploadFile = super.convert(multipartFile)
                    .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File로 전환이 실패했습니다."));
            return upload(uploadFile, userName, response);
        }
        else return "https://timelines3bucket.s3.ap-northeast-2.amazonaws.com/userImage/default_thumbnail.png";
    }

    private String upload(File uploadFile, String userName, HttpServletResponse response) {
        response.setStatus(404);
        if(userName == null) return null;
        String fileName = "userImage/" + userName;
        String uploadImageUrl = super.putS3(uploadFile, fileName);
        super.removeNewFile(uploadFile);
        if(uploadImageUrl != null)
            response.setStatus(200);
        return uploadImageUrl;
    }

    // 파일 삭제
    public void fileDelete(String userName, HttpServletResponse response) {
        String fileName = "userImage/" + userName;
        String imgName = (fileName).replace(File.separatorChar, '/');
        try {
            amazonS3Client.deleteObject(bucket, imgName);
        }
        catch(AmazonS3Exception e) {
            log.error(e.toString());
        }
    }

}
