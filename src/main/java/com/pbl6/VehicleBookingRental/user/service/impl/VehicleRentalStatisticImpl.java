package com.pbl6.VehicleBookingRental.user.service.impl;

import com.pbl6.VehicleBookingRental.user.domain.BusinessPartner;
import com.pbl6.VehicleBookingRental.user.domain.Orders;
import com.pbl6.VehicleBookingRental.user.domain.car_rental.CarRentalOrders;
import com.pbl6.VehicleBookingRental.user.dto.ResultStatisticDTO;
import com.pbl6.VehicleBookingRental.user.dto.car_rental_DTO.VehicleRentalStatisticDTO;
import com.pbl6.VehicleBookingRental.user.interfaces.VehicleRegisterInterface;
import com.pbl6.VehicleBookingRental.user.repository.OrdersRepo;
import com.pbl6.VehicleBookingRental.user.repository.vehicle_rental.VehicleRentalOrderRepo;
import com.pbl6.VehicleBookingRental.user.repository.vehicle_rental.VehicleRentalServiceRepo;
import com.pbl6.VehicleBookingRental.user.repository.vehicle_rental.VehicleTypeRepository;
import com.pbl6.VehicleBookingRental.user.service.BusinessPartnerService;
import com.pbl6.VehicleBookingRental.user.service.VehicleRentalStatisticService;
import com.pbl6.VehicleBookingRental.user.service.statistic.StatisticService;
import com.pbl6.VehicleBookingRental.user.util.DateUtil;
import com.pbl6.VehicleBookingRental.user.util.constant.PartnerTypeEnum;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
@Slf4j
public class VehicleRentalStatisticImpl implements VehicleRentalStatisticService {
    @Autowired
    private  VehicleRentalOrderRepo vehicleRentalOrderRepo;
    @Autowired
    private VehicleTypeRepository vehicleTypeRepository;
    @Autowired
    private VehicleRegisterInterface vehicleRegisterInterface;
    @Autowired
    private BusinessPartnerService businessPartnerService;
    @Autowired
    private DateUtil dateUtil;
    @Autowired
    private OrdersRepo ordersRepo;
    @Autowired
    private StatisticService statisticService;

    @Override
    public List<VehicleRentalStatisticDTO> statisticFromLocationOrVehicleTypeByDate(
            String location, String vehicleType, String startDate, String endDate) {
        List<VehicleRentalStatisticDTO> vehicleRentalStatistics = new ArrayList<>();
        List<String> distinctVehicleTypes = vehicleTypeRepository.findDistinctNames();
        List<String> distinctLocations = vehicleRegisterInterface.getExistFilterValue("location");

        // Nếu cả location và vehicleType đều là "all", lặp qua cả hai danh sách
        if ("all".equalsIgnoreCase(location) || "all".equalsIgnoreCase(vehicleType)) {
            List<String> selectedLocations = "all".equalsIgnoreCase(location) ? distinctLocations : List.of(location);
            List<String> selectedVehicleTypes = "all".equalsIgnoreCase(vehicleType) ? distinctVehicleTypes : List.of(vehicleType);

            for (String loc : selectedLocations) {
                for (String type : selectedVehicleTypes) {
                    vehicleRentalStatistics.addAll(statisticByDate(loc, type, startDate, endDate));
                }
            }
            return vehicleRentalStatistics;
        }

        // Nếu không phải là "all", xử lý từng trường hợp cụ thể
        return statisticByDate(location, vehicleType, startDate, endDate);
    }


    @Override
    public List<VehicleRentalStatisticDTO> statisticByDate(
            String location, String vehicleTypeName, String startDate, String endDate) {
        try {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy").withZone(ZoneId.systemDefault());
            Instant startDateInstant = Instant.from(dateTimeFormatter.parse(startDate));
            Instant endDateInstant = Instant.from(dateTimeFormatter.parse(endDate));

            List<LocalDate> dates = dateUtil.getDaysBetweenDates(startDateInstant, endDateInstant);
            BusinessPartner currentPartner = businessPartnerService.getCurrentBusinessPartner(PartnerTypeEnum.CAR_RENTAL_PARTNER);

            return getRentalVehicleByDate(location, vehicleTypeName, dates, currentPartner.getCarRentalPartner().getId());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Expected format: HH:mm dd-MM-yyyy", e);
        } catch (Exception e) {
            throw new RuntimeException("Error during statistic calculation", e);
        }
    }

