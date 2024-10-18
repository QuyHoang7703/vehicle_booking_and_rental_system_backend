package com.pbl6.VehicleBookingRental.user.dto.request.account;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pbl6.VehicleBookingRental.user.util.constant.GenderEnum;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqAccountInfoDTO {
    private int id;

    private String username;

    private String name;

    private String phoneNumber;


    // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate birthDay;

    private  GenderEnum gender;

    private String avatar;


}
