package com.pure;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pure.Dto.CityDto;
import com.pure.Dto.WeatherDto;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

public class PrimaryController {

    @FXML
    private TextField cityInput;
    @FXML
    private Text weatherText;
    @FXML
    private Text weatherAlerts;
    @FXML
    private ImageView imgView;
    @FXML
    private Text AirText;
    @FXML
    private ImageView Aiview;

    private final HttpClient httpclient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // ✅ FIX 1: primitive boolean (NO null crash)
    private boolean b = true;

    /* ---------------- Connectivity Update ---------------- */
    public void updateConnectivityStatus(boolean isConnected) {
        Platform.runLater(() -> {

            b = isConnected;

            if (!isConnected) {
                Aiview.setDisable(true);
                imgView.setDisable(true);

                File file = new File("weather.json");

                // ✅ FIX 2: file existence check
                if (!file.exists()) {
                    weatherText.setText(
                            "⚠ Offline Mode\n\nNo cached data available.\nPlease connect to the internet.");
                    return;
                }

                try {
                    CityDto dto = objectMapper.readValue(file, CityDto.class);

                    weatherText.setText(
                            "⚠ Offline Mode — Showing cached data\n" +

                                    "🌡️ Temperature: " + dto.getTemp() + " °C\n" +
                                    "🤗 Feels Like: " + dto.getFeelsLike() + " °C\n");

                } catch (Exception e) {
                    weatherText.setText("⚠ Offline Mode\nCached data corrupted.");
                }
            }

            System.out.println("Connectivity b = " + b);
        });
    }

    /* ---------------- Fetch Weather ---------------- */
    @FXML
    void getWeatherData(ActionEvent event) throws Exception {
        String city = cityInput.getText();
        getWeatherDataForCity(city);
    }

    private void getWeatherDataForCity(String city)
            throws URISyntaxException, IOException, InterruptedException {

        if (city == null || city.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("City Required");
            alert.setContentText("Enter your city name");
            alert.show();
            return; // ✅ FIX 3
        }

        if (!b) {
            updateConnectivityStatus(false);
            return;
        }

        /* ---------------- ONLINE MODE ---------------- */
        String url = "http://localhost:8080/weather/city?name=" + city;
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(new URI(url)).GET().build();
        HttpResponse<String> response = httpclient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        CityDto dto = objectMapper.readValue(response.body(), CityDto.class);

        weatherText.setText(
                "🌡️ Temperature: " + dto.getTemp() + "°C\n" +
                        "💧 Humidity: " + dto.getHumidity() + "%\n" +
                        "🌬️ Wind Speed: " + dto.getWindSpeed() + " m/s\n" +
                        "📈 Pressure: " + dto.getPressure() + " hPa\n" +
                        "🤗 Feels Like: " + dto.getFeelsLike() + "°C\n" +
                        "🌤️ Temp Min: " + dto.getTempMin() + "°C\n" +
                        "🔥 Temp Max: " + dto.getTempMax() + "°C\n" +
                        "🌫️ AQI: " + dto.getAqi());

        // AQI Indicator
        if (dto.getAqi() <= 50)
            AirText.setText("🟢 Good Air Quality (" + dto.getAqi() + ")");
        else if (dto.getAqi() <= 100)
            AirText.setText("🟡 Moderate Air Quality (" + dto.getAqi() + ")");
        else if (dto.getAqi() <= 200)
            AirText.setText("🟠 Unhealthy (" + dto.getAqi() + ")");
        else
            AirText.setText("🔴 Poor Air Quality (" + dto.getAqi() + ")");

        // Save cache
        objectMapper.writeValue(new File("weather.json"), dto);
    }

    /* ---------------- AI INSIGHTS (UNCHANGED) ---------------- */
    @FXML
    void getInsights(MouseEvent event) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem add = new MenuItem("💡 AI Assist");
        contextMenu.getItems().add(add);

        Aiview.setOnMouseClicked(e -> contextMenu.show(Aiview, e.getScreenX(), e.getScreenY()));

        add.setOnAction(e -> {
            if (weatherText.getText() == null || weatherText.getText().isEmpty()) {
                new Alert(Alert.AlertType.INFORMATION, "Require weather details for insights").show();
                return;
            }
            WeatherDto dto = parseWeatherText(weatherText.getText());
            callSummarizeAPI(dto);
        });
    }

    private void callSummarizeAPI(WeatherDto dto) {

        try {
            // ✅ MANUAL URL
            String summarizeUrl = "http://localhost:8080/api/summary/process?operation=summarize";

            // Convert DTO → JSON
            ObjectMapper mapper = new ObjectMapper();
            String jsonBody = mapper.writeValueAsString(dto);

            HttpClient httpClient = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(summarizeUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response + "response");
            String aiResponse = response.body();
            System.out.println("airesponse" + "" + aiResponse);

            showAIResponseDialog(aiResponse);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showAIResponseDialog(String response) {

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("🤖 AI Insights");
        dialog.setHeaderText("Weather Summary & Suggestions");

        TextArea textArea = new TextArea(response);
        textArea.setWrapText(true);
        textArea.setEditable(false);
        textArea.setPrefWidth(420);
        textArea.setPrefHeight(250);

        dialog.getDialogPane().setContent(textArea);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        dialog.show();
    }

    private WeatherDto parseWeatherText(String text) {
        WeatherDto dto = new WeatherDto();
        for (String line : text.split("\\n")) {
            if (line.contains("Temperature"))
                dto.setTemp(extractDouble(line));
            else if (line.contains("Humidity"))
                dto.setHumidity(extractDouble(line));
            else if (line.contains("Wind Speed"))
                dto.setWindSpeed(extractDouble(line));
            else if (line.contains("Pressure"))
                dto.setPressure(extractDouble(line));
            else if (line.contains("Feels Like"))
                dto.setFeelsLike(extractDouble(line));
            else if (line.contains("Temp Min"))
                dto.setTempMin(extractDouble(line));
            else if (line.contains("Temp Max"))
                dto.setTempMax(extractDouble(line));
            else if (line.contains("AQI"))
                dto.setAqi(extractInt(line));
        }
        return dto;
    }

    private double extractDouble(String line) {
        return Double.parseDouble(line.replaceAll("[^0-9.]", ""));
    }

    private int extractInt(String line) {
        return Integer.parseInt(line.replaceAll("[^0-9]", ""));
    }
}
