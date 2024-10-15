package com.pbl6.VehicleBookingRental.user.dto.response.login;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.pbl6.VehicleBookingRental.user.domain.account.Role;
import lombok.*;

import java.util.List;

@Data
public class ResLoginDTO {
    @JsonProperty("access_token")
    private String accessToken;
    private AccountLogin accountLogin;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AccountLogin {
        private long id;
        private String username;
        private String name;
        private String avatar;
//        private List<Role> roles;

    }

  
    // @Getter
    // @Setter
    // @AllArgsConstructor
    // @NoArgsConstructor
    // public static class UserGetAccount {
    //     private UserLogin user;
    // }
}
