package com.example.springbootgithubactiondemo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;

@Service
public class S3Service {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String uploadPDF(ByteArrayOutputStream pdfContent, String name) throws IOException {

        // Create a PutObjectRequest
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(name)
                .contentType("application/pdf")
                .build();
        // Upload the file to S3
        PutObjectResponse response = s3Client.putObject(putObjectRequest, RequestBody.fromBytes(pdfContent.toByteArray()) );

        // Return the URL or the object key
        return "https://" + bucketName + ".s3.amazonaws.com/" + name;
    }
}
