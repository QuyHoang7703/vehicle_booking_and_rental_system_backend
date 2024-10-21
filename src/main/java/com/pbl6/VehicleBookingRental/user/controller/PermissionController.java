package com.pbl6.VehicleBookingRental.user.controller;

import com.pbl6.VehicleBookingRental.user.domain.account.Permission;
import com.pbl6.VehicleBookingRental.user.dto.ResponseInfo;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.service.PermissionService;
import com.pbl6.VehicleBookingRental.user.util.annotation.ApiMessage;
import com.pbl6.VehicleBookingRental.user.util.error.IdInValidException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PermissionController {
    private final PermissionService permissionService;

    @PostMapping("/permissions")
    @ApiMessage("Created a new permission")
    public ResponseEntity<Permission> createPermission(@Valid @RequestBody Permission permission) {
        return ResponseEntity.status(HttpStatus.CREATED).body(permissionService.createPermission(permission));
    }

    @PutMapping("/permissions")
    @ApiMessage("Updated a permission")
    public ResponseEntity<Permission> updatePermission(@Valid @RequestBody Permission permission) throws IdInValidException {
        if(this.permissionService.fetchPermissionById(permission.getId()) == null) {
            throw new IdInValidException("Permission with Id: " + permission.getId() + "is not available");
        }
        return ResponseEntity.status(HttpStatus.OK).body(permissionService.updatePermission(permission));
    }

    @DeleteMapping("/permissions/{id}")
    @ApiMessage("Deleted a permission")
    public ResponseEntity<ResponseInfo<String>> deletePermission(@PathVariable("id") int id) throws IdInValidException {
        if(this.permissionService.fetchPermissionById(id) == null) {
            throw new IdInValidException("Permission with Id: " + id + "is not available");
        }
        this.permissionService.deletePermission(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseInfo<>("Deleted permission with Id: " + id));
    }

    @GetMapping("/permissions")
    public ResponseEntity<ResultPaginationDTO> fetchAllPermissions(@Filter Specification<Permission> specification,
                                                                   Pageable pageable) {


        return ResponseEntity.status(HttpStatus.OK).body(this.permissionService.getAllPermissions(specification, pageable));
    }



}
