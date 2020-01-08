package com.app.simpleweather.Utility;

public class OftenUsedStrings {
    public static String OPENSAGEDATA_API ="835b77b309444de689cd7c07b675493e";
    public static String OPEN_WEATHER_MAP_API_KEY = "b542736e613d2382837ad821803eb507";

    public static String RESULTS="results";
    public static String CITY="city";
    public static String VILLAGE="village";
    public static String HAMLET="hamlet";
    public static String FORMATTED="formatted";
    public static String COMPONENTS="components";
    public static String COMMA=",";
    public static String LOCATION="Ваше местоположение";
    public static String NO_SIGNAL="Нет сети";
    public static String CITY_NAME="city_name";
    public static String COUNTY="county";
    protected static final String STATE = "state";
    protected static final String TOWN = "town";
    public static final String ISADAY = "day";
    public static final String JSONARRAY = "jsonArray";
    public static final String MAIN = "main";
    public static final String DT = "dt";
    public static final String WIND = "wind";
    public static final String WEATHER = "weather" ;
    public static final String TEMP = "temp";
    public static final String PRESSURE = "pressure" ;
    public static final String HUDIMITY = "humidity";
    public static final String WINDSPEED = "speed";
    public static final String DEG = "deg";
    public static final String DESCRIPTION = "description";
    public static final String CHANNEL_ID = "1" ;
    public static final String CHANNEL_NAME = "Weather channel";

    public static final String LATITUDE = "cityLat";
    public static final String LONGITUDE = "cityLon";
    public static final String SYS = "sys";
    public static final String LOCATION_NAME = "name";
    public static final String COUNTRY_NAME = "country";

    public static final String SUNRISE = "sunrise";
    public static final String SUNSET = "sunset";
    static final String TEMP_MIN = "temp_min";
    static final String TEMP_MAX = "temp_max";
    public static final String JSONOBJECT = "jsonObj";

    final static String URL_REQUEST_OPENSAGE_GEOPOSITION = "https://api.opencagedata.com/geocode/v1/json?key="
            + OPENSAGEDATA_API + "&q=%s" + COMMA + "%s&pretty=5" + "&no_annotations=1&language=%s";

    public final static String URL_REQUEST_OPEN_WEATHER_MAP_FORECAST_ALERT = "https://api.openweathermap.org/data/2.5/forecast?lat=%s&" +
            "lon=%s&units=metric&cnt=1&appid=%s";

    public final static String URL_REQUEST_OPEN_WEATHER_MAP_FORECAST_CASUAL = "https://api.openweathermap.org/data/2.5/forecast?lat=%s&" +
            "lon=%s&units=metric&cnt=30&appid=%s";

    public final static String URL_REQUEST_OPEN_WEATHER_MAP_CURRENT_WEATHER =
            "https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&units=metric&appid=%s";

    private String oftenUsedString;

    OftenUsedStrings(String s) {
        this.oftenUsedString = s;
    }

    public String getOftenUsedString() {
        return oftenUsedString;
    }
}
