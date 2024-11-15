package com.pbl6.VehicleBookingRental.user.service.impl;

import com.pbl6.VehicleBookingRental.user.domain.BankAccount;
import com.pbl6.VehicleBookingRental.user.domain.Images;
import com.pbl6.VehicleBookingRental.user.domain.VehicleType;
import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.domain.account.AccountRole;
import com.pbl6.VehicleBookingRental.user.domain.account.Role;
import com.pbl6.VehicleBookingRental.user.domain.bookingcar.Driver;
import com.pbl6.VehicleBookingRental.user.dto.Meta;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.businessPartner.ReqPartnerAction;
import com.pbl6.VehicleBookingRental.user.dto.request.businessPartner.ReqDriveDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.bankAccount.ResBankAccountDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.businessPartner.ResCancelDriver;
import com.pbl6.VehicleBookingRental.user.dto.response.driver.ResDriverDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.driver.ResGeneralDriverInfoDTO;
import com.pbl6.VehicleBookingRental.user.repository.account.AccountRoleRepository;
import com.pbl6.VehicleBookingRental.user.repository.account.RoleRepository;
import com.pbl6.VehicleBookingRental.user.repository.businessPartner.DriverRepository;
import com.pbl6.VehicleBookingRental.user.repository.image.ImageRepository;
import com.pbl6.VehicleBookingRental.user.repository.vehicle_rental.VehicleTypeRepository;
import com.pbl6.VehicleBookingRental.user.service.*;
import com.pbl6.VehicleBookingRental.user.util.SecurityUtil;
import com.pbl6.VehicleBookingRental.user.util.constant.ApprovalStatusEnum;
import com.pbl6.VehicleBookingRental.user.util.constant.ImageOfObjectEnum;
import com.pbl6.VehicleBookingRental.user.util.constant.PartnerTypeEnum;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;

