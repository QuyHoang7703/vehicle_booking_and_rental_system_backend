package com.pbl6.VehicleBookingRental.user.dto.request.login;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqLoginDTO { 
    @NotBlank(message = "Username cannot be left blank")
    private String username;
    @NotBlank(message = "Password cannot be left blank")
    private String password;
   
}
