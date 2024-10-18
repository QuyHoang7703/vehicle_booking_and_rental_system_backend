package com.pbl6.VehicleBookingRental.user.interfaces;

import com.pbl6.VehicleBookingRental.user.domain.VehicleType;
import com.pbl6.VehicleBookingRental.user.domain.car_rental.CarRentalPartner;
import com.pbl6.VehicleBookingRental.user.domain.car_rental.CarRentalService;
import com.pbl6.VehicleBookingRental.user.domain.car_rental.VehicleRegister;
import com.pbl6.VehicleBookingRental.user.domain.dto.car_rental_DTO.VehicleRentalServiceDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface VehicleRegisterInterface {
    public VehicleType findVehicleTypeById(int id);
    public CarRentalPartner findCarRentalPartnerById(int id);
    public boolean register_vehicle(VehicleRegister vehicleRegister);

    public List<VehicleRentalServiceDTO> get_all_by_service_type(int serviceType,String status,int car_rental_partner_id);

    public boolean register_service_rental(CarRentalService carRentalService);

    public VehicleRentalServiceDTO get_vehicle_rental_service_by_id(int id);

    public boolean update_vehicle_rental_service(VehicleRentalServiceDTO vehicleRentalServiceDTO);

    public boolean update_status(int vehicleRegisterId, String status);
    public List<VehicleRentalServiceDTO> filter_by_vehicle_attribute(String location,String manufacture,String vehicle_type);

}
