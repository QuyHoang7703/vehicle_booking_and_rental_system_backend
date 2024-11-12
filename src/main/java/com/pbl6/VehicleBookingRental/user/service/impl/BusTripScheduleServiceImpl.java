package com.pbl6.VehicleBookingRental.user.service.impl;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.BreakDay;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.Bus;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusTrip;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusTripSchedule;
import com.pbl6.VehicleBookingRental.user.dto.request.bus.ReqBusTripScheduleDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.bus.ResBusTripDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.bus.ResBusTripScheduleDTO;
import com.pbl6.VehicleBookingRental.user.repository.busPartner.BreakDayRepository;
import com.pbl6.VehicleBookingRental.user.repository.busPartner.BusTripRepository;
import com.pbl6.VehicleBookingRental.user.repository.busPartner.BusTripScheduleRepository;
import com.pbl6.VehicleBookingRental.user.service.BusService;
import com.pbl6.VehicleBookingRental.user.service.BusTripScheduleService;
import com.pbl6.VehicleBookingRental.user.service.BusTripService;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BusTripScheduleServiceImpl implements BusTripScheduleService {
    private final BusTripScheduleRepository busTripScheduleRepository;
    private final BusTripRepository busTripRepository;
    private final BusService busService;
    private final BreakDayRepository breakDayRepository;

    @Override
    public BusTripSchedule createBusTripSchedule(ReqBusTripScheduleDTO reqBusTripScheduleDTO) throws IdInvalidException, ApplicationException {
        BusTripSchedule busTripSchedule = new BusTripSchedule();

        BusTrip busTrip = this.busTripRepository.findById(reqBusTripScheduleDTO.getBusTripId())
                .orElseThrow(()-> new IdInvalidException("BusTrip not found"));
        busTripSchedule.setBusTrip(busTrip);

        Bus bus = this.busService.findBusById(reqBusTripScheduleDTO.getBusId());
        busTripSchedule.setBus(bus);
        busTripSchedule.setAvailableSeats(bus.getBusType().getNumberOfSeat());

        busTripSchedule.setDepartureTime(reqBusTripScheduleDTO.getDepartureTime());
        busTripSchedule.setDiscountPercentage(reqBusTripScheduleDTO.getDiscountPercentage());
        busTripSchedule.setPriceTicket(reqBusTripScheduleDTO.getPriceTicket());
        busTripSchedule.setStartOperationDay(reqBusTripScheduleDTO.getStartOperationDay());

        BusTripSchedule savedBusTripSchedule = this.busTripScheduleRepository.save(busTripSchedule);

        List<BreakDay> breakDays = reqBusTripScheduleDTO.getBreakDays().stream()
                .map(breakDay -> {
                    return BreakDay.builder()
                            .startDay(breakDay.getStartDay())
                            .endDay(breakDay.getEndDay())
                            .busTripSchedule(savedBusTripSchedule)  // Gán BusTripSchedule cho BreakDay
                            .build();
                }).toList();

        savedBusTripSchedule.setBreakDays(breakDays);
        this.breakDayRepository.saveAll(breakDays);
        return savedBusTripSchedule;
    }

    @Override
    public ResBusTripScheduleDTO convertToResBusTripScheduleDTO(BusTripSchedule busTripSchedule) {
        ResBusTripDTO.BusTripInfo busTripInfo = ResBusTripDTO.BusTripInfo.builder()
                .id(busTripSchedule.getBusTrip().getId())
                .departureLocation(busTripSchedule.getBusTrip().getDepartureLocation())
                .arrivalLocation(busTripSchedule.getBusTrip().getArrivalLocation())
                .durationJourney(busTripSchedule.getBusTrip().getDurationJourney())
                .build();
        ResBusTripScheduleDTO.BusInfo busInfo = ResBusTripScheduleDTO.BusInfo.builder()
                .licensePlate(busTripSchedule.getBus().getLicensePlate())
                .busType(busTripSchedule.getBus().getBusType())
                .build();

        ResBusTripScheduleDTO res = ResBusTripScheduleDTO.builder()
                .busTripInfo(busTripInfo)
                .busInfo(busInfo)
                .departureTime(busTripSchedule.getDepartureTime())
                .discountPercentage(busTripSchedule.getDiscountPercentage())
                .priceTicket(busTripSchedule.getPriceTicket())
                .startOperationDay(busTripSchedule.getStartOperationDay())
                .availableSeats(busTripSchedule.getAvailableSeats()) // Có thể thay thế sau này khi order
                .breakDays(busTripSchedule.getBreakDays())
                .build();
        return res;
    }

    @Override
    public ResBusTripScheduleDTO getBusTripScheduleById(int id) throws IdInvalidException {
        BusTripSchedule busTripSchedule = this.busTripScheduleRepository.findById(id)
                .orElseThrow(()-> new IdInvalidException("BusTrip not found"));

        return this.convertToResBusTripScheduleDTO(busTripSchedule);
    }


}
