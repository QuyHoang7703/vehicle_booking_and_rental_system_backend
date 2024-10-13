package com.pbl6.VehicleBookingRental.user.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
public interface S3Service {
    String getImageUrl(String imagePath);
    String uploadFile(MultipartFile file, String folderName);
    //  String saveFile(MultipartFile file);
    //  File convertMultiPartToFile(MultipartFile file ) throws IOException;
    //  public String saveFile2(MultipartFile file);
}
