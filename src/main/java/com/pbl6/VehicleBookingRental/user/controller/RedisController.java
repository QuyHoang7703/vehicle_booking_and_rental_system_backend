//package com.pbl6.VehicleBookingRental.user.controller;
//
//import com.pbl6.VehicleBookingRental.user.RedisTest;
//import com.pbl6.VehicleBookingRental.user.domain.account.Account;
//import com.pbl6.VehicleBookingRental.user.service.BaseRedisService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api/v1/redis")
//@RequiredArgsConstructor
//public class RedisController {
//
//    private final BaseRedisService redisService;
//
//    @PostMapping
//    public void set(){
////        redisService.set("hihi","haha");
//        Account account = new Account();
//        account.setName("HVQ");
//        account.setEmail("hvq@gmail.com");
//        RedisTest redisTest = new RedisTest();
//        redisTest.setEmail("hvqbbbbb@gmail.com");
//        redisTest.setName("HOang van quy 2222");
//        redisTest.setPassword("1234567");
//
//        redisService.hashSet("TEST_REDIS", "Redis Test", account);
//    }
//}
