package com.pbl6.VehicleBookingRental.user.service.impl;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.Bus;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.Utility;
import com.pbl6.VehicleBookingRental.user.dto.Meta;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.repository.UtilityRepository;
import com.pbl6.VehicleBookingRental.user.service.BusinessPartnerService;
import com.pbl6.VehicleBookingRental.user.service.CloudinaryService;
import com.pbl6.VehicleBookingRental.user.service.S3Service;
import com.pbl6.VehicleBookingRental.user.service.UtilityService;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UtilitySeviceImpl implements UtilityService {
    private final UtilityRepository utilityRepository;
    private final BusinessPartnerService businessPartnerService;
    private final S3Service s3Service;
    private final CloudinaryService cloudinaryService;

    @Override
    public Utility findById(int id) throws IdInvalidException {
        return this.utilityRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("This utility is not available"));
    }

    @Override
    public Utility createUtility(Utility reqUtility, MultipartFile utilityImage) throws ApplicationException, IOException {
        Utility utilityDb = this.utilityRepository.findByName(reqUtility.getName())
                .orElse(null);
        if(utilityDb != null){
            throw new ApplicationException("This utility already exists");
        }
        if(utilityImage != null){
            String urlUtilityImage = this.cloudinaryService.uploadFile(utilityImage);
            reqUtility.setImage(urlUtilityImage);
        }
        return this.utilityRepository.save(reqUtility);
    }

    @Override
    public Utility updateUtility(Utility reqUtility, MultipartFile utilityImage) throws IdInvalidException, IOException {
        Utility utilityDb = this.getUtilityById(reqUtility.getId());
        utilityDb.setName(reqUtility.getName());
        utilityDb.setDescription(reqUtility.getDescription());

        if(utilityImage != null){
            if(utilityDb.getImage() != null){
                this.cloudinaryService.deleteFile(utilityDb.getImage());
            }
            String urlUtilityImage = this.cloudinaryService.uploadFile(utilityImage);
            utilityDb.setImage(urlUtilityImage);
        }
        return this.utilityRepository.save(utilityDb);
    }

    @Override
    public void deleteUtility(int idUtility) throws IdInvalidException, IOException {
        Utility utilityDb = this.getUtilityById(idUtility);
        if(utilityDb.getBuses()!= null && !utilityDb.getBuses().isEmpty()){
            throw new RuntimeException("Cannot delete this utility, it has buses");
        }
        this.cloudinaryService.deleteFile(utilityDb.getImage());

        this.utilityRepository.delete(utilityDb);
    }

    @Override
    public Utility getUtilityById(int idUtility) throws IdInvalidException {
        return this.utilityRepository.findById(idUtility)
                .orElseThrow(() -> new IdInvalidException("Id of utility not found"));
    }

    @Override
    public ResultPaginationDTO getAllUtility(Specification<Utility> specification, Pageable pageable) {
        Page<Utility> utilityPage = utilityRepository.findAll(specification, pageable);
        ResultPaginationDTO res = new ResultPaginationDTO();
        Meta meta = new Meta();
        meta.setCurrentPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(utilityPage.getTotalPages());
        meta.setTotal(utilityPage.getTotalElements());

        List<Utility> utilityList = utilityPage.getContent();
        res.setResult(utilityList);

        return res;
    }

    @Override
    public List<Utility> getAllUtilityByBusId(int busId) {
        return this.utilityRepository.findBy_BusId(busId);
    }
}
