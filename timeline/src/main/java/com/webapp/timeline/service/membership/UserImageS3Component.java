package com.webapp.timeline.service.membership;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.webapp.timeline.service.result.SingleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

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

    public SingleResult<String> upload(MultipartFile multipartFile, String userName) throws IOException {
        File uploadFile = convert(multipartFile)
                .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File로 전환이 실패했습니다."));
        return upload(uploadFile,userName);
    }

    private SingleResult<String> upload(File uploadFile, String userName) {
        String fileName = "userImage/" + userName;
        String uploadImageUrl = putS3(uploadFile, fileName);
        SingleResult<String> singleResult = new SingleResult<>();
        removeNewFile(uploadFile);
        if(uploadImageUrl != null){
            singleResult.setData(uploadImageUrl);
            singleResult.setMsg("success upload");
            singleResult.setCode(200);
            singleResult.setSuccess(true);
        }
        return singleResult;
    }

    private String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("파일이 삭제되었습니다.");
        } else {
            log.info("파일이 삭제되지 못했습니다.");
        }
    }

    private Optional<File> convert(MultipartFile file) throws IOException {
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
    public void fileDelete(String userName) {
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
