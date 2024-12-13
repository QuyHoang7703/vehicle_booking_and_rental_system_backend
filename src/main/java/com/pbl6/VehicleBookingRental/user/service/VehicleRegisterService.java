package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.domain.Images;
import com.pbl6.VehicleBookingRental.user.domain.VehicleType;
import com.pbl6.VehicleBookingRental.user.domain.car_rental.CarRentalOrders;
import com.pbl6.VehicleBookingRental.user.domain.car_rental.CarRentalPartner;
import com.pbl6.VehicleBookingRental.user.domain.car_rental.CarRentalService;
import com.pbl6.VehicleBookingRental.user.domain.car_rental.VehicleRegister;
import com.pbl6.VehicleBookingRental.user.dto.car_rental_DTO.VehicleRentalServiceDTO;
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
    private CarRentalPartnerRepo carRentalPartnerRepo;
    @Autowired
    private ImageService imageService;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private VehicleRentalOrderRepo vehicleRentalOrderRepo;
    @Autowired
    private DateUtil dateUtil;
    @Override
    public VehicleType findVehicleTypeById(int id) {
        return vehicleTypeRepo.findById(id).orElse(null);
    }

    @Override
    public CarRentalPartner findCarRentalPartnerById(int id) {
        return carRentalPartnerRepo.findById(id).orElse(null);
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
                vehicleRentalServiceDTO.setVehicle_register_id(vehicleRegister.getId());
                vehicleRentalServiceDTO.setVehicle_type_id(vehicleRegister.getVehicleType().getId());
                vehicleRentalServiceDTO.setPartnerName(vehicleRegister.getCarRentalPartner().getBusinessPartner().getBusinessName());
                vehicleRentalServiceDTO.setPartnerPhoneNumber(vehicleRegister.getCarRentalPartner().getBusinessPartner().getPhoneOfRepresentative());
                vehicleRentalServiceDTO.setVehicleLife(vehicleRegister.getVehicle_life());
                List<Images> images = imageRepository.findByOwnerTypeAndOwnerId(String.valueOf(ImageOfObjectEnum.VEHICLE_REGISTER), vehicleRegister.getId());
                List<String> imagePaths = Optional.ofNullable(images)
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(image->{
                            return image.getPathImage();
                        }).collect(Collectors.toList());
                vehicleRentalServiceDTO.setImagesVehicleRegister(imagePaths);
            }else{
                return null;
            }
        }

        return vehicleRentalServiceDTO;
    }

    @Override
    public boolean update_vehicle_rental_service(VehicleRentalServiceDTO vehicleRentalServiceDTO,List<MultipartFile> images) {
        Optional<CarRentalService> carRentalService = vehicleRentalServiceRepo.findById(vehicleRentalServiceDTO.getId());
        Optional<VehicleType> vehicleType = vehicleTypeRepo.findById(vehicleRentalServiceDTO.getVehicle_type_id());
        if( carRentalService.isPresent() && vehicleType.isPresent()){
            Optional<VehicleRegister> vehicleRegister = vehicleRegisterRepository.findById(carRentalService.get().getVehicleRegister().getId());
            if(vehicleRegister.isPresent()){
                vehicleRegister.get().setVehicleType(vehicleType.get());
                vehicleRegister.get().setVehicle_life(vehicleRentalServiceDTO.getVehicleLife());
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
                vehicleRegister.get().setVehicle_life(vehicleRentalServiceDTO.getVehicleLife());

            }

            //car_rental_service
            carRentalService.get().setVehicleRegister(vehicleRegister.get());
            carRentalService.get().setType(vehicleRentalServiceDTO.getType());
            carRentalService.get().setPrice(vehicleRentalServiceDTO.getPrice());

            try{
                vehicleRentalServiceRepo.save(carRentalService.get());
                //update images
                imageService.deleteImages(vehicleRegister.get().getId(),"CAR_RENTAL_PARTNER");
                imageService.uploadAndSaveImages(images,String.valueOf(ImageOfObjectEnum.VEHICLE_REGISTER), vehicleRegister.get().getId(),String.valueOf(PartnerTypeEnum.CAR_RENTAL_PARTNER));
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
                    List<Images> images = imageRepository.findByOwnerTypeAndOwnerId(String.valueOf(ImageOfObjectEnum.VEHICLE_REGISTER), vehicleRegister.getId());
                    List<String> imagePaths = Optional.ofNullable(images)
                            .orElse(Collections.emptyList())
                            .stream()
                            .map(image->{
                                return image.getPathImage();
                            }).collect(Collectors.toList());
                    vehicleRentalServiceDTO.setImagesVehicleRegister(imagePaths);
                    //
                    vehicleRentalServiceDTO.setId(carRentalService.getId());
                    vehicleRentalServiceDTO.setPrice(carRentalService.getPrice());
                    vehicleRentalServiceDTO.setType(carRentalService.getType());

                    vehicleRentalServiceDTOList.add(vehicleRentalServiceDTO);
                }
              }
        }

        return vehicleRentalServiceDTOList;
    }

    @Override
    public List<VehicleRentalServiceDTO> get_all_by_service_type(int serviceType, String status,int car_rental_partner_id) {
        // Khởi tạo danh sách DTO để lưu kết quả trả về
        List<VehicleRentalServiceDTO> vehicleRentalServiceDTOList = new ArrayList<>();
        List<CarRentalService> carRentalServiceList = new ArrayList<>();
        if(car_rental_partner_id != -1 ) { // List cho khach hang
            // Lấy danh sách CarRentalService theo điều kiện status
            carRentalServiceList = (serviceType == 2)
                    ? vehicleRentalServiceRepo.findAllByVehicleRegister_CarRentalPartner_Id(car_rental_partner_id)
                    : vehicleRentalServiceRepo.findCarRentalServiceByTypeAndVehicleRegister_StatusAndVehicleRegister_CarRentalPartner_Id
                                                (serviceType, status, car_rental_partner_id);
        }else{
            carRentalServiceList = (serviceType == 2)
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

                List<Images> images = imageRepository.findByOwnerTypeAndOwnerId(String.valueOf(ImageOfObjectEnum.VEHICLE_REGISTER), vehicleRegister.getId());
                List<String> imagePaths = Optional.ofNullable(images)
                        .orElse(Collections.emptyList())
                                .stream()
                                        .map(image->{
                                             return image.getPathImage();
                                        }).collect(Collectors.toList());
                vehicleRentalServiceDTO.setImagesVehicleRegister(imagePaths);
                // Thêm DTO vào danh sách kết quả
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
        List<LocalDate> days = dateUtil.getDaysBetweenDates(startDate, endDate);
        // Lấy số lượng xe thuê theo từng ngày
        Map<LocalDate, Integer> carQuantitiesByDay = getCarQuantitiesByDay(days, car_rental_service_id);

        return  carQuantitiesByDay.values().stream()
                .max(Integer::compareTo) // Tìm giá trị lớn nhất
                .orElse(0); // Nếu Map trống, trả về giá trị mặc định là 0
    }

    private Map<LocalDate, Integer> getCarQuantitiesByDay(List<LocalDate> days, int carRentalServiceId) {
        // Giả sử bạn đã có danh sách các đơn đặt xe từ DB >= currentDate
        List<CarRentalOrders> orders = vehicleRentalOrderRepo.findFutureOrdersByCarRentalServiceId(carRentalServiceId,Instant.now());

        // Tạo một Map để lưu số lượng xe theo từng ngày
        Map<LocalDate, Integer> carQuantitiesByDay = new HashMap<>();

        for (LocalDate day : days) {
            // Lọc các đơn đặt có thời gian thuê trùng với ngày hiện tại
            int totalCarsForDay = orders.stream()
                    .filter(order -> {
                        LocalDate orderStart = order.getStart_rental_time().atZone(ZoneId.systemDefault()).toLocalDate();
                        LocalDate orderEnd = order.getEnd_rental_time().atZone(ZoneId.systemDefault()).toLocalDate();
                        return (day.isEqual(orderStart) || day.isEqual(orderEnd) ||
                                (day.isAfter(orderStart) && day.isBefore(orderEnd)));
                    })
                    .mapToInt(CarRentalOrders::getAmount) // Lấy số lượng xe của từng đơn đặt
                    .sum(); // Tính tổng số lượng xe

            // Gán kết quả vào Map
            carQuantitiesByDay.put(day, totalCarsForDay);
        }
        return carQuantitiesByDay;
    }

}

