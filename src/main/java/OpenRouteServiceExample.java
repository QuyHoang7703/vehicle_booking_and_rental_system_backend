import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpHeaders;
import java.util.List;
import java.util.Map;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

public class OpenRouteServiceExample {

    public static void main(String[] args) throws Exception {
        // API Key của bạn từ OpenRouteService
        String apiKey = "5b3ce3597851110001cf6248cfe7ca40f98b4fc3b8183cc113b04834";

        // Tạo client HTTP
        HttpClient client = HttpClient.newHttpClient();

        // Tạo request body (dữ liệu JSON gửi đi)
        String requestBody = "{\n" +
                "  \"locations\": [\n" +
                "    [108.2022, 16.0544],\n" +
                "    [106.1692, 20.4417] \n" +
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

            // Duyệt qua các dữ liệu để in ra thời gian và khoảng cách
            for (int i = 0; i < durations.size(); i++) {
                JsonArray rowDurations = durations.get(i).getAsJsonArray();
                JsonArray rowDistances = distances.get(i).getAsJsonArray();

                for (int j = 0; j < rowDurations.size(); j++) {
                    int hours = rowDurations.get(j).getAsInt() / 3600;
                    int minutes = (rowDurations.get(j).getAsInt() % 3600) / 60;
                    // Chuyển khoảng cách sang km
                    double km = rowDistances.get(j).getAsInt() / 1000.0;

                    // Hiển thị kết quả
                    System.out.println("Từ Đà Nẵng đến Nam Định:");
                    System.out.println("Thời gian: " + hours + " giờ " + minutes + " phút");
                    System.out.println("Khoảng cách: " + km + " km");

                    System.out.println("From " + (i + 1) + " to " + (j + 1) + ": ");
                    System.out.println("Duration: " + rowDurations.get(j).getAsInt() + " seconds");
                    System.out.println("Distance: " + rowDistances.get(j).getAsInt() + " meters");
                    System.out.println();
                }
            }
        } else {
            System.out.println("Request failed with status code: " + response.statusCode());
        }
    }
}
