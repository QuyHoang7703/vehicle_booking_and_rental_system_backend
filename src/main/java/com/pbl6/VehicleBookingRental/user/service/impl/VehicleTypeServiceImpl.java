package com.pbl6.VehicleBookingRental.user.service.impl;

import com.pbl6.VehicleBookingRental.user.domain.VehicleType;
import com.pbl6.VehicleBookingRental.user.dto.Meta;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.repository.vehicle_rental.VehicleTypeRepository;
import com.pbl6.VehicleBookingRental.user.service.VehicleTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleTypeServiceImpl implements VehicleTypeService {
    private final VehicleTypeRepository vehicleTypeRepository;

    @Override
    public VehicleType createVehicleType(VehicleType vehicleType) {
        return this.vehicleTypeRepository.save(vehicleType);
    }

    @Override
    public VehicleType updateVehicleType(VehicleType vehicleType) {
        VehicleType vehicleTypeDb = this.findVehicleTypeById(vehicleType.getId());
        vehicleTypeDb.setName(vehicleType.getName());
        vehicleTypeDb.setPrice(vehicleType.getPrice());
        vehicleTypeDb.setDescription(vehicleType.getDescription());
        return this.vehicleTypeRepository.save(vehicleTypeDb);
    }

    @Override
    public VehicleType findVehicleTypeById(int id) {
        return this.vehicleTypeRepository.findById(id)
                .orElse(null);
    }

    @Override
    public void deleteVehicleType(int id) {
        VehicleType vehicleTypeDb = this.findVehicleTypeById(id);
        this.vehicleTypeRepository.delete(vehicleTypeDb);

    }

    @Override
    public ResultPaginationDTO getAllVehicleTypes(Specification<VehicleType> specification, Pageable pageable) {
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        Page<VehicleType> pageVehicleTypes = this.vehicleTypeRepository.findAll(specification, pageable);
        Meta meta = new Meta();
        meta.setCurrentPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageVehicleTypes.getTotalPages());
        meta.setTotal(pageVehicleTypes.getTotalElements());
        resultPaginationDTO.setMeta(meta);

        List<VehicleType> vehicleTypes = pageVehicleTypes.getContent();
        resultPaginationDTO.setResult(vehicleTypes);
        return resultPaginationDTO;
    }
}
