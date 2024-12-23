package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.domain.bookingcar.Driver;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.businessPartner.ReqPartnerAction;
import com.pbl6.VehicleBookingRental.user.dto.request.businessPartner.ReqDriveDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.businessPartner.ResCancelDriver;
import com.pbl6.VehicleBookingRental.user.dto.response.driver.ResDriverDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.driver.ResGeneralDriverInfoDTO;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DriverService {
    ResGeneralDriverInfoDTO registerDriver(ReqDriveDTO reqDriveDTO,
                                           MultipartFile avatarOfDriver,
                                           List<MultipartFile> citizenImages,
                                           List<MultipartFile> driverLicenseImages,
                                           List<MultipartFile> vehicleRegistrations,
                                           List<MultipartFile> vehicleImages,
                                           List<MultipartFile> vehicleInsuranceImages) throws Exception;
    ResGeneralDriverInfoDTO convertToResGeneralDriverInfoDTO(Account account, Driver driver) throws ApplicationException;
    ResDriverDTO convertoResDriverDTO(ResGeneralDriverInfoDTO resGeneralDriverInfoDTO, Driver driver) throws Exception;
    void verifyDriver(int id) throws IdInvalidException, ApplicationException, IOException;
    void cancelDriver(ReqPartnerAction reqPartnerAction) throws Exception;
    Driver getDriverById(int id) throws IdInvalidException;
    ResultPaginationDTO getAllDrivers(Specification<Driver> specification, Pageable pageable);
    boolean isRegisteredDriver(int accountId);
    ResCancelDriver getInfoCancelDriver(int idDriver) throws IdInvalidException, ApplicationException;
    @Transactional
    void refuseOrDeleteRegisterDriver(ReqPartnerAction reqPartnerAction) throws IdInvalidException, IOException, ApplicationException;
    String getStatusRegister();

}
