package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;

import java.io.IOException;

public interface TokenService {
    public void createToken(String email) throws IdInvalidException, IOException;

    public boolean isValidToken(String token);

    public void sendRequestForgotPassword(String email, String name, String token) throws IOException;
}
