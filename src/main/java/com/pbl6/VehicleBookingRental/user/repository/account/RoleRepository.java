package com.pbl6.VehicleBookingRental.user.repository.account;

import com.pbl6.VehicleBookingRental.user.domain.account.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer>, JpaSpecificationExecutor<Role> {
    Optional<Role> findByName(String name);
    List<Role> findByIdIn(List<Integer> ids);
    boolean existsByName(String name);
}
