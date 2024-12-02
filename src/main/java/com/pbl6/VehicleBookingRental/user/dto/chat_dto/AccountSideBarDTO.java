package com.pbl6.VehicleBookingRental.user.dto.chat_dto;

import lombok.Data;

import java.time.Instant;

@Data
public class AccountSideBarDTO {
    private int accountId;
    private String nameRepresentation;
    private String businessName;
    private String roleAccount;
    private String lastMessage;
    private Instant sendAt;
    private String avatarUrl;
}
