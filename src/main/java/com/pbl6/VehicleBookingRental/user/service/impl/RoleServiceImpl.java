package com.pbl6.VehicleBookingRental.user.service.impl;

import com.pbl6.VehicleBookingRental.user.domain.account.AccountRole;
import com.pbl6.VehicleBookingRental.user.domain.account.Permission;
import com.pbl6.VehicleBookingRental.user.domain.account.Role;
import com.pbl6.VehicleBookingRental.user.dto.Meta;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.repository.account.AccountRoleRepository;
import com.pbl6.VehicleBookingRental.user.repository.account.RoleRepository;
import com.pbl6.VehicleBookingRental.user.repository.permissionRole.PermissionRepository;
import com.pbl6.VehicleBookingRental.user.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final AccountRoleRepository accountRoleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public List<String> getNameRolesByAccountID(int idAccount) {
        List<AccountRole> accountRoles = this.accountRoleRepository.findByAccount_IdAndActive(idAccount, true);
        List<String> roles = accountRoles.stream().map(accountRole -> accountRole.getRole().getName())
                                                    .collect(Collectors.toList());
        return roles;
    }

    @Override
    public Role fetchRoleById(int id) {
        return this.roleRepository.findById(id).orElse(null);
    }

    @Override
    public Role createRole(Role role) {
        if(role.getPermissions() != null) {
            List<Integer> listIdsPermission = role.getPermissions().stream().map(Permission::getId)
                    .toList();
            List<Permission> permissions = this.permissionRepository.findByIdIn(listIdsPermission);
            role.setPermissions(permissions);

        }
        return this.roleRepository.save(role);
    }

    @Override
    public Role updateRole(Role role) {
        if(role.getPermissions() != null) {
            List<Integer> listIdsPermission = role.getPermissions().stream().map(Permission::getId)
                    .toList();
            List<Permission> permissions = this.permissionRepository.findByIdIn(listIdsPermission);
            role.setPermissions(permissions);

        }

        Role roleDb = this.fetchRoleById(role.getId());
        roleDb.setName(role.getName());
        roleDb.setPermissions(role.getPermissions());
        return this.roleRepository.save(roleDb);
    }

    @Override
    public ResultPaginationDTO getRoles(Specification<Role> specification, Pageable pageable) {
        Page<Role> rolePage = this.roleRepository.findAll(specification, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        Meta meta = new Meta();
        meta.setCurrentPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(rolePage.getTotalPages());
        meta.setTotal(rolePage.getTotalElements());

        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(rolePage.getContent());

        return resultPaginationDTO;
    }

    @Override
    public boolean existsByName(String name) {
        return this.roleRepository.existsByName(name);
    }

    @Override
    @Transactional
    public List<GrantedAuthority> getAuthoritiesByRoleName(String roleName) {
        Role role = this.roleRepository.findByName(roleName).orElse(null);
        if(role != null) {
            return role.getPermissions().stream().map(permission -> new SimpleGrantedAuthority(permission.getName()))
                    .collect(Collectors.toList());
        }
        return null;

    }

    @Override
    public List<String> getAllRoleNames() {
        return this.roleRepository.findAllRoleNames();
    }


}
