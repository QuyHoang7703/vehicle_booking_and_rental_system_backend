package com.pbl6.VehicleBookingRental.user.service.impl;

import com.pbl6.VehicleBookingRental.user.domain.Images;
import com.pbl6.VehicleBookingRental.user.repository.image.ImageRepository;
import com.pbl6.VehicleBookingRental.user.service.ImageService;
import com.pbl6.VehicleBookingRental.user.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private final S3Service s3Service;
    private final ImageRepository imageRepository;

    @Override
    public List<String> uploadAndSaveImages(List<MultipartFile> files, String ownerType, int ownerId) {
        if(files != null && !files.isEmpty()) {
            List<String> urlFiles = this.s3Service.uploadFiles(files);

            for (String url : urlFiles) {
                Images image = new Images();
                image.setPathImage(url);
                image.setOwnerType(ownerType);
                image.setOwnerId(ownerId);
                this.imageRepository.save(image);
            }
        }

        return null;
    }
}
