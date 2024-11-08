//package com.pbl6.VehicleBookingRental.user.domain.account;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Entity
//@Table(name = "role_permission")
//@AllArgsConstructor
//@NoArgsConstructor
//@Data
//public class RolePermission {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private long id;
//
//    @ManyToOne
//    @JoinColumn(name = "role_id")
//    private Role role;
//
//    @ManyToOne
//    @JoinColumn(name = "permission_id")
//    private Permission permission;
//
//
//}
