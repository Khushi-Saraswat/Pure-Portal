package com.pure.Dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

//it shows all the attributes that needed to show when fetch from api.
@JsonIgnoreProperties(ignoreUnknown = true)
public class CityDto {

    private double name;
    private double temp;
    private double humidity;
    private double windSpeed;
    private double pressure;
    private double feelsLike;
    private double tempMin;
    private double tempMax;
    private String alert;
    private int aqi;

    public double getName() {
        return name;
    }

    public void setName(double name) {
        this.name = name;
    }

    public int getAqi() {
        return aqi;
    }

    public void setAqi(int aqi) {
        this.aqi = aqi;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public double getFeelsLike() {
        return feelsLike;
    }

    public void setFeelsLike(double feelsLike) {
        this.feelsLike = feelsLike;
    }

    public double getTempMin() {
        return tempMin;
    }

    public void setTempMin(double tempMin) {
        this.tempMin = tempMin;
    }

    public double getTempMax() {
        return tempMax;
    }

    public void setTempMax(double tempMax) {
        this.tempMax = tempMax;
    }

    public CityDto(double temp, double humidity, double windSpeed, double pressure, double feelsLike, double tempMin,
            double tempMax) {
        this.temp = temp;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.pressure = pressure;
        this.feelsLike = feelsLike;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
    }

    public CityDto() {
    }

    public String getAlert() {
        return alert;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }

}
