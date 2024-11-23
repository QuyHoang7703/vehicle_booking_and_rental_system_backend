package com.pbl6.VehicleBookingRental.user.controller.user;

import com.pbl6.VehicleBookingRental.user.dto.ResponseInfo;
import com.pbl6.VehicleBookingRental.user.dto.response.order.ResVnPayDTO;
import com.pbl6.VehicleBookingRental.user.service.OrderService;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final OrderService orderService;

    @GetMapping("orders/create-payment")
    public ResponseEntity<ResVnPayDTO> createPayment(HttpServletRequest request) throws ApplicationException, IdInvalidException {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.orderService.createPayment(request));
    }

    @GetMapping("/vn-pay-callback")
    @Transactional
    public ResponseEntity<ResponseInfo<String>> payCallbackHandler(HttpServletRequest request) throws ApplicationException, IdInvalidException {
        String status = request.getParameter("vnp_ResponseCode");
        String transactionCode = request.getParameter("vnp_TxnRef");
        log.info("Mã giao dịch: " + transactionCode);

        if (status.equals("00")) {
            this.orderService.handlePaymentSuccess(transactionCode);
            log.info("PAYMENT SUCCESSFULLY");
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, "http://localhost:3000/payment-success?transactionId=" + transactionCode)
                    .build();

        } else {
            log.info("PAYMENT UNSUCCESSFULLY");
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, "http://localhost:3000/payment-failure?transactionId=" + transactionCode)
                    .build();
        }
    }
}