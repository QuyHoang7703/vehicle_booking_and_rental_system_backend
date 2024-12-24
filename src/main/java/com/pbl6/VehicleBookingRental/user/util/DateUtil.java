package com.pbl6.VehicleBookingRental.user.util;

import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
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
    public List<String> generateTimeSlots(Instant startTime, Instant endTime){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd:HH:mm");

        //Lam tron startTime va endTime voi moc gio gan nhat
        ZonedDateTime startZonedDateTime = startTime.atZone(ZoneId.systemDefault()).truncatedTo(ChronoUnit.HOURS);
        ZonedDateTime endZonedDateTime =  endTime.atZone(ZoneId.systemDefault()).plusMinutes(59).truncatedTo(ChronoUnit.HOURS);

        List<String> timeSlots = new ArrayList<>();

        ZonedDateTime current = startZonedDateTime;
        while(current.isBefore(endZonedDateTime)){
            ZonedDateTime next = current.plusHours(1);
            timeSlots.add(current.format(dateTimeFormatter) + "_" + next.format(dateTimeFormatter));
            current = next;
        }
        return timeSlots;
    }
    public long calculateAndSetTTL(String timeSlot) {
        // Bước 1: Parse ngày từ timeSlot
        String dateString = timeSlot.split(":")[0]; // Lấy phần "2024-12-22"
        LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // Tạo thời gian kết thúc trong ngày (23:59:59)
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX).plusDays(1); // 2025-12-22T23:59:59

        // Bước 2: Tính khoảng cách từ bây giờ tới cuối ngày
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault()); // Thời gian hiện tại
        ZonedDateTime endOfDayZoned = endOfDay.atZone(ZoneId.systemDefault());

        Duration duration = Duration.between(now, endOfDayZoned);
        long ttlInSeconds = Math.max(duration.getSeconds(), 0); // Tránh TTL âm

        return ttlInSeconds;
    }

}
