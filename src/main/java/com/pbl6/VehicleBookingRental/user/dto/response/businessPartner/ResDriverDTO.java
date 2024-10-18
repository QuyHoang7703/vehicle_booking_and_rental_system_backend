package com.pbl6.VehicleBookingRental.user.dto.response.businessPartner;

import com.pbl6.VehicleBookingRental.user.util.constant.ApprovalStatusEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResDriverDTO {
    private DriverInfo driverInfo;
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DriverInfo {
        private int id;
        @Enumerated(EnumType.STRING)
        private ApprovalStatusEnum approvalStatus = ApprovalStatusEnum.PENDING_APPROVAL;
        private String location;
        private String licensePlate;
        private String vehicleInsurance;
        private List<String> citizenImages;
        private List<String> driverImages;
    }

}
