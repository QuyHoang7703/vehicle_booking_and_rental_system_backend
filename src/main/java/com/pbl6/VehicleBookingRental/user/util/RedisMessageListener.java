package com.pbl6.VehicleBookingRental.user.util;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusTripSchedule;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.OrderBusTrip;
import com.pbl6.VehicleBookingRental.user.domain.car_rental.VehicleRegister;
import com.pbl6.VehicleBookingRental.user.repository.busPartner.BusTripScheduleRepository;
import com.pbl6.VehicleBookingRental.user.repository.vehicle_rental.VehicleRegisterRepo;
import com.pbl6.VehicleBookingRental.user.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedisMessageListener implements MessageListener {
    private final RedisService<String, String, OrderBusTrip> redisServiceOrderBusTrip;
    private final BusTripScheduleRepository busTripScheduleRepository;
    private final VehicleRegisterRepo vehicleRegisterRepo;
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
        String[] parts = key.split("-");
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
        String[] parts = key.split("-");
        String typeOfOrder = parts[1];
        String vehicleRegisterId = parts[3];
        String amount = parts[4];

        log.info("Type of order {}, vehicleRegisterId {}, amount {}", typeOfOrder, vehicleRegisterId, amount);

        VehicleRegister vehicleRegister = this.vehicleRegisterRepo.findById(Integer.parseInt(vehicleRegisterId))
                .orElseThrow(() -> new RuntimeException("BusTripSchedule not found"));
        vehicleRegister.setAmount(vehicleRegister.getAmount() + Integer.parseInt(amount));
        this.vehicleRegisterRepo.save(vehicleRegister);
        log.info("Vehicle register updated amount");
    }
}
