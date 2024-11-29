package com.pbl6.VehicleBookingRental.user.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Create object to response with format like FormatRestResponse
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RestResponse<T> {
    private int statusCode;
    private String error;
    private Object message;
    private T data;
}
