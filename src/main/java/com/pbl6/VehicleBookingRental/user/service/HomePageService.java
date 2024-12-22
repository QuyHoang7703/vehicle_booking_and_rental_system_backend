package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.dto.response.homePage.PopularRouteDTO;

import java.util.List;
import java.util.Map;

public interface HomePageService {
    Map<String, Integer> getHighLightNumber();
    List<PopularRouteDTO> getPopularRoutes();
}
