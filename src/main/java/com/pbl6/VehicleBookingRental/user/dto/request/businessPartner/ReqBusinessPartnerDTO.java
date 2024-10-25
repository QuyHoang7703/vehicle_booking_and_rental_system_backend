package com.pbl6.VehicleBookingRental.user.dto.request.businessPartner;

import com.pbl6.VehicleBookingRental.user.dto.AccountInfo;
import com.pbl6.VehicleBookingRental.user.dto.request.bankAccount.ReqBankAccount;
import com.pbl6.VehicleBookingRental.user.util.constant.PartnerTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqBusinessPartnerDTO {
    private String businessName;
    private String emailOfRepresentative;
    private String nameOfRepresentative;
    private String phoneOfRepresentative;
    private String address;
    private PartnerTypeEnum partnerType;
    private List<String> policies;
    private ReqBankAccount bankAccount;

}
