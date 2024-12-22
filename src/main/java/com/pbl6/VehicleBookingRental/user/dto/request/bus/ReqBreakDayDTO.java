package com.pbl6.VehicleBookingRental.user.dto.request.bus;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.BreakDay;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqBreakDayDTO {
    private int busTripScheduleId;
    private List<BreakDay> breakDays;
}
