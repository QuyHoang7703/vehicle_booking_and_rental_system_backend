package com.pbl6.VehicleBookingRental.user.dto.response.account;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.pbl6.VehicleBookingRental.user.domain.account.Role;
import com.pbl6.VehicleBookingRental.user.util.constant.GenderEnum;


import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ResAccountInfoDTO {
    private AccountInfo accountInfo;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AccountInfo{
        private long id;

        private String email;

        private String name;

        private String phoneNumber;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
        private LocalDate birthDay;

        private  GenderEnum gender;

        private String avatar;

        private boolean active;

        private List<String> roles;

        private Integer formRegisterBusPartnerId;
        private Integer formRegisterCarRentalPartnerId;

        // private String lockReason;
    }



}
