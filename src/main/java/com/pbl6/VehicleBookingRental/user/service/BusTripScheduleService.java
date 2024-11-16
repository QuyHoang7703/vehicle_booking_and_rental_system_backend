package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusTripSchedule;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.bus.ReqBusTripScheduleDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.bus.ResBusTripScheduleDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.bus.ResBusTripScheduleDetailDTO;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface BusTripScheduleService {
    BusTripSchedule createBusTripSchedule(ReqBusTripScheduleDTO reqBusTripScheduleDTO) throws IdInvalidException, ApplicationException;
    ResBusTripScheduleDetailDTO convertToResBusTripScheduleDetailDTO(BusTripSchedule busTripSchedule);
    ResBusTripScheduleDetailDTO getBusTripScheduleById(int id) throws IdInvalidException;
    ResBusTripScheduleDTO convertToResBusTripScheduleDTO(BusTripSchedule busTripSchedule);
    ResultPaginationDTO getAllBusTripSchedules(Specification<BusTripSchedule> spec, Pageable pageable) throws ApplicationException;
    ResultPaginationDTO getAllBusTripScheduleAvailableForUser(Specification<BusTripSchedule> spec, Pageable pageable) throws ApplicationException;
}
