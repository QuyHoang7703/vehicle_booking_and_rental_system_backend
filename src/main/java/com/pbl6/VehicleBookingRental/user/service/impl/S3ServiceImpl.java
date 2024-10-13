package com.pbl6.VehicleBookingRental.user.service.impl;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.pbl6.VehicleBookingRental.user.service.S3Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service{
    @Value("${bucketName}")
    private String bucketName;
    @Value("${aws.url.images}")
    private String imageUrl;

    private final AmazonS3 amazonS3;


    public String getImageUrl(String imagePath) {
        return imageUrl + imagePath; 
    }

    public String uploadFile(MultipartFile file, String folderName) {
        String originalFilename = file.getOriginalFilename();

        String newFilename = UUID.randomUUID().toString().substring(0, 10) + "_" + originalFilename;
      
         String filePath = folderName + "/" + newFilename;

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize()); //Set size for file
            String contentType = file.getContentType(); // Lấy Content-Type từ MultipartFile
            metadata.setContentType(contentType != null ? contentType : "application/octet-stream");
            // Push file to S3
            PutObjectResult putObjectResult = amazonS3.putObject(bucketName, filePath, file.getInputStream(), metadata);
            return getImageUrl(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to S3: " + e.getMessage(), e);
        }
    }
    // public String uploadFile(MultipartFile file, String folderName) {
    //     String originalFilename = file.getOriginalFilename();
    //     String newFilename = UUID.randomUUID().toString().substring(0, 10) + "_" + originalFilename;
    //     String filePath = folderName + "/" + newFilename;
    
    //     try {
    //         ObjectMetadata metadata = new ObjectMetadata();
    //         metadata.setContentLength(file.getSize());
    //         // Thiết lập Content-Type
    //         String contentType = file.getContentType(); // Lấy Content-Type từ MultipartFile
    //         metadata.setContentType(contentType != null ? contentType : "application/octet-stream");
    
    //         // Push file to S3
    //         PutObjectResult putObjectResult = amazonS3.putObject(bucketName, filePath, file.getInputStream(), metadata);
    //         return getImageUrl(filePath);
    //     } catch (IOException e) {
    //         throw new RuntimeException("Failed to upload file to S3: " + e.getMessage(), e);
    //     }
    // }
    


}
