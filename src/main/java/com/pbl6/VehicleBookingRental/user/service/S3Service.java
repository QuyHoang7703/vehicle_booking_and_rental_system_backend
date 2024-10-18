package com.pbl6.VehicleBookingRental.user.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.List;

public interface S3Service {
    String getImageUrl(String imagePath);
    String uploadFile(MultipartFile file);
    List<String> uploadFiles(List<MultipartFile> files);
    void deleteFile(String filePath);
    void deleteFiles(List<String> filePaths);
}