    private List<VehicleRentalStatisticDTO> getRentalVehicleByDate(
            String location, String vehicleTypeName, List<LocalDate> dates, int partnerId) {

        List<CarRentalOrders> carRentalOrders = vehicleRentalOrderRepo.findCarRentalOrdersByLocationOrType(location, vehicleTypeName, partnerId);
        Map<LocalDate, VehicleRentalStatisticDTO> statisticsByDate = new HashMap<>();

        for (LocalDate date : dates) {
            statisticsByDate.putIfAbsent(date, createEmptyStatistic(date, location, vehicleTypeName));

            for (CarRentalOrders order : carRentalOrders) {
                if (isOrderWithinDate(order, date)) {
                    VehicleRentalStatisticDTO dto = statisticsByDate.get(date);
                    dto.setVehicle_type(order.getCarRentalService().getVehicleRegister().getVehicleType().getName());
                    dto.setLocation(order.getCarRentalService().getVehicleRegister().getLocation());

                    if (order.getStatus().equalsIgnoreCase("canceled")) {
                        dto.setCanceledVehicleAmount(dto.getCanceledVehicleAmount() + order.getAmount());
                    } else {
                        dto.setVehicleRentalAmount(dto.getVehicleRentalAmount() + order.getAmount());
                    }
                }
            }
        }

        return new ArrayList<>(statisticsByDate.values());
    }

    private boolean isOrderWithinDate(CarRentalOrders order, LocalDate date) {
        LocalDate startDate = order.getStart_rental_time().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endDate = order.getEnd_rental_time().atZone(ZoneId.systemDefault()).toLocalDate();
        return (date.equals(startDate) || date.equals(endDate) || (date.isAfter(startDate) && date.isBefore(endDate)));
    }

    private VehicleRentalStatisticDTO createEmptyStatistic(LocalDate date, String location, String vehicleTypeName) {
        VehicleRentalStatisticDTO statistic = new VehicleRentalStatisticDTO();
        statistic.setDate(date);
        statistic.setLocation(location);
        statistic.setVehicle_type(vehicleTypeName);
        statistic.setVehicleRentalAmount(0);
        statistic.setCanceledVehicleAmount(0);
        return statistic;
    }

