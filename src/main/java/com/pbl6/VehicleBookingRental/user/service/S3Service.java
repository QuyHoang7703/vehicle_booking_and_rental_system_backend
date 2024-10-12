package com.pbl6.VehicleBookingRental.user.service;

import org.springframework.web.multipart.MultipartFile;

public interface S3Service {
    String getImageUrl(String imagePath);
    String uploadFile(MultipartFile file, String folderName);
}
