package com.pbl6.VehicleBookingRental.user.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CloudinaryService {
    String uploadImage(MultipartFile file) throws IOException;
    void deleteFile(String imageName) throws IOException;
    List<String> uploadImages(List<MultipartFile> files) throws IOException;
    void deleteFiles(List<String> imageNames) throws IOException;
}
