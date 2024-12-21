package com.pbl6.VehicleBookingRental.user.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CloudinaryService {
    String uploadFile(MultipartFile file) throws IOException;
    void deleteFile(String imageName) throws IOException;
    List<String> uploadFiles(List<MultipartFile> files) throws IOException;
    void deleteFiles(List<String> imageNames) throws IOException;
}
