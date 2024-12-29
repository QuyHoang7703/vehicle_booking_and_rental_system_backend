package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.domain.BusinessPartner;
import com.pbl6.VehicleBookingRental.user.domain.Images;
import com.pbl6.VehicleBookingRental.user.domain.VehicleType;
import com.pbl6.VehicleBookingRental.user.domain.car_rental.CarRentalOrders;
import com.pbl6.VehicleBookingRental.user.domain.car_rental.CarRentalPartner;
import com.pbl6.VehicleBookingRental.user.domain.car_rental.CarRentalService;
import com.pbl6.VehicleBookingRental.user.domain.car_rental.VehicleRegister;
import com.pbl6.VehicleBookingRental.user.dto.car_rental_DTO.VehicleRentalServiceDTO;
import com.pbl6.VehicleBookingRental.user.dto.car_rental_DTO.VehicleRentalStatisticDTO;
import com.pbl6.VehicleBookingRental.user.interfaces.VehicleRegisterInterface;
import com.pbl6.VehicleBookingRental.user.repository.image.ImageRepository;
import com.pbl6.VehicleBookingRental.user.repository.vehicle_rental.*;
import com.pbl6.VehicleBookingRental.user.util.DateUtil;
import com.pbl6.VehicleBookingRental.user.util.constant.ImageOfObjectEnum;
import com.pbl6.VehicleBookingRental.user.util.constant.PartnerTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class VehicleRegisterService implements VehicleRegisterInterface {
    @Autowired
    private VehicleRegisterRepo vehicleRegisterRepository;
    @Autowired
    private VehicleRentalServiceRepo vehicleRentalServiceRepo;
    @Autowired
    private VehicleTypeRepository vehicleTypeRepo;
    @Autowired
    private ImageService imageService;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private VehicleRentalOrderRepo vehicleRentalOrderRepo;
    @Autowired
    private DateUtil dateUtil;
    @Autowired
    private BusinessPartnerService businessPartnerService;
    @Override
    public VehicleType findVehicleTypeById(int id) {
        return vehicleTypeRepo.findById(id).orElse(null);
    }

    @Override
    public boolean register_vehicle(VehicleRegister vehicleRegister, List<MultipartFile> images) {
        try{
            VehicleRegister savedVehicleRegister = vehicleRegisterRepository.save(vehicleRegister);
            imageService.uploadAndSaveImages(images,String.valueOf(ImageOfObjectEnum.VEHICLE_REGISTER), vehicleRegister.getId(),String.valueOf(PartnerTypeEnum.CAR_RENTAL_PARTNER));
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
    public List<VehicleRentalServiceDTO> getVehicleRentalServiceByVehicleRegister(int id){
        List<VehicleRentalServiceDTO> vehicleRentalServiceDTOList = new ArrayList<>();
        Optional<VehicleRegister> vehicleRegisterOptional = vehicleRegisterRepository.findById(id);
        if(vehicleRegisterOptional.isPresent()){

            List<CarRentalService> carRentalServiceList = vehicleRegisterOptional.get().getTypeOfRentalServiceList();
            for(CarRentalService i : carRentalServiceList){
                VehicleRentalServiceDTO vehicleRentalServiceDTO = new VehicleRentalServiceDTO();

                VehicleRegister vehicleRegister = vehicleRegisterOptional.get();

                // Thiết lập các thuộc tính từ VehicleRegister
                vehicleRentalServiceDTO.setVehicle_register_id(vehicleRegister.getId());
                vehicleRentalServiceDTO.setManufacturer(vehicleRegister.getManufacturer());
                vehicleRentalServiceDTO.setVehicleLife(vehicleRegister.getVehicle_life());
                vehicleRentalServiceDTO.setDescription(vehicleRegister.getDescription());
                vehicleRentalServiceDTO.setVehicle_type(vehicleRegister.getVehicleType().getName());
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
                vehicleRentalServiceDTO.setVehicle_type_id(vehicleRegister.getVehicleType().getId());
                vehicleRentalServiceDTO.setPartnerName(vehicleRegister.getCarRentalPartner().getBusinessPartner().getBusinessName());
                vehicleRentalServiceDTO.setPartnerPhoneNumber(vehicleRegister.getCarRentalPartner().getBusinessPartner().getPhoneOfRepresentative());
                vehicleRentalServiceDTO.setPartnerId(vehicleRegister.getCarRentalPartner().getId());

                vehicleRentalServiceDTO.setVehicle_rental_service_id(i.getId());
                vehicleRentalServiceDTO.setType(i.getType());
                if(i.getType() == 0){
                    vehicleRentalServiceDTO.setSelfDriverPrice(i.getPrice());
                }else if(i.getType() == 2){
                    vehicleRentalServiceDTO.setDriverPrice(i.getPrice());
                }

                vehicleRentalServiceDTOList.add(vehicleRentalServiceDTO);
            }
        }
        return vehicleRentalServiceDTOList;
    }
    @Override
    public VehicleRentalServiceDTO get_vehicle_rental_service_by_vehicle_register_id(int vehicleRegisterId) {
        VehicleRentalServiceDTO vehicleRentalServiceDTO = new VehicleRentalServiceDTO();

        Optional<VehicleRegister> vehicleRegisterOptional = vehicleRegisterRepository.findById(vehicleRegisterId);
        if (vehicleRegisterOptional.isPresent()) {
            VehicleRegister vehicleRegister = vehicleRegisterOptional.get();

            // Thiết lập các thuộc tính từ VehicleRegister
            vehicleRentalServiceDTO.setVehicle_register_id(vehicleRegister.getId());
            vehicleRentalServiceDTO.setManufacturer(vehicleRegister.getManufacturer());
            vehicleRentalServiceDTO.setVehicleLife(vehicleRegister.getVehicle_life());
            vehicleRentalServiceDTO.setDescription(vehicleRegister.getDescription());
            vehicleRentalServiceDTO.setVehicle_type(vehicleRegister.getVehicleType().getName());
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
            vehicleRentalServiceDTO.setVehicle_type_id(vehicleRegister.getVehicleType().getId());
            vehicleRentalServiceDTO.setPartnerName(vehicleRegister.getCarRentalPartner().getBusinessPartner().getBusinessName());
            vehicleRentalServiceDTO.setPartnerPhoneNumber(vehicleRegister.getCarRentalPartner().getBusinessPartner().getPhoneOfRepresentative());
            vehicleRentalServiceDTO.setPartnerId(vehicleRegister.getCarRentalPartner().getId());

            // Thiết lập thông tin từ CarRentalService nếu tồn tại
            List<CarRentalService> serviceList = vehicleRegister.getTypeOfRentalServiceList();
            if (serviceList != null && !serviceList.isEmpty()) {
                for (CarRentalService service : serviceList) {
                    if (service.getType() == 0) {
                        vehicleRentalServiceDTO.setSelfDriverPrice(service.getPrice());
                    } else if (service.getType() == 1) {
                        vehicleRentalServiceDTO.setDriverPrice(service.getPrice());
                    }
                    // Lấy type của dịch vụ đầu tiên nếu cần
                    vehicleRentalServiceDTO.setType(service.getType());
                }
                if(serviceList.size() > 1){
                    vehicleRentalServiceDTO.setType(2);
                }
            }

            // Thiết lập danh sách hình ảnh
            List<Images> images = imageRepository.findByOwnerTypeAndOwnerId(
                    String.valueOf(ImageOfObjectEnum.VEHICLE_REGISTER),
                    vehicleRegister.getId()
            );
            List<String> imagePaths = Optional.ofNullable(images)
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(Images::getPathImage)
                    .collect(Collectors.toList());
            vehicleRentalServiceDTO.setImagesVehicleRegister(imagePaths);
        } else {
            return null; // Trả về null nếu VehicleRegister không tồn tại
        }

        return vehicleRentalServiceDTO;
    }
    @Override
    public VehicleRentalServiceDTO get_vehicle_rental_service_by_vehicleRentalServiceID(int vehicleRentalID) {
        VehicleRentalServiceDTO vehicleRentalServiceDTO = new VehicleRentalServiceDTO();

        Optional<CarRentalService> carRentalService = vehicleRentalServiceRepo.findById(vehicleRentalID);
        if (carRentalService.isPresent()) {
            VehicleRegister vehicleRegister = carRentalService.get().getVehicleRegister();

            // Thiết lập các thuộc tính từ VehicleRegister
            vehicleRentalServiceDTO.setVehicle_register_id(vehicleRegister.getId());
            vehicleRentalServiceDTO.setManufacturer(vehicleRegister.getManufacturer());
            vehicleRentalServiceDTO.setVehicleLife(vehicleRegister.getVehicle_life());
            vehicleRentalServiceDTO.setDescription(vehicleRegister.getDescription());
            vehicleRentalServiceDTO.setVehicle_type(vehicleRegister.getVehicleType().getName());
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
            vehicleRentalServiceDTO.setVehicle_type_id(vehicleRegister.getVehicleType().getId());
            vehicleRentalServiceDTO.setPartnerName(vehicleRegister.getCarRentalPartner().getBusinessPartner().getBusinessName());
            vehicleRentalServiceDTO.setPartnerPhoneNumber(vehicleRegister.getCarRentalPartner().getBusinessPartner().getPhoneOfRepresentative());
            vehicleRentalServiceDTO.setPartnerId(vehicleRegister.getCarRentalPartner().getId());

            // Thiết lập thông tin từ CarRentalService nếu tồn tại
            vehicleRentalServiceDTO.setVehicle_rental_service_id(carRentalService.get().getId());
            vehicleRentalServiceDTO.setType(carRentalService.get().getType());

            if (carRentalService.get().getType() == 0) {
                vehicleRentalServiceDTO.setSelfDriverPrice(carRentalService.get().getPrice());
            } else if (carRentalService.get().getType() == 1) {
                vehicleRentalServiceDTO.setDriverPrice(carRentalService.get().getPrice());
            }

            // Thiết lập danh sách hình ảnh
            List<Images> images = imageRepository.findByOwnerTypeAndOwnerId(
                    String.valueOf(ImageOfObjectEnum.VEHICLE_REGISTER),
                    vehicleRegister.getId()
            );
            List<String> imagePaths = Optional.ofNullable(images)
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(Images::getPathImage)
                    .collect(Collectors.toList());
            vehicleRentalServiceDTO.setImagesVehicleRegister(imagePaths);
        } else {
            return null; // Trả về null nếu VehicleRegister không tồn tại
        }

        return vehicleRentalServiceDTO;
    }


    @Override
    public boolean update_vehicle_rental_service(VehicleRentalServiceDTO vehicleRentalServiceDTO, List<MultipartFile> images) {
        Optional<VehicleRegister> vehicleRegister = vehicleRegisterRepository.findById(vehicleRentalServiceDTO.getVehicle_register_id());
        Optional<VehicleType> vehicleType = vehicleTypeRepo.findById(vehicleRentalServiceDTO.getVehicle_type_id());

        if (vehicleRegister.isPresent() && vehicleType.isPresent()) {
            // Cập nhật thông tin VehicleRegister
            VehicleRegister register = vehicleRegister.get();
            register.setVehicleType(vehicleType.get());
            register.setVehicle_life(vehicleRentalServiceDTO.getVehicleLife());
            register.setManufacturer(vehicleRentalServiceDTO.getManufacturer());
            register.setDescription(vehicleRentalServiceDTO.getDescription());
            register.setQuantity(vehicleRentalServiceDTO.getQuantity());
            register.setStatus(vehicleRentalServiceDTO.getStatus());
            register.setDate_of_status(vehicleRentalServiceDTO.getDate_of_status());
            register.setDiscount_percentage(vehicleRentalServiceDTO.getDiscount_percentage());
            register.setCar_deposit(vehicleRentalServiceDTO.getCar_deposit());
            register.setReservation_fees(vehicleRentalServiceDTO.getReservation_fees());
            register.setUlties(vehicleRentalServiceDTO.getUlties());
            register.setPolicy(vehicleRentalServiceDTO.getPolicy());
            register.setAmount(vehicleRentalServiceDTO.getAmount());
            register.setLocation(vehicleRentalServiceDTO.getLocation());

            // Cập nhật danh sách các dịch vụ cho thuê
            List<CarRentalService> serviceList = register.getTypeOfRentalServiceList();
            if (serviceList != null && !serviceList.isEmpty()) {
                for (CarRentalService service : serviceList) {
                    // Nếu không phải type 2, cập nhật lại loại dịch vụ
                    if (vehicleRentalServiceDTO.getType() != 2) {
                        service.setType(vehicleRentalServiceDTO.getType());
                    }
                    // Cập nhật giá theo type của DTO
                    if (vehicleRentalServiceDTO.getType() == 0) {
                        service.setPrice(vehicleRentalServiceDTO.getSelfDriverPrice());
                    } else if (vehicleRentalServiceDTO.getType() == 1) {
                        service.setPrice(vehicleRentalServiceDTO.getDriverPrice());
                    } else if (vehicleRentalServiceDTO.getType() == 2) {
                        // Cập nhật cả hai giá trị nếu type = 2
                        if (service.getType() == 0) {
                            service.setPrice(vehicleRentalServiceDTO.getSelfDriverPrice());
                        } else if (service.getType() == 1) {
                            service.setPrice(vehicleRentalServiceDTO.getDriverPrice());
                        }
                    }
                }
            }


            try {
                // Lưu thông tin VehicleRegister
                vehicleRegisterRepository.save(register);

                // Cập nhật ảnh
                imageService.deleteImages(register.getId(), "CAR_RENTAL_PARTNER");
                imageService.uploadAndSaveImages(
                        images,
                        String.valueOf(ImageOfObjectEnum.VEHICLE_REGISTER),
                        register.getId(),
                        String.valueOf(PartnerTypeEnum.CAR_RENTAL_PARTNER)
                );

                return true;
            } catch (Exception e) {
                System.out.println("Error updating VehicleRegister: " + e.getLocalizedMessage());
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
    public List<VehicleRentalServiceDTO> filter_by_vehicle_attribute(String location, String manufacturer, String vehicle_type, int service_type, String startDate,String endDate) {
        List<VehicleRentalServiceDTO> vehicleRentalServiceDTOList = new ArrayList<>();

        List<VehicleRegister> vehicleRegisterList =
                vehicleRegisterRepository.findVehicleRegisterByLocationOrManufacturerOrVehicleType_Name
                        (location,manufacturer,vehicle_type);

        for(VehicleRegister vehicleRegister : vehicleRegisterList){
            List<CarRentalService> carRentalServiceList = vehicleRegister.getTypeOfRentalServiceList();

            for(CarRentalService carRentalService : carRentalServiceList){
                //Lấy ra các car_ rental_service bằng service_type, value = 2 thì lấy cả hai
                if(service_type == 2 || carRentalService.getType() == service_type){
                    //Tính toán số lượng xe cho mỗi dịch vụ dựa theo order trong ngày
                    // Định dạng chuỗi ngày giờ
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy").withZone(ZoneId.systemDefault());

                    // Khai báo giá trị mặc định hoặc xử lý null
                    Instant startInstant = null;
                    Instant endInstant = null;

                    if (startDate != null && !startDate.isEmpty()) {
                        try {
                            startInstant = Instant.from(formatter.parse(startDate));
                        } catch (DateTimeParseException e) {
                            // Xử lý lỗi nếu chuỗi không hợp lệ
                            System.out.println("Invalid start date format: " + startDate);
                        }
                    }

                    if (endDate != null && !endDate.isEmpty()) {
                        try {
                            endInstant = Instant.from(formatter.parse(endDate));
                        } catch (DateTimeParseException e) {
                            // Xử lý lỗi nếu chuỗi không hợp lệ
                            System.out.println("Invalid end date format: " + endDate);
                        }
                    }
                    Instant now = Instant.now();  // Lấy thời gian hiện tại một lần duy nhất
                    int amount = (startInstant == null || endInstant == null)
                            ? calculateAmountByOrder(now, now, carRentalService.getId())  // Sử dụng giá trị `now` cho cả hai tham số
                            : calculateAmountByOrder(startInstant, endInstant, carRentalService.getId());
                    VehicleRentalServiceDTO vehicleRentalServiceDTO = new VehicleRentalServiceDTO();

                    // Thiết lập các thuộc tính từ đối tượng VehicleRegister
                    vehicleRentalServiceDTO.setManufacturer(vehicleRegister.getManufacturer());
                    vehicleRentalServiceDTO.setDescription(vehicleRegister.getDescription());
                    vehicleRentalServiceDTO.setQuantity(vehicleRegister.getQuantity());
                    vehicleRentalServiceDTO.setVehicle_type(vehicleRegister.getVehicleType().getName());
                    vehicleRentalServiceDTO.setStatus(vehicleRegister.getStatus());
                    vehicleRentalServiceDTO.setDate_of_status(vehicleRegister.getDate_of_status());
                    vehicleRentalServiceDTO.setDiscount_percentage(vehicleRegister.getDiscount_percentage());
                    vehicleRentalServiceDTO.setCar_deposit(vehicleRegister.getCar_deposit());
                    vehicleRentalServiceDTO.setReservation_fees(vehicleRegister.getReservation_fees());
                    vehicleRentalServiceDTO.setUlties(vehicleRegister.getUlties());
                    vehicleRentalServiceDTO.setPolicy(vehicleRegister.getPolicy());
                    vehicleRentalServiceDTO.setRating_total(vehicleRegister.getRating_total());
                    vehicleRentalServiceDTO.setAmount(vehicleRegister.getAmount() - amount);
                    vehicleRentalServiceDTO.setVehicle_register_id(vehicleRegister.getId());
                    vehicleRentalServiceDTO.setLocation(vehicleRegister.getLocation());
                    vehicleRentalServiceDTO.setVehicle_type_id(vehicleRegister.getVehicleType().getId());
                    vehicleRentalServiceDTO.setVehicleLife(vehicleRegister.getVehicle_life());
                    vehicleRentalServiceDTO.setPartnerName(vehicleRegister.getCarRentalPartner().getBusinessPartner().getBusinessName());
                    vehicleRentalServiceDTO.setPartnerPhoneNumber(vehicleRegister.getCarRentalPartner().getBusinessPartner().getPhoneOfRepresentative());
                    vehicleRentalServiceDTO.setPartnerId(vehicleRegister.getCarRentalPartner().getId());

                    List<Images> images = imageRepository.findByOwnerTypeAndOwnerId(String.valueOf(ImageOfObjectEnum.VEHICLE_REGISTER), vehicleRegister.getId());
                    List<String> imagePaths = Optional.ofNullable(images)
                            .orElse(Collections.emptyList())
                            .stream()
                            .map(image->{
                                return image.getPathImage();
                            }).collect(Collectors.toList());
                    vehicleRentalServiceDTO.setImagesVehicleRegister(imagePaths);
                    //
                    vehicleRentalServiceDTO.setVehicle_rental_service_id(carRentalService.getId());
                    vehicleRentalServiceDTO.setType(carRentalService.getType());
                    if(carRentalService.getType() == 0){
                        vehicleRentalServiceDTO.setSelfDriverPrice(carRentalService.getPrice());
                    }else{
                        vehicleRentalServiceDTO.setDriverPrice(carRentalService.getPrice());
                    }

                    vehicleRentalServiceDTOList.add(vehicleRentalServiceDTO);
                }
              }
        }
        return vehicleRentalServiceDTOList;
    }
    @Override
    public List<VehicleRentalServiceDTO> get_all_by_service_type(int serviceType, String status) {
        int car_rental_partner_id = 0;

        try {
            BusinessPartner businessPartner = businessPartnerService.getCurrentBusinessPartner(PartnerTypeEnum.CAR_RENTAL_PARTNER);
            car_rental_partner_id = businessPartner.getCarRentalPartner().getId();
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }

        // Khởi tạo danh sách DTO để lưu kết quả trả về
        List<VehicleRentalServiceDTO> vehicleRentalServiceDTOList = new ArrayList<>();

        // Lấy danh sách VehicleRegister theo đối tác và điều kiện status
        List<VehicleRegister> vehicleRegisterList = vehicleRegisterRepository.findVehicleRegisterByCarRentalPartnerIdAndStatus(car_rental_partner_id,status);
        List<VehicleRegister> vehicleRegisters = vehicleRegisterList.stream()
                .filter(v -> v.getTypeOfRentalServiceList().stream()
                        .anyMatch(t -> t.getType() == serviceType || serviceType == 2 ))
                .collect(Collectors.toList());
        for (VehicleRegister vehicleRegister : vehicleRegisters) {
            if (vehicleRegister != null) {
                VehicleRentalServiceDTO vehicleRentalServiceDTO = new VehicleRentalServiceDTO();

                // Kiểm tra danh sách typeOfRentalServiceList
                List<CarRentalService> rentalServiceList = vehicleRegister.getTypeOfRentalServiceList();
                if (rentalServiceList.size() > 1) {
                    vehicleRentalServiceDTO.setType(2); // Nhiều dịch vụ
                } else {
                    rentalServiceList.stream().findFirst().ifPresent(r -> vehicleRentalServiceDTO.setType(r.getType()));
                }

                for (CarRentalService rentalService : rentalServiceList) {
                        if (rentalService.getType() == 0) {
                            vehicleRentalServiceDTO.setSelfDriverPrice(rentalService.getPrice());
                        } else {
                            vehicleRentalServiceDTO.setDriverPrice(rentalService.getPrice());
                        }
                }

                // Thiết lập các thuộc tính từ VehicleRegister
                vehicleRentalServiceDTO.setManufacturer(vehicleRegister.getManufacturer());
                vehicleRentalServiceDTO.setDescription(vehicleRegister.getDescription());
                vehicleRentalServiceDTO.setVehicle_type(vehicleRegister.getVehicleType().getName());
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
                vehicleRentalServiceDTO.setVehicleLife(vehicleRegister.getVehicle_life());

                vehicleRentalServiceDTO.setPartnerName(vehicleRegister.getCarRentalPartner().getBusinessPartner().getBusinessName());
                vehicleRentalServiceDTO.setPartnerPhoneNumber(vehicleRegister.getCarRentalPartner().getBusinessPartner().getPhoneOfRepresentative());
                vehicleRentalServiceDTO.setPartnerId(vehicleRegister.getCarRentalPartner().getId());


                // Lấy danh sách hình ảnh
                List<Images> images = imageRepository.findByOwnerTypeAndOwnerId(
                        String.valueOf(ImageOfObjectEnum.VEHICLE_REGISTER), vehicleRegister.getId());
                List<String> imagePaths = Optional.ofNullable(images)
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(Images::getPathImage)
                        .collect(Collectors.toList());
                vehicleRentalServiceDTO.setImagesVehicleRegister(imagePaths);

                // Thêm vào danh sách kết quả
                vehicleRentalServiceDTOList.add(vehicleRentalServiceDTO);
            }
        }

        return vehicleRentalServiceDTOList;
    }
    @Override
    public List<String> getExistFilterValue(String properties){
        List<String> result = new ArrayList<>();
        if(properties.equalsIgnoreCase("location")){
            result = vehicleRegisterRepository.findDistinctLocation();
        }
        if(properties.equalsIgnoreCase("manufacturer")){
            result = vehicleRegisterRepository.findDistinctManufacturer();
        }
        return result;
    }
    private int calculateAmountByOrder(Instant startDate,Instant endDate,int car_rental_service_id){
        // Lấy danh sách các ngày
        List<Map<LocalDateTime,LocalDateTime>> dayTimes = dateUtil.getDaysBetweenDateTimes(startDate, endDate);
        // Lấy số lượng xe thuê theo từng ngày
        Map<LocalDate, Integer> carQuantitiesByDay = getCarAmountsByDay(dayTimes, car_rental_service_id);

        return  carQuantitiesByDay.values().stream()
                .max(Integer::compareTo) // Tìm giá trị lớn nhất
                .orElse(0); // Nếu Map trống, trả về giá trị mặc định là 0
    }

    private Map<LocalDate, Integer> getCarAmountsByDay(List<Map<LocalDateTime, LocalDateTime>> dayTimes, int carRentalServiceId) {
        // Giả sử bạn đã có danh sách các đơn đặt xe từ DB
        List<CarRentalOrders> orders = vehicleRentalOrderRepo.findFutureOrdersByCarRentalServiceId(carRentalServiceId, Instant.now(),"not_returned");

        // Tạo một Map để lưu số lượng xe theo từng ngày
        Map<LocalDate, Integer> carAmountsByDay = new HashMap<>();

        for (Map<LocalDateTime, LocalDateTime> range : dayTimes) {
            for (Map.Entry<LocalDateTime, LocalDateTime> entry : range.entrySet()) {
                LocalDateTime dayStart = entry.getKey();
                LocalDateTime dayEnd = entry.getValue();


                // Lọc các đơn đặt có thời gian thuê trùng với khoảng thời gian hiện tại
                int totalCarsForDay = orders.stream()
                        .filter(order -> {
                            LocalDateTime orderStart = order.getStart_rental_time().atZone(ZoneId.systemDefault()).toLocalDateTime();
                            LocalDateTime orderEnd = order.getEnd_rental_time().atZone(ZoneId.systemDefault()).toLocalDateTime();
                            return (dayStart.isEqual(orderStart) || dayEnd.isEqual(orderEnd) ||
                                    (dayStart.isBefore(orderEnd) && dayEnd.isAfter(orderStart)));
                        })
                        .mapToInt(CarRentalOrders::getAmount) // Lấy số lượng xe của từng đơn đặt
                        .sum(); // Tính tổng số lượng xe

                // Gán kết quả vào Map
                carAmountsByDay.put(dayStart.toLocalDate(), totalCarsForDay);
            }
        }
        return carAmountsByDay;
    }
}

