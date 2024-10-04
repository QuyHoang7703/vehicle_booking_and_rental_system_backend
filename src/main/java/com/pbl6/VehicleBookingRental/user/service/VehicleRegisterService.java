package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.domain.car_rental.CarRentalService;
import com.pbl6.VehicleBookingRental.user.domain.car_rental.VehicleRegister;
import com.pbl6.VehicleBookingRental.user.domain.dto.car_rental_DTO.VehicleRentalServiceDTO;
import com.pbl6.VehicleBookingRental.user.interfaces.VehicleRegisterInterface;
import com.pbl6.VehicleBookingRental.user.repository.vehicle_rental.VehicleRegisterRepo;
import com.pbl6.VehicleBookingRental.user.repository.vehicle_rental.VehicleRentalServiceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VehicleRegisterService implements VehicleRegisterInterface {
    @Autowired
    private VehicleRegisterRepo vehicleRegisterRepository;
    @Autowired
    private VehicleRentalServiceRepo vehicleRentalServiceRepo;
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
    public List<VehicleRentalServiceDTO> get_all_by_service_type(int serviceType, String status) {
        // Khởi tạo danh sách DTO để lưu kết quả trả về
        List<VehicleRentalServiceDTO> vehicleRentalServiceDTOList = new ArrayList<>();

        // Lấy danh sách CarRentalService theo điều kiện status
        List<CarRentalService> carRentalServiceList = status.equals("all")
                ? vehicleRentalServiceRepo.findAll()
                : vehicleRentalServiceRepo.findCarRentalServiceByTypeAndVehicleRegister_Status(serviceType, status);
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
                vehicleRentalServiceDTO.setDateOfStatus(vehicleRegister.getDateOfStatus());
                vehicleRentalServiceDTO.setDiscountPercentage(vehicleRegister.getDiscountPercentage());
                vehicleRentalServiceDTO.setCarDeposit(vehicleRegister.getCarDeposit());
                vehicleRentalServiceDTO.setReservationFees(vehicleRegister.getReservationFees());
                vehicleRentalServiceDTO.setUlties(vehicleRegister.getUlties());
                vehicleRentalServiceDTO.setPolicy(vehicleRegister.getPolicy());
                vehicleRentalServiceDTO.setRatingTotal(vehicleRegister.getRatingTotal());
                vehicleRentalServiceDTO.setAmount(vehicleRegister.getAmount());

                // Thêm DTO vào danh sách kết quả
                vehicleRentalServiceDTOList.add(vehicleRentalServiceDTO);
            }
        }

        return vehicleRentalServiceDTOList;
    }

}

