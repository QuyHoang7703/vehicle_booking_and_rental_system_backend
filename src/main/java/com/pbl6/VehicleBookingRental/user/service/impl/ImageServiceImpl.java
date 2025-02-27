package com.pbl6.VehicleBookingRental.user.service.impl;

import com.pbl6.VehicleBookingRental.user.domain.Images;
import com.pbl6.VehicleBookingRental.user.repository.image.ImageRepository;
import com.pbl6.VehicleBookingRental.user.service.ImageService;
import com.pbl6.VehicleBookingRental.user.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private final CloudinaryService cloudinaryService;
    private final ImageRepository imageRepository;

    @Override
    public List<String> uploadAndSaveImages(List<MultipartFile> files, String ownerType, int ownerId, String ownerGroup) throws IOException {
        if(files != null && !files.isEmpty()) {
            List<String> urlFiles = this.cloudinaryService.uploadFiles(files);

            for (String url : urlFiles) {
                Images image = new Images();
                image.setPathImage(url);
                image.setOwnerType(ownerType);
                image.setOwnerId(ownerId);
                image.setOwnerGroup(ownerGroup);
                this.imageRepository.save(image);
            }
        }

        return null;
    }

    @Override
    public void deleteImages(int ownerId, String ownerGroup) throws IOException {
        List<Images> imagesList = this.imageRepository.findByOwnerIdAndOwnerGroup(ownerId, ownerGroup);

        // Delete urls of images on s3 aws
        List<String> urlImages = imagesList.stream().map(Images::getPathImage).toList();
        this.cloudinaryService.deleteFiles(urlImages);

        //Delete data images in database
        this.imageRepository.deleteAll(imagesList);

    }
}
