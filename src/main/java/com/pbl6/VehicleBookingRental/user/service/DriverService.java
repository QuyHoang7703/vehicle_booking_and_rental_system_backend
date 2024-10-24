package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.domain.bookingcar.Driver;
import com.pbl6.VehicleBookingRental.user.dto.request.businessPartner.ReqDriveDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.driver.ResDriverDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.driver.ResGeneralDriverInfoDTO;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DriverService {
    ResGeneralDriverInfoDTO registerDriver(ReqDriveDTO reqDriveDTO,
                                           MultipartFile avatarOfDriver,
                                           List<MultipartFile> citizenImages,
                                           List<MultipartFile> vehicleImages,
                                           List<MultipartFile> driverLicenseImages,
                                           List<MultipartFile> vehicleInsuranceImages) throws ApplicationException;
    ResGeneralDriverInfoDTO convertToResGeneralDriverInfoDTO(Account account, Driver driver);
    ResDriverDTO convertoResDriverDTO(ResGeneralDriverInfoDTO resGeneralDriverInfoDTO, Driver driver);
    void verifyDriver(int id) throws IdInvalidException;
    void cancelDriver(int id) throws IdInvalidException;
    Driver getDriverById(int id) throws IdInvalidException;

}