    @Override
    public List<VehicleRentalStatisticDTO> statisticFromLocationOrVehicleTypeByMonthAndYear(
            String location, String vehicleType, int month, int year) {
        List<VehicleRentalStatisticDTO> vehicleRentalStatistics = new ArrayList<>();
        List<String> distinctVehicleTypes = vehicleTypeRepository.findDistinctNames();
        List<String> distinctLocations = vehicleRegisterInterface.getExistFilterValue("location");


        // Nếu cả location và vehicleType đều là "all", lặp qua cả hai danh sách
        if ("all".equalsIgnoreCase(location) || "all".equalsIgnoreCase(vehicleType)) {
            List<String> selectedLocations = "all".equalsIgnoreCase(location) ? distinctLocations : List.of(location);
            List<String> selectedVehicleTypes = "all".equalsIgnoreCase(vehicleType) ? distinctVehicleTypes : List.of(vehicleType);

            for (String loc : selectedLocations) {
                for (String type : selectedVehicleTypes) {
                    vehicleRentalStatistics.add(statisticByMonthAndYear(loc, type, month, year));
                }
            }
            return vehicleRentalStatistics;
        }

        // Nếu không phải "all", chỉ xử lý từng trường hợp cụ thể
        vehicleRentalStatistics.add(statisticByMonthAndYear(location, vehicleType, month, year));
        return vehicleRentalStatistics;
    }
    public VehicleRentalStatisticDTO statisticByMonthAndYear(
            String location, String vehicleTypeName, int month, int year) {
        try {
            YearMonth yearMonth = YearMonth.of(year, month);
            BusinessPartner currentBusinessPartner = businessPartnerService.getCurrentBusinessPartner(PartnerTypeEnum.CAR_RENTAL_PARTNER);

            return getRentalVehicleBySpecificMonth(location, vehicleTypeName, yearMonth, currentBusinessPartner.getCarRentalPartner().getId());
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            return null;
        }
    }
    public  VehicleRentalStatisticDTO getRentalVehicleBySpecificMonth(
            String location, String vehicleTypeName, YearMonth yearMonth, int carRentalPartnerId) {

        // Lấy danh sách đơn hàng thuê xe
        List<CarRentalOrders> carRentalOrders = vehicleRentalOrderRepo.findCarRentalOrdersByLocationOrType(location, vehicleTypeName, carRentalPartnerId);

        int totalAmountInMonth = 0;
        int totalCancelAmountInMonth = 0;

        LocalDate firstDayOfMonth = yearMonth.atDay(1);
        LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();

        for (CarRentalOrders order : carRentalOrders) {
            LocalDate startDate = order.getStart_rental_time().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate endDate = order.getEnd_rental_time().atZone(ZoneId.systemDefault()).toLocalDate();

            // Kiểm tra đơn hàng thuộc tháng và năm
            if ((startDate.isBefore(lastDayOfMonth) && endDate.isAfter(firstDayOfMonth)) ||
                    (startDate.equals(firstDayOfMonth) || endDate.equals(lastDayOfMonth))) {

                if (order.getStatus().equalsIgnoreCase("canceled")) {
                    totalCancelAmountInMonth += order.getAmount();
                } else {
                    totalAmountInMonth += order.getAmount();
                }
            }
        }

        // Tạo và trả về kết quả thống kê cho tháng
        VehicleRentalStatisticDTO vehicleRentalStatisticDTO = new VehicleRentalStatisticDTO();
        vehicleRentalStatisticDTO.setDate(firstDayOfMonth); // Gán ngày đầu tiên của tháng
        vehicleRentalStatisticDTO.setVehicleRentalAmount(totalAmountInMonth);
        vehicleRentalStatisticDTO.setCanceledVehicleAmount(totalCancelAmountInMonth);

        vehicleRentalStatisticDTO.setLocation(location);
        vehicleRentalStatisticDTO.setVehicle_type(vehicleTypeName);

        return vehicleRentalStatisticDTO;
    }
    @Override
    public List<VehicleRentalStatisticDTO> statisticFromLocationOrVehicleTypeByYear(String location, String vehicleType, List<Integer> year) throws ApplicationException {
        BusinessPartner businessPartner = this.businessPartnerService.getCurrentBusinessPartner(PartnerTypeEnum.CAR_RENTAL_PARTNER);

        List<VehicleRentalStatisticDTO> vehicleRentalStatistics = new ArrayList<>();
        List<String> distinctVehicleType = vehicleTypeRepository.findDistinctNames();
        List<String> distinctLocation = vehicleRegisterInterface.getExistFilterValue("location");
        // Nếu cả location và vehicleType đều là "all", lặp qua cả hai danh sách
        if ("all".equalsIgnoreCase(location) || "all".equalsIgnoreCase(vehicleType)) {
            List<String> selectedLocations = "all".equalsIgnoreCase(location) ? distinctLocation : List.of(location);
            List<String> selectedVehicleTypes = "all".equalsIgnoreCase(vehicleType) ? distinctVehicleType : List.of(vehicleType);

            for (String loc : selectedLocations) {
                for (String type : selectedVehicleTypes) {
                    vehicleRentalStatistics.addAll(getRentalVehicleByYear(loc, type, year,businessPartner.getCarRentalPartner().getId()));
                }
            }
            return vehicleRentalStatistics;
        }
        return getRentalVehicleByYear(location,vehicleType,year,businessPartner.getCarRentalPartner().getId());
    }



    public List<VehicleRentalStatisticDTO> getRentalVehicleByYear(String location, String vehicleTypeName, List<Integer> years, int carRentalPartnerId) {

        // Lấy tất cả các đơn hàng thuê xe liên quan
        List<CarRentalOrders> carRentalOrders = vehicleRentalOrderRepo.findCarRentalOrdersByLocationOrType(location, vehicleTypeName, carRentalPartnerId);

        List<VehicleRentalStatisticDTO> vehicleRentalStatisticDTOS = new ArrayList<>();

        // Duyệt qua từng năm để thống kê
        for (Integer year : years) {
            int totalAmountInYear = 0;
            int totalCancelAmountInYear = 0;

            for (CarRentalOrders order : carRentalOrders) {
                // Lấy ngày bắt đầu và kết thúc của đơn hàng
                LocalDate startDate = order.getStart_rental_time().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate endDate = order.getEnd_rental_time().atZone(ZoneId.systemDefault()).toLocalDate();

                // Kiểm tra nếu đơn hàng thuộc trong năm
                if ((startDate.getYear() == year || endDate.getYear() == year) ||
                        (startDate.isBefore(LocalDate.of(year, 12, 31)) && endDate.isAfter(LocalDate.of(year, 1, 1)))) {

                    if (order.getStatus().equalsIgnoreCase("canceled")) {
                        totalCancelAmountInYear += order.getAmount();
                    } else {
                        totalAmountInYear += order.getAmount();
                    }
                }
            }

            // Tạo DTO kết quả
            VehicleRentalStatisticDTO vehicleRentalStatisticDTO = new VehicleRentalStatisticDTO();
            vehicleRentalStatisticDTO.setDate(LocalDate.of(year, 1, 1)); // Gán ngày đại diện là ngày đầu tiên của năm
            vehicleRentalStatisticDTO.setVehicleRentalAmount(totalAmountInYear);
            vehicleRentalStatisticDTO.setCanceledVehicleAmount(totalCancelAmountInYear);

            vehicleRentalStatisticDTO.setLocation(location);
            vehicleRentalStatisticDTO.setVehicle_type(vehicleTypeName);

            vehicleRentalStatisticDTOS.add(vehicleRentalStatisticDTO);
        }

        return vehicleRentalStatisticDTOS;
    }

