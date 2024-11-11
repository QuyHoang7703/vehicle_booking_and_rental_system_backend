package com.pbl6.VehicleBookingRental.user.dto.response.driver;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pbl6.VehicleBookingRental.user.util.constant.ApprovalStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResGeneralDriverInfoDTO {
    private GeneralDriverInfo generalDriverInfo;
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GeneralDriverInfo{
        private int id;
        private String email;
        private String name;
        private String phoneNumber;
        private String permanentAddress;
        private String location;
        private String avatar;
        private int formRegisterId;
        private ApprovalStatusEnum approvalStatus = ApprovalStatusEnum.PENDING_APPROVAL;
        private Instant timeBecomePartner;
        private Instant timeUpdate;
        private String cancelReason;
    }

}
