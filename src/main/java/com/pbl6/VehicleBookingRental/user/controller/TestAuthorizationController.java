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
    @PreAuthorize("hasAuthority('Get all accounts v2')")
    public String test2() {
        log.info("Authenication after using jwt: " + SecurityContextHolder.getContext().getAuthentication());
        return "test2";
    }
}
