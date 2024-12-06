package com.picktartup.wallet.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.picktartup.wallet.exception.BusinessException;
import com.picktartup.wallet.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.amazonaws.services.s3.model.CannedAccessControlList;

import java.io.File;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadFile(String filePath, String fileName) {
        try {
            File file = new File(filePath);
            String s3FileName = "wallets/" + fileName;

            // CannedAccessControlList 사용 방법
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucket,
                    s3FileName,
                    file
            ).withCannedAcl(CannedAccessControlList.Private);

            amazonS3Client.putObject(putObjectRequest);

            return amazonS3Client.getUrl(bucket, s3FileName).toString();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.FILE_UPLOAD_ERROR);
        }
    }
}