    @Override
    public ResultStatisticDTO calculateMonthlyRevenue(Integer year) throws ApplicationException {
        Map<String, Double> statistic = new LinkedHashMap<>();

        List<String> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .toList();

        List<CarRentalOrders> carRentalOrders;

        if(authorities.contains("ROLE_ADMIN")) {
            // Get all car rental order
            List<Orders> orders = this.ordersRepo.findByOrderType("VEHICLE_RENTAL_ORDER");
            carRentalOrders = orders.stream().map(Orders::getCarRentalOrders).toList();
        }else{
            BusinessPartner businessPartner = this.businessPartnerService.getCurrentBusinessPartner(PartnerTypeEnum.CAR_RENTAL_PARTNER);
            carRentalOrders = this.vehicleRentalOrderRepo.findCarRentalOrdersByCarRentalService_VehicleRegister_CarRentalPartner_Id(businessPartner.getCarRentalPartner().getId());
        }

        if(year!=null) {
            this.getMonthlyRevenue(statistic, carRentalOrders, year);

        }else{
            this.getYearlyRevenue(statistic, carRentalOrders);
        }
        return this.statisticService.createResultStatisticDTO(statistic);
    }
    @Override
    public Map<Integer, Double> calculateRevenueByYear(List<Integer> years) throws ApplicationException {
        Map<Integer,Double> result = new HashMap<>();
        BusinessPartner businessPartner = this.businessPartnerService.getCurrentBusinessPartner(PartnerTypeEnum.CAR_RENTAL_PARTNER);
        List<CarRentalOrders> carRentalOrders = this.vehicleRentalOrderRepo.findCarRentalOrdersByCarRentalService_VehicleRegister_CarRentalPartner_Id(businessPartner.getCarRentalPartner().getId());
        for(Integer year : years){
            Map<String, Double> statistic = new LinkedHashMap<>();
            this.getMonthlyRevenue(statistic, carRentalOrders, year);
            double sum = 0;
            for (double value : statistic.values()) {
                sum += value;
            }
            result.put(year,sum);
        }
        return result;
    }

    private void getYearlyRevenue(Map<String, Double> statistics, List<CarRentalOrders> carRentalOrders) {
        for(CarRentalOrders order : carRentalOrders){
            LocalDate startDate = LocalDate.ofInstant(order.getStart_rental_time(), ZoneId.systemDefault());
            LocalDate endDate = LocalDate.ofInstant(order.getEnd_rental_time(), ZoneId.systemDefault());
            double revenue = order.getTotal() - order.getReservation_fee() - order.getCar_deposit();
            // Tạo key theo năm của endDate
            String key = String.valueOf(endDate.getYear());
            double currentRevenue = statistics.getOrDefault(key, 0.0);
            statistics.put(key, revenue+currentRevenue);
        }
    }

    private void getMonthlyRevenue(Map<String, Double> statistics, List<CarRentalOrders> carRentalOrders, Integer year) {
        // Initialize revenue for each month
        for (int month = 1; month <= 12; month++) {
            String key = month + "-" + year;
//            String key =  String.valueOf(month);
            statistics.put(key, 0.0);
        }

        for (CarRentalOrders order : carRentalOrders) {
            LocalDate startDate = LocalDate.ofInstant(order.getStart_rental_time(), ZoneId.systemDefault());
            LocalDate endDate = LocalDate.ofInstant(order.getEnd_rental_time(), ZoneId.systemDefault());    

            //Số tiền dư ra của các tháng hoặc năm khác nhau

            if (startDate.getYear() != year && endDate.getYear() != year) {
                continue; // Skip orders that don't overlap the target year
            }
            // Ensure the start and end dates are within the same year
            if(startDate.getYear() < year){
                startDate = LocalDate.of(year, 1, 1);
            }
            if(endDate.getYear() > year){
                endDate = LocalDate.of(year, 12, 31);
            }

            int currentMonth = startDate.getMonthValue();
            String key = currentMonth + "-" +year;
            statistics.put(key, statistics.get(key) + order.getTotal() - order.getReservation_fee() - order.getCar_deposit());

        }
    }


}
