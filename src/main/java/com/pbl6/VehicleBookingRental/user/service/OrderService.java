package com.pbl6.VehicleBookingRental.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pbl6.VehicleBookingRental.user.domain.Orders;
import com.pbl6.VehicleBookingRental.user.dto.response.order.ResVnPayDTO;
import com.pbl6.VehicleBookingRental.user.util.constant.OrderTypeEnum;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import jakarta.servlet.http.HttpServletRequest;

public interface OrderService {
    ResVnPayDTO createPayment(HttpServletRequest request) throws ApplicationException, IdInvalidException;
    String handlePaymentSuccess(String transactionCode) throws ApplicationException, IdInvalidException, JsonProcessingException;
    Orders findByTransactionCode(String transactionCode) throws ApplicationException;

}
