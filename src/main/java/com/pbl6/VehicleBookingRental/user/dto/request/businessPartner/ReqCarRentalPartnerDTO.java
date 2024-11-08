package com.pbl6.VehicleBookingRental.user.dto.request.businessPartner;

import com.pbl6.VehicleBookingRental.user.util.constant.ClientTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqCarRentalPartnerDTO extends ReqBusinessPartnerDTO{
    private ClientTypeEnum clientType;

}
