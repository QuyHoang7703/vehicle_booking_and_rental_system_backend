package com.pbl6.VehicleBookingRental.user.dto.request.businessPartner;

import com.pbl6.VehicleBookingRental.user.util.constant.PartnerTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqPartnerAction {
    private int formRegisterId;
    private String reason;
    private PartnerTypeEnum partnerType;

}
