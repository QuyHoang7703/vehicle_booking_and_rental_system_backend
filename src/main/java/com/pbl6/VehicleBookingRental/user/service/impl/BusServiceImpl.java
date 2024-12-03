package com.pbl6.VehicleBookingRental.user.service.impl;

import com.pbl6.VehicleBookingRental.user.domain.BusinessPartner;
import com.pbl6.VehicleBookingRental.user.domain.Images;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.Bus;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusPartner;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusType;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.Utility;
import com.pbl6.VehicleBookingRental.user.dto.Meta;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.bus.ReqBusDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.bus.ResBusDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.bus.ResBusDetailDTO;
import com.pbl6.VehicleBookingRental.user.repository.UtilityRepository;
import com.pbl6.VehicleBookingRental.user.repository.busPartner.BusRepository;
import com.pbl6.VehicleBookingRental.user.repository.busPartner.BusTypeRepository;
import com.pbl6.VehicleBookingRental.user.repository.image.ImageRepository;
import com.pbl6.VehicleBookingRental.user.service.*;
import com.pbl6.VehicleBookingRental.user.util.constant.ImageOfObjectEnum;
import com.pbl6.VehicleBookingRental.user.util.constant.PartnerTypeEnum;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import jakarta.persistence.criteria.Join;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BusServiceImpl implements BusService {
    private final BusRepository busRepository;
    private final UtilityRepository utilityRepository;
    private final BusTypeRepository busTypeRepository;
    private final BusinessPartnerService businessPartnerService;
    private final ImageRepository imageRepository;
    private final ImageService imageService;
    private final S3Service s3Service;

    @Override
    public Bus createBus(ReqBusDTO reqBus, List<MultipartFile> busImages) throws IdInvalidException, ApplicationException {
        if(this.busRepository.existsByLicensePlate(reqBus.getLicensePlate())) {
            throw new ApplicationException("Bus is available");
        }
        Bus bus = new Bus();

        bus.setLicensePlate(reqBus.getLicensePlate());

        // Add utilities for bus
        List<Integer> idOfUtilities = reqBus.getUtilities().stream().map(utility -> utility.getId())
                .toList();
        List<Utility> utilities = this.utilityRepository.findByIdIn(idOfUtilities);
        bus.setUtilities(utilities);

        // Add bus type for bus
        BusType busType = this.busTypeRepository.findById(reqBus.getBusType().getId())
                .orElseThrow(() -> new IdInvalidException("Bus type not found"));
        bus.setBusType(busType);

        BusinessPartner businessPartner = this.businessPartnerService.getCurrentBusinessPartner(PartnerTypeEnum.BUS_PARTNER);
        bus.setBusPartner(businessPartner.getBusPartner());

        Bus savedBus = this.busRepository.save(bus);

        // Add images for bus
        this.imageService.uploadAndSaveImages(busImages, String.valueOf(ImageOfObjectEnum.BUS), savedBus.getId(), String.valueOf(ImageOfObjectEnum.BUS));

        return savedBus;
    }

    @Override
    public Bus updateBus(ReqBusDTO reqBus, List<MultipartFile> busImages) throws IdInvalidException, ApplicationException {
        Bus busDb = this.busRepository.findById(reqBus.getId())
                .orElseThrow(() -> new IdInvalidException("Bus not found"));
        BusinessPartner businessPartner = this.businessPartnerService.getCurrentBusinessPartner(PartnerTypeEnum.BUS_PARTNER);
        if(!busDb.getBusPartner().equals(businessPartner.getBusPartner())) {
            throw new ApplicationException("You are not authorized to update this bus");
        }
        busDb.setLicensePlate(reqBus.getLicensePlate());

        // Update utilities
        List<Integer> idOfUtilities = reqBus.getUtilities().stream()
                .map(Utility::getId).toList();
        List<Utility> utilities = this.utilityRepository.findByIdIn(idOfUtilities);
        busDb.setUtilities(utilities);

        // Update bus type
        BusType busType = this.busTypeRepository.findById(reqBus.getBusType().getId())
                .orElseThrow(() -> new IdInvalidException("Bus type not found"));
        busDb.setBusType(busType);

        // Update bus images
        if(busImages != null && !busImages.isEmpty()) {
            List<String> currentUrlImages = this.imageRepository.findByOwnerTypeAndOwnerId(String.valueOf(ImageOfObjectEnum.BUS), busDb.getId())
                    .stream().map(Images::getPathImage).toList();
            this.s3Service.deleteFiles(currentUrlImages);
            this.imageService.uploadAndSaveImages(busImages, String.valueOf(ImageOfObjectEnum.BUS), busDb.getId(), String.valueOf(ImageOfObjectEnum.BUS));
        }

        return this.busRepository.save(busDb);
    }

    @Override
    public ResBusDetailDTO convertToResBusDetail(Bus bus) {
        ResBusDetailDTO resBus = new ResBusDetailDTO();
        resBus.setLicensePlate(bus.getLicensePlate());
        resBus.setUtilities(bus.getUtilities());
        resBus.setBusType(bus.getBusType());

        List<String> urlImagesBus = this.imageRepository.findByOwnerTypeAndOwnerId(String.valueOf(ImageOfObjectEnum.BUS), bus.getId())
                .stream().map(Images::getPathImage).toList();

        resBus.setImagesBus(urlImagesBus);

        return resBus;
    }

    @Override
    public void deleteBus(int busId) throws IdInvalidException, ApplicationException {
        Bus busDb = this.busRepository.findById(busId)
                .orElseThrow(() -> new IdInvalidException("Bus is unavailable"));
        BusinessPartner businessPartner = this.businessPartnerService.getCurrentBusinessPartner(PartnerTypeEnum.BUS_PARTNER);
        if(!busDb.getBusPartner().equals(businessPartner.getBusPartner())) {
            throw new ApplicationException("You are not authorized to delete this bus");
        }
        if(busDb.getBusTripSchedules()!=null && !busDb.getBusTripSchedules().isEmpty()) {
            throw new ApplicationException("This bus is serving for a bus trip schedule");
        }
        this.busRepository.delete(busDb);
    }

    @Override
    public Bus findBusById(int busId) throws IdInvalidException, ApplicationException {
        Bus bus = this.busRepository.findById(busId)
                .orElseThrow(() -> new IdInvalidException("Bus not found"));
        BusinessPartner businessPartner = this.businessPartnerService.getCurrentBusinessPartner(PartnerTypeEnum.BUS_PARTNER);
        if(!bus.getBusPartner().equals(businessPartner.getBusPartner())) {
            throw new ApplicationException("You are not authorized to find this bus");
        }
        return bus;
    }

    @Override
    public ResultPaginationDTO getAllBuses(Specification<Bus> spec, Pageable pageable) throws IdInvalidException, ApplicationException {
        BusinessPartner businessPartner = this.businessPartnerService.getCurrentBusinessPartner(PartnerTypeEnum.BUS_PARTNER);

        Specification<Bus> newSpec = (root, query, criteriaBuilder) ->{
            Join<Bus, BusPartner> joinBusParner = root.join("busPartner");
            return criteriaBuilder.equal(joinBusParner.get("id"), businessPartner.getBusPartner().getId());
        };
        Specification<Bus> finalSpec = spec.and(newSpec);
        Page<Bus> busPage = this.busRepository.findAll(finalSpec, pageable);
        ResultPaginationDTO res = new ResultPaginationDTO();
        Meta meta = new Meta();
        meta.setCurrentPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(busPage.getTotalPages());
        meta.setTotal(busPage.getTotalElements());
        res.setMeta(meta);

        List<ResBusDTO> buses = busPage.getContent().stream().map(bus -> {
                    try {
                        return this.convertToResBus(bus);
                    } catch (IdInvalidException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
        res.setResult(buses);
        return res;
    }

    @Override
    public ResBusDTO convertToResBus(Bus bus) throws IdInvalidException {
        ResBusDTO resBus = new ResBusDTO();
        resBus.setBusId(bus.getId());
        resBus.setLicensePlate(bus.getLicensePlate());

        BusType busType = this.busTypeRepository.findById(bus.getBusType().getId())
                .orElseThrow(() -> new IdInvalidException("Bus type is unavailable"));
        resBus.setNameBusType(busType.getName());

        List<Images> images = this.imageRepository.findByOwnerTypeAndOwnerId(String.valueOf(ImageOfObjectEnum.BUS), bus.getId());
        resBus.setImageRepresentative(images.get(0).getPathImage());

        return resBus;
    }

    @Override
    public Map<Integer, String> getBusesByBusTypeId(String busTypeName) throws IdInvalidException, ApplicationException {
        List<Bus> buses = this.busRepository.findByBusType_Name(busTypeName);
        BusinessPartner businessPartner = this.businessPartnerService.getCurrentBusinessPartner(PartnerTypeEnum.BUS_PARTNER);
        boolean authorized = false;
        for (Bus bus : buses) {
            // Kiểm tra xem bus này có nằm trong danh sách các xe của busPartner hay không
            if (businessPartner.getBusPartner().getBuses().contains(bus)) {
                authorized = true;
                break;
            }
        }
        if (!authorized) {
            throw new ApplicationException("You are not authorized to find this bus");
        }
        Map<Integer, String> map = new HashMap<>();
        for(Bus bus : buses) {
            map.put(bus.getId(), bus.getLicensePlate());
        }
        return map;
    }

    @Override
    public List<String> getImages(int busId) throws IdInvalidException {
        List<String> imageUrls = this.imageRepository.findByOwnerTypeAndOwnerId(String.valueOf(ImageOfObjectEnum.BUS), busId)
                .stream()
                .map(Images::getPathImage)
                .toList();
        return imageUrls;
    }


}
