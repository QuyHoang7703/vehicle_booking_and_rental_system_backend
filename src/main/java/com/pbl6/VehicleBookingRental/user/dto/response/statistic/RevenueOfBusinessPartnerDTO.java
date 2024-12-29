package com.pbl6.VehicleBookingRental.user.dto.response.statistic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RevenueOfBusinessPartnerDTO {
    private int businessPartnerId;
    private String businessName;
    private String email;
    private String bankAccountNumber;
    private String bankName;
    private String revenue;
}
