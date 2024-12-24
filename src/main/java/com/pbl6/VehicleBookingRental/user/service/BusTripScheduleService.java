package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.BreakDay;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusTripSchedule;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.bus.ReqBreakDayDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.bus.ReqBusTripScheduleDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.bus.ResBusTripScheduleDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.bus.ResBusTripScheduleDetailForAdminDTO;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;

public interface BusTripScheduleService {
    BusTripSchedule createBusTripSchedule(ReqBusTripScheduleDTO reqBusTripScheduleDTO) throws IdInvalidException, ApplicationException;
    ResBusTripScheduleDetailForAdminDTO convertToResBusTripScheduleDetailDTO(BusTripSchedule busTripSchedule, LocalDate departureDate);
    ResBusTripScheduleDetailForAdminDTO getBusTripScheduleById(int id, LocalDate departureDate) throws IdInvalidException;
    ResultPaginationDTO getAllBusTripScheduleByBusTripId(Specification<BusTripSchedule> spec, Pageable pageable, int busTripId, LocalDate departureDate) throws ApplicationException, IdInvalidException;
    ResultPaginationDTO getAllBusTripScheduleAvailableForUser(Specification<BusTripSchedule> spec, Pageable pageable,String departureLocation, String arrivalProvince, LocalDate departureDate) throws ApplicationException;
    ResBusTripScheduleDTO getBusTripScheduleByIdForUser(int busTripScheduleId, LocalDate departureDate, String arrivalProvince) throws IdInvalidException, ApplicationException;
    List<BreakDay> getBreakDaysForBusTripSchedule(int busTripScheduleId) throws IdInvalidException, ApplicationException;
    void cancelBusTripSchedule(int busTripScheduleId, LocalDate cancelDate) throws IdInvalidException, ApplicationException;
    boolean checkBusTripScheduleHasOrder(int busTripScheduleId) throws IdInvalidException, ApplicationException;

    public ResultPaginationDTO getAllBusTripSchedules(Specification<BusTripSchedule> spec, Pageable pageable) throws ApplicationException;

    void addBreakDay(ReqBreakDayDTO reqBreakDayDTO) throws IdInvalidException, ApplicationException;
    void deleteBreakDay(int breakDayId) throws IdInvalidException;

}
