package com.pbl6.VehicleBookingRental.user.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageService {
    public List<String> uploadAndSaveImages(List<MultipartFile> files, String ownerType, int ownerId);
}
