package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusType;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.busType.ResBusType;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface BusTypeService {
    ResBusType convertToResBusType(BusType busType);
    ResBusType createBusType(BusType busType) throws Exception;
    ResBusType updateBusType(BusType busType) throws IdInvalidException, ApplicationException;
    ResBusType findById(int id) throws IdInvalidException, ApplicationException;
    void deleteById(int id) throws IdInvalidException, ApplicationException;
    ResultPaginationDTO getAllBusTypes(Specification<BusType> specification, Pageable pageable) throws ApplicationException;
}
