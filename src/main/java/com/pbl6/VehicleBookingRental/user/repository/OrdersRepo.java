package com.pbl6.VehicleBookingRental.user.repository;

import com.pbl6.VehicleBookingRental.user.domain.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdersRepo extends JpaRepository<Orders,String> {
    Optional<Orders> findByTransactionCode(String transactionCode);

    @Query("SELECT o FROM Orders o " +
            "WHERE o.order_type = :orderType " +
            "AND o.cancelTime IS NULL")
    List<Orders> findByOrderType(@Param("orderType") String orderType);

//    @Query("SELECT o FROM Orders o " +
//            "JOIN o.orderBusTrip obt " +
//            "JOIN obt.busTripSchedule bts " +
//            "JOIN bts.busTrip bt " +
//            "JOIN bt.busPartner bp " +
//            "WHERE bp.id = :busPartnerId")
//    List<Orders> findOrdersByBusPartnerId(@Param("busPartnerId") int busPartnerId);

}
