package com.webapp.timeline.membership.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
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
public class UserImageS3Component {

    Logger log = LoggerFactory.getLogger(this.getClass());
    private AmazonS3Client amazonS3Client;

    @Autowired
    public UserImageS3Component(AmazonS3Client amazonS3Client){
        this.amazonS3Client = amazonS3Client;
    }
    @Value("${cloud.aws.bucket}")
    private String bucket;

    public String upload(MultipartFile multipartFile, String userName, HttpServletResponse response) throws IOException {
        if(multipartFile == null) response.setStatus(404);
        else {
            File uploadFile = convert(multipartFile,response)
                    .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File로 전환이 실패했습니다."));
            return upload(uploadFile, userName,response);
        }
        return null;
    }

    private String upload(File uploadFile, String userName, HttpServletResponse response) {
        response.setStatus(404);
        if(userName == null) return null;
        String fileName = "userImage/" + userName;
        String uploadImageUrl = putS3(uploadFile, fileName,response);
        removeNewFile(uploadFile,response);
        if(uploadImageUrl != null)
            response.setStatus(200);
        return uploadImageUrl;
    }

    private String putS3(File uploadFile, String fileName, HttpServletResponse response) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    private void removeNewFile(File targetFile, HttpServletResponse response) {
        if (targetFile.delete()) {
            log.info("파일이 삭제되었습니다.");
        } else {
            log.info("파일이 삭제되지 못했습니다.");
        }
    }

    private Optional<File> convert(MultipartFile file, HttpServletResponse response) throws IOException {
        File convertFile = new File(file.getOriginalFilename());
        if(convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }

        return Optional.empty();
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
