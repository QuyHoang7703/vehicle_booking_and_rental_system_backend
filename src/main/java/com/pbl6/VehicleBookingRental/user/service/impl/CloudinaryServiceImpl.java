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
    private final String folderName = "Vehicle_Rental_Booking";

//    @Override
//    public String uploadFile(MultipartFile file) throws IOException {
//        assert file.getOriginalFilename() != null;
//        String publicValue = generatePublicValue(file.getOriginalFilename());
//        log.info("publicValue is: {}", publicValue);
//        String extension = getFileName(file.getOriginalFilename())[1];
//        log.info("extension is: {}", extension);
//        byte[] fileBytes = file.getBytes();
//        cloudinary.uploader().upload(
//                fileBytes,
//                ObjectUtils.asMap(
//                        "folder", folderName, // Chỉ định thư mục rõ ràng
//                        "public_id", publicValue, // ID của file trong thư mục
//                        "resource_type", "auto"
//                )
//        );
//        log.info("url: " + cloudinary.url().secure(true).generate(StringUtils.join(folderName, "/", publicValue, ".", extension)));
//        return cloudinary.url().secure(true).generate(StringUtils.join(folderName, "/", publicValue, ".", extension));
//    }
    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        assert file.getOriginalFilename() != null;
        String publicValue = generatePublicValue(file.getOriginalFilename());
        log.info("publicValue is: {}", publicValue);

        // Upload file lên Cloudinary
        Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", folderName,
                        "public_id", publicValue,
                        "resource_type", "auto",
                        "invalidate", true // Đảm bảo CDN làm mới cache
                )
        );

        // Lấy URL tối ưu
        String secureUrl = (String) uploadResult.get("secure_url");
        log.info("Secure URL: {}", secureUrl);
        return secureUrl;
    }


    @Override
    public void deleteFile(String imageUrl) throws IOException {
        try {
            // Kiểm tra URL có chứa thư mục "PBL06" hay không
            if (!imageUrl.contains("/" + folderName + "/")) {
                System.out.println("URL không chứa thư mục " + folderName);
                return;
            }

            // Tách theo thư mục PBL06
            String[] parts = imageUrl.split("/" + folderName + "/");
            // In ra kết quả để kiểm tra
//            System.out.println("imageUrl: " + imageUrl);
//            System.out.println("parts.length: " + parts.length);
//            if (parts.length >= 2) {
//                System.out.println("parts[0]: " + parts[0]);
//                System.out.println("parts[1]: " + parts[1]);
//            }

            // Kiểm tra mảng parts có ít nhất 2 phần tử không
            if (parts.length < 2) {
                throw new IllegalArgumentException("URL không hợp lệ hoặc không có tên file");
            }

            // Lấy imageName từ URL
            String imageName = parts[1]; // Phần sau thư mục PBL06

            // Lấy publicId từ imageName (loại bỏ phần mở rộng)
            String publicId = imageName.split("\\.")[0]; // Chỉ lấy phần trước dấu chấm

            // In ra giá trị publicId để kiểm tra
            System.out.println("publicId: " + publicId);

            // Xóa ảnh theo publicId
            Map result = cloudinary.uploader().destroy(
                    folderName + "/" + publicId, // Kèm tên thư mục trong public_id
                    ObjectUtils.emptyMap()
            );

            System.out.println("Delete result: " + result);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Lỗi khi xóa file: " + e.getMessage());
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

//    private File convert(MultipartFile file) throws IOException {
//        assert file.getOriginalFilename() != null;
//        File convFile = new File(StringUtils.join(generatePublicValue(file.getOriginalFilename()), getFileName(file.getOriginalFilename())[1]));
//        try(InputStream is = file.getInputStream()) {
//            Files.copy(is, convFile.toPath());
//        }
//        return convFile;
//    }
//
//    private void cleanDisk(File file) {
//        try {
//            log.info("file.toPath(): {}", file.toPath());
//            Path filePath = file.toPath();
//            Files.delete(filePath);
//        } catch (IOException e) {
//            log.error("Error");
//        }
//    }

    public String generatePublicValue(String originalName){
        String fileName = getFileName(originalName)[0];
        return StringUtils.join(UUID.randomUUID().toString(), "_", fileName);
    }

    public String[] getFileName(String originalName) {
        return originalName.split("\\.");
    }

}
