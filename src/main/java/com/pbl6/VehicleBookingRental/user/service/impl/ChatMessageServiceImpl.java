package com.pbl6.VehicleBookingRental.user.service.impl;

import com.pbl6.VehicleBookingRental.user.domain.chat.Conversation;
import com.pbl6.VehicleBookingRental.user.domain.chat.ConversationAccount;
import com.pbl6.VehicleBookingRental.user.domain.chat.Message;
import com.pbl6.VehicleBookingRental.user.dto.chat_dto.MessageDTO;
import com.pbl6.VehicleBookingRental.user.repository.chat.ConversationAccountRepo;
import com.pbl6.VehicleBookingRental.user.repository.chat.ConversationRepo;
import com.pbl6.VehicleBookingRental.user.repository.chat.MessageRepo;
import com.pbl6.VehicleBookingRental.user.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatMessageServiceImpl implements ChatMessageService {
    @Autowired
    private ConversationRepo conversationRepo;
    @Autowired
    private  MessageRepo messageRepo;
    @Autowired
    private ConversationAccountRepo conversationAccountRepo;
    @Override
    public List<MessageDTO> getMessageByConservationAndSender(int conversation_id, int sender_id,String sender_type) {
        List<Message> messages = messageRepo.findMessagesByConversationAndSender(conversation_id,sender_id,sender_type);

        List<MessageDTO> messageDTOS = new ArrayList<>();
        if(messages !=null){
            for(Message message: messages){
                MessageDTO messageDTO = new MessageDTO();
                messageDTO.setId(message.getId());
                messageDTO.setSenderId(message.getSenderId());
                messageDTO.setContent(message.getContent());
                messageDTO.setSeen(message.isSeen());
                messageDTO.setSeen_at(message.getSeen_at());
                messageDTO.setSendAt(message.getSendAt());
                messageDTO.setConversation_id(message.getConversation().getId());

                messageDTOS.add(messageDTO);
            }
        }

        return messageDTOS;
    }

    @Override
    public List<Integer> getAccountConnected(int conversation_id, int account_id, String role_account) {
        List<ConversationAccount> conversationAccountList =
                conversationAccountRepo.getConnectedAccount(conversation_id,account_id,role_account);
        return Optional.ofNullable(conversationAccountList)
                .orElse(Collections.emptyList())
                .stream().map(conversationAccount -> conversationAccount.getAccount().getId())
                .collect(Collectors.toList());
    }
    @Override
    public MessageDTO lastMessageOfConversation(int conversation_id) {
        MessageDTO messageDTO = new MessageDTO();
        Message lastMessage = messageRepo.findLastMessageOfSenderId(conversation_id);
        if(lastMessage !=null){
            messageDTO.setId(lastMessage.getId());
            messageDTO.setSenderId(lastMessage.getSenderId());
            messageDTO.setSender_type(lastMessage.getSender_type());
            messageDTO.setContent(lastMessage.getContent());
            messageDTO.setSeen(lastMessage.isSeen());
            messageDTO.setSeen_at(lastMessage.getSeen_at());
            messageDTO.setSendAt(lastMessage.getSendAt());
            messageDTO.setConversation_id(lastMessage.getConversation().getId());
            messageDTO.setRecipientId(lastMessage.getRecipientId());
            messageDTO.setRecipient_type(lastMessage.getRecipient_type());
        }
        return messageDTO;
    }

    @Override
    public MessageDTO saveMessage(MessageDTO messageDTO) {
        Message message = new Message();
        message.setContent(messageDTO.getContent());
        message.setSendAt(messageDTO.getSendAt());
        message.setSeen_at(messageDTO.getSeen_at());
        message.setSeen(messageDTO.isSeen());
        message.setSender_type(messageDTO.getSender_type());
        message.setSenderId(messageDTO.getSenderId());
        message.setSender_type(messageDTO.getSender_type());
        message.setRecipientId(messageDTO.getRecipientId());
        message.setRecipient_type(messageDTO.getRecipient_type());

        Optional<Conversation> conversation = conversationRepo.findById(messageDTO.getConversation_id());
        if(conversation.isPresent()){
            message.setConversation(conversation.get());
        }

        try{
            messageRepo.save(message);
        }catch (Exception e){
            System.out.println(e.getLocalizedMessage());
        }
        return messageDTO;
    }

    @Override
    public MessageDTO updateMessage(MessageDTO messageDTO) {
        Optional<Message> message = messageRepo.findById(messageDTO.getId());
        if(message.isPresent()){
            try{
                message.get().setContent(messageDTO.getContent());
                message.get().setSendAt(messageDTO.getSendAt());
                message.get().setSeen_at(messageDTO.getSeen_at());
                message.get().setSeen(messageDTO.isSeen());
                message.get().setSender_type(messageDTO.getSender_type());
                message.get().setSenderId(messageDTO.getSenderId());
                message.get().setSender_type(messageDTO.getSender_type());
                message.get().setRecipientId(messageDTO.getRecipientId());
                message.get().setRecipient_type(messageDTO.getRecipient_type());

                Optional<Conversation> conversation = conversationRepo.findById(messageDTO.getConversation_id());
                if(conversation.isPresent()){
                    message.get().setConversation(conversation.get());
                }
                messageRepo.save(message.get());
            }catch (Exception e){
                System.out.println(e.getLocalizedMessage());
            }
        }
        return messageDTO;
    }
}
