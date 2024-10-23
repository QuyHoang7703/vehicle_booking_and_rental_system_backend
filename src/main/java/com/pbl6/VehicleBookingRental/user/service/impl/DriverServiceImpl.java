package com.pbl6.VehicleBookingRental.user.service.impl;

import com.pbl6.VehicleBookingRental.user.domain.VehicleType;
import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.domain.account.AccountRole;
import com.pbl6.VehicleBookingRental.user.domain.account.Role;
import com.pbl6.VehicleBookingRental.user.domain.bookingcar.Driver;
import com.pbl6.VehicleBookingRental.user.dto.request.businessPartner.ReqDriveDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.driver.ResDriverDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.driver.ResGeneralDriverInfoDTO;
import com.pbl6.VehicleBookingRental.user.repository.account.AccountRoleRepository;
import com.pbl6.VehicleBookingRental.user.repository.account.RoleRepository;
import com.pbl6.VehicleBookingRental.user.repository.businessPartner.DriverRepository;
import com.pbl6.VehicleBookingRental.user.repository.image.ImageRepository;
import com.pbl6.VehicleBookingRental.user.repository.vehicle_rental.VehicleTypeRepo;
import com.pbl6.VehicleBookingRental.user.service.AccountService;
import com.pbl6.VehicleBookingRental.user.service.DriverService;
import com.pbl6.VehicleBookingRental.user.service.ImageService;
import com.pbl6.VehicleBookingRental.user.util.SecurityUtil;
import com.pbl6.VehicleBookingRental.user.util.constant.ApprovalStatusEnum;
import com.pbl6.VehicleBookingRental.user.util.constant.ImageOfObjectEnum;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DriverServiceImpl implements DriverService {
    private final AccountService accountService;
    private final ImageService imageService;
    private final DriverRepository driverRepository;
    private final ImageRepository imageRepository;
    private final RoleRepository roleRepository;
    private final AccountRoleRepository accountRoleRepository;
    private final VehicleTypeRepo vehicleTypeRepo;
    @Override
    public ResGeneralDriverInfoDTO registerDriver(ReqDriveDTO reqDriveDTO,
                                                  MultipartFile avatarOfDriver,
                                                  List<MultipartFile> citizenImages,
                                                  List<MultipartFile> vehicleImages,
                                                  List<MultipartFile> driverLicenseImages,
                                                  List<MultipartFile> vehicleInsuranceImages) throws ApplicationException{
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = SecurityUtil.getCurrentLogin().isPresent()?
                SecurityUtil.getCurrentLogin().get() : "";
        Account account = this.accountService.handleGetAccountByUsername(username);
        if(account == null) {
            throw new UsernameNotFoundException("Username not found");
        }
//        ResAccountInfoDTO resAccountInfoDTO = this.accountService.convertToResAccountInfoDTO(account);
        Driver driver = new Driver();
        driver.setCitizenID(reqDriveDTO.getCitizenID());
        driver.setDateOfIssue(reqDriveDTO.getDateOfIssue());
        driver.setPlaceOfIssue(reqDriveDTO.getPlaceOfIssue());
        driver.setExpiryDate(reqDriveDTO.getExpiryDate());
        driver.setLicensePlateNumber(reqDriveDTO.getLicensePlateNumber());
        driver.setDriverLicenseNumber(reqDriveDTO.getDriverLicenseNumber());
        driver.setLicenseType(reqDriveDTO.getLicenseType());
        driver.setIssueDateLicense(reqDriveDTO.getIssueDateLicense());
        driver.setPermanentAddress(reqDriveDTO.getPermanentAddress());
        driver.setPhoneNumberOfRelative(reqDriveDTO.getPhoneNumberOfRelative());
        driver.setLocation(reqDriveDTO.getLocation());
        VehicleType vehicleType = this.vehicleTypeRepo.findVehicleTypeByName(reqDriveDTO.getVehicleType())
                .orElseThrow(() -> new ApplicationException("Vehicle type not found"));
        driver.setVehicleType(vehicleType);
        driver.setAccount(account);

        Driver savedDriver = this.driverRepository.save(driver);


        List<String> urlAvatarOfDriver = this.imageService.uploadAndSaveImages(Collections.singletonList(avatarOfDriver)
                , String.valueOf(ImageOfObjectEnum.AVATAR_OF_DRIVER)
                , savedDriver.getId());
        List<String> urlCitizenImages = this.imageService.uploadAndSaveImages(citizenImages
                , String.valueOf(ImageOfObjectEnum.CITIZEN_IDENTIFICATION)
                , savedDriver.getId());
        List<String> urlVehicleImages = this.imageService.uploadAndSaveImages(vehicleImages
                , String.valueOf(ImageOfObjectEnum.VEHICLE_IMAGES)
                , savedDriver.getId());
        List<String> urlDriverLicense = this.imageService.uploadAndSaveImages(driverLicenseImages
                , String.valueOf(ImageOfObjectEnum.DRIVER_LICENSE)
                , savedDriver.getId());
        List<String> urlVehicleInsurance = this.imageService.uploadAndSaveImages(vehicleInsuranceImages
                , String.valueOf(ImageOfObjectEnum.VEHICLE_INSURANCE)
                , savedDriver.getId());


        return this.convertToResGeneralDriverInfoDTO(account, savedDriver);
    }

    @Override
    public ResDriverDTO convertoResDriverDTO(ResGeneralDriverInfoDTO resGeneralDriverInfoDTO, Driver driver) {
        ResDriverDTO resDriverDTO = new ResDriverDTO();
        resDriverDTO.setGeneralDriverInfo(resGeneralDriverInfoDTO.getGeneralDriverInfo());

        ResDriverDTO.DetailDriverInfo detailDriverInfo = new ResDriverDTO.DetailDriverInfo();
        detailDriverInfo.setId(driver.getId());
        detailDriverInfo.setCitizenID(driver.getCitizenID());
        detailDriverInfo.setDateOfIssue(driver.getDateOfIssue());
        detailDriverInfo.setPlaceOfIssue(driver.getPlaceOfIssue());
        detailDriverInfo.setExpiryDate(driver.getExpiryDate());
        detailDriverInfo.setLicensePlateNumber(driver.getLicensePlateNumber());
        detailDriverInfo.setDriverLicenseNumber(driver.getDriverLicenseNumber());
        detailDriverInfo.setLicenseType(driver.getLicenseType());
        detailDriverInfo.setIssueDateLicense(driver.getIssueDateLicense());
        detailDriverInfo.setPhoneNumberOfRelative(driver.getPhoneNumberOfRelative());
        detailDriverInfo.setVehicleType(driver.getVehicleType().getName());
        detailDriverInfo.setApprovalStatus(ApprovalStatusEnum.PENDING_APPROVAL);

        List<String> urlAvatarOfDriver = this.imageRepository.findByOwnerTypeAndOwnerId(String.valueOf(ImageOfObjectEnum.AVATAR_OF_DRIVER), driver.getId())
                .stream().map(image -> image.getPathImage())
                .collect(Collectors.toList());
        List<String> urlCitizenImages = this.imageRepository.findByOwnerTypeAndOwnerId(String.valueOf(ImageOfObjectEnum.CITIZEN_IDENTIFICATION), driver.getId())
                .stream().map(image -> image.getPathImage())
                .collect(Collectors.toList());
        List<String> urlVehicleImages = this.imageRepository.findByOwnerTypeAndOwnerId(String.valueOf(ImageOfObjectEnum.VEHICLE_IMAGES), driver.getId())
                .stream().map(image -> image.getPathImage())
                .collect(Collectors.toList());
        List<String> urlDriverLicenseImage = this.imageRepository.findByOwnerTypeAndOwnerId(String.valueOf(ImageOfObjectEnum.DRIVER_LICENSE), driver.getId())
                .stream().map(image -> image.getPathImage())
                .collect(Collectors.toList());
        List<String> urlVehicleInsurance = this.imageRepository.findByOwnerTypeAndOwnerId(String.valueOf(ImageOfObjectEnum.VEHICLE_INSURANCE), driver.getId())
                .stream().map(image -> image.getPathImage())
                .collect(Collectors.toList());

        detailDriverInfo.setAvatarOfDriver(urlAvatarOfDriver.get(0));
        detailDriverInfo.setCitizenImages(urlCitizenImages);
        detailDriverInfo.setVehicleImages(urlVehicleImages);
        detailDriverInfo.setDriverLicenseImage(urlDriverLicenseImage);
        detailDriverInfo.setVehicleInsurance(urlVehicleInsurance);

        resDriverDTO.setDetailDriverInfo(detailDriverInfo);

        return resDriverDTO;
    }

    @Override
    public void verifyDriver(int id) throws IdInvalidException {
        Driver driver = this.driverRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Id is invalid"));
        driver.setApprovalStatus(ApprovalStatusEnum.APPROVED);
        this.driverRepository.save(driver);
        Account account = driver.getAccount();
        Role role = this.roleRepository.findByName("DRIVER")
                .orElseThrow(() -> new RuntimeException("Role is invalid"));
        AccountRole accountRole = new AccountRole();
        accountRole.setRole(role);
        accountRole.setAccount(account);
        this.accountRoleRepository.save(accountRole);

    }

    @Override
    @Transactional
    public void cancelDriver(int id) throws IdInvalidException {
        Driver driver = this.driverRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Id is invalid"));
        driver.setApprovalStatus(ApprovalStatusEnum.PENDING_APPROVAL);
        this.driverRepository.save(driver);
        Role role = this.roleRepository.findByName("DRIVER")
                .orElseThrow(() -> new RuntimeException("Role is invalid"));
        this.accountRoleRepository.deleteAccountRolesByAccountAndRole(driver.getAccount(), role);
        log.info("Deleted account " + driver.getAccount().getEmail() + "with role " + role.getName());
//        this.driverRepository.deleteById(id);


    }

    @Override
    public Driver getDriverById(int id) throws IdInvalidException {
        return this.driverRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Driver ID is invalid"));
    }

    @Override
    public ResGeneralDriverInfoDTO convertToResGeneralDriverInfoDTO(Account account, Driver driver) {
        ResGeneralDriverInfoDTO res = new ResGeneralDriverInfoDTO();
        ResGeneralDriverInfoDTO.GeneralDriverInfo generalDriverInfo = new ResGeneralDriverInfoDTO.GeneralDriverInfo();
        generalDriverInfo.setId(account.getId());
        generalDriverInfo.setEmail(account.getEmail());
        generalDriverInfo.setName(account.getName());
        generalDriverInfo.setPhoneNumber(account.getPhoneNumber());
        generalDriverInfo.setPermanentAddress(driver.getPermanentAddress());
        generalDriverInfo.setLocation(driver.getLocation());
        generalDriverInfo.setFormRegisterId(driver.getId());
        res.setGeneralDriverInfo(generalDriverInfo);
        return res;
    }


}
