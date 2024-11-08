package com.pbl6.VehicleBookingRental.user.dto.response.businessPartner;

import com.pbl6.VehicleBookingRental.user.dto.response.bankAccount.ResBankAccountDTO;
import com.pbl6.VehicleBookingRental.user.util.constant.ClientTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResCarRentalPartnerDTO extends ResBusinessPartnerDTO{
    private CarRentalPartnerInfo carRentalPartnerInfo;
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CarRentalPartnerInfo{
        private ClientTypeEnum clientType;
        private List<String> policies;
        private List<String> urlLicenses;
        private List<String> urlImages;
        private ResBankAccountDTO bankAccount;
    }

//    private String cancelReason;
//    private Instant timeCancel;
}
