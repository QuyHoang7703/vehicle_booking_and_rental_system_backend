package com.pbl6.VehicleBookingRental.user.dto.response.driver;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResGeneralDriverInfoDTO {
    private GeneralDriverInfo generalDriverInfo;
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GeneralDriverInfo{
        private int id;
        private String email;
        private String name;
        private String phoneNumber;
        private String permanentAddress;
        private String location;
        private String avatar;
        private int formRegisterId;

    }
}
