package com.pbl6.VehicleBookingRental.user.dto.response.businessPartner;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
        private String insuranceInformation;
        private List<String> urlLicenses;
        private List<String> urlImages;
    }
}
