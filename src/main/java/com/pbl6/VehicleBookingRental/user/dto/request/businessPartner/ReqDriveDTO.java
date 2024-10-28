package com.pbl6.VehicleBookingRental.user.dto.request.businessPartner;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pbl6.VehicleBookingRental.user.domain.VehicleType;
import com.pbl6.VehicleBookingRental.user.dto.request.account.ReqAccountInfoDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.bankAccount.ReqBankAccount;
import com.pbl6.VehicleBookingRental.user.util.constant.ApprovalStatusEnum;
import com.pbl6.VehicleBookingRental.user.util.constant.LicenseTypeEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqDriveDTO {
    private ReqAccountInfoDTO accountInfo;
    private CitizenDTO citizen;
    private DriverLicenseDTO driverLicense;
    private VehicleDTO vehicle;
    private RelativeDTO relative;
    private ReqBankAccount bankAccount;


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
        private String permanentAddress;
        private String location;

    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DriverLicenseDTO{
        private String driverLicenseNumber;
        private LicenseTypeEnum licenseType;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
        private LocalDate issueDateLicense;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VehicleDTO{
        private String licensePlateNumber;
        private String vehicleType;

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
