package com.pbl6.VehicleBookingRental.user.service.impl;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.pbl6.VehicleBookingRental.user.service.S3Service;

import java.io.File;
import lombok.RequiredArgsConstructor;
import java.io.FileOutputStream;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.CannedAccessControlList;

@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service{
    @Value("${bucketName}")
    private String bucketName;
    @Value("${aws.url.images}")
    private String imageUrl;

    private final AmazonS3 amazonS3;


    public String getImageUrl(String imagePath) {
        return imageUrl + imagePath; 
    }

    public String uploadFile(MultipartFile file, String folderName) {
        // return "abc";
        String originalFilename = file.getOriginalFilename();

        String newFilename = UUID.randomUUID().toString().substring(0, 10) + "_" + originalFilename;
      
         String filePath = folderName + "/" + newFilename;
        //  String filePath =  newFilename;


        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize()); //Set size for file
            String contentType = file.getContentType(); // Lấy Content-Type từ MultipartFile
            System.out.println(">>>>>>>>>>>> CONTENT TYPE: " + contentType);
            if (contentType != null && contentType.startsWith("image/")) {
                metadata.setContentType(contentType);
            } else {
                // Nếu không phải là hình ảnh, có thể đặt giá trị mặc định hoặc xử lý lỗi
                throw new IllegalArgumentException("Uploaded file is not an image.");
            }
            // metadata.setContentType(contentType != null ? contentType : "multipart/form-data");
            // Push file to S3
            PutObjectResult putObjectResult = amazonS3.putObject(bucketName, filePath, file.getInputStream(), metadata);
            return getImageUrl(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to S3: " + e.getMessage(), e);
        }
    }

  
//     public String saveFile(MultipartFile file) {
//         String originalFilename = file.getOriginalFilename();
//         int count = 0;
//         int maxTries = 3;
//         while(true) {
//             try {
//                 File file1 = convertMultiPartToFile(file);
//                 PutObjectResult putObjectResult = amazonS3.putObject(bucketName, originalFilename, file1);
//                 return putObjectResult.getContentMd5();
//             } catch (IOException e) {
//                 if (++count == maxTries) throw new RuntimeException(e);
//             }
//         }

//     }

//    public  File convertMultiPartToFile(MultipartFile file ) throws IOException
//     {
//         File convFile = new File( file.getOriginalFilename() );
//         FileOutputStream fos = new FileOutputStream( convFile );
//         fos.write( file.getBytes() );
//         fos.close();
//         return convFile;
//     }

//     public String saveFile2(MultipartFile file) {
//         String originalFilename = "abc/" + file.getOriginalFilename(); // Thêm folder khi lưu file
//         int count = 0;
//         int maxTries = 3;
//         while (true) {
//             try {
//                 ObjectMetadata metadata = new ObjectMetadata();
//                 metadata.setContentLength(file.getSize());
//                 metadata.setContentType(file.getContentType()); // Đặt Content-Type chính xác

//                 // Đẩy lên S3 và đặt quyền public
//                 amazonS3.putObject(new PutObjectRequest(bucketName, originalFilename, file.getInputStream(), metadata)
//                         .withCannedAcl(CannedAccessControlList.PublicRead));

//                 return originalFilename; // Trả về tên file hoặc URL tùy ý
//             } catch (IOException e) {
//                 if (++count == maxTries) throw new RuntimeException(e);
//             }
//         }
//     }
 
    


}
