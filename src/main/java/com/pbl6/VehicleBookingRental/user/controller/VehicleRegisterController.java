package com.pbl6.VehicleBookingRental.user.controller;

import com.pbl6.VehicleBookingRental.user.domain.BusinessPartner;
import com.pbl6.VehicleBookingRental.user.domain.RestResponse;
import com.pbl6.VehicleBookingRental.user.domain.VehicleType;
import com.pbl6.VehicleBookingRental.user.domain.car_rental.CarRentalPartner;
import com.pbl6.VehicleBookingRental.user.domain.car_rental.CarRentalService;
import com.pbl6.VehicleBookingRental.user.domain.car_rental.VehicleRegister;
import com.pbl6.VehicleBookingRental.user.dto.car_rental_DTO.VehicleRentalServiceDTO;
import com.pbl6.VehicleBookingRental.user.interfaces.VehicleRegisterInterface;
import com.pbl6.VehicleBookingRental.user.service.BusinessPartnerService;
import com.pbl6.VehicleBookingRental.user.util.constant.PartnerTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("api/user/vehicle-register")
public class VehicleRegisterController {
    @Autowired
    private VehicleRegisterInterface vehicleRegisterInterface;
    @Autowired
    private  BusinessPartnerService businessPartnerService;

    @GetMapping("/all")
    public ResponseEntity<?> get_all_by_service_type(@RequestParam("service_type") int service_type,
                                                     @RequestParam("status")String status
                                                     )
    {
        return ResponseEntity.status(HttpStatus.OK).body(vehicleRegisterInterface.get_all_by_service_type(service_type,status));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register_vehicle(
            @RequestPart("vehicleRegisterInfo") VehicleRegister vehicleRegister,
            @RequestParam("service_type") int service_type,
            @RequestParam(value = "no_driver_price",required = false) Optional<Double> no_driver_price,
            @RequestParam(value = "driver_price",required = false) Optional<Double> driver_price,
            @RequestParam("vehicle_type_id") int vehicle_type_id,
            @RequestParam(value = "vehicleRegisterImages",required = false) List<MultipartFile> images

    ){
        RestResponse<Object> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.OK.value());
        response.setError(null);
        response.setData(null);
    try{
        BusinessPartner currentBusinessPartner = businessPartnerService.getCurrentBusinessPartner(PartnerTypeEnum.CAR_RENTAL_PARTNER);
        VehicleType vehicleType = vehicleRegisterInterface.findVehicleTypeById(vehicle_type_id);
        vehicleRegister.setVehicleType(vehicleType);
        vehicleRegister.setCarRentalPartner(currentBusinessPartner.getCarRentalPartner());
        vehicleRegisterInterface.register_vehicle(vehicleRegister,images);

        CarRentalService carRentalService = new CarRentalService();
        if(service_type != 2){
            carRentalService.setVehicleRegister(vehicleRegister);
            carRentalService.setType(service_type);
            if(service_type == 0 ){
                carRentalService.setPrice(no_driver_price.get());
            } else{
                carRentalService.setPrice(driver_price.get()) ;
            }
            vehicleRegisterInterface.register_service_rental(carRentalService);
        }else{
            carRentalService.setType(0);
            carRentalService.setPrice(no_driver_price.get());
            carRentalService.setVehicleRegister(vehicleRegister);
            vehicleRegisterInterface.register_service_rental(carRentalService);

            CarRentalService carRentalService1 = new CarRentalService();
            carRentalService1.setType(1);
            carRentalService1.setPrice(driver_price.get());
            carRentalService1.setVehicleRegister(vehicleRegister);
            vehicleRegisterInterface.register_service_rental(carRentalService1);
        }

        response.setMessage("Register Successfully");

    }catch (Exception e){
        System.out.println(e.getLocalizedMessage());
        response.setMessage("Register Failed");
    }


        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @GetMapping("/get-vehicle-register")
    public ResponseEntity<?> getVehicleRegisterById(@RequestParam("vehicle_register_id") int id){
        VehicleRentalServiceDTO vehicleRentalServiceDTO = vehicleRegisterInterface.get_vehicle_rental_service_by_vehicle_register_id(id);
        return ResponseEntity.status(HttpStatus.OK).body(vehicleRentalServiceDTO);
    }
    @GetMapping("/get-vehicle-rental-service")
    public ResponseEntity<?> getVehicleRentalById(@RequestParam("vehicle_rental_service_id") int id){
        VehicleRentalServiceDTO vehicleRentalServiceDTO = vehicleRegisterInterface.get_vehicle_rental_service_by_vehicleRentalServiceID(id);
        return ResponseEntity.status(HttpStatus.OK).body(vehicleRentalServiceDTO);
    }
    @PatchMapping("/update-vehicle-rental-service")
    public ResponseEntity<?> update_vehicle_rental_service(@RequestPart("vehicleRentalService") VehicleRentalServiceDTO vehicleRentalServiceDTO,
                                                           @RequestParam(value = "vehicleRegisterImages",required = false) List<MultipartFile> images){
        RestResponse<String> restResponse = new RestResponse<>();
        restResponse.setStatusCode(200);

        boolean status = vehicleRegisterInterface.update_vehicle_rental_service(vehicleRentalServiceDTO,images);
        if(status == true){
            restResponse.setMessage("Update Successfully");
        }else{
            restResponse.setMessage("Update Failed");
        }
        return  new ResponseEntity<>(restResponse,HttpStatus.OK);
    }
    @PatchMapping("/update-status")
    public ResponseEntity<?> updateStatus
            (@RequestParam("vehicle_register_id")int vehicle_register_id,
             @RequestParam("status")String status
             )
    {
        RestResponse<String> restResponse = new RestResponse<>();
        restResponse.setStatusCode(200);

        boolean result = vehicleRegisterInterface.update_status(vehicle_register_id,status);
        if(result){
            restResponse.setMessage("Update Successfully");
        }else{
            restResponse.setMessage("Update Failed");
        }
        return new ResponseEntity<>(restResponse,HttpStatus.OK);
    }
    @GetMapping("/filters-rental-service")
    public ResponseEntity<?> getRentalService(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String manufacturer,
            @RequestParam(required = false) String vehicle_type,
            @RequestParam(required = false, value = "service_type") int service_type,
            @RequestParam(required = false,value = "start_date")String startDate,
            @RequestParam(required = false,value = "end_date") String endDate
            )
    {
        return ResponseEntity.status(HttpStatus.OK).body(vehicleRegisterInterface.filter_by_vehicle_attribute(location,manufacturer,vehicle_type,service_type,startDate,endDate));
    }
    @GetMapping("/get-exist-filter-properties")
    public ResponseEntity<?> getExistFilterValue(@RequestParam("properties") String properties){
        return ResponseEntity.status(HttpStatus.OK).body(vehicleRegisterInterface.getExistFilterValue(properties));
    }
    @GetMapping("/get-vehicle-rental-service-by-vehicle-register")
    public ResponseEntity<?> getVRSByVR(@RequestParam("vehicleRegisterId") int id){
        return ResponseEntity.status(HttpStatus.OK).body(vehicleRegisterInterface.getVehicleRentalServiceByVehicleRegister(id));
    }
}
