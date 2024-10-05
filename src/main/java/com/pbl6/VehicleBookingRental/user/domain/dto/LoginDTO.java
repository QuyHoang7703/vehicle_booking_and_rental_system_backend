package com.pbl6.VehicleBookingRental.user.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDTO { 
    @NotBlank(message = "Username cannot be left blank")
    private String username;
    @NotBlank(message = "Password cannot be left blank")
    private String password;
    // @NotBlank(message = "Confirm password cannot be left blank")
    // private String confirmPassword;
}
