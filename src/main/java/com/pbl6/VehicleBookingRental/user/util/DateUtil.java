package com.pbl6.VehicleBookingRental.user.util;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
@Service
public class DateUtil {
    public List<LocalDate> getDaysBetweenDates(Instant startDate, Instant endDate) {
        // Chuyển đổi Instant sang LocalDate
        LocalDate start = startDate.atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate end = endDate.atZone(ZoneId.systemDefault()).toLocalDate();

        // Tạo stream các ngày từ start đến end
        return Stream.iterate(start, date -> date.plusDays(1))
                .limit(ChronoUnit.DAYS.between(start, end) + 1) // Thêm 1 để bao gồm cả end
                .collect(Collectors.toList());
    }
}
