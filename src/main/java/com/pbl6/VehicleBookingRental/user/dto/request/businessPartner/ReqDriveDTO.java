package com.pbl6.VehicleBookingRental.user.dto.request.businessPartner;

import com.pbl6.VehicleBookingRental.user.util.constant.ApprovalStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqDriveDTO {
//    private String citizenId;
//    private ApprovalStatusEnum status;
    private String location;
    private String licensePlate;
    private String vehicleInsurance;
}
