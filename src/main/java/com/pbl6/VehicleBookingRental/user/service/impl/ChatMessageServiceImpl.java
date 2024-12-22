package com.pbl6.VehicleBookingRental.user.service.impl;

import com.nimbusds.jose.util.Pair;
import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.domain.bookingcar.Driver;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusPartner;
import com.pbl6.VehicleBookingRental.user.domain.car_rental.CarRentalPartner;
import com.pbl6.VehicleBookingRental.user.domain.chat.Conversation;
import com.pbl6.VehicleBookingRental.user.domain.chat.ConversationAccount;
import com.pbl6.VehicleBookingRental.user.domain.chat.Message;
import com.pbl6.VehicleBookingRental.user.dto.chat_dto.AccountSideBarDTO;
import com.pbl6.VehicleBookingRental.user.dto.chat_dto.MessageDTO;
import com.pbl6.VehicleBookingRental.user.repository.account.AccountRepository;
import com.pbl6.VehicleBookingRental.user.repository.businessPartner.BusPartnerRepository;
import com.pbl6.VehicleBookingRental.user.repository.businessPartner.DriverRepository;
import com.pbl6.VehicleBookingRental.user.repository.chat.ConversationAccountRepo;
import com.pbl6.VehicleBookingRental.user.repository.chat.ConversationRepo;
import com.pbl6.VehicleBookingRental.user.repository.chat.MessageRepo;
import com.pbl6.VehicleBookingRental.user.repository.vehicle_rental.CarRentalPartnerRepo;
import com.pbl6.VehicleBookingRental.user.service.ChatMessageService;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatMessageServiceImpl implements ChatMessageService {
    @Autowired
    private ConversationRepo conversationRepo;
    @Autowired
    private  MessageRepo messageRepo;
    @Autowired
    private ConversationAccountRepo conversationAccountRepo;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CarRentalPartnerRepo carRentalPartnerRepo;
    @Autowired
    private BusPartnerRepository busPartnerRepository;
    @Autowired
    private DriverRepository driverRepository;
    @Override
    public List<MessageDTO> getMessageByConservationAndSender(int conversation_id, int sender_id,String sender_type) {
        List<Message> messages = messageRepo.findMessagesByConversationAndSender(conversation_id,sender_id,sender_type);

        List<MessageDTO> messageDTOS = new ArrayList<>();
        if(messages !=null){
            for(Message message: messages){
                MessageDTO messageDTO = new MessageDTO();
                messageDTO.setId(message.getId());
                messageDTO.setSenderId(message.getSenderId());
                messageDTO.setSender_type(message.getSender_type());
                messageDTO.setContent(message.getContent());
                messageDTO.setSeen(message.isSeen());
                messageDTO.setSeen_at(message.getSeen_at() != null ? message.getSeen_at().toInstant() : null);
                messageDTO.setSendAt(message.getSendAt() != null ? message.getSendAt().toInstant() : null);
                messageDTO.setConversation_id(message.getConversation().getId());

                messageDTOS.add(messageDTO);
            }
        }

        return messageDTOS;
    }

    @Override
    public List<AccountSideBarDTO> getAccountConnected(int account_id, String role_account) {
        List<ConversationAccount> connectedAccount = new ArrayList<>();
        // Find conversation relate account_id
        List<ConversationAccount> conversationByAccountId = conversationAccountRepo.getConversationAccountByAccount(account_id,role_account);

        // Each conversation , get connected account
        if(conversationByAccountId != null){
            for(ConversationAccount i : conversationByAccountId){
                List<ConversationAccount> conversationAccountList =
                        conversationAccountRepo.getConnectedAccountWithConversation
                                (i.getConversation().getId(),account_id,role_account);
                connectedAccount.addAll(conversationAccountList);
            }
        }

        return Optional.ofNullable(connectedAccount)
                .orElse(Collections.emptyList())
                .stream().map(conversationAccount -> {
                    AccountSideBarDTO sideBarDTO = new AccountSideBarDTO();
                    try{
                        //get information account
                        int accountId = conversationAccount.getAccount().getId();
                        String roleAccount = conversationAccount.getRoleAccount();
                        sideBarDTO.setAccountId(accountId);
                        sideBarDTO.setRoleAccount(roleAccount);
                        sideBarDTO.setConversationId(conversationAccount.getConversation().getId());
                        switch (roleAccount){
                            case "CAR_RENTAL_PARTNER":
                            {
                                Optional<CarRentalPartner> carRentalPartner = carRentalPartnerRepo.findCarRentalPartnerByBusinessPartner_AccountId
                                        (accountId);
                                if(carRentalPartner.isPresent()){
                                    sideBarDTO.setNameRepresentation(carRentalPartner.get().getBusinessPartner().getBusinessName());
                                    sideBarDTO.setBusinessName(carRentalPartner.get().getBusinessPartner().getNameOfRepresentative());
                                    sideBarDTO.setAvatarUrl(carRentalPartner.get().getBusinessPartner().getAvatar());
                                }
                            };break;
                            case "BUS_PARTNER":
                            {
                                Optional<BusPartner> busPartner = busPartnerRepository.findBusPartnerByBusinessPartner_AccountId(accountId);
                                if(busPartner.isPresent()){
                                    sideBarDTO.setNameRepresentation(busPartner.get().getBusinessPartner().getBusinessName());
                                    sideBarDTO.setBusinessName(busPartner.get().getBusinessPartner().getNameOfRepresentative());
                                    sideBarDTO.setAvatarUrl(busPartner.get().getBusinessPartner().getAvatar());
                                }
                            };break;
                            case "DRIVER":
                            {
                                Optional<Driver> driver = driverRepository.findDriverByAccountId(accountId);
                                if(driver.isPresent()){
                                    sideBarDTO.setNameRepresentation(driver.get().getNameOfRelative());
                                    sideBarDTO.setAvatarUrl(driver.get().getAccount().getAvatar());
                                }
                            };break;
                            default:{
                                Optional<Account> account = accountRepository.findById(accountId);
                                if(account.isPresent()){
                                    sideBarDTO.setNameRepresentation(account.get().getName());
                                    sideBarDTO.setAvatarUrl(account.get().getAvatar());
                                }
                            }
                        }
                        //get last message and SendAt in conversation
                        MessageDTO lastMessage = lastMessageOfConversation(conversationAccount.getConversation().getId());
                        if (lastMessage != null) {
                            sideBarDTO.setLastMessageId(lastMessage.getId());
                            // Cập nhật thông tin `sendAt`
                            sideBarDTO.setSendAt(lastMessage.getSendAt());
                            sideBarDTO.setSeen(lastMessage.isSeen());
                            // Kiểm tra xem tin nhắn cuối có phải do tài khoản này gửi không
                            if (accountId == lastMessage.getSenderId() && roleAccount.equals(lastMessage.getSender_type())) {
                                sideBarDTO.setLastMessage(lastMessage.getContent());
                            } else {
                                sideBarDTO.setLastMessage("Bạn: " + lastMessage.getContent());
                            }
                        } else {
                            // If no message
                            sideBarDTO.setLastMessage("No messages yet");
                            sideBarDTO.setSendAt(null);
                        }
                    }catch (Exception e){
                        System.out.println(e.getLocalizedMessage());
                    }

                    return sideBarDTO;
                } )
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
            messageDTO.setSeen_at(lastMessage.getSeen_at() != null ? lastMessage.getSeen_at().toInstant() : null);
            messageDTO.setSendAt(lastMessage.getSendAt() != null ? lastMessage.getSendAt().toInstant() : null);
            messageDTO.setConversation_id(lastMessage.getConversation().getId());
        }
        return messageDTO;
    }

    @Override
    public MessageDTO saveMessage(MessageDTO messageDTO) {
        Message message = new Message();
        message.setContent(messageDTO.getContent());
        message.setSendAt(
                Optional.ofNullable(messageDTO.getSendAt())
                .map(instant -> new Date(instant.toEpochMilli()))
                .orElse(null));
        message.setSeen_at(
                Optional.ofNullable(messageDTO.getSeen_at())
                .map(instant -> new Date(instant.toEpochMilli()))
                .orElse(null));
        message.setSeen(messageDTO.isSeen());
        message.setSender_type(messageDTO.getSender_type());
        message.setSenderId(messageDTO.getSenderId());

        Optional<Conversation> conversation = conversationRepo.findById(messageDTO.getConversation_id());
        if(conversation.isPresent()){
            message.setConversation(conversation.get());
        }

        try{
            Message savedMessage = messageRepo.save(message);
            messageDTO.setId(savedMessage.getId());
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
                message.get().setSendAt(
                        Optional.ofNullable(messageDTO.getSendAt())
                                .map(instant -> new Date(instant.toEpochMilli()))
                                .orElse(null));
                message.get().setSeen_at(
                        Optional.ofNullable(messageDTO.getSeen_at())
                                .map(instant -> new Date(instant.toEpochMilli()))
                                .orElse(null));
                message.get().setSeen(messageDTO.isSeen());
                message.get().setSender_type(messageDTO.getSender_type());
                message.get().setSenderId(messageDTO.getSenderId());

                Optional<Conversation> conversation = conversationRepo.findById(messageDTO.getConversation_id());
                if(conversation.isPresent()){
                    message.get().setConversation(conversation.get());
                    //Get recipient
                    List<ConversationAccount> recipient = conversationAccountRepo.getConnectedAccountWithConversation
                            (conversation.get().getId(), messageDTO.getSenderId(), messageDTO.getSender_type());
                    messageDTO.setRecipientId(recipient.get(0).getAccount().getId());
                    messageDTO.setRecipient_type(recipient.get(0).getRoleAccount());
                }
                Message savedMessage = messageRepo.save(message.get());
                messageDTO.setId(savedMessage.getId());
            }catch (Exception e){
                System.out.println(e.getLocalizedMessage());
            }
        }
        return messageDTO;
    }
    @Override
    public boolean updateMessageIndependent(MessageDTO messageDTO) {
        Optional<Message> message = messageRepo.findById(messageDTO.getId());
        if(message.isPresent()){
            try{
                // Cập nhật chỉ khi giá trị mới có
                if (messageDTO.getContent() != null) {
                    message.get().setContent(messageDTO.getContent());
                }

                if (messageDTO.isSeen() != message.get().isSeen()) {
                    message.get().setSeen(messageDTO.isSeen());
                }

                if (messageDTO.getSendAt() != null) {
                    message.get().setSendAt(
                            new Date(messageDTO.getSendAt().toEpochMilli()));
                }

                if (messageDTO.getSeen_at() != null) {
                    message.get().setSeen_at(
                            new Date(messageDTO.getSeen_at().toEpochMilli()));
                }

                // Cập nhật senderType và senderId chỉ khi có sự thay đổi
                if (messageDTO.getSender_type() != null) {
                    message.get().setSender_type(messageDTO.getSender_type());
                }

                if (messageDTO.getSenderId() != 0) {
                    message.get().setSenderId(messageDTO.getSenderId());
                }

                // Cập nhật Conversation nếu có
                Optional<Conversation> conversation = conversationRepo.findById(messageDTO.getConversation_id());
                if(conversation.isPresent()){
                    message.get().setConversation(conversation.get());
                }

                messageRepo.save(message.get());
                return true;
            } catch (Exception e) {
                System.out.println(e.getLocalizedMessage());
                return false;
            }
        }
        return false;
    }

    @Override
    @Transactional
    public Integer createConversation(int senderId, String senderType, int recipientId, String recipientType) {
        try {
            Optional<Integer> conversationId = conversationAccountRepo.findExistingConversation(senderId,senderType,recipientId,recipientType);
            if(conversationId.isPresent()){
                return conversationId.get();
            }
            // Create conversation
            Conversation conversation = new Conversation();
            conversation.setCreateAt(new Date());
            Conversation savedConversation = conversationRepo.save(conversation);

            // Find accounts
            List<Account> accounts = accountRepository.findAllById(Arrays.asList(senderId, recipientId));
            if (accounts.size() != 2) {
                throw new ApplicationException("One or both accounts not found!");
            }

            Account accountSender = accounts.stream().filter(acc -> acc.getId() == senderId).findFirst().orElseThrow(() -> new ApplicationException("Sender not found!"));
            Account accountRecipient = accounts.stream().filter(acc -> acc.getId() == recipientId).findFirst().orElseThrow(() -> new ApplicationException("Recipient not found!"));

            // Create conversation accounts
            ConversationAccount conversationAccount1 = new ConversationAccount();
            conversationAccount1.setAccount(accountSender);
            conversationAccount1.setRoleAccount(senderType);
            conversationAccount1.setConversation(savedConversation);

            ConversationAccount conversationAccount2 = new ConversationAccount();
            conversationAccount2.setAccount(accountRecipient);
            conversationAccount2.setRoleAccount(recipientType);
            conversationAccount2.setConversation(savedConversation);

            // Save conversation accounts
            conversationAccountRepo.saveAll(Arrays.asList(conversationAccount1, conversationAccount2));

            return savedConversation.getId();
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            return null;
        }
    }

    @Override
    public List<MessageDTO> getMessagesByConversationId(int conversationId) {
        List<Message> messages = messageRepo.getMessagesByConversationId(conversationId);
        return Optional.ofNullable(messages)
                .orElse(Collections.emptyList())
                .stream().map(message -> {
                    MessageDTO messageDTO = new MessageDTO();
                    messageDTO.setId(message.getId());
                    messageDTO.setSenderId(message.getSenderId());
                    messageDTO.setSender_type(message.getSender_type());
                    messageDTO.setContent(message.getContent());
                    messageDTO.setSeen(message.isSeen());
                    messageDTO.setSeen_at(message.getSeen_at() != null ? message.getSeen_at().toInstant() : null);
                    messageDTO.setSendAt(message.getSendAt() != null ? message.getSendAt().toInstant() : null);
                    messageDTO.setConversation_id(message.getConversation().getId());
                    return messageDTO;
                }).collect(Collectors.toList());
    }
    @Override
    public boolean updateUnseenMessages(int senderId,int conversationId,String senderType) {
        try {
            // Tìm các message chưa seen của người gửi
            List<Message> unseenMessages = messageRepo.findUnseenMessagesBySenderAndConversation(senderId,conversationId,senderType);

            if (!unseenMessages.isEmpty()) {
                // Cập nhật trạng thái seen cho từng message
                for (Message message : unseenMessages) {
                    message.setSeen(true);
                    message.setSeen_at(new Date()); // Ghi lại thời gian đã seen
                }

                // Lưu lại tất cả thay đổi
                messageRepo.saveAll(unseenMessages);
                return true;
            }
            return false; // Không có message nào cần cập nhật
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            return false;
        }
    }

    @Override
    public MessageDTO getMsgById(int msgID) {
        Message message = messageRepo.findById(msgID).orElse(null);
        MessageDTO messageDTO = new MessageDTO();
        if(message != null){
            messageDTO.setId(message.getId());
            messageDTO.setSenderId(message.getSenderId());
            messageDTO.setSender_type(message.getSender_type());
            messageDTO.setContent(message.getContent());
            messageDTO.setSeen(message.isSeen());
            messageDTO.setSeen_at(message.getSeen_at() != null ? message.getSeen_at().toInstant() : null);
            messageDTO.setSendAt(message.getSendAt() != null ? message.getSendAt().toInstant() : null);
            messageDTO.setConversation_id(message.getConversation().getId());
        }
        return messageDTO;
    }

}
