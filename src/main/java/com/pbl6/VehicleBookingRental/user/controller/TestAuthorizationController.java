package com.pbl6.VehicleBookingRental.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TestAuthorizationController {
    @GetMapping("/test")
    @PreAuthorize("hasRole('USER')")
    public String test() {
        log.info("AUthenication: " + SecurityContextHolder.getContext().getAuthentication().getName());
        return "test";
    }

    @GetMapping("/test2")
    @PreAuthorize("hasRole('BUS_PARTNER')")
    public String test2() {
        log.info("AUthenication: " + SecurityContextHolder.getContext().getAuthentication().getName());
        return "test2";
    }
}
