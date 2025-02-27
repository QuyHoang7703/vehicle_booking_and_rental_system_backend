package com.pbl6.VehicleBookingRental.user.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // cho phép các URL nào có thể kết nối tới backend
        configuration.setAllowedOrigins(
                Arrays.asList("http://localhost:3000", "http://localhost:4173",
                        "http://localhost:5173", "https://vehicle-booking-and-rental-system.vercel.app",
                        "https://safelytravel.online",
                        "https://150.95.110.230:443",
                        "http://safelytravel.online",
                        "http://150.95.110.230:80",
                        "http://150.95.110.230:3000",
                        "http://safelytravel:3000",
                        "https://safelytravel.online"));
//        configuration.setAllowedOrigins(
//                Arrays.asList("*"));

        // các method nào đc kết nối
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // các phần header được phép gửi lên
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "x-no-retry"));

        // gửi kèm cookies hay không
        configuration.setAllowCredentials(true);

        // thời gian pre-flight request có thể cache (tính theo seconds)
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // cấu hình cors cho tất cả api
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
