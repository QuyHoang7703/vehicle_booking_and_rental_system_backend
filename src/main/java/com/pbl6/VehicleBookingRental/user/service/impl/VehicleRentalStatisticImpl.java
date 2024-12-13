package com.pbl6.VehicleBookingRental.user.service.impl;

import com.pbl6.VehicleBookingRental.user.domain.BusinessPartner;
import com.pbl6.VehicleBookingRental.user.domain.VehicleType;
import com.pbl6.VehicleBookingRental.user.domain.car_rental.CarRentalOrders;
import com.pbl6.VehicleBookingRental.user.domain.car_rental.CarRentalService;
import com.pbl6.VehicleBookingRental.user.dto.car_rental_DTO.VehicleRentalServiceDTO;
import com.pbl6.VehicleBookingRental.user.dto.car_rental_DTO.VehicleRentalStatisticDTO;
import com.pbl6.VehicleBookingRental.user.interfaces.VehicleRegisterInterface;
import com.pbl6.VehicleBookingRental.user.repository.vehicle_rental.VehicleRentalOrderRepo;
import com.pbl6.VehicleBookingRental.user.repository.vehicle_rental.VehicleRentalServiceRepo;
import com.pbl6.VehicleBookingRental.user.repository.vehicle_rental.VehicleTypeRepository;
import com.pbl6.VehicleBookingRental.user.service.BusinessPartnerService;
import com.pbl6.VehicleBookingRental.user.service.VehicleRegisterService;
import com.pbl6.VehicleBookingRental.user.service.VehicleRentalStatisticService;
import com.pbl6.VehicleBookingRental.user.util.DateUtil;
import com.pbl6.VehicleBookingRental.user.util.constant.PartnerTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VehicleRentalStatisticImpl implements VehicleRentalStatisticService {
    @Autowired
    private  VehicleRentalOrderRepo vehicleRentalOrderRepo;
    @Autowired
    private VehicleTypeRepository vehicleTypeRepository;
    @Autowired
    private VehicleRegisterInterface vehicleRegisterInterface;
    @Autowired
    private VehicleRentalServiceRepo vehicleRentalServiceRepo;
    @Autowired
    private BusinessPartnerService businessPartnerService;
    @Autowired
    private DateUtil dateUtil;

    @Override
    public List<VehicleRentalStatisticDTO> statisticFromLocationOrVehicleType(String location, String vehicleType) {
        List<CarRentalOrders> carRentalOrders = vehicleRentalOrderRepo.findCarRentalOrdersByVehicleRegisterProperties(location,vehicleType);
        List<VehicleRentalStatisticDTO> vehicleRentalStatisticDTOS = new ArrayList<>();
        List<String> distinctVehicleType = vehicleTypeRepository.findDistinctNames();
        List<String> distinctLocation = vehicleRegisterInterface.getExistFilterValue("location");
        if(location !=null && vehicleType != null){
            int vehicleRentalAmount = carRentalOrders.stream().mapToInt(CarRentalOrders::getAmount).sum();

            VehicleRentalStatisticDTO vehicleRentalStatisticDTO = new VehicleRentalStatisticDTO();
            vehicleRentalStatisticDTO.setLocation(location);
            vehicleRentalStatisticDTO.setVehicle_type(vehicleType);
            vehicleRentalStatisticDTO.setVehicleRentalAmount(vehicleRentalAmount);
            return  vehicleRentalStatisticDTOS;
        }
        if(vehicleType !=null){
            if(vehicleType.equalsIgnoreCase("all")){
                for(String i : distinctVehicleType){
                    int vehicleRentalAmount = carRentalOrders.stream().filter(order->{
                        return order.getCarRentalService().getVehicleRegister()
                                .getVehicleType().getName().equalsIgnoreCase(i);
                    }).mapToInt(CarRentalOrders::getAmount).sum();

                    VehicleRentalStatisticDTO vehicleRentalStatisticDTO = new VehicleRentalStatisticDTO();
                    vehicleRentalStatisticDTO.setVehicle_type(i);
                    vehicleRentalStatisticDTO.setVehicleRentalAmount(vehicleRentalAmount);
                    vehicleRentalStatisticDTOS.add(vehicleRentalStatisticDTO);
                }
                return  vehicleRentalStatisticDTOS;
            }else{
                int vehicleRentalAmount = carRentalOrders.stream().filter(order->{
                    return order.getCarRentalService().getVehicleRegister()
                            .getVehicleType().getName().equalsIgnoreCase(vehicleType);
                }).mapToInt(CarRentalOrders::getAmount).sum();

                VehicleRentalStatisticDTO vehicleRentalStatisticDTO = new VehicleRentalStatisticDTO();
                vehicleRentalStatisticDTO.setVehicle_type(vehicleType);
                vehicleRentalStatisticDTO.setVehicleRentalAmount(vehicleRentalAmount);
                vehicleRentalStatisticDTOS.add(vehicleRentalStatisticDTO);
                return  vehicleRentalStatisticDTOS;
            }
        }
        if( location!=null){
            if(location.equalsIgnoreCase("all")){
                for(String i : distinctLocation){
                    int vehicleRentalAmount = carRentalOrders.stream().filter(order->{
                        return order.getCarRentalService().getVehicleRegister()
                                .getLocation().equalsIgnoreCase(i);
                    }).mapToInt(CarRentalOrders::getAmount).sum();

                    VehicleRentalStatisticDTO vehicleRentalStatisticDTO = new VehicleRentalStatisticDTO();
                    vehicleRentalStatisticDTO.setLocation(i);
                    vehicleRentalStatisticDTO.setVehicleRentalAmount(vehicleRentalAmount);
                    vehicleRentalStatisticDTOS.add(vehicleRentalStatisticDTO);
                }
                return  vehicleRentalStatisticDTOS;
            }else{
                int vehicleRentalAmount = carRentalOrders.stream().filter(order->{
                    return order.getCarRentalService().getVehicleRegister()
                            .getLocation().equalsIgnoreCase(location);
                }).mapToInt(CarRentalOrders::getAmount).sum();

                VehicleRentalStatisticDTO vehicleRentalStatisticDTO = new VehicleRentalStatisticDTO();
                vehicleRentalStatisticDTO.setLocation(location);
                vehicleRentalStatisticDTO.setVehicleRentalAmount(vehicleRentalAmount);
                vehicleRentalStatisticDTOS.add(vehicleRentalStatisticDTO);
                return  vehicleRentalStatisticDTOS;
            }
        }
        return null;
    }

    @Override
    public List<VehicleRentalStatisticDTO> statisticByDate(String startDate, String endDate) {
        try{
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy").withZone(ZoneId.systemDefault());
            Instant startDateInstant = Instant.from(dateTimeFormatter.parse(startDate));
            Instant endDateInstant = Instant.from(dateTimeFormatter.parse(endDate));

            List<LocalDate> dates = dateUtil.getDaysBetweenDates(startDateInstant,endDateInstant);
            BusinessPartner currentBusinessPartner = businessPartnerService.getCurrentBusinessPartner(PartnerTypeEnum.CAR_RENTAL_PARTNER);

            return getRentalVehicleByDate(dates,currentBusinessPartner.getCarRentalPartner().getId());
        }catch (Exception e){
            System.out.println(e.getLocalizedMessage());
            return null;
        }
    }
    private List<VehicleRentalStatisticDTO> getRentalVehicleByDate(List<LocalDate> dates,int carRentalPartnerId){
        List<CarRentalOrders> carRentalOrders = vehicleRentalOrderRepo.findCarRentalOrdersByCarRentalService_VehicleRegister_CarRentalPartner_Id(carRentalPartnerId);
        List<VehicleRentalStatisticDTO> vehicleRentalStatisticDTOS = new ArrayList<>();
        for(LocalDate date : dates){
            int totalAmountInDate = carRentalOrders.stream()
                    .filter(order ->{
                        LocalDate startDate = order.getStart_rental_time().atZone(ZoneId.systemDefault()).toLocalDate();
                        LocalDate endDate = order.getEnd_rental_time().atZone(ZoneId.systemDefault()).toLocalDate();

                        return date.equals(startDate) || date.equals(endDate) ||
                                (date.isAfter(startDate) && date.isBefore(endDate));
                    }).mapToInt(CarRentalOrders::getAmount).sum();


            VehicleRentalStatisticDTO vehicleRentalStatisticDTO = new VehicleRentalStatisticDTO();
            vehicleRentalStatisticDTO.setDate(date);
            vehicleRentalStatisticDTO.setVehicleRentalAmount(totalAmountInDate);

            vehicleRentalStatisticDTOS.add(vehicleRentalStatisticDTO);
        }
        return vehicleRentalStatisticDTOS;
    }
//    public Map<Integer, Double> calculateMonthlyRevenue(List<CarRentalOrders> orders, int year){
//        Map<Integer, Double> monthlyRevenue = new HashMap<>();
//        // Initialize revenue for each month
//        for (int month = 1; month <= 12; month++) {
//            monthlyRevenue.put(month, 0.0);
//        }
//
//        for (CarRentalOrders order : orders) {
//            LocalDate startDate = LocalDate.ofInstant(order.getStart_rental_time(), ZoneId.systemDefault());
//            LocalDate endDate = LocalDate.ofInstant(order.getEnd_rental_time(), ZoneId.systemDefault());
//            LocalDate oldStartDate , oldEndDate ; // stored to calculate venue
//            if (startDate.getYear() != year && endDate.getYear() != year) {
//                continue; // Skip orders that don't overlap the target year
//            }
//            // both startDate and endDate belong to year
//            if (startDate.getYear() >= year && endDate.getYear() <= year) {
//
//            }
//            // Ensure the start and end dates are within the same year
//            if(startDate.getYear() < year){
//                startDate = LocalDate.of(year, 1, 1);
//                oldStartDate = startDate;
//            }
//            if(endDate.getYear() > year){
//                endDate = LocalDate.of(year, 12, 31);
//                oldEndDate = endDate;
//            }
//            // Distribute revenue across the months
//            while (!startDate.isAfter(endDate)) {
//                int currentMonth = startDate.getMonthValue();
//                LocalDate monthEnd = startDate.withDayOfMonth(startDate.lengthOfMonth());
//
//                if (monthEnd.isAfter(endDate)) {
//                    monthEnd = endDate;
//                }
//
//                long totalDays = ChronoUnit.DAYS.between(
//                        startDate,monthEnd.plusDays(1)
//                );
//
//                double priceOrders = (order.getAmount() * daysInMonth) / totalDays;
//                monthlyRevenue.put(currentMonth, monthlyRevenue.get(currentMonth) + monthlyAmount);
//
//                startDate = monthEnd.plusDays(1); // Move to the next month
//            }
//
//        }
//        return monthlyRevenue;
//    }
    public double calculatePriceOrderByStartAndEndDate(Instant startRentalTime,Instant endRentalTime,double priceOneDay){
        double total = 0.0;
        // Chuyển đổi Instant thành LocalDateTime để làm việc với giờ cụ thể
        LocalDateTime start = LocalDateTime.ofInstant(startRentalTime, ZoneId.systemDefault());
        LocalDateTime end = LocalDateTime.ofInstant(endRentalTime, ZoneId.systemDefault());

        //Thue trong cung 1 ngay
        if(start.toLocalDate().equals(end.toLocalDate())){
            total = calculatePriceInDay(start,end,priceOneDay);
        }else{
            //Thuê nhiều ngày
            //Tiền startTime
            total += calculatePriceInDay(start,LocalDateTime.of(end.toLocalDate(), LocalTime.of(22, 0)),priceOneDay);
            total += calculatePriceInDay(LocalDateTime.of(end.toLocalDate(), LocalTime.of(6, 0)),end,priceOneDay);

            // Tính giá cho các ngày đầy đủ ở giữa
            LocalDateTime currentDay = start.toLocalDate().atStartOfDay().plusDays(1); // 12-12-2024 (toLocalDate)-> 00:00:00 12-12-2024 -> 00:00:00 13-12-2024
            while (currentDay.isBefore(end.toLocalDate().atStartOfDay())) {
                total += priceOneDay;
                currentDay = currentDay.plusDays(1);
            }
        }
        return total;
    }
    //tính giá tiền khi startTime và endTime trong cùng 1 ngày
    public double calculatePriceInDay(LocalDateTime start,LocalDateTime end,double priceOneDay){
        double total = 0.0;
        if (start.getHour() < 12) {
            // Nếu bắt đầu trước 12 giờ
            if (end.getHour() <= 12) {
                total += (Duration.between(start, end).toHours() * priceOneDay) / 8;; // Thue theo tieng
            } else if(end.getHour() < 18){
                total += priceOneDay / 2 + (Duration.between(LocalDateTime.of(end.toLocalDate(), java.time.LocalTime.NOON), end).toHours() * priceOneDay) / 8; // Thuê nửa ngày + số tiếng từ 12 giờ -> endDate
            }else{
                total += priceOneDay;
            }
        } else if (start.getHour() < 18) {
            // Từ 12 giờ đến 18 giờ
            if (end.getHour() <= 18) {
                total += (Duration.between(start, end).toHours() * priceOneDay) / 8;; // Thue theo tieng
            } else if(end.getHour() < 22){
                total += priceOneDay / 2 + (Duration.between(LocalDateTime.of(end.toLocalDate(), LocalTime.of(18, 0)), end).toHours()
                        * priceOneDay) / 8; // Thuê nửa ngày + số tiếng từ 12 giờ -> endDate
            } else{
                //Sau 18 giờ
                total += priceOneDay;
            }
        } else {
            // Sau 18 giờ
            total += (Duration.between(start, end).toHours() * priceOneDay) / 8; // Tính theo giờ còn lại sau 18 giờ
        }
        return total;
    }
}
