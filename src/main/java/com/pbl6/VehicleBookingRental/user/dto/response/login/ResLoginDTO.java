package com.pbl6.VehicleBookingRental.user.dto.response.login;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.pbl6.VehicleBookingRental.user.util.constant.GenderEnum;
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

        private String email;

        private String name;

        private String phoneNumber;

//        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
//        private String birthDay;

        private GenderEnum gender;

        private String avatar;

        private boolean active;
        private List<String> roles;

    }

  
    // @Getter
    // @Setter
    // @AllArgsConstructor
    // @NoArgsConstructor
    // public static class UserGetAccount {
    //     private UserLogin user;
    // }
}
