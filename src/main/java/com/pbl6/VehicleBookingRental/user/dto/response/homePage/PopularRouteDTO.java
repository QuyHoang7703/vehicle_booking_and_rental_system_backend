package com.pbl6.VehicleBookingRental.user.dto.response.homePage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PopularRouteDTO {
    private String route;
    private String infoPrice;
    private String imageUrl;
}
