package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.Utility;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

public interface UtilityService {
    Utility createUtility(Utility reqUtility, MultipartFile utilityImage) throws ApplicationException;
    Utility updateUtility(Utility reqUtility, MultipartFile utilityImage) throws ApplicationException;
    void deleteUtility(int idUtility) throws ApplicationException;
    Utility getUtility(int idUtility) throws ApplicationException;
    ResultPaginationDTO getAllUtility(Specification<Utility> specification, Pageable pageable);

}
