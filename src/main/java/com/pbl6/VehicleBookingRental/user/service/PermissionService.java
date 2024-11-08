package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.domain.account.Permission;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface PermissionService {
    boolean isPermissionExist(Permission permission);
    Permission createPermission(Permission permission);
    Permission fetchPermissionById(int id) ;
    Permission updatePermission(Permission permission) ;
    void deletePermission(int id);
    ResultPaginationDTO getAllPermissions(Specification<Permission> specification, Pageable pageable);
}
