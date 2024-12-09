package com.pbl6.VehicleBookingRental.user.service.impl;

import com.pbl6.VehicleBookingRental.user.domain.VehicleType;
import com.pbl6.VehicleBookingRental.user.domain.car_rental.CarRentalOrders;
import com.pbl6.VehicleBookingRental.user.dto.car_rental_DTO.VehicleRentalStatisticDTO;
import com.pbl6.VehicleBookingRental.user.interfaces.VehicleRegisterInterface;
import com.pbl6.VehicleBookingRental.user.repository.vehicle_rental.VehicleRentalOrderRepo;
import com.pbl6.VehicleBookingRental.user.repository.vehicle_rental.VehicleRentalServiceRepo;
import com.pbl6.VehicleBookingRental.user.repository.vehicle_rental.VehicleTypeRepository;
import com.pbl6.VehicleBookingRental.user.service.VehicleRegisterService;
import com.pbl6.VehicleBookingRental.user.service.VehicleRentalStatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VehicleRentalStatisticImpl implements VehicleRentalStatisticService {
    @Autowired
    private  VehicleRentalOrderRepo vehicleRentalOrderRepo;
    @Autowired
    private VehicleTypeRepository vehicleTypeRepository;
    @Autowired
    private VehicleRegisterInterface vehicleRegisterInterface;
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
}
