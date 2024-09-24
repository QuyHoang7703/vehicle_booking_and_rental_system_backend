package com.pbl6.VehicleBookingRental.domain;

import lombok.Getter;
import lombok.Setter;

// Create object to response with format like FormatRestResponse
@Getter
@Setter
public class RestResponse<T> {
    private int statusCode;
    private String error;
    private Object message;
    private T data;
}
