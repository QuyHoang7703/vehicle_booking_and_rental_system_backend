package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.dto.response.order.ResVnPayDTO;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import jakarta.servlet.http.HttpServletRequest;

public interface OrderService {
    ResVnPayDTO createOrder(HttpServletRequest request) throws ApplicationException, IdInvalidException;
    void handlePaymentSuccess(String transactionCode) throws ApplicationException, IdInvalidException;
}
