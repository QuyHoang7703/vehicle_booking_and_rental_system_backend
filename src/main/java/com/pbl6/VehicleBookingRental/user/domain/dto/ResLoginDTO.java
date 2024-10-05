package com.pbl6.VehicleBookingRental.user.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class ResLoginDTO {
    @JsonProperty("access_token")
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
