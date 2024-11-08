package com.pbl6.VehicleBookingRental.user.dto.chat_dto;

import com.pbl6.VehicleBookingRental.user.util.constant.NotificationTypeEnum;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO {
    private int id;
    private String title;
    private String message;
    private boolean isSeen;
    private Date create_at;
    private NotificationTypeEnum type;
}
