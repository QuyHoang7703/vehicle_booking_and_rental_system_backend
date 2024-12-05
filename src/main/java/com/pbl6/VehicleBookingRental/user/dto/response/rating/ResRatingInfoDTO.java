package com.pbl6.VehicleBookingRental.user.dto.response.rating;

import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResRatingInfoDTO {
    double averageRating;
    ResultPaginationDTO result;
}
