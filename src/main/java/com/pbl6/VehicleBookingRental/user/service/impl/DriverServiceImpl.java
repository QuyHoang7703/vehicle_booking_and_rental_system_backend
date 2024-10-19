package com.pbl6.VehicleBookingRental.user.service.impl;

import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.domain.account.AccountRole;
import com.pbl6.VehicleBookingRental.user.domain.account.Role;
import com.pbl6.VehicleBookingRental.user.domain.bookingcar.Driver;
import com.pbl6.VehicleBookingRental.user.dto.request.businessPartner.ReqDriveDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.businessPartner.ResDriverDTO;
import com.pbl6.VehicleBookingRental.user.repository.account.AccountRoleRepository;
import com.pbl6.VehicleBookingRental.user.repository.account.RoleRepository;
import com.pbl6.VehicleBookingRental.user.repository.businessPartner.DriverRepository;
import com.pbl6.VehicleBookingRental.user.repository.image.ImageRepository;
import com.pbl6.VehicleBookingRental.user.service.AccountService;
import com.pbl6.VehicleBookingRental.user.service.DriverService;
import com.pbl6.VehicleBookingRental.user.service.ImageService;
import com.pbl6.VehicleBookingRental.user.util.constant.ApprovalStatusEnum;
import com.pbl6.VehicleBookingRental.user.util.constant.ImageOfObjectEnum;
import com.pbl6.VehicleBookingRental.user.util.error.IdInValidException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    @Override
    public ResDriverDTO registerDriver(ReqDriveDTO reqDriveDTO, List<MultipartFile> citizenImages, List<MultipartFile> driverImages) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Account account = this.accountService.handleGetAccountByUsername(username);
        if(account == null) {
            throw new UsernameNotFoundException("Username not found");
        }
        Driver driver = new Driver();
        driver.setLocation(reqDriveDTO.getLocation());
        driver.setLicensePlate(reqDriveDTO.getLicensePlate());
        driver.setVehicleInsurance(reqDriveDTO.getVehicleInsurance());
        driver.setAccount(account);

        Driver savedDriver = this.driverRepository.save(driver);


        List<String> urlCitizenImages = this.imageService.uploadAndSaveImages(citizenImages
                , String.valueOf(ImageOfObjectEnum.CITIZEN_IDENTIFICATION)
                , savedDriver.getId());
        List<String> urlDriverImages = this.imageService.uploadAndSaveImages(citizenImages
                , String.valueOf(ImageOfObjectEnum.DRIVER)
                , savedDriver.getId());

        ResDriverDTO resDriverDTO = this.convertoResDriverDTO(savedDriver);


        return resDriverDTO;
    }

    @Override
    public ResDriverDTO convertoResDriverDTO(Driver driver) {
        ResDriverDTO resDriverDTO = new ResDriverDTO();
        ResDriverDTO.DriverInfo driverInfo = new ResDriverDTO.DriverInfo();
        driverInfo.setId(driver.getId());
        driverInfo.setLocation(driver.getLocation());
        driverInfo.setLicensePlate(driver.getLicensePlate());
        driverInfo.setVehicleInsurance(driver.getVehicleInsurance());
        List<String> urlCitizenImages = this.imageRepository.findByOwnerTypeAndOwnerId(String.valueOf(ImageOfObjectEnum.CITIZEN_IDENTIFICATION), driverInfo.getId())
                        .stream().map(image -> image.getPathImage())
                        .collect(Collectors.toList());
        List<String> urlDriverImages = this.imageRepository.findByOwnerTypeAndOwnerId(String.valueOf(ImageOfObjectEnum.DRIVER), driverInfo.getId())
                .stream().map(image -> image.getPathImage())
                .collect(Collectors.toList());
        driverInfo.setCitizenImages(urlCitizenImages);
        driverInfo.setDriverImages(urlDriverImages);
        resDriverDTO.setDriverInfo(driverInfo);
        return resDriverDTO;
    }

    @Override
    public void verifyDriver(int id) throws IdInValidException {
        Driver driver = this.driverRepository.findById(id)
                .orElseThrow(() -> new IdInValidException("Id is invalid"));
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
    public void cancelDriver(int id) throws IdInValidException {
        Driver driver = this.driverRepository.findById(id)
                .orElseThrow(() -> new IdInValidException("Id is invalid"));
        driver.setApprovalStatus(ApprovalStatusEnum.PENDING_APPROVAL);
        this.driverRepository.save(driver);
        Role role = this.roleRepository.findByName("DRIVER")
                .orElseThrow(() -> new RuntimeException("Role is invalid"));
        this.accountRoleRepository.deleteAccountRolesByAccountAndRole(driver.getAccount(), role);
        log.info("Deleted account " + driver.getAccount().getEmail() + "with role " + role.getName());
//        this.driverRepository.deleteById(id);


    }


}
