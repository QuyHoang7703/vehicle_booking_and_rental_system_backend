package com.pbl6.VehicleBookingRental.user.service.statistic;

import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.dto.ResultStatisticDTO;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;


public interface OrderBusTripStatisticService {
    ResultStatisticDTO getOrderBusTripRevenueByPeriod(Integer year) throws ApplicationException;
//    ResultStatisticDTO getOrderBusTripRevenueByYear() throws ApplicationException;
    ResultPaginationDTO getStatisticOfOrdersByDays(Pageable pageable,
                                                   LocalDate startDate,
                                                   LocalDate endDate,
                                                   String route,
                                                   Integer month,
                                                   Integer year) throws ApplicationException;
}
