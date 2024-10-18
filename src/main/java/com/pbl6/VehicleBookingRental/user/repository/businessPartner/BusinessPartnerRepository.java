package com.pbl6.VehicleBookingRental.user.repository.businessPartner;

import com.pbl6.VehicleBookingRental.user.domain.BusinessPartner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessPartnerRepository extends JpaRepository<BusinessPartner, Integer>, JpaSpecificationExecutor<BusinessPartner> {

}
