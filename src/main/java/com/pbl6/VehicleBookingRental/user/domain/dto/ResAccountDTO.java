package com.pbl6.VehicleBookingRental.user.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pbl6.VehicleBookingRental.user.util.constant.AccountEnum;


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

    private boolean male;

    private String avatar;

    private boolean active;

    private String lockReason;
    
    private AccountEnum accountType;


}
