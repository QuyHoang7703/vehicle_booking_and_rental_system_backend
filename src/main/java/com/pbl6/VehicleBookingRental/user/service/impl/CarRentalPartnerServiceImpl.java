package com.pbl6.VehicleBookingRental.user.service.impl;

import com.pbl6.VehicleBookingRental.user.domain.BankAccount;
import com.pbl6.VehicleBookingRental.user.domain.BusinessPartner;
import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.domain.account.AccountRole;
import com.pbl6.VehicleBookingRental.user.domain.car_rental.CarRentalPartner;
import com.pbl6.VehicleBookingRental.user.dto.chat_dto.NotificationDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.businessPartner.ReqCarRentalPartnerDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.bankAccount.ResBankAccountDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.businessPartner.ResBusinessPartnerDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.businessPartner.ResCarRentalPartnerDTO;
import com.pbl6.VehicleBookingRental.user.repository.businessPartner.BusinessPartnerRepository;
import com.pbl6.VehicleBookingRental.user.repository.businessPartner.CarRentalPartnerRepository;
import com.pbl6.VehicleBookingRental.user.repository.image.ImageRepository;
import com.pbl6.VehicleBookingRental.user.service.*;
import com.pbl6.VehicleBookingRental.user.util.SecurityUtil;
import com.pbl6.VehicleBookingRental.user.util.constant.*;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarRentalPartnerServiceImpl implements CarRentalPartnerService {
    private final AccountService accountService;
    private final ImageService imageService;
    private final CloudinaryService cloudinaryService;
    private final BusinessPartnerRepository businessPartnerRepository;
    private final CarRentalPartnerRepository carRentalPartnerRepository;
    private final BusinessPartnerService businessPartnerService;
    private final BankAccountService bankAccountService;
    private final ImageRepository imageRepository;
    private final AccountRoleService accountRoleService;
    private final NotificationService notificationService;

    @Override
    public ResBusinessPartnerDTO registerCarRentalPartner(ReqCarRentalPartnerDTO reqCarRentalPartnerDTO,
                                                    MultipartFile avatar,
                                                    List<MultipartFile> licenses,
                                                    List<MultipartFile> images) throws Exception {
        String username = SecurityUtil.getCurrentLogin().isPresent()?
                SecurityUtil.getCurrentLogin().get() : "";
        Account account = this.accountService.handleGetAccountByUsername(username);
        if(account == null) {
            throw new UsernameNotFoundException("Username not found");
        }
        if(this.businessPartnerService.isRegistered(account.getId(), reqCarRentalPartnerDTO.getPartnerType())){
            log.info("Already registered");
            throw new ApplicationException("You have already registered this business partner");
        }
        // Create BusinessPartner
        BusinessPartner businessPartner = new BusinessPartner();
        businessPartner.setNameOfRepresentative(reqCarRentalPartnerDTO.getNameOfRepresentative());
        businessPartner.setBusinessName(reqCarRentalPartnerDTO.getBusinessName());
        businessPartner.setEmailOfRepresentative(reqCarRentalPartnerDTO.getEmailOfRepresentative());
        businessPartner.setPhoneOfRepresentative(reqCarRentalPartnerDTO.getPhoneOfRepresentative());
        businessPartner.setAddress(reqCarRentalPartnerDTO.getAddress());
        businessPartner.setPartnerType(reqCarRentalPartnerDTO.getPartnerType());
        businessPartner.setApprovalStatus(ApprovalStatusEnum.PENDING_APPROVAL);
        businessPartner.setAccount(account);
        List<String> policies = reqCarRentalPartnerDTO.getPolicies();
        String policiesAsString = String.join("!", policies);
        businessPartner.setPolicy(policiesAsString);

        if(avatar != null) {
            String url = this.cloudinaryService.uploadFile(avatar);
            businessPartner.setAvatar(url);
        }
        BusinessPartner savedBusinessPartner = this.businessPartnerRepository.save(businessPartner);
        this.bankAccountService.createBankAccount(reqCarRentalPartnerDTO.getBankAccount(), account);
        // Create BusPartner
        CarRentalPartner carRentalPartner = new CarRentalPartner();
        carRentalPartner.setClientType(reqCarRentalPartnerDTO.getClientType());
        carRentalPartner.setBusinessPartner(savedBusinessPartner);
        CarRentalPartner savedCarRentalPartner = this.carRentalPartnerRepository.save(carRentalPartner);

//        businessPartner.setBusPartner(carRentalPartner);

        // Add business license images for bus partner
        this.imageService.uploadAndSaveImages(licenses, String.valueOf(ImageOfObjectEnum.BUSINESS_LICENSE), savedCarRentalPartner.getBusinessPartner().getId(), String.valueOf(PartnerTypeEnum.CAR_RENTAL_PARTNER));
        this.imageService.uploadAndSaveImages(images, String.valueOf(ImageOfObjectEnum.CAR_RENTAL_PARTNER), savedCarRentalPartner.getBusinessPartner().getId(), String.valueOf(PartnerTypeEnum.CAR_RENTAL_PARTNER));

        ResBusinessPartnerDTO resBusinessPartnerDTO = this.businessPartnerService.convertToResBusinessPartnerDTO(savedBusinessPartner);

        // Create notification for admin about register
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setMessage("Đối tác cho thuê xe" + businessPartner.getBusinessName() + " muốn đăng ký trở thành đối tác");
        notificationDTO.setTitle("Đơn đăng ký đối tác cho thuê xe");
        notificationDTO.setType(NotificationTypeEnum.RECEIVED_REGISTER_PARTNER_FORM);
        notificationDTO.setCreate_at(Instant.now());
        notificationDTO.setSeen(false);
        notificationService.createNotificationToAccount(1, AccountEnum.ADMIN, notificationDTO);

        return resBusinessPartnerDTO;

    }

    @Override
    public CarRentalPartner getCarRentalPartnerByBusinessPartnerId(int id) throws IdInvalidException {
        BusinessPartner businessPartner = this.businessPartnerRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Business Partner not found"));
        CarRentalPartner carRentalPartner = this.carRentalPartnerRepository.findByBusinessPartner(businessPartner)
                .orElseThrow(() -> new IdInvalidException("Car Rental Partner not found"));
        return carRentalPartner;
    }

    @Override
    public ResCarRentalPartnerDTO convertoCarRentalPartnerDTO(CarRentalPartner carRentalPartner) throws Exception {
        ResBusinessPartnerDTO resBusinessPartnerDTO = this.businessPartnerService.convertToResBusinessPartnerDTO(carRentalPartner.getBusinessPartner());
        // Tạo BusinessPartnerInfo từ BusPartner
//        ResBusinessPartnerDTO.BusinessPartnerInfo businessPartnerInfo = createBusinessPartnerInfo(carRentalPartner);
        // Tạo CarRentalPartner từ CarRentalPartner
        ResCarRentalPartnerDTO.CarRentalPartnerInfo carRentalPartnerInfo = createCarRentalPartnerInfo(carRentalPartner);

        // Tạo và trả về ResCarRentalPartnerDTO
        ResCarRentalPartnerDTO resCarRentalPartnerDTO = new ResCarRentalPartnerDTO();

        resCarRentalPartnerDTO.setBusinessInfo(resBusinessPartnerDTO.getBusinessInfo());
        resCarRentalPartnerDTO.setCarRentalPartnerInfo(carRentalPartnerInfo);
        resCarRentalPartnerDTO.setTimeBecomePartner(resBusinessPartnerDTO.getTimeBecomePartner());
        resCarRentalPartnerDTO.setCancelReason(resBusinessPartnerDTO.getCancelReason());
        resCarRentalPartnerDTO.setTimeUpdate(resBusinessPartnerDTO.getTimeUpdate());

        return resCarRentalPartnerDTO;
    }


    private ResCarRentalPartnerDTO.CarRentalPartnerInfo createCarRentalPartnerInfo(CarRentalPartner carRentalPartner) throws Exception {
        ResCarRentalPartnerDTO.CarRentalPartnerInfo carRentalPartnerInfo = new ResCarRentalPartnerDTO.CarRentalPartnerInfo();
        carRentalPartnerInfo.setClientType(carRentalPartner.getClientType());

        List<String> urlLicenses = this.imageRepository.findByOwnerTypeAndOwnerId(String.valueOf(ImageOfObjectEnum.BUSINESS_LICENSE),
                        carRentalPartner.getBusinessPartner().getId()).stream().map(image -> image.getPathImage())
                .collect(Collectors.toList());
        carRentalPartnerInfo.setUrlLicenses(urlLicenses);

        List<String> urlImages = this.imageRepository.findByOwnerTypeAndOwnerId(String.valueOf(ImageOfObjectEnum.CAR_RENTAL_PARTNER),
                        carRentalPartner.getBusinessPartner().getId()).stream().map(image -> image.getPathImage())
                .collect(Collectors.toList());
        carRentalPartnerInfo.setUrlImages(urlImages);

        String policiesString = carRentalPartner.getBusinessPartner().getPolicy();
        List<String> policiesList = Arrays.asList(policiesString.split("!"));
        carRentalPartnerInfo.setPolicies(policiesList);

        ResBankAccountDTO resBankAccount = this.bankAccountService.convertoResBankAccountDTO(carRentalPartner.getBusinessPartner().getAccount().getId(), PartnerTypeEnum.CAR_RENTAL_PARTNER);

        carRentalPartnerInfo.setBankAccount(resBankAccount);

        return carRentalPartnerInfo;
    }


}
