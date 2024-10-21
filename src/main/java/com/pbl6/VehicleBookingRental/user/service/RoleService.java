package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.domain.account.Role;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public interface RoleService {
    public List<String> getNameRolesByAccountID(int idAccount);
    Role fetchRoleById(int id);
    Role createRole(Role role);
    Role updateRole(Role role);
    ResultPaginationDTO getRoles(Specification<Role> spec, Pageable pageable);
    boolean existsByName(String name);
    List<GrantedAuthority> getAuthoritiesByRoleName(String roleName);
}
