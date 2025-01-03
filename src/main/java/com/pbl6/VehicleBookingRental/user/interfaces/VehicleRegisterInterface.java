package com.pbl6.VehicleBookingRental.user.interfaces;

import com.pbl6.VehicleBookingRental.user.domain.VehicleType;
import com.pbl6.VehicleBookingRental.user.domain.car_rental.CarRentalPartner;
import com.pbl6.VehicleBookingRental.user.domain.car_rental.CarRentalService;
import com.pbl6.VehicleBookingRental.user.domain.car_rental.VehicleRegister;
import com.pbl6.VehicleBookingRental.user.dto.car_rental_DTO.VehicleRentalServiceDTO;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface VehicleRegisterInterface {
    public VehicleType findVehicleTypeById(int id);
    public boolean register_vehicle(VehicleRegister vehicleRegister, List<MultipartFile> images);

    public List<VehicleRentalServiceDTO> get_all_by_service_type(int serviceType,String status);

    public boolean register_service_rental(CarRentalService carRentalService);

    public VehicleRentalServiceDTO get_vehicle_rental_service_by_vehicle_register_id(int id);

    public boolean update_vehicle_rental_service(VehicleRentalServiceDTO vehicleRentalServiceDTO,List<MultipartFile> images);

    public boolean update_status(int vehicleRegisterId, String status);
    public List<VehicleRentalServiceDTO> filter_by_vehicle_attribute(String location, String manufacture, String vehicle_type, int service_type, String startDate,String endDate);
    public List<String>getExistFilterValue(String properties);
    public VehicleRentalServiceDTO get_vehicle_rental_service_by_vehicleRentalServiceID(int vehicleRentalID);
    public List<VehicleRentalServiceDTO> getVehicleRentalServiceByVehicleRegister(int id);
}
