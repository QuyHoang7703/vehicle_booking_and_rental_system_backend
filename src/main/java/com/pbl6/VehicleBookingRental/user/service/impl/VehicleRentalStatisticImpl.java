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
    public List<VehicleRentalStatisticDTO> statisticFromLocationOrVehicleTypeByDate(String location, String vehicleType, String startDate, String endDate)  {
        List<VehicleRentalStatisticDTO> vehicleRentalStatisticDTOS = new ArrayList<>();
        List<String> distinctVehicleType = vehicleTypeRepository.findDistinctNames();
        List<String> distinctLocation = vehicleRegisterInterface.getExistFilterValue("location");
        if(location !=null && vehicleType != null){
            List<VehicleRentalStatisticDTO> statisticDTOS = statisticByDate(location,vehicleType,startDate,endDate);
            vehicleRentalStatisticDTOS.addAll(statisticDTOS);
            return  vehicleRentalStatisticDTOS;
        }
        if(vehicleType !=null){
            if(vehicleType.equalsIgnoreCase("all")){
                for(String i : distinctVehicleType){
                    List<VehicleRentalStatisticDTO> statisticDTOS = statisticByDate(location,i,startDate,endDate);
                    vehicleRentalStatisticDTOS.addAll(statisticDTOS);
                }
                return  vehicleRentalStatisticDTOS;
            }else{
                List<VehicleRentalStatisticDTO> statisticDTOS = statisticByDate(null,vehicleType,startDate,endDate);
                vehicleRentalStatisticDTOS.addAll(statisticDTOS);
                return  vehicleRentalStatisticDTOS;
            }
        }
        if( location!=null){
            if(location.equalsIgnoreCase("all")){
                for(String i : distinctLocation){
                    List<VehicleRentalStatisticDTO> statisticDTOS = statisticByDate(i,vehicleType,startDate,endDate);
                    vehicleRentalStatisticDTOS.addAll(statisticDTOS);
                }
                return  vehicleRentalStatisticDTOS;
            }else{
                List<VehicleRentalStatisticDTO> statisticDTOS = statisticByDate(location,null,startDate,endDate);
                vehicleRentalStatisticDTOS.addAll(statisticDTOS);
                return  vehicleRentalStatisticDTOS;
            }
        }
        return null;
    }
    @Override
    public List<VehicleRentalStatisticDTO> statisticFromLocationOrVehicleTypeByMonthAndYear(String location, String vehicleType, int month , int year)  {
        List<VehicleRentalStatisticDTO> vehicleRentalStatisticDTOS = new ArrayList<>();
        List<String> distinctVehicleType = vehicleTypeRepository.findDistinctNames();
        List<String> distinctLocation = vehicleRegisterInterface.getExistFilterValue("location");
        if(location !=null && vehicleType != null){
            VehicleRentalStatisticDTO statisticDTOS = statisticByMonthAndYear(location,vehicleType,month,year);
            vehicleRentalStatisticDTOS.add(statisticDTOS);
            return  vehicleRentalStatisticDTOS;
        }
        if(vehicleType !=null){
            if(vehicleType.equalsIgnoreCase("all")){
                for(String i : distinctVehicleType){
                    VehicleRentalStatisticDTO statisticDTOS = statisticByMonthAndYear(location,i,month,year);
                    vehicleRentalStatisticDTOS.add(statisticDTOS);
                }
                return  vehicleRentalStatisticDTOS;
            }else{
                VehicleRentalStatisticDTO statisticDTOS = statisticByMonthAndYear(null,vehicleType,month,year);
                vehicleRentalStatisticDTOS.add(statisticDTOS);
                return  vehicleRentalStatisticDTOS;
            }
        }
        if( location!=null){
            if(location.equalsIgnoreCase("all")){
                for(String i : distinctLocation){
                    VehicleRentalStatisticDTO statisticDTOS = statisticByMonthAndYear(i,vehicleType,month,year);
                    vehicleRentalStatisticDTOS.add(statisticDTOS);
                }
                return  vehicleRentalStatisticDTOS;
            }else{
                VehicleRentalStatisticDTO statisticDTOS = statisticByMonthAndYear(location,null,month,year);
                vehicleRentalStatisticDTOS.add(statisticDTOS);
                return  vehicleRentalStatisticDTOS;
            }
        }
        return null;
    }
    @Override
    public List<VehicleRentalStatisticDTO> statisticFromLocationOrVehicleTypeByYear(String location, String vehicleType, List<Integer> year) throws ApplicationException {
        BusinessPartner businessPartner = this.businessPartnerService.getCurrentBusinessPartner(PartnerTypeEnum.CAR_RENTAL_PARTNER);

        List<VehicleRentalStatisticDTO> vehicleRentalStatisticDTOS = new ArrayList<>();
        List<String> distinctVehicleType = vehicleTypeRepository.findDistinctNames();
        List<String> distinctLocation = vehicleRegisterInterface.getExistFilterValue("location");
        if(location !=null && vehicleType != null){
            List<VehicleRentalStatisticDTO> statisticDTOS = getRentalVehicleByYear(location,vehicleType,year,businessPartner.getCarRentalPartner().getId());
            vehicleRentalStatisticDTOS.addAll(statisticDTOS);
            return  vehicleRentalStatisticDTOS;
        }
        if(vehicleType !=null){
            if(vehicleType.equalsIgnoreCase("all")){
                for(String i : distinctVehicleType){
                    List<VehicleRentalStatisticDTO> statisticDTOS = getRentalVehicleByYear(location,i,year,businessPartner.getCarRentalPartner().getId());
                    vehicleRentalStatisticDTOS.addAll(statisticDTOS);
                }
                return  vehicleRentalStatisticDTOS;
            }else{
                List<VehicleRentalStatisticDTO> statisticDTOS = getRentalVehicleByYear(null,vehicleType,year,businessPartner.getCarRentalPartner().getId());
                vehicleRentalStatisticDTOS.addAll(statisticDTOS);
                return  vehicleRentalStatisticDTOS;
            }
        }
        if( location!=null){
            if(location.equalsIgnoreCase("all")){
                for(String i : distinctLocation){
                    List<VehicleRentalStatisticDTO> statisticDTOS = getRentalVehicleByYear(i,vehicleType,year,businessPartner.getCarRentalPartner().getId());
                    vehicleRentalStatisticDTOS.addAll(statisticDTOS);
                }
                return  vehicleRentalStatisticDTOS;
            }else{
                List<VehicleRentalStatisticDTO> statisticDTOS = getRentalVehicleByYear(location,null,year,businessPartner.getCarRentalPartner().getId());
                vehicleRentalStatisticDTOS.addAll(statisticDTOS);
                return  vehicleRentalStatisticDTOS;
            }
        }
        return null;
    }

    @Override
    public List<VehicleRentalStatisticDTO> statisticByDate(String location,String vehicleTypeName,String startDate, String endDate) {

        try{
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy").withZone(ZoneId.systemDefault());
            Instant startDateInstant = Instant.from(dateTimeFormatter.parse(startDate));
            Instant endDateInstant = Instant.from(dateTimeFormatter.parse(endDate));

            List<LocalDate> dates = dateUtil.getDaysBetweenDates(startDateInstant,endDateInstant);
            BusinessPartner currentBusinessPartner = businessPartnerService.getCurrentBusinessPartner(PartnerTypeEnum.CAR_RENTAL_PARTNER);

            return getRentalVehicleByDate(location,vehicleTypeName,dates,currentBusinessPartner.getCarRentalPartner().getId());
        }catch (Exception e){
            System.out.println(e.getLocalizedMessage());
            return null;
        }
    }
    private List<VehicleRentalStatisticDTO> getRentalVehicleByDate(String location,String vehicleTypeName,List<LocalDate> dates,int carRentalPartnerId){

        List<CarRentalOrders> carRentalOrders = vehicleRentalOrderRepo.findCarRentalOrdersByLocationOrType(location,vehicleTypeName,carRentalPartnerId);

        List<VehicleRentalStatisticDTO> vehicleRentalStatisticDTOS = new ArrayList<>();
        for(LocalDate date : dates){
            int totalAmountInDate = 0;
            int totalCancelAmount = 0;
            for (CarRentalOrders order : carRentalOrders) {
                LocalDate startDate = order.getStart_rental_time().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate endDate = order.getEnd_rental_time().atZone(ZoneId.systemDefault()).toLocalDate();

                if ((date.equals(startDate) || date.equals(endDate) ||
                        (date.isAfter(startDate) && date.isBefore(endDate))) ) {
                    if(order.getStatus().equalsIgnoreCase("canceled")){
                        totalCancelAmount += order.getAmount();
                    }else{
                        totalAmountInDate += order.getAmount();
                    }
                }
            }


            VehicleRentalStatisticDTO vehicleRentalStatisticDTO = new VehicleRentalStatisticDTO();
            vehicleRentalStatisticDTO.setDate(date);
            vehicleRentalStatisticDTO.setVehicleRentalAmount(totalAmountInDate);
            vehicleRentalStatisticDTO.setCanceledVehicleAmount(totalCancelAmount);
            vehicleRentalStatisticDTO.setVehicle_type(vehicleTypeName);
            vehicleRentalStatisticDTO.setLocation(location);

            vehicleRentalStatisticDTOS.add(vehicleRentalStatisticDTO);
        }
        return vehicleRentalStatisticDTOS;
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

        return vehicleRentalStatisticDTO;
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
            String key =  String.valueOf(month);
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
            statistics.put(String.valueOf(currentMonth), statistics.get(String.valueOf(currentMonth)) + order.getTotal() - order.getReservation_fee() - order.getCar_deposit());

        }
    }


}
