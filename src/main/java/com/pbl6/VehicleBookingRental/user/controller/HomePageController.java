package com.pbl6.VehicleBookingRental.user.controller;

import com.pbl6.VehicleBookingRental.user.dto.response.homePage.PopularRouteDTO;
import com.pbl6.VehicleBookingRental.user.service.BusTripService;
import com.pbl6.VehicleBookingRental.user.service.HomePageService;
import com.pbl6.VehicleBookingRental.user.service.OrderBusTripService;
import com.pbl6.VehicleBookingRental.user.service.statistic.StatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class HomePageController {
    private final HomePageService homePageService;

    @GetMapping("home-page/popular-routes")
    public ResponseEntity<List<PopularRouteDTO>> getPopularRoute() {
        return ResponseEntity.status(HttpStatus.OK).body(this.homePageService.getPopularRoutes());
    }

    @GetMapping("home-page/highlight-numbers")
    public ResponseEntity<Map<String, Integer>> getHighlightNumber() {

        return ResponseEntity.status(HttpStatus.OK).body(this.homePageService.getHighLightNumber());
    }


}
