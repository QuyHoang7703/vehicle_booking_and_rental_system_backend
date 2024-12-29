package com.pbl6.VehicleBookingRental.user.service.impl.statistic;

import com.amazonaws.services.iotwireless.model.PartnerType;
import com.pbl6.VehicleBookingRental.user.domain.BankAccount;
import com.pbl6.VehicleBookingRental.user.domain.BusinessPartner;
import com.pbl6.VehicleBookingRental.user.domain.Orders;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.OrderBusTrip;
import com.pbl6.VehicleBookingRental.user.domain.car_rental.CarRentalOrders;
import com.pbl6.VehicleBookingRental.user.dto.Meta;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.bankAccount.ResBankAccountDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.statistic.CustomerStatisticDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.statistic.ResultStatisticDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.statistic.RevenueOfBusinessPartnerDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.statistic.RevenueStatisticDTO;
import com.pbl6.VehicleBookingRental.user.repository.BankAccountRepository;
import com.pbl6.VehicleBookingRental.user.repository.OrdersRepo;
import com.pbl6.VehicleBookingRental.user.repository.businessPartner.BusinessPartnerRepository;
import com.pbl6.VehicleBookingRental.user.repository.order.OrderBusTripRepository;
import com.pbl6.VehicleBookingRental.user.repository.vehicle_rental.VehicleRentalOrderRepo;
import com.pbl6.VehicleBookingRental.user.service.BankAccountService;
import com.pbl6.VehicleBookingRental.user.service.VehicleRentalStatisticService;
import com.pbl6.VehicleBookingRental.user.service.statistic.OrderBusTripStatisticService;
import com.pbl6.VehicleBookingRental.user.service.statistic.StatisticService;
import com.pbl6.VehicleBookingRental.user.util.CurrencyFormatterUtil;
import com.pbl6.VehicleBookingRental.user.util.constant.PartnerTypeEnum;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticServiceImpl implements StatisticService {
    private final OrderBusTripStatisticService orderBusTripStatisticService;
    private final VehicleRentalStatisticService vehicleRentalStatisticService;
    private final BusinessPartnerRepository businessPartnerRepository;
    private final BankAccountService bankAccountService;
    private final VehicleRentalOrderRepo vehicleRentalOrderRepo;
    private final OrderBusTripRepository orderBusTripRepository;

    public StatisticServiceImpl(@Lazy OrderBusTripStatisticService orderBusTripStatisticService,
                                @Lazy VehicleRentalStatisticService vehicleRentalStatisticService,
                                @Lazy BusinessPartnerRepository businessPartnerRepository,
                                @Lazy BankAccountService bankAccountService,
                                @Lazy VehicleRentalOrderRepo vehicleRentalOrderRepo,
                                @Lazy OrderBusTripRepository orderBusTripRepository) {
        this.orderBusTripStatisticService = orderBusTripStatisticService;
        this.vehicleRentalStatisticService = vehicleRentalStatisticService;
        this.businessPartnerRepository = businessPartnerRepository;
        this.bankAccountService = bankAccountService;
        this.vehicleRentalOrderRepo = vehicleRentalOrderRepo;
        this.orderBusTripRepository = orderBusTripRepository;

    }

    @Override
    public ResultStatisticDTO createResultStatisticDTO(Map<String, Double> statistics) {
        List<RevenueStatisticDTO> revenueStatisticDTOS = statistics.entrySet().stream()
                .map(entry -> new RevenueStatisticDTO(entry.getKey(), CurrencyFormatterUtil.formatToVND(entry.getValue())))
                .toList();

        Double totalRevenue = 0.0;
        for (Map.Entry<String, Double> entry : statistics.entrySet()) {
            totalRevenue += entry.getValue();
        }

        return ResultStatisticDTO.builder()
                .totalRevenue(CurrencyFormatterUtil.formatToVND(totalRevenue))
                .revenueStatistic(revenueStatisticDTOS)
                .build();
    }

    @Override
    public ResultStatisticDTO getRevenueByMonthOrByYear(Integer year) throws ApplicationException {
        ResultStatisticDTO statisticFromBusPartner = this.orderBusTripStatisticService.getOrderBusTripRevenueByPeriod(year);
        ResultStatisticDTO statisticFromCarRentalPartner = this.vehicleRentalStatisticService.calculateMonthlyRevenue(year);

        Map<String, Double> combinedStatistics = new LinkedHashMap<>();


        for (RevenueStatisticDTO statistic : statisticFromBusPartner.getRevenueStatistic()) {
            String key = statistic.getPeriod();
            String revenueString = statistic.getRevenue().split("VND")[0];
            revenueString = revenueString.replace(".", "");
            double revenue = Double.parseDouble(revenueString);
            combinedStatistics.put(key, combinedStatistics.getOrDefault(key, 0.0) + revenue);
        }

        for (RevenueStatisticDTO statistic : statisticFromCarRentalPartner.getRevenueStatistic()) {
            String key = statistic.getPeriod();
            String revenueString = statistic.getRevenue().split("VND")[0];
            revenueString = revenueString.replace(".", "");
            double revenue = Double.parseDouble(revenueString);
            combinedStatistics.put(key, combinedStatistics.getOrDefault(key, 0.0) + revenue);
        }

        return this.createResultStatisticDTO(combinedStatistics);
    }

    @Override
    public ResultPaginationDTO getRevenueOfBusinessPartner(Integer month, Integer year, PartnerTypeEnum partnerType, Pageable pageable) throws ApplicationException {
        ResultPaginationDTO res = new ResultPaginationDTO();

        Page<BusinessPartner> businessPartnerPage = this.businessPartnerRepository.findByPartnerType(partnerType, pageable);

        Meta meta = new Meta();
        meta.setCurrentPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(businessPartnerPage.getTotalPages());
        meta.setTotal(businessPartnerPage.getTotalElements());
        res.setMeta(meta);

        List<RevenueOfBusinessPartnerDTO> revenueOfBusinessPartnerDTOS = businessPartnerPage.getContent().stream()
                .map(businessPartner -> {
                    try {
                        return this.convertToRevenueOfBusinessPartnerDTO(businessPartner, month, year);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();

        res.setResult(revenueOfBusinessPartnerDTOS);

        return res;
    }



    private RevenueOfBusinessPartnerDTO convertToRevenueOfBusinessPartnerDTO(BusinessPartner businessPartner, Integer month, Integer year) throws Exception {
        ResBankAccountDTO resBankAccountDTO = this.bankAccountService.convertoResBankAccountDTO(businessPartner.getAccount().getId(), businessPartner.getPartnerType());
        String revenue = "";
        if (businessPartner.getPartnerType().equals(PartnerTypeEnum.BUS_PARTNER)) {
            List<OrderBusTrip> ordersOfBusiness = this.orderBusTripRepository.findOrderBusTripsOfBusPartner(businessPartner.getId());
            revenue = CurrencyFormatterUtil.formatToVND(this.calculateRevenueOfBusPartner(month, year, ordersOfBusiness));
        } else {
            List<CarRentalOrders> carRentalOrders = this.vehicleRentalOrderRepo.findCarRentalOrdersByCarRentalService_VehicleRegister_CarRentalPartner_Id(businessPartner.getCarRentalPartner().getId());
            revenue = CurrencyFormatterUtil.formatToVND(this.calculateRevenueOfCarRentalPartner(month, year, carRentalOrders));
        }

        RevenueOfBusinessPartnerDTO revenueOfBusinessPartnerDTO = RevenueOfBusinessPartnerDTO.builder()
                .businessPartnerId(businessPartner.getId())
                .businessName(businessPartner.getBusinessName())
                .email(businessPartner.getEmailOfRepresentative())
                .bankAccountNumber(resBankAccountDTO.getAccountNumber())
                .bankName(resBankAccountDTO.getBankName())
                .revenue(revenue)
                .build();

        return revenueOfBusinessPartnerDTO;
    }

    private Double calculateRevenueOfBusPartner(Integer month, Integer year, List<OrderBusTrip> orderBusTrips) {
        double sum = 0.0;
        for (OrderBusTrip orderBusTrip : orderBusTrips) {
            Orders order = orderBusTrip.getOrder();
            LocalDateTime orderDateTime = order.getCreate_at().atZone(ZoneId.systemDefault()).toLocalDateTime();
            if (month != null && year != null && orderDateTime.getMonthValue() == month && orderDateTime.getYear() == year) {
                sum += order.getOrderBusTrip().getPriceTotal();

            }
            if (month == null && year != null && orderDateTime.getYear() == year) {
                sum += order.getOrderBusTrip().getPriceTotal();
            }
        }
        return sum;
    }

    private Double calculateRevenueOfCarRentalPartner(Integer month, Integer year, List<CarRentalOrders> carRentalOrders) {
        double sum = 0.0;
        for (CarRentalOrders carRentalOrder : carRentalOrders) {
            Orders order = carRentalOrder.getOrder();
            LocalDateTime orderDateTime = order.getCreate_at().atZone(ZoneId.systemDefault()).toLocalDateTime();
            if (month != null && year != null && orderDateTime.getMonthValue() == month && orderDateTime.getYear() == year) {
                sum += carRentalOrder.getTotal() - carRentalOrder.getReservation_fee() - carRentalOrder.getCar_deposit();
            }
            if (month == null && year != null && orderDateTime.getYear() == year) {
                sum += carRentalOrder.getTotal() - carRentalOrder.getReservation_fee() - carRentalOrder.getCar_deposit();
            }
        }
            return sum;
    }

    @Override
    public ResultPaginationDTO getCustomerOfBusinessPartner(Integer month, Integer year, int businessPartnerId, Pageable pageable) throws IdInvalidException {
        ResultPaginationDTO res = new ResultPaginationDTO();
        BusinessPartner businessPartner = this.businessPartnerRepository.findById(businessPartnerId)
                .orElseThrow(()-> new IdInvalidException("Business partner not found"));
        if(businessPartner.getPartnerType().equals(PartnerTypeEnum.BUS_PARTNER)) {
            this.createResFromBusPartner(month, year, businessPartner.getBusPartner().getId(), pageable, res);
        }else{
            this.createResFromCarRentalPartner(month, year, businessPartner.getCarRentalPartner().getId(), pageable, res);
        }

        return res;
    }

    private void createResFromBusPartner(Integer month, Integer year, int busPartnerId, Pageable pageable, ResultPaginationDTO res)  {
        Page<OrderBusTrip> orderBusTripPage = this.orderBusTripRepository.findOrderBusTripsOfBusPartner(busPartnerId, month, year, pageable);

        Meta meta = new Meta();
        meta.setCurrentPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(orderBusTripPage.getTotalPages());
        meta.setTotal(orderBusTripPage.getTotalElements());
        res.setMeta(meta);

        List<CustomerStatisticDTO> customerStatisticDTOS = orderBusTripPage.getContent().stream()
                .map(orderBusTrip -> this.convertToCustomerStatisticDTO(orderBusTrip))
                .toList();

        res.setResult(customerStatisticDTOS);
    }

    private CustomerStatisticDTO convertToCustomerStatisticDTO(OrderBusTrip orderBusTrip) {
        Orders order = orderBusTrip.getOrder();
        return CustomerStatisticDTO.builder()
                .customerName(order.getCustomerName())
                .customerPhoneNumber(order.getCustomerPhoneNumber())
                .totalPrice(CurrencyFormatterUtil.formatToVND(orderBusTrip.getPriceTotal()))
                .orderDate(order.getCreate_at())
                .build();
    }

    private void createResFromCarRentalPartner(Integer month, Integer year, int carRentalPartnerId, Pageable pageable, ResultPaginationDTO res)  {
        Page<CarRentalOrders> carRentalOrdersPage = this.vehicleRentalOrderRepo.findCarRentalOrderByCarRentalPartner(carRentalPartnerId, month, year, pageable);

        Meta meta = new Meta();
        meta.setCurrentPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(carRentalOrdersPage.getTotalPages());
        meta.setTotal(carRentalOrdersPage.getTotalElements());
        res.setMeta(meta);

        List<CustomerStatisticDTO> customerStatisticDTOS = carRentalOrdersPage.getContent().stream()
                .map(carRentalOrder -> this.convertToCustomerStatisticDTO(carRentalOrder))
                .toList();

        res.setResult(customerStatisticDTOS);
    }

    private CustomerStatisticDTO convertToCustomerStatisticDTO(CarRentalOrders carRentalOrders) {
        Orders order = carRentalOrders.getOrder();
        return CustomerStatisticDTO.builder()
                .customerName(order.getCustomerName())
                .customerPhoneNumber(order.getCustomerPhoneNumber())
                .totalPrice(CurrencyFormatterUtil.formatToVND(carRentalOrders.getTotal()))
                .orderDate(order.getCreate_at())
                .build();
    }



}
