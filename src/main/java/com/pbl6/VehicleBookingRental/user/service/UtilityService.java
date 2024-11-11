package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.Utility;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

public interface UtilityService {
    Utility findById(int id) throws IdInvalidException;
    Utility createUtility(Utility reqUtility, MultipartFile utilityImage) throws ApplicationException;
    Utility updateUtility(Utility reqUtility, MultipartFile utilityImage) throws IdInvalidException;
    void deleteUtility(int idUtility) throws IdInvalidException;
    Utility getUtilityById(int idUtility) throws IdInvalidException;
    ResultPaginationDTO getAllUtility(Specification<Utility> specification, Pageable pageable);

}
