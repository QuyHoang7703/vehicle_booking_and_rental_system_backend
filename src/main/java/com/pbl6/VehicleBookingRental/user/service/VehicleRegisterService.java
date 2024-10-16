package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.domain.VehicleType;
import com.pbl6.VehicleBookingRental.user.domain.car_rental.CarRentalPartner;
import com.pbl6.VehicleBookingRental.user.domain.car_rental.CarRentalService;
import com.pbl6.VehicleBookingRental.user.domain.car_rental.VehicleRegister;
import com.pbl6.VehicleBookingRental.user.dto.car_rental_DTO.VehicleRentalServiceDTO;
import com.pbl6.VehicleBookingRental.user.interfaces.VehicleRegisterInterface;
import com.pbl6.VehicleBookingRental.user.repository.vehicle_rental.CarRentalPartnerRepo;
import com.pbl6.VehicleBookingRental.user.repository.vehicle_rental.VehicleRegisterRepo;
import com.pbl6.VehicleBookingRental.user.repository.vehicle_rental.VehicleRentalServiceRepo;
import com.pbl6.VehicleBookingRental.user.repository.vehicle_rental.VehicleTypeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VehicleRegisterService implements VehicleRegisterInterface {
    @Autowired
    private VehicleRegisterRepo vehicleRegisterRepository;
    @Autowired
    private VehicleRentalServiceRepo vehicleRentalServiceRepo;
    @Autowired
    private  VehicleTypeRepo vehicleTypeRepo;
    @Autowired
    private CarRentalPartnerRepo carRentalPartnerRepo;

    @Override
    public VehicleType findVehicleTypeById(int id) {
        return vehicleTypeRepo.findById(id).orElse(null);
    }

    @Override
    public CarRentalPartner findCarRentalPartnerById(int id) {
        return carRentalPartnerRepo.findById(id).orElse(null);
    }

    @Override
    public boolean register_vehicle(VehicleRegister vehicleRegister) {
        try{
            vehicleRegisterRepository.save(vehicleRegister);
            return true;
        }catch (Exception e){
            System.out.println(e.getLocalizedMessage());
            return false;
        }
    }

    @Override
    public boolean register_service_rental(CarRentalService carRentalService) {
        try {
            vehicleRentalServiceRepo.save(carRentalService);
            return true;
        }catch (Exception e){
                System.out.println(e.getLocalizedMessage());
                return false;
            }
    }

    @Override
    public VehicleRentalServiceDTO get_vehicle_rental_service_by_id(int id) {
        VehicleRentalServiceDTO vehicleRentalServiceDTO = new VehicleRentalServiceDTO();
        Optional<CarRentalService> carRentalService = vehicleRentalServiceRepo.findById(id);
        if(carRentalService.isPresent()){
            VehicleRegister vehicleRegister = carRentalService.get().getVehicleRegister();
            if (vehicleRegister != null) {


                // Thiết lập các thuộc tính từ CarRentalService và VehicleRegister
                vehicleRentalServiceDTO.setId(carRentalService.get().getId());
                vehicleRentalServiceDTO.setPrice(carRentalService.get().getPrice());
                vehicleRentalServiceDTO.setType(carRentalService.get().getType());

                // Thiết lập các thuộc tính từ đối tượng VehicleRegister
                vehicleRentalServiceDTO.setManufacturer(vehicleRegister.getManufacturer());
                vehicleRentalServiceDTO.setDescription(vehicleRegister.getDescription());
                vehicleRentalServiceDTO.setQuantity(vehicleRegister.getQuantity());
                vehicleRentalServiceDTO.setStatus(vehicleRegister.getStatus());
                vehicleRentalServiceDTO.setDate_of_status(vehicleRegister.getDate_of_status());
                vehicleRentalServiceDTO.setDiscount_percentage(vehicleRegister.getDiscount_percentage());
                vehicleRentalServiceDTO.setCar_deposit(vehicleRegister.getCar_deposit());
                vehicleRentalServiceDTO.setReservation_fees(vehicleRegister.getReservation_fees());
                vehicleRentalServiceDTO.setUlties(vehicleRegister.getUlties());
                vehicleRentalServiceDTO.setPolicy(vehicleRegister.getPolicy());
                vehicleRentalServiceDTO.setRating_total(vehicleRegister.getRating_total());
                vehicleRentalServiceDTO.setAmount(vehicleRegister.getAmount());
                vehicleRentalServiceDTO.setLocation(vehicleRegister.getLocation());
                vehicleRentalServiceDTO.setVehicle_register_id(vehicleRegister.getId());
                vehicleRentalServiceDTO.setVehicle_type_id(vehicleRegister.getVehicleType().getId());
            }else{
                return null;
            }
        }

        return vehicleRentalServiceDTO;
    }

    @Override
    public boolean update_vehicle_rental_service(VehicleRentalServiceDTO vehicleRentalServiceDTO) {
        Optional<VehicleRegister> vehicleRegister = vehicleRegisterRepository.findById(vehicleRentalServiceDTO.getVehicle_register_id());
        Optional<CarRentalService> carRentalService = vehicleRentalServiceRepo.findById(vehicleRentalServiceDTO.getId());
        Optional<VehicleType> vehicleType = vehicleTypeRepo.findById(vehicleRentalServiceDTO.getVehicle_type_id());
        if(vehicleRegister.isPresent() && carRentalService.isPresent() && vehicleType.isPresent()){
            vehicleRegister.get().setVehicleType(vehicleType.get());
            vehicleRegister.get().setManufacturer(vehicleRentalServiceDTO.getManufacturer());
            vehicleRegister.get().setDescription(vehicleRentalServiceDTO.getDescription());
            vehicleRegister.get().setQuantity(vehicleRentalServiceDTO.getQuantity());
            vehicleRegister.get().setStatus(vehicleRentalServiceDTO.getStatus());
            vehicleRegister.get().setDate_of_status(vehicleRentalServiceDTO.getDate_of_status());
            vehicleRegister.get().setDiscount_percentage(vehicleRentalServiceDTO.getDiscount_percentage());
            vehicleRegister.get().setCar_deposit(vehicleRentalServiceDTO.getCar_deposit());
            vehicleRegister.get().setReservation_fees(vehicleRentalServiceDTO.getReservation_fees());
            vehicleRegister.get().setUlties(vehicleRentalServiceDTO.getUlties());
            vehicleRegister.get().setPolicy(vehicleRentalServiceDTO.getPolicy());
            vehicleRegister.get().setRating_total(vehicleRentalServiceDTO.getRating_total());
            vehicleRegister.get().setAmount(vehicleRentalServiceDTO.getAmount());
            vehicleRegister.get().setLocation(vehicleRentalServiceDTO.getLocation());

            //car_rental_service
            carRentalService.get().setVehicleRegister(vehicleRegister.get());
            carRentalService.get().setType(vehicleRentalServiceDTO.getType());
            carRentalService.get().setPrice(vehicleRentalServiceDTO.getPrice());

            try{
                vehicleRentalServiceRepo.save(carRentalService.get());
                return true;
            }catch (Exception e){
                System.out.println(e.getLocalizedMessage());

            }
        }
        return false;
    }

    @Override
    public boolean update_status(int vehicleRegisterId, String status) {
        try{
            vehicleRegisterRepository.updateStatus(vehicleRegisterId,status);
            return true;
        }catch(Exception e){
            System.out.println("Error:"+e.getLocalizedMessage());
        }
        return false;
    }

    @Override
    public List<VehicleRentalServiceDTO> filter_by_vehicle_attribute(String location, String manufacturer, String vehicle_type) {
        List<VehicleRentalServiceDTO> vehicleRentalServiceDTOList = new ArrayList<>();
        System.out.println(location);
        System.out.println(manufacturer);
        System.out.println(vehicle_type);
        List<VehicleRegister> vehicleRegisterList =
                vehicleRegisterRepository.findVehicleRegisterByLocationOrManufacturerOrVehicleType_Name
                        (location,manufacturer,vehicle_type);
//        System.out.println(vehicleRegisterList);

        for(VehicleRegister vehicleRegister : vehicleRegisterList){
            List<CarRentalService> carRentalServiceList = vehicleRegister.getTypeOfRentalServiceList();

            for(CarRentalService carRentalService : carRentalServiceList){
                VehicleRentalServiceDTO vehicleRentalServiceDTO = new VehicleRentalServiceDTO();

                // Thiết lập các thuộc tính từ đối tượng VehicleRegister
                vehicleRentalServiceDTO.setManufacturer(vehicleRegister.getManufacturer());
                vehicleRentalServiceDTO.setDescription(vehicleRegister.getDescription());
                vehicleRentalServiceDTO.setQuantity(vehicleRegister.getQuantity());
                vehicleRentalServiceDTO.setStatus(vehicleRegister.getStatus());
                vehicleRentalServiceDTO.setDate_of_status(vehicleRegister.getDate_of_status());
                vehicleRentalServiceDTO.setDiscount_percentage(vehicleRegister.getDiscount_percentage());
                vehicleRentalServiceDTO.setCar_deposit(vehicleRegister.getCar_deposit());
                vehicleRentalServiceDTO.setReservation_fees(vehicleRegister.getReservation_fees());
                vehicleRentalServiceDTO.setUlties(vehicleRegister.getUlties());
                vehicleRentalServiceDTO.setPolicy(vehicleRegister.getPolicy());
                vehicleRentalServiceDTO.setRating_total(vehicleRegister.getRating_total());
                vehicleRentalServiceDTO.setAmount(vehicleRegister.getAmount());
                vehicleRentalServiceDTO.setVehicle_register_id(vehicleRegister.getId());
                vehicleRentalServiceDTO.setLocation(vehicleRegister.getLocation());
                vehicleRentalServiceDTO.setVehicle_type_id(vehicleRegister.getVehicleType().getId());
                //
                vehicleRentalServiceDTO.setId(carRentalService.getId());
                vehicleRentalServiceDTO.setPrice(carRentalService.getPrice());
                vehicleRentalServiceDTO.setType(carRentalService.getType());

                vehicleRentalServiceDTOList.add(vehicleRentalServiceDTO);
              }
        }

        return vehicleRentalServiceDTOList;
    }

    @Override
    public List<VehicleRentalServiceDTO> get_all_by_service_type(int serviceType, String status,int car_rental_partner_id) {
        // Khởi tạo danh sách DTO để lưu kết quả trả về
        List<VehicleRentalServiceDTO> vehicleRentalServiceDTOList = new ArrayList<>();
        List<CarRentalService> carRentalServiceList = new ArrayList<>();
        if(car_rental_partner_id != -1) { // List cho khach hang
            // Lấy danh sách CarRentalService theo điều kiện status
            carRentalServiceList = status.equals("all")
                    ? vehicleRentalServiceRepo.findAllByVehicleRegister_CarRentalPartner_Id(car_rental_partner_id)
                    : vehicleRentalServiceRepo.findCarRentalServiceByTypeAndVehicleRegister_StatusAndVehicleRegister_CarRentalPartner_Id
                                                (serviceType, status, car_rental_partner_id);
        }else{
            carRentalServiceList = status.equals("all")
                    ? vehicleRentalServiceRepo.findAll()
                    : vehicleRentalServiceRepo.findCarRentalServiceByTypeAndVehicleRegister_Status
                                                (serviceType, status);
        }
        // Duyệt qua danh sách carRentalServiceList và chuyển đổi từng đối tượng sang DTO
        for (CarRentalService carRentalService : carRentalServiceList) {
            // Kiểm tra vehicleRegister không null trước khi sử dụng
            VehicleRegister vehicleRegister = carRentalService.getVehicleRegister();
            if (vehicleRegister != null) {
                VehicleRentalServiceDTO vehicleRentalServiceDTO = new VehicleRentalServiceDTO();

                // Thiết lập các thuộc tính từ CarRentalService và VehicleRegister
                vehicleRentalServiceDTO.setId(carRentalService.getId());
                vehicleRentalServiceDTO.setPrice(carRentalService.getPrice());
                vehicleRentalServiceDTO.setType(carRentalService.getType());

                // Thiết lập các thuộc tính từ đối tượng VehicleRegister
                vehicleRentalServiceDTO.setManufacturer(vehicleRegister.getManufacturer());
                vehicleRentalServiceDTO.setDescription(vehicleRegister.getDescription());
                vehicleRentalServiceDTO.setQuantity(vehicleRegister.getQuantity());
                vehicleRentalServiceDTO.setStatus(vehicleRegister.getStatus());
                vehicleRentalServiceDTO.setDate_of_status(vehicleRegister.getDate_of_status());
                vehicleRentalServiceDTO.setDiscount_percentage(vehicleRegister.getDiscount_percentage());
                vehicleRentalServiceDTO.setCar_deposit(vehicleRegister.getCar_deposit());
                vehicleRentalServiceDTO.setReservation_fees(vehicleRegister.getReservation_fees());
                vehicleRentalServiceDTO.setUlties(vehicleRegister.getUlties());
                vehicleRentalServiceDTO.setPolicy(vehicleRegister.getPolicy());
                vehicleRentalServiceDTO.setRating_total(vehicleRegister.getRating_total());
                vehicleRentalServiceDTO.setAmount(vehicleRegister.getAmount());
                vehicleRentalServiceDTO.setLocation(vehicleRegister.getLocation());
                vehicleRentalServiceDTO.setVehicle_register_id(vehicleRegister.getId());
                vehicleRentalServiceDTO.setVehicle_type_id(vehicleRegister.getVehicleType().getId());

                // Thêm DTO vào danh sách kết quả
                vehicleRentalServiceDTOList.add(vehicleRentalServiceDTO);
            }
        }

        return vehicleRentalServiceDTOList;
    }

}

