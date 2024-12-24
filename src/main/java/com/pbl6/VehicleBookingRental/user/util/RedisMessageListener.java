package com.pbl6.VehicleBookingRental.user.util;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusTripSchedule;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.OrderBusTrip;
import com.pbl6.VehicleBookingRental.user.domain.car_rental.CarRentalOrders;
import com.pbl6.VehicleBookingRental.user.domain.car_rental.CarRentalService;
import com.pbl6.VehicleBookingRental.user.domain.car_rental.VehicleRegister;
import com.pbl6.VehicleBookingRental.user.dto.car_rental_DTO.VehicleRentalServiceDTO;
import com.pbl6.VehicleBookingRental.user.dto.redis.OrderVehicleRentalRedisDTO;
import com.pbl6.VehicleBookingRental.user.repository.busPartner.BusTripScheduleRepository;
import com.pbl6.VehicleBookingRental.user.repository.vehicle_rental.VehicleRegisterRepo;
import com.pbl6.VehicleBookingRental.user.repository.vehicle_rental.VehicleRentalOrderRepo;
import com.pbl6.VehicleBookingRental.user.repository.vehicle_rental.VehicleRentalServiceRepo;
import com.pbl6.VehicleBookingRental.user.service.RedisService;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedisMessageListener implements MessageListener {
    private final RedisService<String, String, OrderBusTrip> redisServiceOrderBusTrip;
    private final BusTripScheduleRepository busTripScheduleRepository;
    private final VehicleRentalServiceRepo vehicleRentalServiceRepo;
    private final DateUtil dateUtil;
    private final RedisService<String, String, OrderVehicleRentalRedisDTO> redisOrderVehicleRental;
    private final RedisService<String,String,Integer> redisServiceVehicleRental;
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String key  = new String(message.getBody());
        log.info("Key expired: " + key);
        if(key.contains("BUS_TRIP")){
            handleBusTripOrder(key);
        }
        if(key.contains("VEHICLE_RENTAL")){
            handleVehicleRentalOrder(key);
        }
        // Xử lý các order loại khác ...

    }

    private void handleBusTripOrder(String key) {
        String[] parts = key.split("\\$");
        String typeOfOrder = parts[1];
        String busTripScheduleId = parts[3];
        String numberOfTicket = parts[4];

        log.info("Type of order {}, busTripScheduleId {}, numberOfTicket {}", typeOfOrder, busTripScheduleId, numberOfTicket);

        BusTripSchedule busTripSchedule = this.busTripScheduleRepository.findById(Integer.parseInt(busTripScheduleId))
                .orElseThrow(() -> new RuntimeException("BusTripSchedule not found"));
//        busTripSchedule.setAvailableSeats(busTripSchedule.getAvailableSeats() + Integer.parseInt(numberOfTicket));
        this.busTripScheduleRepository.save(busTripSchedule);
        log.info("BusTripSchedule updated number of seats");
    }
    private void handleVehicleRentalOrder(String key) {
        String[] parts = key.split("\\$");
        String typeOfOrder = parts[1];
        String orderId = parts[2];
        String vehicleRentalServiceId = parts[3];
        String amount = parts[4];
        String startTime = parts[5];
        String endTime = parts[6];

        log.info("Type of order {}, vehicleRentalServiceId {}, amount {}", typeOfOrder, vehicleRentalServiceId, amount);

        CarRentalService carRentalService = this.vehicleRentalServiceRepo.findById(Integer.parseInt(vehicleRentalServiceId))
                .orElseThrow(()->new RuntimeException("Vehicle Rental Order not found"));
        Instant startInstant = Instant.parse(startTime);
        Instant endInstant = Instant.parse(endTime);

        //update amount key redis
        List<String> timeSlots = dateUtil.generateTimeSlots(startInstant,endInstant);
        for (String timeSlot : timeSlots) {
            String keyy = "vehicle-rental:" +vehicleRentalServiceId;
            int rentalAmount = Integer.parseInt(amount);
            int vehicleRegisterAmount = carRentalService.getVehicleRegister().getAmount();
            // Lấy số lượng hiện tại từ Redis
            Integer currentAmount = redisServiceVehicleRental.getHashValue(keyy, timeSlot);
            if (currentAmount != null) {
                // Trường hợp có dữ liệu: kiểm tra số lượng và tăng lại
                long updatedAmount = redisServiceVehicleRental.incrementHashValue(keyy, timeSlot, rentalAmount);

                // Nếu tăng vượt quá số xe gốc
                if (updatedAmount > vehicleRegisterAmount ) {
                    // Giảm lại vì tăng đã cộng  nhầm
                    redisServiceVehicleRental.incrementHashValue(keyy, timeSlot, -rentalAmount);
                }
            } else {
                // Trường hợp hết xe
                log.info("Khung giờ không khả dụng "+ timeSlot );
            }
        }

        log.info("Vehicle register updated amount");
    }
}
