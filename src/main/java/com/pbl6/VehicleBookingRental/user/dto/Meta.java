package com.pbl6.VehicleBookingRental.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Meta {
    private int currentPage;
    private int pageSize;
    private int pages;
    private long total;
}
