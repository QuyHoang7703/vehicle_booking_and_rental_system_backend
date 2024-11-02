package com.pbl6.VehicleBookingRental.user.service.impl;

import com.pbl6.VehicleBookingRental.user.domain.BusinessPartner;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusPartner;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusType;
import com.pbl6.VehicleBookingRental.user.dto.Meta;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.busType.ResBusType;
import com.pbl6.VehicleBookingRental.user.repository.busPartner.BusTypeRepository;
import com.pbl6.VehicleBookingRental.user.service.BusTypeService;
import com.pbl6.VehicleBookingRental.user.service.BusinessPartnerService;
import com.pbl6.VehicleBookingRental.user.util.constant.PartnerTypeEnum;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import jakarta.persistence.criteria.Join;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BusTypeServiceImpl implements BusTypeService {
    private final BusTypeRepository busTypeRepository;
    private final BusinessPartnerService businessPartnerService;

    @Override
    public ResBusType createBusType(BusType busType) throws Exception {
        if(this.busTypeRepository.existsByNameAndNumberOfSeatAndChairType(busType.getName(), busType.getNumberOfSeat(), busType.getChairType())) {
            throw new ApplicationException("This bus type has already existed");
        }

        BusinessPartner businessPartner = this.businessPartnerService.getCurrentBusinessPartner(PartnerTypeEnum.BUS_PARTNER);

        log.info("ID BUSINESS PARTNER: " + businessPartner.getId());
//        BusPartner busPartnerDb = this.busPartnerService.findById(busType.getBusPartner().getId());
        busType.setBusPartner(businessPartner.getBusPartner());
        this.busTypeRepository.save(busType);
        return this.convertToResBusType(busType);
    }

    @Override
    public ResBusType findById(int id) throws IdInvalidException, ApplicationException {
        BusType busType = this.busTypeRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Don't exist id of bus type is: " + id));
        BusinessPartner businessPartner = this.businessPartnerService.getCurrentBusinessPartner(PartnerTypeEnum.BUS_PARTNER);
        if(!busType.getBusPartner().equals(businessPartner.getBusPartner())) {
            throw new ApplicationException("You are not authorized to update this bus type");
        }
        return this.convertToResBusType(busType);
    }

    @Override
    public ResBusType updateBusType(BusType busType) throws IdInvalidException, ApplicationException {
        BusType busTypeDb = this.busTypeRepository.findById(busType.getId())
                .orElseThrow(() -> new IdInvalidException("Don't exist id of bus type is: " +busType.getId()));
        BusinessPartner businessPartner = this.businessPartnerService.getCurrentBusinessPartner(PartnerTypeEnum.BUS_PARTNER);
        if(!busTypeDb.getBusPartner().equals(businessPartner.getBusPartner())) {
            throw new ApplicationException("You are not authorized to update this bus type");
        }
        busTypeDb.setName(busType.getName());
        busTypeDb.setNumberOfSeat(busType.getNumberOfSeat());
        busTypeDb.setChairType(busType.getChairType());

        BusType savedBusType = this.busTypeRepository.save(busTypeDb);
        return this.convertToResBusType(savedBusType);
    }


    @Override
    public void deleteById(int id) throws IdInvalidException, ApplicationException {
        BusType busTypeDb = this.busTypeRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Don't exist id of bus type is: " + id));

        BusinessPartner businessPartner = this.businessPartnerService.getCurrentBusinessPartner(PartnerTypeEnum.BUS_PARTNER);
        if(!busTypeDb.getBusPartner().equals(businessPartner.getBusPartner())) {
            throw new ApplicationException("You do not have permission to delete this bus type");
        }
        this.busTypeRepository.delete(busTypeDb);
    }

    @Override
    public ResultPaginationDTO getAllBusTypes(Specification<BusType> spec, Pageable pageable) throws ApplicationException {
        BusinessPartner businessPartner = this.businessPartnerService.getCurrentBusinessPartner(PartnerTypeEnum.BUS_PARTNER);

        Specification<BusType> newSpec = (root, query, criteriaBuilder) ->{
            Join<BusType, BusPartner> busPartnerJoin = root.join("busPartner");
            return criteriaBuilder.equal(busPartnerJoin.get("id"), businessPartner.getBusPartner().getId());
        };
        Specification<BusType> finalSpec = spec.and(newSpec);
        Page<BusType> busTypePage = this.busTypeRepository.findAll(finalSpec, pageable);
        ResultPaginationDTO res = new ResultPaginationDTO();
        Meta meta = new Meta();
        meta.setCurrentPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(busTypePage.getTotalPages());
        meta.setTotal(busTypePage.getTotalElements());
        res.setMeta(meta);

        List<ResBusType> busTypes = busTypePage.getContent().stream().map(busType -> this.convertToResBusType(busType))
                .toList();
        res.setResult(busTypes);
        return res;
    }

    @Override
    public ResBusType convertToResBusType(BusType busType){
        ResBusType res = new ResBusType();
        res.setId(busType.getId());
        res.setName(busType.getName());
        res.setNumberOfSeat(busType.getNumberOfSeat());
        res.setChairType(busType.getChairType());
        return res;
    }


}
