package com.pbl6.VehicleBookingRental.user.domain;

import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "rating")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int ratingValue;
    @Column(columnDefinition = "MEDIUMTEXT")
    private String comment;
    private String orderType;
    private Instant createAt;
    private Instant updateAt;

    @PrePersist
    public void prePersist() {
        this.createAt = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updateAt = Instant.now();
    }

    @OneToOne
    @JoinColumn(name = "order_id")
    private Orders order; // Giả sử bạn đã có lớp Order

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
}

