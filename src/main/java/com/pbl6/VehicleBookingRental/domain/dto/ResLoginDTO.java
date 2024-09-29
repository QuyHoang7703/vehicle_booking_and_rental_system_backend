package com.pbl6.VehicleBookingRental.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class ResLoginDTO {
    private String accessToken;
    private AccountLogin accountLogin;
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AccountLogin {
        private long id;
        private String username;
        private String name;
    }

    // public static class 
    // @Getter
    // @Setter
    // @AllArgsConstructor
    // @NoArgsConstructor
    // public static class UserGetAccount {
    //     private UserLogin user;
    // }
}
