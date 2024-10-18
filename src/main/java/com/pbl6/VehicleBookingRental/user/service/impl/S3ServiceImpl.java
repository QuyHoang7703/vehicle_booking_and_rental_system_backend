package com.pbl6.VehicleBookingRental.user.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.amazonaws.services.s3.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.pbl6.VehicleBookingRental.user.service.S3Service;

import java.io.File;
import lombok.RequiredArgsConstructor;
import java.io.FileOutputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3ServiceImpl implements S3Service{
    @Value("${bucketName}")
    private String bucketName;
    @Value("${aws.url.images}")
    private String imageUrl;

    private final AmazonS3 amazonS3;


    public String getImageUrl(String imagePath) {
        return imageUrl + imagePath; 
    }

    @Override
    public String uploadFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String newFilename = UUID.randomUUID().toString()+ "_" + originalFilename;
        String filePath =  newFilename;

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize()); //Set size for file
            String contentType = file.getContentType(); // Lấy Content-Type từ MultipartFile
            System.out.println(">>>>>>>>>>>> CONTENT TYPE: " + contentType);
            if (contentType != null && contentType.startsWith("image/")) {
                metadata.setContentType(contentType);
            } else {
                // Nếu không phải là hình ảnh, có thể đặt giá trị mặc định hoặc xử lý lỗi
                throw new IllegalArgumentException("Uploaded file is not an image.");
            }
            // Push file to S3
            PutObjectResult putObjectResult = amazonS3.putObject(bucketName, filePath, file.getInputStream(), metadata);
            return getImageUrl(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to S3: " + e.getMessage(), e);
        }
    }

    @Override
    public List<String> uploadFiles(List<MultipartFile> files) {
        List<String> imageUrls = new ArrayList<>(); // Lưu trữ URL của các ảnh đã upload
        for (MultipartFile file : files) {
            String imageUrl = this.uploadFile(file);
            imageUrls.add(imageUrl);
        }
        return imageUrls;
    }

    @Override
    public void deleteFile(String filePath) {
        String [] parts = filePath.split("/");
        String keyFromUrl = parts[parts.length - 1];
        try {
           amazonS3.deleteObject(bucketName, keyFromUrl);
           log.info("Deleted file: " + parts[parts.length - 1]);
        }catch (AmazonS3Exception e) {
           throw new RuntimeException("Failed to delete file from S3: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteFiles(List<String> filePaths) {
        for(String filePath : filePaths) {
            deleteFile(filePath);
        }
    }


}
