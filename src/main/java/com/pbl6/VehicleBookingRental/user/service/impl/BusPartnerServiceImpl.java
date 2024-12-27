package com.pbl6.VehicleBookingRental.user.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pbl6.VehicleBookingRental.user.domain.BankAccount;
import com.pbl6.VehicleBookingRental.user.domain.BusinessPartner;
import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusPartner;
import com.pbl6.VehicleBookingRental.user.dto.chat_dto.NotificationDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.businessPartner.ReqBusPartnerDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.bankAccount.ResBankAccountDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.businessPartner.ResBusPartnerDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.businessPartner.ResBusinessPartnerDTO;
import com.pbl6.VehicleBookingRental.user.repository.businessPartner.BusPartnerRepository;
import com.pbl6.VehicleBookingRental.user.repository.businessPartner.BusinessPartnerRepository;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BusPartnerServiceImpl implements BusPartnerService {
    private final BusinessPartnerRepository businessPartnerRepository;
    private final BusPartnerRepository busPartnerRepository;
    private final AccountService accountService;
    private final CloudinaryService cloudinaryService;
    private final ImageRepository imageRepository;
    private final ImageService imageService;
    private final BankAccountService bankAccountService;
    private final BusinessPartnerService businessPartnerService;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @Override
    public ResBusinessPartnerDTO registerBusPartner(ReqBusPartnerDTO reqBusPartnerDTO,
                                                    MultipartFile avatar,
                                                    List<MultipartFile> licenses,
                                                    List<MultipartFile> images) throws Exception {
        String username = SecurityUtil.getCurrentLogin().isPresent()?
                SecurityUtil.getCurrentLogin().get() : "";
        Account account = this.accountService.handleGetAccountByUsername(username);
        if(account == null) {
            throw new UsernameNotFoundException("Username not found");
        }
        if(this.businessPartnerService.isRegistered(account.getId(), reqBusPartnerDTO.getPartnerType())){
            log.info("Already registered");
            throw new ApplicationException("You have already registered this business partner");
        }
        // Create BusinessPartner
        BusinessPartner businessPartner = new BusinessPartner();
        businessPartner.setNameOfRepresentative(reqBusPartnerDTO.getNameOfRepresentative());
        businessPartner.setBusinessName(reqBusPartnerDTO.getBusinessName());
        businessPartner.setEmailOfRepresentative(reqBusPartnerDTO.getEmailOfRepresentative());
        businessPartner.setPhoneOfRepresentative(reqBusPartnerDTO.getPhoneOfRepresentative());
        businessPartner.setAddress(reqBusPartnerDTO.getAddress());
        businessPartner.setPartnerType(reqBusPartnerDTO.getPartnerType());
        businessPartner.setApprovalStatus(ApprovalStatusEnum.PENDING_APPROVAL);
        List<String> policies = reqBusPartnerDTO.getPolicies();
        String policiesAsString = String.join("!", policies);
        businessPartner.setPolicy(policiesAsString);

        businessPartner.setAccount(account);
        if(avatar != null) {
            String url = this.cloudinaryService.uploadFile(avatar);
            businessPartner.setAvatar(url);
        }
        BusinessPartner savedBusinessPartner = this.businessPartnerRepository.save(businessPartner);
        this.bankAccountService.createBankAccount(reqBusPartnerDTO.getBankAccount(), account);

        // Create BusPartner
        BusPartner busPartner = new BusPartner();
        busPartner.setDescription(reqBusPartnerDTO.getDescription());
        busPartner.setUrlFanpage(reqBusPartnerDTO.getUrlFanpage());
//        busPartner.setPolicy(reqBusPartnerDTO.getPolicy());
        busPartner.setBusinessPartner(savedBusinessPartner);
        BusPartner savedBusPartner = this.busPartnerRepository.save(busPartner);

//        businessPartner.setBusPartner(busPartner);
        // Add business license images for bus partner
        this.imageService.uploadAndSaveImages(licenses, String.valueOf(ImageOfObjectEnum.BUSINESS_LICENSE), savedBusPartner.getBusinessPartner().getId(), String.valueOf(PartnerTypeEnum.BUS_PARTNER));
        this.imageService.uploadAndSaveImages(images, String.valueOf(ImageOfObjectEnum.BUS_PARTNER), savedBusPartner.getBusinessPartner().getId(), String.valueOf(PartnerTypeEnum.BUS_PARTNER));

        // Create notification for admin about register
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setMessage("Đơn đăng ký đối tác nhà xe " + businessPartner.getBusinessName() + " đang chờ xét duyệt");
        notificationDTO.setTitle("Đơn đăng ký đối tác nhà xe");
        notificationDTO.setType(NotificationTypeEnum.RECEIVED_REGISTER_PARTNER_FORM);
        notificationDTO.setCreate_at(Instant.now());
        notificationDTO.setSeen(false);
        Map<String, String> valueParams = new LinkedHashMap<>();
        valueParams.put("formRegisterId", String.valueOf(businessPartner.getId()));
        notificationDTO.setMetadata(objectMapper.writeValueAsString(valueParams));
        notificationService.createNotificationToAccount(1, AccountEnum.ADMIN, notificationDTO);

        return this.businessPartnerService.convertToResBusinessPartnerDTO(savedBusinessPartner);
    }

    @Override
    public BusPartner getBusPartnerByBusinessPartnerId(int id) throws IdInvalidException {
        BusinessPartner businessPartner = this.businessPartnerRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Business Partner not found"));
        BusPartner busPartner = this.busPartnerRepository.findByBusinessPartner(businessPartner)
                .orElseThrow(()-> new IdInvalidException("Bus Partner not found"));

        return busPartner;
    }


    @Override
    public ResBusPartnerDTO convertToResBusPartnerDTO(BusPartner busPartner) throws Exception {
        ResBusinessPartnerDTO resBusinessPartnerDTO = this.businessPartnerService.convertToResBusinessPartnerDTO(busPartner.getBusinessPartner());
        // Tạo BusinessPartnerInfo từ BusPartner
//        ResBusinessPartnerDTO.BusinessPartnerInfo businessPartnerInfo = createBusinessPartnerInfo(busPartner);

        // Tạo BusPartnerInfo từ BusPartner
        ResBusPartnerDTO.BusPartnerInfo busPartnerInfo = createBusPartnerInfo(busPartner);

        // Tạo và trả về ResBusPartnerDTO
        ResBusPartnerDTO resBusPartnerDTO = new ResBusPartnerDTO();
        resBusPartnerDTO.setBusinessInfo(resBusinessPartnerDTO.getBusinessInfo());
        resBusPartnerDTO.setTimeBecomePartner(resBusinessPartnerDTO.getTimeBecomePartner());
        resBusPartnerDTO.setTimeUpdate(resBusinessPartnerDTO.getTimeUpdate());
        resBusPartnerDTO.setCancelReason(resBusinessPartnerDTO.getCancelReason());
        resBusPartnerDTO.setBusPartnerInfo(busPartnerInfo);

        return resBusPartnerDTO;
    }

    @Override
    public BusPartner findById(int id) throws IdInvalidException {
        return this.busPartnerRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("BusPartner not found"));
    }

    @Override
    public List<String> getNamesBusPartner() {

        return this.busPartnerRepository.getNamesBusPartner();
    }


    private ResBusPartnerDTO.BusPartnerInfo createBusPartnerInfo(BusPartner busPartner) throws Exception {
        ResBusPartnerDTO.BusPartnerInfo busPartnerInfo = new ResBusPartnerDTO.BusPartnerInfo();
        busPartnerInfo.setDescription(busPartner.getDescription());
        busPartnerInfo.setUrlFanpage(busPartner.getUrlFanpage());
        String policiesString = busPartner.getBusinessPartner().getPolicy();
        List<String> policiesList = Arrays.asList(policiesString.split("!"));
        busPartnerInfo.setPolicy(policiesList);

        List<String> urlLicenses = this.imageRepository.findByOwnerTypeAndOwnerId("BUSINESS_LICENSE",
                        busPartner.getBusinessPartner().getId()).stream().map(image -> image.getPathImage())
                .collect(Collectors.toList());
        busPartnerInfo.setUrlLicenses(urlLicenses);

        List<String> urlImages = this.imageRepository.findByOwnerTypeAndOwnerId("BUS_PARTNER",
                        busPartner.getBusinessPartner().getId()).stream().map(image -> image.getPathImage())
                .collect(Collectors.toList());
        busPartnerInfo.setUrlImages(urlImages);

//        ResBankAccountDTO resBankAccount = new ResBankAccountDTO();
//        Account account = busPartner.getBusinessPartner().getAccount();
//        BankAccount bankAccount = account.getBankAccounts().get(0);
//        resBankAccount.setAccountNumber(bankAccount.getAccountNumber());
//        resBankAccount.setAccountHolderName(bankAccount.getAccountHolderName());
//        resBankAccount.setBankName(bankAccount.getBankName());
//        resBankAccount.setIdAccount(account.getId());
        ResBankAccountDTO resBankAccount = this.bankAccountService.convertoResBankAccountDTO(busPartner.getBusinessPartner().getAccount().getId(), PartnerTypeEnum.BUS_PARTNER);

        busPartnerInfo.setBankAccount(resBankAccount);

        return busPartnerInfo;
    }




}
