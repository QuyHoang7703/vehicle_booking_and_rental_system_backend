package com.pbl6.VehicleBookingRental.user.domain.account;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Date;

@Entity
@Table(name = "account_role")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AccountRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timeCancel;

    private Instant timeRegister;

    @PrePersist
    public void handleBeforeCreated(){

        this.timeRegister = Instant.now();
    }

}
