package com.pbl6.VehicleBookingRental.user.controller;

import com.pbl6.VehicleBookingRental.user.service.OrderBusTripService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class HomePageController {
    private final OrderBusTripService orderBusTripService;
    @GetMapping("home-page/popular-route")
    public ResponseEntity<String> getPopularRoute() {

        return ResponseEntity.status(HttpStatus.OK).body("Popular Route");
    }
}
