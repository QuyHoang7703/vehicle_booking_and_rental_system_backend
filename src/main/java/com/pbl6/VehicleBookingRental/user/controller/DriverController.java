package com.pbl6.VehicleBookingRental.user.controller;

import com.pbl6.VehicleBookingRental.user.domain.bookingcar.Driver;
import com.pbl6.VehicleBookingRental.user.dto.ResponseInfo;
import com.pbl6.VehicleBookingRental.user.dto.request.businessPartner.ReqDriveDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.driver.ResDriverDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.driver.ResGeneralDriverInfoDTO;
import com.pbl6.VehicleBookingRental.user.service.DriverService;
import com.pbl6.VehicleBookingRental.user.util.constant.ApprovalStatusEnum;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class DriverController {
    private final DriverService driverService;

    @PostMapping("/drivers")
    public ResponseEntity<ResGeneralDriverInfoDTO> registerDriver(@RequestPart("driverInfo") ReqDriveDTO reqDriveDTO,
                                                                  @RequestParam("avatarOfDriver") MultipartFile avatarOfDriver,
                                                                  @RequestParam("citizenImages") List<MultipartFile> citizenImages,
                                                                  @RequestParam("driverLicenseImages") List<MultipartFile> driverLicenseImages,
                                                                  @RequestParam("vehicleRegistration") List<MultipartFile> vehicleRegistration,
                                                                  @RequestParam("vehicleImages") List<MultipartFile> vehicleImages,
                                                                  @RequestParam("vehicleInsurance") List<MultipartFile> vehicleInsurance) throws ApplicationException {
        ResGeneralDriverInfoDTO driverInfo = driverService.registerDriver(reqDriveDTO
                , avatarOfDriver, citizenImages
                , driverLicenseImages, vehicleRegistration
                , vehicleImages, vehicleInsurance);
        return ResponseEntity.status(HttpStatus.OK).body(driverInfo);
    }

    @PutMapping("/drivers/verify")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseInfo<String>> verifyDriver(@RequestParam("formRegisterId") int id) throws IdInvalidException {
        Driver driver = this.driverService.getDriverById(id);
        if(driver.getApprovalStatus() == ApprovalStatusEnum.APPROVED){
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseInfo<>("Bạn đã duyệt đơn đăng ký này rồi"));
        }
        this.driverService.verifyDriver(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseInfo<>("Đã đăng ký thành công đối tác tài xế" ));
    }

    @DeleteMapping("/drivers/cancel-partnership")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseInfo<String>> cancelDriver(@RequestParam("formRegisterId") int id) throws IdInvalidException {
        Driver driver = this.driverService.getDriverById(id);
        if(driver.getApprovalStatus() == ApprovalStatusEnum.PENDING_APPROVAL){
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseInfo<>("Bạn đã hủy đơn đăng ký này rồi"));
        }
        this.driverService.cancelDriver(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseInfo<>("Đã hủy đối tác tài xế" ));
    }

    @GetMapping("/drivers/detail")
    public ResponseEntity<ResDriverDTO> getDriverRegisterForm(@RequestParam("formRegisterId") int formRegisterId) throws IdInvalidException {
        Driver driver = this.driverService.getDriverById(formRegisterId);
        ResGeneralDriverInfoDTO resGeneralDriverInfoDTO = this.driverService.convertToResGeneralDriverInfoDTO(driver.getAccount(), driver);
        ResDriverDTO resDriverDTO = this.driverService.convertoResDriverDTO(resGeneralDriverInfoDTO, driver);
        return ResponseEntity.status(HttpStatus.OK).body(resDriverDTO);
    }
}
