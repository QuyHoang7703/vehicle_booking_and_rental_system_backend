package com.pbl6.VehicleBookingRental.user.config;

import com.pbl6.VehicleBookingRental.user.domain.account.Role;
import com.pbl6.VehicleBookingRental.user.repository.account.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ApplicationInitConfig {
    private final RoleRepository roleRepository;

    @Bean
    ApplicationRunner applicationRunner() {
        return args -> {
            if(roleRepository.findByName("ADMIN").isEmpty()) {
                Role adminRole = Role.builder().name("ADMIN").build();
                this.roleRepository.save(adminRole);
            }
            if(roleRepository.findByName("USER").isEmpty()) {
                Role userRole = Role.builder().name("USER").build();
                this.roleRepository.save(userRole);
            }
            if(roleRepository.findByName("BUS_PARTNER").isEmpty()) {
                Role busPartnerRole = Role.builder().name("BUS_PARTNER").build();
                this.roleRepository.save(busPartnerRole);
            }
            if(roleRepository.findByName("CAR_RENTAL_PARTNER").isEmpty()) {
                Role carRentalPartner = Role.builder().name("CAR_RENTAL_PARTNER").build();
                this.roleRepository.save(carRentalPartner);
            }
            if(roleRepository.findByName("DRIVER").isEmpty()) {
                Role driver = Role.builder().name("DRIVER").build();
                this.roleRepository.save(driver);
            }

        };
    }
}
