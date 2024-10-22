package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.domain.bookingcar.Driver;
import com.pbl6.VehicleBookingRental.user.dto.request.businessPartner.ReqDriveDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.businessPartner.ResDriverDTO;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DriverService {
    ResDriverDTO registerDriver(ReqDriveDTO reqDriveDTO, List<MultipartFile> citizenImages, List<MultipartFile> driverImages);
    ResDriverDTO convertoResDriverDTO(Driver driver);
    void verifyDriver(int id) throws IdInvalidException;
    void cancelDriver(int id) throws IdInvalidException;

}
