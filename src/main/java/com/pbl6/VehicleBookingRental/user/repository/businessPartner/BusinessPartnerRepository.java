package com.pbl6.VehicleBookingRental.user.repository.businessPartner;

import com.pbl6.VehicleBookingRental.user.domain.BusinessPartner;
import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.dto.response.statistic.RevenueOfBusinessPartnerDTO;
import com.pbl6.VehicleBookingRental.user.util.constant.PartnerTypeEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface BusinessPartnerRepository extends JpaRepository<BusinessPartner, Integer>, JpaSpecificationExecutor<BusinessPartner> {
    Optional<BusinessPartner> findByIdAndPartnerType(int id, PartnerTypeEnum partnerType);
    boolean existsByAccount_IdAndPartnerType(int accountId, PartnerTypeEnum partnerType);
    Optional<BusinessPartner> findByAccount_IdAndPartnerType(int accountId, PartnerTypeEnum partnerType);
    Page<BusinessPartner> findByPartnerType(PartnerTypeEnum partnerType, Pageable pageable);

    List<Integer> findIdsByPartnerType(PartnerTypeEnum partnerType);
}
