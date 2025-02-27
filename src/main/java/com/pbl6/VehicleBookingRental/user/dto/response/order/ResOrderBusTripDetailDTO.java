package com.pbl6.VehicleBookingRental.user.dto.response.order;

import com.pbl6.VehicleBookingRental.user.dto.response.bus.ResBusTripScheduleDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.bus.ResBusTripScheduleDetailForAdminDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.bus.ResBusTripScheduleForAdminDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResOrderBusTripDetailDTO {
    private CustomerInfo customerInfo;
    private ResBusTripScheduleDTO.BusinessPartnerInfo businessPartnerInfo;
    private ResBusTripScheduleForAdminDTO.BusInfo busInfo;
    private ResOrderBusTripDTO.OrderInfo orderInfo;
    private ResOrderBusTripDTO.TripInfo tripInfo;
    private Instant cancelTime;
    private Integer cancelUserId;
//    private String key;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CustomerInfo {
        private String email;
        private String name;
        private String phoneNumber;
    }

}
