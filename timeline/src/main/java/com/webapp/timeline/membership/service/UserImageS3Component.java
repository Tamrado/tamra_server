package com.webapp.timeline.membership.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.webapp.timeline.config.SuperS3Uploader;
import com.webapp.timeline.exception.NoInformationException;
import com.webapp.timeline.exception.WrongCodeException;
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

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public UserImageS3Component(AmazonS3Client amazonS3Client) {
        super(amazonS3Client);
    }

    public String upload(MultipartFile multipartFile, String userName) throws IOException {
        if(multipartFile != null) {
            File uploadFile = super.convert(multipartFile)
                    .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File로 전환이 실패했습니다."));
            return upload(uploadFile, userName);
        }
        else return "https://repotimeline.s3.ap-northeast-2.amazonaws.com/userImage/default_thumbnail.png";
    }

    private String upload(File uploadFile, String userName){
        if(userName == null) return null;
        String fileName = "userImage/" + userName;
        String uploadImageUrl = super.putS3(uploadFile, fileName);
        super.removeNewFile(uploadFile);
        return uploadImageUrl;
    }

}
