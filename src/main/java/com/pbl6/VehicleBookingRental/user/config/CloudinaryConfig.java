//package com.pbl6.VehicleBookingRental.user.config;
//
//import com.cloudinary.Cloudinary;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.stereotype.Component;
//
//import java.util.HashMap;
//import java.util.Map;
//@Component
//public class CloudinaryConfig {
//    @Value("${cloudinary.cloud_name}")
//    private String cloud_name;
//
//    @Value("${cloudinary.api_key}")
//    private String api_key;
//
//    @Value("${cloudinary.api_secret}")
//    private String api_secret;
//    @Bean
//    public Cloudinary configKey() {
//        Map<String, String> config = new HashMap<>();
//        config.put("cloud_name", cloud_name);
//        config.put("api_key", api_key);
//        config.put("api_secret", api_secret);
//        return new Cloudinary(config);
//    }
//}
