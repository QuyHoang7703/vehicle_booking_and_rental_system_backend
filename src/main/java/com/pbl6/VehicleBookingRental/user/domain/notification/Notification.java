package com.pbl6.VehicleBookingRental.user.domain.notification;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pbl6.VehicleBookingRental.user.util.constant.NotificationTypeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "notification")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 255)
    private String title;
    @Column(name="is_seen")
    private boolean isSeen;
    @Column(name="type")
    private NotificationTypeEnum type;
    @Column(columnDefinition = "TEXT")
    private String message;

    private String metadata;

    @Column(name = "create_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date create_at;
    @OneToMany(mappedBy = "notification")
    @JsonIgnore
    private List<NotificationAccount> notificationAccountList;
}
