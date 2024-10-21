package com.pbl6.VehicleBookingRental.user.dto.request.businessPartner;

import com.pbl6.VehicleBookingRental.user.util.constant.PartnerTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqBusinessPartnerDTO {

//    private String businessLicense;
    private String businessName;
    private String emailOfRepresentative;
    private String nameOfRepresentative;
    private String phoneOfRepresentative;
    private String address;
    private PartnerTypeEnum partnerType;


}
