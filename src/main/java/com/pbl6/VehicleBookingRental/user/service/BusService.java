package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.Bus;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusTripSchedule;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.bus.ReqBusDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.bus.ResBusDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.bus.ResBusDetailDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.bus.ResScheduleOfBusDTO;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface BusService {
    Bus createBus(ReqBusDTO reqBus, List<MultipartFile> busImages) throws IdInvalidException, ApplicationException, IOException;
    Bus updateBus(ReqBusDTO reqBus, List<MultipartFile> busImages) throws IdInvalidException, ApplicationException, IOException;
    ResBusDetailDTO convertToResBusDetail(Bus bus);
    void deleteBus(int busId) throws IdInvalidException, ApplicationException;
    Bus findBusById(int busId) throws IdInvalidException, ApplicationException;
    ResultPaginationDTO getAllBuses(Specification<Bus> spec, Pageable pageable) throws IdInvalidException, ApplicationException;
    ResBusDTO convertToResBus(Bus bus) throws IdInvalidException;
    Map<Integer, String> getBusesByBusTypeId(String busTypeName) throws IdInvalidException, ApplicationException;
    List<String> getImages(int busId) throws IdInvalidException;
    ResultPaginationDTO getScheduleOfBuses(int busId, Pageable pageable) throws ApplicationException;
    ResScheduleOfBusDTO convertToResScheduleOfBus(BusTripSchedule busTripSchedule) throws ApplicationException;
}
