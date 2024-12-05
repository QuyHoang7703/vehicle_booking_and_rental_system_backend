package com.pbl6.VehicleBookingRental.user.dto.request.rating;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReqCreateRatingDTO {
    private String orderId;
    private int ratingValue;
    private String comment;

}
