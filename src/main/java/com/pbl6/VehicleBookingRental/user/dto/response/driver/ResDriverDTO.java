package com.pbl6.VehicleBookingRental.user.dto.response.driver;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.pbl6.VehicleBookingRental.user.dto.response.bankAccount.ResBankAccount;
import com.pbl6.VehicleBookingRental.user.util.constant.ApprovalStatusEnum;
import com.pbl6.VehicleBookingRental.user.util.constant.LicenseTypeEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResDriverDTO {
    private ResGeneralDriverInfoDTO.GeneralDriverInfo generalDriverInfo;
    private CitizenDTO citizen;
    private DriverLicenseDTO driverLicense;
    private VehicleDTO vehicle;
    private RelativeDTO relative;
    private ResBankAccount bankAccount;
    @Enumerated(EnumType.STRING)
    private ApprovalStatusEnum approvalStatus = ApprovalStatusEnum.PENDING_APPROVAL;
//    private DetailDriverInfo detailDriverInfo;

//    @Data
//    @AllArgsConstructor
//    @NoArgsConstructor
//    public static class DetailDriverInfo {
//        private int id;
//        private String citizenID;
//        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
//        private LocalDate dateOfIssue;
//        private String placeOfIssue;
//        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
//        private LocalDate expiryDate;
//        private String licensePlateNumber;
//        private String driverLicenseNumber;
//        private LicenseTypeEnum licenseType;
//        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
//        private LocalDate issueDateLicense;
////        private String permanentAddress;
//        private String phoneNumberOfRelative;
////        private String location;
//        private String vehicleType;
//
//        private String avatarOfDriver;
//        private List<String> citizenImages;
//        private List<String> vehicleImages;
//        private List<String> driverLicenseImage;
//        private List<String> vehicleInsurance;
//
//    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CitizenDTO {
        private String citizenId;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
        private LocalDate dateOfIssue;
        private String placeOfIssue;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
        private LocalDate expiryDate;
        private List<String> citizenImages;

    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DriverLicenseDTO{
        private String driverLicenseNumber;
        private LicenseTypeEnum licenseType;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
        private LocalDate issueDateLicense;
        private List<String> driverLicenseImage;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VehicleDTO{
        private String licensePlateNumber;
        private String vehicleType;
        private List<String> vehicleImages;
        private List<String> vehicleInsurance;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RelativeDTO{
        private String nameOfRelative;
        private String phoneOfRelative;
        private String relationship;
    }



}
