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
    @Override
    public Map<Integer, Double> calculateMonthlyRevenue( int year){
        Map<Integer, Double> monthlyRevenue = new HashMap<>();
        // Initialize revenue for each month
        for (int month = 1; month <= 12; month++) {
            monthlyRevenue.put(month, 0.0);
        }

        for (CarRentalOrders order : vehicleRentalOrderRepo.findAll()) {
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
            monthlyRevenue.put(currentMonth, monthlyRevenue.get(currentMonth) + order.getTotal() );

        }
        return monthlyRevenue;
    }

}
