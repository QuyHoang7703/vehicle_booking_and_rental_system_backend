package com.pbl6.VehicleBookingRental.user.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pbl6.VehicleBookingRental.user.util.constant.AccountEnum;
import com.pbl6.VehicleBookingRental.user.util.constant.GenderEnum;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResAccountDTO {
    private long id;

    private String email;

    private String name;

    private String phoneNumber;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate birthDay;

    private  GenderEnum gender;

    private String avatar;

    private boolean active;

    private String lockReason;


}
