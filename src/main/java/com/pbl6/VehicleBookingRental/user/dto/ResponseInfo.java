package com.pbl6.VehicleBookingRental.user.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class ResponseInfo<T>{
    private T info;
}
