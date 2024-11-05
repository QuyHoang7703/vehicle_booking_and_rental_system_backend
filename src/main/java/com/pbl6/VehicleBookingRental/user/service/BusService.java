package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.Bus;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.bus.ReqBus;
import com.pbl6.VehicleBookingRental.user.dto.response.bus.ResBus;
import com.pbl6.VehicleBookingRental.user.dto.response.bus.ResBusDetail;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BusService {
    Bus createBus(ReqBus reqBus, List<MultipartFile> busImages) throws IdInvalidException, ApplicationException;
    Bus updateBus(ReqBus reqBus, List<MultipartFile> busImages) throws IdInvalidException, ApplicationException;
    ResBusDetail convertToResBusDetail(Bus bus);
    void deleteBus(int busId) throws IdInvalidException, ApplicationException;
    Bus findBusById(int busId) throws IdInvalidException, ApplicationException;
    ResultPaginationDTO getAllBuses(Specification<Bus> spec, Pageable pageable) throws IdInvalidException, ApplicationException;
    ResBus convertToResBus(Bus bus) throws IdInvalidException;

}
