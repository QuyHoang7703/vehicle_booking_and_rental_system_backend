package com.pbl6.VehicleBookingRental.user.domain.notification;

import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "account_notification")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class NotificationAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "is_seen")
    private boolean isSeen;

    // Many-to-One relationship với Notification
    @ManyToOne
    @JoinColumn(name = "notification_id")
    private Notification notification;
    // Many-to-One relationship với Notification
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
}
