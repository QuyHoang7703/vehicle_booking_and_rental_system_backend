package com.pbl6.VehicleBookingRental.user.domain.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "role")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private Instant createAt;
    private Instant updateAt;

    public Role(String name) {
        this.name = name;
    }

    @PrePersist
    public void handleBeforeCreated(){

        this.createAt = Instant.now();
    }

    @PreUpdate
    public void handleBeforeUpdate(){

        this.updateAt = Instant.now();
    }
    @ToString.Exclude
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<AccountRole> accountRole;

    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="permission_role", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private List<Permission> permissions;

}
