package com.pbl6.VehicleBookingRental.user.dto.request.bankAccount;

import com.pbl6.VehicleBookingRental.user.util.constant.PartnerTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqBankAccount {
    private String accountNumber;
    private String accountHolderName;
    private String bankName;
    private PartnerTypeEnum partnerType;
}
