package com.pbl6.VehicleBookingRental.domain.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pbl6.VehicleBookingRental.util.constant.GenderEnum;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;
@Getter
@Setter
@Builder
public class AccountInfoDTO {
    private String username;
    private String name;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate birthDay;
    private String phoneNumber;
    
    private GenderEnum gender;
    private String avatar;

}
