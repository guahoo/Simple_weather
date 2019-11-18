package com.example.simpleweather;

public class Weather_model {
    String date_time,weather_type,temperature,
            pressure, humidity, wind;

    public Weather_model(String date_time, String weather_type,
                         String temperature, String pressure,
                         String humidity, String wind) {
        this.date_time = date_time;
        this.weather_type = weather_type;
        this.temperature = temperature;
        this.pressure = pressure;
        this.humidity = humidity;
        this.wind = wind;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getWeather_type() {
        return weather_type;
    }

    public void setWeather_type(String weather_type) {
        this.weather_type = weather_type;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getHudimity() {
        return humidity;
    }

    public void setHudimity(String hudimity) {
        this.humidity = hudimity;
    }

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }


}
