package com.pbl6.VehicleBookingRental.user.repository.chat;

import com.pbl6.VehicleBookingRental.user.domain.chat.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversationRepo extends JpaRepository<Conversation,Integer> {
}
