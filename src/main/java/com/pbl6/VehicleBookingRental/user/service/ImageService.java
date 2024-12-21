package com.pbl6.VehicleBookingRental.user.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageService {
    List<String> uploadAndSaveImages(List<MultipartFile> files, String ownerType, int ownerId, String ownerGroup);
    void deleteImages(int ownerId, String ownerGroup);
}
