package com.pbl6.VehicleBookingRental.user.repository.chat;

import com.pbl6.VehicleBookingRental.user.domain.chat.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepo extends JpaRepository<Message,Integer> {
//    public List<Message> findMessageByConversationIdAndSenderIdAndSender_type(int conversation_id,int sender_id,String sender_type);
    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId AND m.senderId = :senderId AND m.sender_type = :senderType")
    public  List<Message> findMessagesByConversationAndSender(@Param("conversationId") int conversationId,
                                                                      @Param("senderId") int senderId,
                                                                      @Param("senderType") String senderType);

    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversation_id ORDER BY m.sendAt DESC LIMIT 1 " )
    public Message findLastMessageOfSenderId (@Param("conversation_id") int conversation_id);
}
