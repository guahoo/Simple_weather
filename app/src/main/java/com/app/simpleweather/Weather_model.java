package com.app.simpleweather;

public class Weather_model {
    String date_time,weather_type,temperature,
            pressure, humidity, wind, windDirection;

    public Weather_model(String date_time, String weather_type,
                         String temperature, String pressure,
                         String humidity, String wind,int windDirection) {
        this.date_time = date_time;
        this.weather_type = weather_type;
        this.temperature = temperature;
        this.pressure = pressure;
        this.humidity = humidity;
        this.wind = wind;
        this.windDirection=setWindDirection(windDirection);
    }

    String getDate_time() {
        return date_time;
    }


    String getWeather_type() {
        return weather_type;
    }


    String getTemperature() {
        return temperature;
    }


    String getPressure() {
        return pressure;
    }


    String getHumidity() {
        return humidity;
    }


    String getWind() {
        return wind;
    }


    private String setWindDirection(int wind) {
        if (wind < 10 && wind >= 0) {
            return "N";
        } else if (wind <= 359 && wind > 350) {
            return "N";
        } else if (wind > 80 && wind < 100) {
            return "E";
        } else if (wind > 170 && wind < 190) {
            return "S";
        } else if (wind > 260 && wind < 280) {
            return "W";
        } else if (wind >= 10 && wind <= 80) {
            return "NE";
        } else if (wind >= 100 && wind <= 170) {
            return "SE";
        } else if (wind >= 190 && wind <= 260) {
            return "SW";
        } else if (wind >= 280 && wind <= 350) {
            return "NW";
        }else {
            return "@";
        }
    }


}
