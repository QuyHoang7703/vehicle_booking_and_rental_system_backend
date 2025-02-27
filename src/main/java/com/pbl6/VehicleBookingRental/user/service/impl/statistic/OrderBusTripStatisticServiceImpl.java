package com.pbl6.VehicleBookingRental.user.service.impl.statistic;

import com.pbl6.VehicleBookingRental.user.domain.BusinessPartner;
import com.pbl6.VehicleBookingRental.user.domain.Orders;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusTrip;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusTripSchedule;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.OrderBusTrip;
import com.pbl6.VehicleBookingRental.user.dto.Meta;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.statistic.ResultStatisticDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.bus.ResultStatisticBusTripDTO;
import com.pbl6.VehicleBookingRental.user.repository.OrdersRepo;
import com.pbl6.VehicleBookingRental.user.repository.busPartner.BusTripRepository;
import com.pbl6.VehicleBookingRental.user.repository.order.OrderBusTripRepository;
import com.pbl6.VehicleBookingRental.user.service.BusinessPartnerService;
import com.pbl6.VehicleBookingRental.user.service.statistic.OrderBusTripStatisticService;
import com.pbl6.VehicleBookingRental.user.service.statistic.StatisticService;
import com.pbl6.VehicleBookingRental.user.util.constant.OrderStatusEnum;
import com.pbl6.VehicleBookingRental.user.util.constant.PartnerTypeEnum;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderBusTripStatisticServiceImpl implements OrderBusTripStatisticService {
    private final OrderBusTripRepository orderBusTripRepository;
    private final BusinessPartnerService businessPartnerService;
    private final BusTripRepository busTripRepository;
    private final OrdersRepo ordersRepo;
    private final StatisticService statisticService;

    @Override
    public ResultStatisticDTO getOrderBusTripRevenueByPeriod(Integer year) throws ApplicationException {
        Map<String, Double> statistics = new LinkedHashMap<>();

        List<String> authorities =SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .toList();

        List<OrderBusTrip> orderBusTrips;

        if(authorities.contains("ROLE_ADMIN")) {
            // Get all order bus trip
            List<Orders> orders = this.ordersRepo.findByOrderType("BUS_TRIP_ORDER");
            orderBusTrips = orders.stream().map(Orders::getOrderBusTrip).toList();
        }else{
            BusinessPartner businessPartner = this.businessPartnerService.getCurrentBusinessPartner(PartnerTypeEnum.BUS_PARTNER);
            // Get order bus trip of bus partner has
            orderBusTrips = this.orderBusTripRepository.findOrderBusTripsOfBusPartner(businessPartner.getBusPartner().getId());
        }

        if(year != null) {
            this.getRevenueByMonth(statistics, year, orderBusTrips);
        }else{
            this.getRevenueByYear(statistics, orderBusTrips);
        }

        return this.statisticService.createResultStatisticDTO(statistics);
    }

    private void getRevenueByMonth(Map<String, Double> statistics, Integer year, List<OrderBusTrip> orderBusTrips) {
        // Create key in hash map
        for(int i = 1; i<=12; i++) {
            String key = i + "-" + year;
            statistics.put(key, 0.0);
        }

        // Calculate revenue for keys
        for(OrderBusTrip orderBusTrip : orderBusTrips) {
            Orders order = orderBusTrip.getOrder();
            if(order.getCancelTime() != null) {
                continue;
            }
            LocalDateTime createdOrderAt = order.getCreate_at().atZone(ZoneId.systemDefault()).toLocalDateTime();
            if(createdOrderAt.getYear() == year) {
                String key = createdOrderAt.getMonthValue() + "-" + createdOrderAt.getYear();
                double revenue = orderBusTrip.getPriceTotal();
                double currentRevenue = statistics.getOrDefault(key, 0.0);
                statistics.put(key, revenue+currentRevenue);
            }
        }
    }

    private void getRevenueByYear(Map<String, Double> statistics, List<OrderBusTrip> orderBusTrips) {
        for(OrderBusTrip orderBusTrip : orderBusTrips) {
            Orders order = orderBusTrip.getOrder();
            if(order.getCancelTime() != null) {
                continue;
            }
            LocalDateTime createdOrderAt = order.getCreate_at().atZone(ZoneId.systemDefault()).toLocalDateTime();
            String key = String.valueOf(createdOrderAt.getYear());
            double revenue = orderBusTrip.getPriceTotal();
            double currentRevenue = statistics.getOrDefault(key, 0.0);
            statistics.put(key, revenue+currentRevenue);
        }
    }



    @Override
    public ResultPaginationDTO getStatisticOfOrdersByDays(Pageable pageable,
                                                          LocalDate startDate,
                                                          LocalDate endDate,
                                                          String route,
                                                          Integer month,
                                                          Integer year) throws ApplicationException {
        BusinessPartner businessPartner = this.businessPartnerService.getCurrentBusinessPartner(PartnerTypeEnum.BUS_PARTNER);
        Page<BusTrip> busTripPage;
        if(route!=null && !route.isEmpty()) {
            List<String> province = Arrays.asList(route.split("-"));
            log.info("Province: " +  province);
            busTripPage = this.busTripRepository.findByBusPartner_IdAndDepartureLocationAndArrivalLocation(businessPartner.getBusPartner().getId(), province.get(0), province.get(1), pageable);
        }else{
            busTripPage = this.busTripRepository.findByBusPartner_Id(businessPartner.getBusPartner().getId(), pageable);
        }
        ResultPaginationDTO res = new ResultPaginationDTO();
        Meta meta = new Meta();
        meta.setCurrentPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(busTripPage.getTotalPages());
        meta.setTotal(busTripPage.getTotalElements());
        res.setMeta(meta);

        List<ResultStatisticBusTripDTO> resultStatisticBusTripDTOS = busTripPage.getContent().stream()
                .map(busTrip -> this.getStatisticFromBusTrip(busTrip, startDate, endDate, month, year))
                .toList();

        res.setResult(resultStatisticBusTripDTOS);

        return res;
    }




    private ResultStatisticBusTripDTO getStatisticFromBusTrip(BusTrip busTrip, LocalDate startDate, LocalDate endDate, Integer month, Integer year) {
        ResultStatisticBusTripDTO res = new ResultStatisticBusTripDTO();
        res.setRoute(busTrip.getDepartureLocation() + "-" + busTrip.getArrivalLocation());
        res.setSoldTickets(0);
        res.setCancelledTickets(0);
        List<BusTripSchedule> busTripSchedules = busTrip.getBusTripSchedules();
        for(BusTripSchedule busTripSchedule : busTripSchedules) {
            List<OrderBusTrip> orderBusTrips = busTripSchedule.getOrderBusTrips();
            for(OrderBusTrip orderBusTrip : orderBusTrips) {
                LocalDate orderDate = orderBusTrip.getOrder().getCreate_at().atZone(ZoneId.systemDefault()).toLocalDate();
                if(month != null && year!=null && orderDate.getMonthValue() == month && orderDate.getYear() == year) {
                    this.updateTicketStatistics(orderBusTrip, res);
                    continue;
                }
                if(month == null && year != null && orderDate.getYear() == year) {
                    this.updateTicketStatistics(orderBusTrip, res);
                    continue;
                }

                if(startDate != null && endDate != null && !orderDate.isBefore(startDate) && !orderDate.isAfter(endDate)) {
                    this.updateTicketStatistics(orderBusTrip, res);
                }
            }
        }

        return res;
    }

    private void updateTicketStatistics(OrderBusTrip orderBusTrip, ResultStatisticBusTripDTO resultStatisticBusTripDTO) {
        if(orderBusTrip.getStatus().equals(OrderStatusEnum.COMPLETED)){
            resultStatisticBusTripDTO.setSoldTickets(resultStatisticBusTripDTO.getSoldTickets()+ orderBusTrip.getNumberOfTicket());
        }
        else{
            resultStatisticBusTripDTO.setCancelledTickets(resultStatisticBusTripDTO.getSoldTickets()+ orderBusTrip.getNumberOfTicket());
        }
    }

}