import java.io.IOException;
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
    private final VehicleTypeRepository vehicleTypeRepo;
    private final BankAccountService bankAccountService;
    private final AccountRoleService accountRoleService;
    private final EmailService emailService;


    @Override
    public ResGeneralDriverInfoDTO registerDriver(ReqDriveDTO reqDriveDTO,
                                                  MultipartFile avatarOfDriver,
                                                  List<MultipartFile> citizenImages,
                                                  List<MultipartFile> driverLicenseImages,
                                                  List<MultipartFile> vehicleRegistrations,
                                                  List<MultipartFile> vehicleImages,
                                                  List<MultipartFile> vehicleInsuranceImages) throws Exception {
        String username = SecurityUtil.getCurrentLogin().isPresent()?
                SecurityUtil.getCurrentLogin().get() : "";
        Account account = this.accountService.handleGetAccountByUsername(username);
        if(account == null) {
            throw new UsernameNotFoundException("Username not found");
        }
        if(this.isRegisteredDriver(account.getId())){
            throw new ApplicationException("Driver already registered");
        }
        this.accountService.handleUpdateAccount(null, reqDriveDTO.getAccountInfo());
//        if(account.getName() == null || account.getPhoneNumber() == null
//                || account.getBirthDay() == null || account.getGender() == null) {
//
//        }
        Driver driver = new Driver();

        driver.setCitizenID(reqDriveDTO.getCitizen().getCitizenId());
        driver.setDateOfIssue(reqDriveDTO.getCitizen().getDateOfIssue());
        driver.setPlaceOfIssue(reqDriveDTO.getCitizen().getPlaceOfIssue());
        driver.setExpiryDate(reqDriveDTO.getCitizen().getExpiryDate());
        driver.setPermanentAddress(reqDriveDTO.getCitizen().getPermanentAddress());
        driver.setLocation(reqDriveDTO.getCitizen().getLocation());

        driver.setDriverLicenseNumber(reqDriveDTO.getDriverLicense().getDriverLicenseNumber());
        driver.setLicenseType(reqDriveDTO.getDriverLicense().getLicenseType());
        driver.setIssueDateLicense(reqDriveDTO.getDriverLicense().getIssueDateLicense());

        driver.setNameOfRelative(reqDriveDTO.getRelative().getNameOfRelative());
        driver.setPhoneNumberOfRelative(reqDriveDTO.getRelative().getPhoneOfRelative());
        driver.setRelationship(reqDriveDTO.getRelative().getRelationship());

        driver.setLicensePlateNumber(reqDriveDTO.getVehicle().getLicensePlateNumber());
        VehicleType vehicleType = this.vehicleTypeRepo.findVehicleTypeByName(reqDriveDTO.getVehicle().getVehicleType())
                .orElseThrow(() -> new ApplicationException("Vehicle type not found"));
        driver.setVehicleType(vehicleType);
        driver.setAccount(account);

        Driver savedDriver = this.driverRepository.save(driver);

        this.bankAccountService.createBankAccount(reqDriveDTO.getBankAccount(), account);

        List<String> urlAvatarOfDriver = this.imageService.uploadAndSaveImages(Collections.singletonList(avatarOfDriver)
                , String.valueOf(ImageOfObjectEnum.AVATAR_OF_DRIVER)
                , savedDriver.getId()
                , String.valueOf(PartnerTypeEnum.DRIVER));
        List<String> urlCitizenImages = this.imageService.uploadAndSaveImages(citizenImages
                , String.valueOf(ImageOfObjectEnum.CITIZEN_IDENTIFICATION)
                , savedDriver.getId()
                , String.valueOf(PartnerTypeEnum.DRIVER));
        List<String> urlDriverLicense = this.imageService.uploadAndSaveImages(driverLicenseImages
                , String.valueOf(ImageOfObjectEnum.DRIVER_LICENSE)
                , savedDriver.getId()
                , String.valueOf(PartnerTypeEnum.DRIVER));
        List<String> urlVehicleRegistration = this.imageService.uploadAndSaveImages(vehicleRegistrations
                , String.valueOf(ImageOfObjectEnum.VEHICLE_REGISTRATION)
                , savedDriver.getId()
                , String.valueOf(PartnerTypeEnum.DRIVER));
        List<String> urlVehicleImages = this.imageService.uploadAndSaveImages(vehicleImages
                , String.valueOf(ImageOfObjectEnum.VEHICLE_IMAGES)
                , savedDriver.getId()
                , String.valueOf(PartnerTypeEnum.DRIVER));
        List<String> urlVehicleInsurance = this.imageService.uploadAndSaveImages(vehicleInsuranceImages
                , String.valueOf(ImageOfObjectEnum.VEHICLE_INSURANCE)
                , savedDriver.getId()
                , String.valueOf(PartnerTypeEnum.DRIVER));

        return this.convertToResGeneralDriverInfoDTO(account, savedDriver);
    }

    @Override
    public ResDriverDTO convertoResDriverDTO(ResGeneralDriverInfoDTO resGeneralDriverInfoDTO, Driver driver) throws Exception {
        ResDriverDTO resDriverDTO = new ResDriverDTO();
        resDriverDTO.setGeneralDriverInfo(resGeneralDriverInfoDTO.getGeneralDriverInfo());

        ResDriverDTO.CitizenDTO citizenDTO = new ResDriverDTO.CitizenDTO();
        citizenDTO.setCitizenId(driver.getCitizenID());
        citizenDTO.setDateOfIssue(driver.getDateOfIssue());
        citizenDTO.setPlaceOfIssue(driver.getPlaceOfIssue());
        citizenDTO.setExpiryDate(driver.getExpiryDate());
        List<String> urlCitizenImages = this.imageRepository.findByOwnerTypeAndOwnerId(String.valueOf(ImageOfObjectEnum.CITIZEN_IDENTIFICATION), driver.getId())
                .stream().map(image -> image.getPathImage())
                .collect(Collectors.toList());
        citizenDTO.setCitizenImages(urlCitizenImages);

        ResDriverDTO.DriverLicenseDTO driverLicenseDTO = new ResDriverDTO.DriverLicenseDTO();
        driverLicenseDTO.setDriverLicenseNumber(driver.getDriverLicenseNumber());
        driverLicenseDTO.setLicenseType(driver.getLicenseType());
        driverLicenseDTO.setIssueDateLicense(driver.getIssueDateLicense());
        List<String> urlDriverLicenseImage = this.imageRepository.findByOwnerTypeAndOwnerId(String.valueOf(ImageOfObjectEnum.DRIVER_LICENSE), driver.getId())
                .stream().map(image -> image.getPathImage())
                .collect(Collectors.toList());
        driverLicenseDTO.setDriverLicenseImage(urlDriverLicenseImage);

        ResDriverDTO.VehicleDTO vehicleDTO = new ResDriverDTO.VehicleDTO();
        vehicleDTO.setLicensePlateNumber(driver.getLicensePlateNumber());
        vehicleDTO.setVehicleType(driver.getVehicleType().getName());
        List<String> urlVehicleImages = this.imageRepository.findByOwnerTypeAndOwnerId(String.valueOf(ImageOfObjectEnum.VEHICLE_IMAGES), driver.getId())
                .stream().map(image -> image.getPathImage())
                .collect(Collectors.toList());

        List<String> urlVehicleInsurance = this.imageRepository.findByOwnerTypeAndOwnerId(String.valueOf(ImageOfObjectEnum.VEHICLE_INSURANCE), driver.getId())
                .stream().map(image -> image.getPathImage())
                .collect(Collectors.toList());
        vehicleDTO.setVehicleImages(urlVehicleImages);
        vehicleDTO.setVehicleInsurance(urlVehicleInsurance);

        ResDriverDTO.RelativeDTO relativeDTO = new ResDriverDTO.RelativeDTO();
        relativeDTO.setNameOfRelative(driver.getNameOfRelative());
        relativeDTO.setPhoneOfRelative(driver.getPhoneNumberOfRelative());
        relativeDTO.setRelationship(driver.getRelationship());

        ResBankAccountDTO resBankAccount = this.bankAccountService.convertoResBankAccountDTO(driver.getAccount().getId(), PartnerTypeEnum.DRIVER);

        resDriverDTO.setCitizen(citizenDTO);
        resDriverDTO.setDriverLicense(driverLicenseDTO);
        resDriverDTO.setVehicle(vehicleDTO);
        resDriverDTO.setRelative(relativeDTO);
        resDriverDTO.setBankAccount(resBankAccount);

        return resDriverDTO;
    }

    @Override
    public void verifyDriver(int id) throws IdInvalidException, ApplicationException, IOException {
        Driver driver = this.driverRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Id is invalid"));
        driver.setApprovalStatus(ApprovalStatusEnum.APPROVED);
        String email = driver.getAccount().getEmail();
        this.driverRepository.save(driver);
        AccountRole accountRoleDb = this.accountRoleService.getAccountRole(email, String.valueOf(PartnerTypeEnum.DRIVER));
        if (accountRoleDb != null) {
            accountRoleDb.setActive(true);
            accountRoleDb.setLockReason(null);
            this.accountRoleRepository.save(accountRoleDb);
        }else{
            Account account = driver.getAccount();
            Role role = this.roleRepository.findByName("DRIVER")
                    .orElseThrow(() -> new RuntimeException("Role is invalid"));
            AccountRole accountRole = new AccountRole();
            accountRole.setRole(role);
            accountRole.setAccount(account);
            accountRole.setActive(true);
//            accountRole.getTimeUpdate(null);
            this.accountRoleRepository.save(accountRole);

        }
        // Send accepted request register to user
        Context context = new Context();
        context.setVariable("cssContent", this.emailService.loadCssFromFile());
        context.setVariable("email", email);
        context.setVariable("partner", "tài xế");
        this.emailService.sendEmail(email, "Xác nhận trở thành đối tác", "verify_partner", context);
        log.info("Sent email verification to driver");

    }

    @Override
    @Transactional
    public void cancelDriver(ReqPartnerAction reqPartnerAction) throws Exception {
        int id = reqPartnerAction.getFormRegisterId();;
        Driver driver = this.driverRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Id is invalid"));
        driver.setApprovalStatus(ApprovalStatusEnum.CANCEL);
//        driver.getP
        this.driverRepository.save(driver);
        Role role = this.roleRepository.findByName("DRIVER")
                .orElseThrow(() -> new RuntimeException("Role is invalid"));
        AccountRole accountRole = this.accountRoleService.getAccountRole(driver.getAccount().getEmail(), String.valueOf(PartnerTypeEnum.DRIVER));
        accountRole.setActive(false);
        accountRole.setLockReason(reqPartnerAction.getReason());

        this.accountRoleRepository.save(accountRole);
        Context context = new Context();
        context.setVariable("cssContent", this.emailService.loadCssFromFile());
        context.setVariable("partner", "tài xế");
        context.setVariable("reasonCancel", reqPartnerAction.getReason());
        this.emailService.sendEmail(driver.getAccount().getEmail(), "Thông báo dừng việc hợp tác đối tác", "cancel_partner", context);
//        this.accountRoleRepository.deleteAccountRolesByAccountAndRole(driver.getAccount(), role);
//        log.info("Deleted account " + driver.getAccount().getEmail() + "with role " + role.getName());
//        this.driverRepository.deleteById(id);

    }

    @Override
    public Driver getDriverById(int id) throws IdInvalidException {
        return this.driverRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Driver ID is invalid"));
    }

    @Override
    public ResultPaginationDTO getAllDrivers(Specification<Driver> specification, Pageable pageable) {
        Page<Driver> driverPage = this.driverRepository.findAll(specification, pageable);
        ResultPaginationDTO res = new ResultPaginationDTO();
        Meta meta = new Meta();
        meta.setCurrentPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(driverPage.getTotalPages());
        meta.setTotal(driverPage.getTotalElements());
        res.setMeta(meta);
        List<ResGeneralDriverInfoDTO> generalDriverInfoDTOList = driverPage.getContent().stream()
                .map(driver -> {
                    try {
                        return this.convertToResGeneralDriverInfoDTO(driver.getAccount(), driver);
                    } catch (ApplicationException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
        res.setResult(generalDriverInfoDTOList);
        return res;
    }

    @Override
    public boolean isRegisteredDriver(int accountId) {
        return this.driverRepository.existsByAccount_Id(accountId);
    }

    @Override
    public ResCancelDriver getInfoCancelDriver(int idDriver) throws IdInvalidException, ApplicationException {
        Driver driver = this.driverRepository.findById(idDriver)
                .orElseThrow(() -> new IdInvalidException("Driver ID is invalid"));
        AccountRole accountRole = this.accountRoleService.getAccountRole(driver.getAccount().getEmail(), String.valueOf(PartnerTypeEnum.DRIVER));
        ResCancelDriver resCancelDriver = new ResCancelDriver();
        resCancelDriver.setLockReason(accountRole.getLockReason());
        resCancelDriver.setTimeUpdate(accountRole.getTimeUpdate());

        return resCancelDriver;
    }

    @Transactional
    @Override
    public void refuseOrDeleteRegisterDriver(ReqPartnerAction reqPartnerAction) throws IdInvalidException, IOException, ApplicationException {
        Driver driver = this.driverRepository.findById(reqPartnerAction.getFormRegisterId())
                .orElseThrow(() -> new IdInvalidException("Driver ID is invalid"));
        this.imageService.deleteImages(driver.getId(), String.valueOf(PartnerTypeEnum.DRIVER));
        log.info("Deleted driver with id: " + reqPartnerAction.getFormRegisterId());
        this.bankAccountService.deleteBankAccount(driver.getAccount().getId(), PartnerTypeEnum.DRIVER);
        this.driverRepository.delete(driver);

        // Delete accountRole in delete business partner
        String email = driver.getAccount().getEmail();
        AccountRole accountRole = this.accountRoleService.getAccountRole(email, String.valueOf(PartnerTypeEnum.DRIVER));
        if(accountRole != null){
            this.accountRoleRepository.delete(accountRole);
        }

        if(reqPartnerAction.isRefuse()){
            Context context = new Context();
            context.setVariable("cssContent", this.emailService.loadCssFromFile());
            context.setVariable("partner", "tài xế");
            context.setVariable("reasonCancel", reqPartnerAction.getReason());
            this.emailService.sendEmail(email, "Thông báo từ chối hợp tác", "refuse_partner", context);
        }
    }

    @Override
    public String getStatusRegister() {
        String email = SecurityUtil.getCurrentLogin().isPresent() ? SecurityUtil.getCurrentLogin().get() : null;
        Account account = this.accountService.handleGetAccountByUsername(email);
        Driver driver = account.getDriver();
        if(driver != null){
            ApprovalStatusEnum approvalStatus = driver.getApprovalStatus();
            return String.valueOf(approvalStatus);
        }
        return null;
    }

    @Override
    public ResGeneralDriverInfoDTO convertToResGeneralDriverInfoDTO(Account account, Driver driver) throws ApplicationException {
        ResGeneralDriverInfoDTO res = new ResGeneralDriverInfoDTO();
        ResGeneralDriverInfoDTO.GeneralDriverInfo generalDriverInfo = new ResGeneralDriverInfoDTO.GeneralDriverInfo();
        generalDriverInfo.setId(account.getId());
        generalDriverInfo.setEmail(account.getEmail());
        generalDriverInfo.setName(account.getName());
        generalDriverInfo.setPhoneNumber(account.getPhoneNumber());
        generalDriverInfo.setPermanentAddress(driver.getPermanentAddress());
        generalDriverInfo.setLocation(driver.getLocation());
        generalDriverInfo.setFormRegisterId(driver.getId());
        generalDriverInfo.setApprovalStatus(driver.getApprovalStatus());
        List<String> urlAvatarOfDriver = this.imageRepository.findByOwnerTypeAndOwnerId(String.valueOf(ImageOfObjectEnum.AVATAR_OF_DRIVER), driver.getId())
                .stream().map(image -> image.getPathImage())
                .collect(Collectors.toList());
        generalDriverInfo.setAvatar(urlAvatarOfDriver.get(0));
        AccountRole accountRole = this.accountRoleService.getAccountRole(driver.getAccount().getEmail()
                , String.valueOf(PartnerTypeEnum.DRIVER));
        if(accountRole != null) {
            generalDriverInfo.setTimeBecomePartner(accountRole.getTimeBecomePartner());
            generalDriverInfo.setCancelReason(accountRole.getLockReason());
            generalDriverInfo.setTimeUpdate(accountRole.getTimeUpdate());
        }
        res.setGeneralDriverInfo(generalDriverInfo);
        return res;
    }


}
