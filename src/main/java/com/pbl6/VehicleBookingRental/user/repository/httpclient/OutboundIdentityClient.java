package com.pbl6.VehicleBookingRental.user.repository.httpclient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

import com.pbl6.VehicleBookingRental.user.dto.request.oauth2.ExchangeTokenRequest;
import com.pbl6.VehicleBookingRental.user.dto.response.oauth2.ExchangeTokenResponse;

import feign.QueryMap;

@FeignClient(name = "outbound-identity", url = "https://oauth2.googleapis.com")
public interface OutboundIdentityClient {
    @PostMapping(value = "/token", produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ExchangeTokenResponse exchangeToken(@QueryMap ExchangeTokenRequest request);
    //    ExchangeTokenResponse exchangeToken(@QueryMap ExchangeTokenRequest request);

}