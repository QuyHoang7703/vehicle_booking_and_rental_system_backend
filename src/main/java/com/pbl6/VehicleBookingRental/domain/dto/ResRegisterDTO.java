package com.pbl6.VehicleBookingRental.domain.dto;
import com.pbl6.VehicleBookingRental.util.constant.GenderEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class ResRegisterDTO {
    private long id;

    private String email;

    private String name;

    private String phoneNumber;

    private GenderEnum gender;

    private String avatar;
    
}
