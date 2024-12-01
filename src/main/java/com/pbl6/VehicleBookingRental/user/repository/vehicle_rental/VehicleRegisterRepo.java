package com.pbl6.VehicleBookingRental.user.repository.vehicle_rental;

import com.pbl6.VehicleBookingRental.user.domain.car_rental.VehicleRegister;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRegisterRepo extends JpaRepository<VehicleRegister,Integer> {
    @Transactional
    @Modifying
    @Query(value="update  vehicle_register set status = ?2 where vehicle_register.id =?1 ",nativeQuery = true)
    public int updateStatus(int vehicle_register_id,String status);

    @Query("SELECT u FROM VehicleRegister u INNER JOIN VehicleType v ON u.vehicleType.id = v.id " +
            "WHERE (:location IS NULL OR u.location LIKE CONCAT('%', :location, '%')) " +
            "AND (:manufacturer IS NULL OR u.manufacturer LIKE CONCAT('%', :manufacturer, '%')) " +
            "AND (:vehicle_type_name IS NULL OR v.name LIKE CONCAT('%', :vehicle_type_name, '%'))")
    public List<VehicleRegister> findVehicleRegisterByLocationOrManufacturerOrVehicleType_Name(
            @Param("location") String location,
            @Param("manufacturer") String manufacturer,
            @Param("vehicle_type_name") String vehicleType);
    // Lấy danh sách giá trị DISTINCT của thuộc tính 'color'
    @Query("SELECT DISTINCT v.location FROM VehicleRegister v")
    List<String> findDistinctLocation();

    // Lấy danh sách giá trị DISTINCT của thuộc tính 'brand'
    @Query("SELECT DISTINCT v.manufacturer FROM VehicleRegister v")
    List<String> findDistinctManufacturer();

}
