package com.pbl6.VehicleBookingRental.user.dto.response.businessPartner;

import com.pbl6.VehicleBookingRental.user.dto.response.bankAccount.ResBankAccountDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResBusPartnerDTO extends ResBusinessPartnerDTO{
    private BusPartnerInfo busPartnerInfo;
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BusPartnerInfo{
        private String description;
        private String urlFanpage;
        private List<String> policy;
        private List<String> urlLicenses;
        private List<String> urlImages;
        private ResBankAccountDTO bankAccount;
    }

}
