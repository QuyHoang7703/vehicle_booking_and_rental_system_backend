package com.pbl6.VehicleBookingRental.user.repository.permissionRole;

import com.pbl6.VehicleBookingRental.user.domain.account.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Integer>, JpaSpecificationExecutor<Permission> {
    boolean existsByModuleAndApiPathAndMethod(String module, String apiPath, String method);
    List<Permission> findByIdIn(List<Integer> ids);
}
