package com.pbl6.VehicleBookingRental.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyDTO {
    private String email;
    private String otp;
}

