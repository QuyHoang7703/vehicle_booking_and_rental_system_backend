package com.pbl6.VehicleBookingRental.user.config;

import com.pbl6.VehicleBookingRental.user.domain.account.Permission;
import com.pbl6.VehicleBookingRental.user.domain.account.Role;
import com.pbl6.VehicleBookingRental.user.repository.account.RoleRepository;
import com.pbl6.VehicleBookingRental.user.repository.permissionRole.PermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Database initialization started");
        long countRole = this.roleRepository.count();
        long countPermission = this.permissionRepository.count();

        if(countRole == 0) {
            ArrayList<Role> roles = new ArrayList<>();
            roles.add(new Role("ADMIN"));
            roles.add(new Role("USER"));
            roles.add(new Role("BUS_PARTNER"));
            roles.add(new Role("CAR_RENTAL_PARTNER"));
            roles.add(new Role("DRIVER"));
            this.roleRepository.saveAll(roles);
            log.info("Initialize roles successfully");
        }
        if(countPermission == 0) {
            ArrayList<Permission> permissions = new ArrayList<>();
            permissions.add(new Permission("Fetch all accounts", "/api/v1/acounts", "GET", "ACCOUNTS"));
            permissions.add(new Permission("Detail request register for bus partner", "/api/v1/bus-partners/{id}", "GET", "BUSINESS_PARTNER"));
            permissions.add(new Permission("Detail request register for car rental partner", "/api/v1/car-rental-partners/{id}", "GET", "CAR_RENTAL_PARTNER"));
            permissions.add(new Permission("Verify register for business partner", "/api/v1/business-partner/verify/{id}", "PUT", "BUSINESS_PARTNER"));
            permissions.add(new Permission("Cancel partnership for business partner", "/api/v1/business-partner/cancel-partnership/{id}", "DELETE", "BUSINESS_PARTNER"));






        }

    }
}
