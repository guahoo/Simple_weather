package com.app.simpleweather.Utility;

public enum OftenUsedStrings {
    OPENSAGEDATA_API("835b77b309444de689cd7c07b675493e"),
    RESULTS("results"),


    CITY("city"),
    VILLAGE("village"),
    HAMLET("hamlet"),
    FORMATTED("formatted"),
    COMPONENTS("components"),
    COMMA(","),
    LOCATION("Ваше местоположение"),
    NO_SIGNAL("Нет сети"),
    CITY_NAME("city_name"),
    COUNTY("county");

    private String oftenUsedString;

    OftenUsedStrings(String s) {
        this.oftenUsedString = s;
    }

    public String getOftenUsedString() {
        return oftenUsedString;
    }
}
