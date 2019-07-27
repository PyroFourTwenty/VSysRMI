package Roomtemperature.Data;

import java.util.Date;

public class Measurement {
    public float temperature;
    public float humidity;
    public String location;
    private Date date;

    public Measurement(float temperature, float humidity, String location, Date date) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.location = location;
        this.date = date;
    }
}
