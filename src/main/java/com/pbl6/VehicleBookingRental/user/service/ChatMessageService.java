package com.pbl6.VehicleBookingRental.user.service;

import com.nimbusds.jose.util.Pair;
import com.pbl6.VehicleBookingRental.user.dto.chat_dto.MessageDTO;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;

import java.util.List;

public interface ChatMessageService {
    //Get Message base on Conversation and Sender
    public List<MessageDTO> getMessageByConservationAndSender(int conversation_id, int sender_id, String sender_type);

    // Get Accounts are connected with account_id and Their last Message
    public List<Pair<Integer, String>>  getAccountConnected(int account_id, String role_account);
    public MessageDTO lastMessageOfConversation(int account_id);

    public MessageDTO saveMessage(MessageDTO messageDTO);
    public MessageDTO updateMessage(MessageDTO messageDTO);
    public boolean createConversation(int senderId,String senderType,int recipientId,String recipientType) ;
}
