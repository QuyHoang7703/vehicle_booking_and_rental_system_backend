package com.pbl6.VehicleBookingRental.user.util;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public List<Map<LocalDateTime, LocalDateTime>> getDaysBetweenDateTimes(Instant startDate, Instant endDate) {
        // Chuyển đổi Instant sang LocalDateTime
        LocalDateTime start = startDate.atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime end = endDate.atZone(ZoneId.systemDefault()).toLocalDateTime();

        List<Map<LocalDateTime, LocalDateTime>> result = new ArrayList<>();

        LocalDateTime current = start;

        while (!current.toLocalDate().isAfter(end.toLocalDate())) {
            // Xác định thời gian kết thúc trong ngày
            LocalDateTime dayEnd = current.toLocalDate().atTime(23, 59, 59);

            // Nếu ngày cuối cùng thì thời gian kết thúc là end
            if (dayEnd.isAfter(end)) {
                dayEnd = end;
            }

            // Thêm vào danh sách
            Map<LocalDateTime, LocalDateTime> range = new HashMap<>();
            range.put(current, dayEnd);
            result.add(range);

            // Chuyển sang đầu ngày tiếp theo
            current = current.plusDays(1).toLocalDate().atStartOfDay();
        }

        return result;
    }


}
