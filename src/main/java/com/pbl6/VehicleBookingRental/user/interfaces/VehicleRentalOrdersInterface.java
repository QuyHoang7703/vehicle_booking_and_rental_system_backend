package com.pbl6.VehicleBookingRental.user.interfaces;

import com.pbl6.VehicleBookingRental.user.domain.Orders;
import com.pbl6.VehicleBookingRental.user.dto.car_rental_DTO.VehicleRentalOrdersDTO;
import com.pbl6.VehicleBookingRental.user.dto.redis.OrderVehicleRentalRedisDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.order.ResOrderKey;
import com.pbl6.VehicleBookingRental.user.dto.response.order.ResVehicleRentalOrderDetailDTO;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;

import java.time.Instant;
import java.util.List;

public interface VehicleRentalOrdersInterface {
    OrderVehicleRentalRedisDTO create_order_Rental(VehicleRentalOrdersDTO vehicleRentalOrdersDTO) throws ApplicationException;
    ResOrderKey getKeyOfOrderVehicleRentalRedisDTO(OrderVehicleRentalRedisDTO orderVehicleRentalRedisDTO) throws ApplicationException;
    ResVehicleRentalOrderDetailDTO convertToResVehicleRentalOrderDetailDTO(Orders orders) throws ApplicationException;
    public double calculatePriceOrderByStartAndEndDate(Instant startRentalTime, Instant endRentalTime, double priceOneDay);
    public List<ResVehicleRentalOrderDetailDTO> getOrderByServiceId(int id);
    public List<ResVehicleRentalOrderDetailDTO> getAllOrder();
    public List<ResVehicleRentalOrderDetailDTO> getAllOrderUser(String status);

    public List<ResVehicleRentalOrderDetailDTO> getOrderByStatus(String status);
    public boolean cancelVehicleRentalService(String vehicleRentalOrderId) throws ApplicationException;
    public boolean updateStatusRentalOrder(String orderId , String status);
}
