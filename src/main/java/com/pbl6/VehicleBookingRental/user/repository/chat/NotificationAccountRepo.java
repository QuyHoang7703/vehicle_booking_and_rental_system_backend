package com.pbl6.VehicleBookingRental.user.repository.chat;

import com.pbl6.VehicleBookingRental.user.domain.notification.NotificationAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationAccountRepo extends JpaRepository<NotificationAccount,Integer> {
}
