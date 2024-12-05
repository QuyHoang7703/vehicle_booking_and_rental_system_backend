package com.pbl6.VehicleBookingRental.user.dto.response.rating;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ResRatingOrderDTO {
    private int accountId;
    private String customerName;
    private int ratingValue;
    private String comment;
    private Instant commentDate;
}
