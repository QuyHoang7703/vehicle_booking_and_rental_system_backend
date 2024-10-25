package com.pbl6.VehicleBookingRental.user.dto.response.businessPartner;

import com.pbl6.VehicleBookingRental.user.dto.AccountInfo;
import com.pbl6.VehicleBookingRental.user.dto.response.bankAccount.ResBankAccount;
import com.pbl6.VehicleBookingRental.user.util.constant.ApprovalStatusEnum;
import com.pbl6.VehicleBookingRental.user.util.constant.PartnerTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResBusinessPartnerDTO {
    private BusinessPartnerInfo businessInfo;
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BusinessPartnerInfo{
        private int id;
        private String businessName;
        private String emailOfRepresentative;
        private String nameOfRepresentative;
        private String phoneOfRepresentative;
        private String address;
        private PartnerTypeEnum partnerType;
        private ApprovalStatusEnum approvalStatus;
        private String avatar;
        private AccountInfo accountInfo;
    }
//    private ResBankAccount resBankAccount;


}
