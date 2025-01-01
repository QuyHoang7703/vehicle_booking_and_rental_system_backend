package com.pbl6.VehicleBookingRental.user.controller.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pbl6.VehicleBookingRental.user.domain.Orders;
import com.pbl6.VehicleBookingRental.user.dto.ResponseInfo;
import com.pbl6.VehicleBookingRental.user.dto.response.order.ResVnPayDTO;
import com.pbl6.VehicleBookingRental.user.service.OrderBusTripService;
import com.pbl6.VehicleBookingRental.user.service.OrderService;
import com.pbl6.VehicleBookingRental.user.service.VehicleRentalOrderService;
import com.pbl6.VehicleBookingRental.user.util.constant.OrderTypeEnum;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final OrderService orderService;
    private final OrderBusTripService orderBusTripService;
    private final VehicleRentalOrderService vehicleRentalOrderService;

    @GetMapping("orders/create-payment")
    public ResponseEntity<ResVnPayDTO> createPayment(HttpServletRequest request) throws ApplicationException, IdInvalidException {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.orderService.createPayment(request));
    }

    @GetMapping("/vn-pay-callback")
    @Transactional
    public ResponseEntity<ResponseInfo<String>> payCallbackHandler(HttpServletRequest request) throws Exception {
        // Lấy URL đầy đủ từ request
//        String fullUrl = request.getRequestURL().toString();
//        String queryString = request.getQueryString();
//        String fullUrlWithQuery = fullUrl + (queryString != null ? "?" + queryString : "");
//
//        log.info("Full request URL: " + fullUrlWithQuery); // In ra URL đầy đủ
//
//        // Lấy và log toàn bộ tham số trả về
//        Map<String, String[]> parameterMap = request.getParameterMap();
//        parameterMap.forEach((key, value) -> {
//            log.info("Param: " + key + " = " + String.join(",", value));
//        });


        String status = request.getParameter("vnp_ResponseCode");
        String transactionCode = request.getParameter("vnp_TxnRef");
        log.info("Transaction code: " + transactionCode);
        log.info("Status of transaction: " + status);

        if (status.equals("00")) {
            String orderType = this.orderService.handlePaymentSuccess(transactionCode);
            log.info("PAYMENT SUCCESSFULLY");

            return ResponseEntity.status(HttpStatus.FOUND)
//                    .header(HttpHeaders.LOCATION, "http://localhost:3000/payment-success?transactionCode=" + transactionCode + "&orderType=" + orderType)
                    .header(HttpHeaders.LOCATION, "http://150.95.110.230:3000/payment-success?transactionCode=" + transactionCode + "&orderType=" + orderType)
                    .build();

        } else {
            log.info("PAYMENT UNSUCCESSFULLY");
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, "http://150.95.110.230:3000/payment-failure?transactionCode=" + transactionCode)
                    .build();
        }
    }

    @GetMapping("/orders/detail")
    public ResponseEntity<?> getDetailOrderBusTripSchedule(@RequestParam("transactionCode") String transactionCode,
                                                           @RequestParam("orderType") OrderTypeEnum orderType) throws ApplicationException{
        if(orderType==OrderTypeEnum.BUS_TRIP_ORDER) {
            Orders order = this.orderService.findByTransactionCode(transactionCode);
            return ResponseEntity.status(HttpStatus.OK).body(this.orderBusTripService.convertToResOrderBusTripDetailDTO(order));
        }
        if(orderType==OrderTypeEnum.VEHICLE_RENTAL_ORDER) {
            Orders order = this.orderService.findByTransactionCode(transactionCode);

            return ResponseEntity.status(HttpStatus.OK).body(this.vehicleRentalOrderService.convertToResVehicleRentalOrderDetailDTO(order));
        }
        // if different types

        return  ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @PostMapping("orders/payment-from-mobile")
    public ResponseEntity<Map<String, String>> handlePaymentFromMobile(@RequestParam("vnp_ResponseCode") String vnp_ResponseCode,
                                                        @RequestParam("vnp_TxnRef") String vnp_TxnRef) throws ApplicationException, IdInvalidException, JsonProcessingException {
        if(vnp_ResponseCode.equals("00")) {
            Map<String, String> params = new LinkedHashMap<>();
            String orderType = this.orderService.handlePaymentSuccess(vnp_TxnRef);
            params.put("transactionCode: ", vnp_TxnRef);
            params.put("orderType", orderType);
            log.info("PAYMENT SUCCESSFULLY");
            return ResponseEntity.status(HttpStatus.OK).body(params);
        }else{
            log.info("PAYMENT UNSUCCESSFULLY");
            return ResponseEntity.status(HttpStatus.OK).body(null);
        }

    }


}
