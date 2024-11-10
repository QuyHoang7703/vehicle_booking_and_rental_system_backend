package com.pbl6.VehicleBookingRental.user.repository.image;

import com.pbl6.VehicleBookingRental.user.domain.Images;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ImageRepository extends JpaRepository<Images, Integer>, JpaSpecificationExecutor<Images> {
    List<Images> findByOwnerTypeAndOwnerId(String ownerType, int ownerId);
    List<Images> findByOwnerIdAndOwnerGroup(int ownerId, String ownerGroup);
}
