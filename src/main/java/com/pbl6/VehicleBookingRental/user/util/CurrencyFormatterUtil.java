package com.pbl6.VehicleBookingRental.user.util;

import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyFormatterUtil {
    public static String formatToVND(double amount) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        return numberFormat.format(amount) + " VND";
    }
}
