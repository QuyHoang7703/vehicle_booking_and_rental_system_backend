package com.pbl6.VehicleBookingRental.user.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.pbl6.VehicleBookingRental.user.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryServiceImpl implements CloudinaryService {
    private final Cloudinary cloudinary;

    @Override

    public String uploadFile(MultipartFile file) throws IOException {

        assert file.getOriginalFilename() != null;
        String publicValue = generatePublicValue(file.getOriginalFilename());
        log.info("publicValue is: {}", publicValue);
        String extension = getFileName(file.getOriginalFilename())[1];
        log.info("extension is: {}", extension);
        File fileUpload = convert(file);
        log.info("fileUpload is: {}", fileUpload);
        cloudinary.uploader().upload(fileUpload, ObjectUtils.asMap("public_id", publicValue));
//        String folderName = "pbl6";
//        cloudinary.uploader().upload(fileUpload,
//                ObjectUtils.asMap("public_id", folderName + "/" + publicValue));
        cleanDisk(fileUpload);
//        return cloudinary.url().generate(folderName + "/" + publicValue + "." + extension);
        log.info("url: " + cloudinary.url().secure(true).generate(StringUtils.join(publicValue, ".", extension)));
        return cloudinary.url().secure(true).generate(StringUtils.join(publicValue, ".", extension));
    }


    @Override
    public void deleteFile(String imageUrl) throws IOException {
        String imageName = imageUrl.split("/upload/")[1];
        String publicId = imageName.split("\\.")[0];
        try {
            // Xóa ảnh theo public_id
            Map result = cloudinary.uploader().destroy(
                    publicId,
                    ObjectUtils.emptyMap()
            );
            System.out.println("Delete result: " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override

    public List<String> uploadFiles(List<MultipartFile> files) throws IOException {
        List<String> imageUrls = new ArrayList<>();
        for(MultipartFile file : files) {
            String imageUrl = this.uploadFile(file);

            imageUrls.add(imageUrl);
        }
        return imageUrls;
    }

    @Override
    public void deleteFiles(List<String> imageNames) throws IOException {
        for(String imageName : imageNames) {
            this.deleteFile(imageName);
        }
    }

    private File convert(MultipartFile file) throws IOException {
        assert file.getOriginalFilename() != null;
        File convFile = new File(StringUtils.join(generatePublicValue(file.getOriginalFilename()), getFileName(file.getOriginalFilename())[1]));
        try(InputStream is = file.getInputStream()) {
            Files.copy(is, convFile.toPath());
        }
        return convFile;
    }

    private void cleanDisk(File file) {
        try {
            log.info("file.toPath(): {}", file.toPath());
            Path filePath = file.toPath();
            Files.delete(filePath);
        } catch (IOException e) {
            log.error("Error");
        }
    }

    public String generatePublicValue(String originalName){
        String fileName = getFileName(originalName)[0];
        return StringUtils.join(UUID.randomUUID().toString(), "_", fileName);
    }

    public String[] getFileName(String originalName) {
        return originalName.split("\\.");
    }

}
