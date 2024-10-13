package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.util.error.IdInValidException;

public interface TokenService {
    public void createToken(String email) throws IdInValidException;

    public boolean isValidToken(String token);

    public void sendRequestForgotPassword(String email, String name, String token);
}
