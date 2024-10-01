package com.pbl6.VehicleBookingRental.user.domain.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "permission")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String api_path;
    private String method;

    @OneToMany(mappedBy = "permission", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<RolePermission> permissionRole;
}
