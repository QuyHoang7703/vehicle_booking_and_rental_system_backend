package com.pbl6.VehicleBookingRental.user.controller;

import com.pbl6.VehicleBookingRental.user.domain.bookingcar.Driver;
import com.pbl6.VehicleBookingRental.user.dto.ResponseInfo;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.businessPartner.ReqPartnerAction;
import com.pbl6.VehicleBookingRental.user.dto.request.businessPartner.ReqDriveDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.businessPartner.ResCancelDriver;
import com.pbl6.VehicleBookingRental.user.dto.response.driver.ResDriverDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.driver.ResGeneralDriverInfoDTO;
import com.pbl6.VehicleBookingRental.user.service.DriverService;
import com.pbl6.VehicleBookingRental.user.util.constant.ApprovalStatusEnum;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
                                                                  @RequestParam("vehicleInsurance") List<MultipartFile> vehicleInsurance) throws Exception {
        ResGeneralDriverInfoDTO driverInfo = driverService.registerDriver(reqDriveDTO
                , avatarOfDriver, citizenImages
                , driverLicenseImages, vehicleRegistration
                , vehicleImages, vehicleInsurance);
        return ResponseEntity.status(HttpStatus.OK).body(driverInfo);
    }

    @PutMapping("/drivers/verify")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseInfo<String>> verifyDriver(@RequestParam("formRegisterId") int id) throws Exception {
        Driver driver = this.driverService.getDriverById(id);
        if(driver.getApprovalStatus() == ApprovalStatusEnum.APPROVED){
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseInfo<>("Bạn đã duyệt đơn đăng ký này rồi"));
        }
        this.driverService.verifyDriver(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseInfo<>("Đã đăng ký thành công đối tác tài xế" ));
    }

    @PutMapping("/drivers/cancel-partnership")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseInfo<String>> cancelDriver(@RequestBody ReqPartnerAction reqPartnerAction) throws Exception {
        Driver driver = this.driverService.getDriverById(reqPartnerAction.getFormRegisterId());
        if(driver.getApprovalStatus() == ApprovalStatusEnum.CANCEL){
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseInfo<>("Bạn đã hủy đơn đăng ký này rồi"));
        }
        this.driverService.cancelDriver(reqPartnerAction);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseInfo<>("Đã hủy đối tác tài xế" ));
    }

    @GetMapping("/drivers/detail")
    public ResponseEntity<ResDriverDTO> getDriverRegisterForm(@RequestParam("formRegisterId") int formRegisterId) throws Exception {
        Driver driver = this.driverService.getDriverById(formRegisterId);
        ResGeneralDriverInfoDTO resGeneralDriverInfoDTO = this.driverService.convertToResGeneralDriverInfoDTO(driver.getAccount(), driver);
        ResDriverDTO resDriverDTO = this.driverService.convertoResDriverDTO(resGeneralDriverInfoDTO, driver);
        return ResponseEntity.status(HttpStatus.OK).body(resDriverDTO);
    }

    @GetMapping("/drivers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResultPaginationDTO> fetchAllDrivers(@Filter Specification<Driver> specification, Pageable pageable) {
        ResultPaginationDTO resultPaginationDTO = this.driverService.getAllDrivers(specification, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(resultPaginationDTO);
    }

    @GetMapping("drivers/reason-cancel-driver")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResCancelDriver> getCancelReasonDriver(@RequestParam("idDriver") int idDriver) throws ApplicationException, IdInvalidException {
//        return ResponseEntity.status(HttpStatus.OK).body(this.accountService.getInfoDeactivatedAccount(email));
        return ResponseEntity.status(HttpStatus.OK).body(this.driverService.getInfoCancelDriver(idDriver));
    }

    @DeleteMapping("/drivers/refuse-register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseInfo<String>> refuseRegisterDriver(@RequestBody ReqPartnerAction reqPartnerAction) throws Exception {
        this.driverService.refuseOrDeleteRegisterDriver(reqPartnerAction);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseInfo<>("Refused this register"));
    }



}
