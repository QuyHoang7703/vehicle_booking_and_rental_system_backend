package com.pbl6.VehicleBookingRental.user.repository.busPartner;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.Bus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusRepository extends JpaRepository<Bus, Integer>, JpaSpecificationExecutor<Bus> {
    boolean existsByLicensePlate(String licensePlate);
    List<Bus> findByBusType_Name(String busTypeName);
//    Page<Bus> findAll(Specification<Bus> spec, Pageable pageable);

}
