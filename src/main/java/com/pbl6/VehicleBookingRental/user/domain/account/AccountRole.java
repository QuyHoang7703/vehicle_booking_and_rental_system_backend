package com.pbl6.VehicleBookingRental.user.domain.account;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Instant timeCancel;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Instant timeRegister;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String lockReason;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @PrePersist
    public void handleBeforeCreated(){

        this.timeRegister = Instant.now();
    }

    @PreUpdate
    public void handleBeforeModified(){
        this.timeCancel = Instant.now();
    }

}
