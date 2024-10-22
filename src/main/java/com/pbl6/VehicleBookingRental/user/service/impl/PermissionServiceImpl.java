package com.pbl6.VehicleBookingRental.user.service.impl;

import com.pbl6.VehicleBookingRental.user.domain.account.Permission;
import com.pbl6.VehicleBookingRental.user.dto.Meta;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.repository.permissionRole.PermissionRepository;
import com.pbl6.VehicleBookingRental.user.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionServiceImpl implements PermissionService {
    private final PermissionRepository permissionRepository;

    @Override
    public boolean isPermissionExist(Permission permission) {
        return this.permissionRepository.existsByName(permission.getName());
    }

    @Override
    public Permission createPermission(Permission permission) {
        if (!this.isPermissionExist(permission)) {
            return this.permissionRepository.save(permission);
        }
        log.warn("Can't create the permission");
        return null;
    }

    @Override
    public Permission fetchPermissionById(int id){
        Optional<Permission> optionalPermission = this.permissionRepository.findById(id);
        return optionalPermission.orElse(null);
    }

    @Override
    public Permission updatePermission(Permission permission)  {
        Permission permissionDb = this.fetchPermissionById(permission.getId());
        if (permissionDb != null) {
            permissionDb.setName(permission.getName());
            permissionDb.setDescription(permission.getDescription());
            return this.permissionRepository.save(permissionDb);
        }
        return null;
    }

    @Override
    public void deletePermission(int id) {
        this.permissionRepository.deleteById(id);
    }

    @Override
    public ResultPaginationDTO getAllPermissions(Specification<Permission> specification, Pageable pageable) {
        Page<Permission> permissions = this.permissionRepository.findAll(specification, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        Meta meta = new Meta();
        meta.setCurrentPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(permissions.getTotalPages());
        meta.setTotal(permissions.getTotalElements());
        resultPaginationDTO.setMeta(meta);

        List<Permission> permissionList = permissions.getContent();
        resultPaginationDTO.setResult(permissionList);
        return resultPaginationDTO;
    }


}
