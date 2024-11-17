//package com.pbl6.VehicleBookingRental.user.service.impl;
//
//import com.pbl6.VehicleBookingRental.user.dto.redis.BusTripScheduleRedis;
//import com.pbl6.VehicleBookingRental.user.service.BaseRedisServiceV2;
//import com.pbl6.VehicleBookingRental.user.service.BusTripScheduleRedisService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class BusTripScheduleRedisServiceImpl implements BusTripScheduleRedisService {
//    private final BaseRedisServiceV2<String, String, BusTripScheduleRedis> redisService;
//    private static final String PREFIX_KEY = "busTripSchedule:";
//    @Override
//    public void createBusTripScheduleRedis(BusTripScheduleRedis busTripScheduleRedis) {
//        String redisKey = PREFIX_KEY + busTripScheduleRedis.getBusTripScheduleId();
//        redisService.setHashSet(redisKey, "info", busTripScheduleRedis);
//
//        BusTripScheduleRedis busTripScheduleRedis1 = redisService.getHashValue(redisKey, "info");
//        log.info("AVAILABLE OF REDIS " + String.valueOf(busTripScheduleRedis1.getAvailableSeats()));
//        log.info("AVAILABLE OF REDIS " + String.valueOf(busTripScheduleRedis1.getBusTripScheduleId()));
//    }
//
//    @Override
//    public int getAvailableSeats(int id) {
////        String redisKey = PREFIX_KEY + id;
////        return redisService.getHashValue(redisKey, "availableSeat");
//        return 1;
//    }
//
//
//}
