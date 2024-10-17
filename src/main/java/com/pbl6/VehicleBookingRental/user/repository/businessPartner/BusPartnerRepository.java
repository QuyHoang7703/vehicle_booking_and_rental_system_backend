package com.pbl6.VehicleBookingRental.user.repository.businessPartner;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusPartner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BusPartnerRepository extends JpaRepository<BusPartner, Integer>, JpaSpecificationExecutor<BusPartnerRepository> {
}
