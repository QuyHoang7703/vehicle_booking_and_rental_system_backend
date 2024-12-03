package com.pbl6.VehicleBookingRental.user.service.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pbl6.VehicleBookingRental.user.dto.LocationDTO;
import com.pbl6.VehicleBookingRental.user.dto.OpenRouteServiceDTO;
import com.pbl6.VehicleBookingRental.user.service.OSRService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Service
public class OSRImplement implements OSRService {
    @Value("${osr.private.key}")
    private String apiKey ;
    @Override
    public OpenRouteServiceDTO getDistanceAndDuration(LocationDTO source, LocationDTO destination) throws IOException, InterruptedException {
        OpenRouteServiceDTO openRouteServiceDTO = new OpenRouteServiceDTO();
        HttpClient client = HttpClient.newHttpClient();
        String requestBody = "{\n" +
                "  \"locations\": [\n" +
                "    ["+source.getLongitude()+","+source.getLatitude()+"],\n" +
                "    ["+destination.getLongitude()+","+destination.getLatitude()+"]\n" +
                "  ],\n" +
                "  \"metrics\": [\"duration\", \"distance\"],\n" +
                "  \"units\": \"m\"\n" +
                "}";

        // Tạo yêu cầu HTTP
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openrouteservice.org/v2/matrix/driving-car"))
                .header("Authorization", apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        // Gửi yêu cầu và nhận phản hồi
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Kiểm tra phản hồi
        if (response.statusCode() == 200) {
            // Chuyển đổi dữ liệu JSON từ phản hồi
            JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonArray durations = jsonResponse.getAsJsonArray("durations");
            JsonArray distances = jsonResponse.getAsJsonArray("distances");

            //duration and distance
            JsonArray rowDurations =  durations.get(0).getAsJsonArray();
            JsonArray rowDistances = distances.get(0).getAsJsonArray();
            openRouteServiceDTO.setDistance(rowDistances.get(1).getAsInt()/1000.0);
            openRouteServiceDTO.setDuration(convertSecondsToHHmm(rowDurations.get(1).getAsInt()));
            System.out.println("Từ Đà Nẵng đến Nam Định:");
            System.out.println("Thời gian: " +openRouteServiceDTO.getDuration());
            System.out.println("Khoảng cách: " + openRouteServiceDTO.getDistance() + " km");
            }
        else {
            System.out.println("Request failed with status code: " + response.statusCode());
        }
        return openRouteServiceDTO;
    }

    private String convertSecondsToHHmm(int seconds) {
        Duration duration = Duration.ofSeconds(seconds);
        long hours = duration.toHours();
//        long minutes = duration.minusHours(hours).toMinutes();
        long minutes = duration.toMinutes() % 60;
        if(minutes>0 && minutes<=30) {
            minutes = 30;
        }
        else{
            minutes = 0;
            hours ++;
        }
        return String.format("%02dh:%02dm", hours, minutes);
    }

}
