package com.pbl6.VehicleBookingRental.user.service.impl;

import com.pbl6.VehicleBookingRental.user.domain.BusinessPartner;
import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.domain.account.AccountRole;
import com.pbl6.VehicleBookingRental.user.domain.account.Role;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusPartner;
import com.pbl6.VehicleBookingRental.user.dto.request.businessPartner.BusinessPartnerDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.businessPartner.ReqBusPartnerDTO;
import com.pbl6.VehicleBookingRental.user.repository.account.RoleRepository;
import com.pbl6.VehicleBookingRental.user.repository.businessPartner.BusPartnerRepository;
import com.pbl6.VehicleBookingRental.user.repository.businessPartner.BusinessPartnerRepository;
import com.pbl6.VehicleBookingRental.user.service.AccountService;
import com.pbl6.VehicleBookingRental.user.service.BusPartnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BusPartnerServiceImpl implements BusPartnerService {
    private final BusinessPartnerRepository businessPartnerRepository;
    private final BusPartnerRepository busPartnerRepository;
    private final AccountService accountService;
    private final RoleRepository roleRepository;
    @Override
    public BusPartner registerBusPartner(ReqBusPartnerDTO reqBusPartnerDTO, List<MultipartFile> licenses, List<MultipartFile> images) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Account account = this.accountService.handleGetAccountByUsername(username);
        if(account == null) {
            throw new UsernameNotFoundException("Username not found");
        }
        // Create BusinessPartner
        BusinessPartner businessPartner = new BusinessPartner();
        businessPartner.setNameOfRepresentative(reqBusPartnerDTO.getNameOfRepresentative());
        businessPartner.setBusinessName(reqBusPartnerDTO.getBusinessName());
        businessPartner.setEmailOfRepresentative(reqBusPartnerDTO.getEmailOfRepresentative());
        businessPartner.setPhoneOfRepresentative(reqBusPartnerDTO.getPhoneOfRepresentative());
        businessPartner.setAddress(reqBusPartnerDTO.getAddress());
        businessPartner.setPartnerType(reqBusPartnerDTO.getPartnerType());
        businessPartner.setAccount(account);
        BusinessPartner savedBusinessPartner = this.businessPartnerRepository.save(businessPartner);

        // Create BusPartner
        BusPartner busPartner = new BusPartner();
        busPartner.setDescription(reqBusPartnerDTO.getDescription());
        busPartner.setUrl(reqBusPartnerDTO.getUrlFanpage());
        busPartner.setPolicy(reqBusPartnerDTO.getPolicy());
        busPartner.setBusinessPartner(savedBusinessPartner);
        this.busPartnerRepository.save(busPartner);

//        businessPartner.setBusPartner(busPartner);



//        Role role = this.roleRepository.findByName("BUS_PARTNER").orElseThrow(() -> new UsernameNotFoundException("Role not found"));
//        AccountRole accountRole = new AccountRole();
//        accountRole.setRole(role);
//        accountRole.setAccount(account);

        return busPartner;
    }
}
