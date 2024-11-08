package com.pbl6.VehicleBookingRental.user.controller;

import com.pbl6.VehicleBookingRental.user.domain.account.Role;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.service.RoleService;
import com.pbl6.VehicleBookingRental.user.util.annotation.ApiMessage;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
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
@RequestMapping("api/v1")
public class RoleController {
    private final RoleService roleService;

    @PostMapping("/roles")
    @ApiMessage("Create a role")
    public ResponseEntity<Role> create(@Valid @RequestBody Role role) throws IdInvalidException {
        // check name
        if (this.roleService.existsByName(role.getName())) {
            throw new IdInvalidException("Role với name = " + role.getName() + " đã tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService. createRole(role));
    }

    @PutMapping("/roles")
    @ApiMessage("Update a role")
    public ResponseEntity<Role> update(@Valid @RequestBody Role role) throws IdInvalidException {
        // check id
        if (this.roleService.fetchRoleById(role.getId()) == null) {
            throw new IdInvalidException("Role với id = " + role.getId() + " không tồn tại");
        }

        return ResponseEntity.ok().body(this.roleService.updateRole(role));
    }

    @GetMapping("/roles/{id}")
    @ApiMessage("Fetch role by id")
    public ResponseEntity<Role> getById(@PathVariable("id") int id) throws IdInvalidException {

        Role role = this.roleService.fetchRoleById(id);
        if (role == null) {
            throw new IdInvalidException("Resume với id = " + id + " không tồn tại");
        }

        return ResponseEntity.ok().body(role);
    }

    @GetMapping("/roles")
    @ApiMessage("Fetch roles")
    public ResponseEntity<ResultPaginationDTO> getPermissions(@Filter Specification<Role> specification
            , Pageable pageable) {

        return ResponseEntity.ok(this.roleService.getRoles(specification, pageable));
    }
}
