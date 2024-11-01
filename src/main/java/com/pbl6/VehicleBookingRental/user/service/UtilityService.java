package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.Utility;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface UtilityService {
    Utility createUtility(Utility reqUtility) throws ApplicationException;
    Utility updateUtility(Utility reqUtility);
    void deleteUtility(int idUtility);
    Utility getUtility(int idUtility);
    ResultPaginationDTO getAllUtility(Specification<Utility> specification, Pageable pageable);

}
