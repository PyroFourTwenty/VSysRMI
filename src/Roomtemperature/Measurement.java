package Roomtemperature;

import java.util.Date;

public class Measurement
{
    public float temperature, humidity;
    public String location;
    public Date date;

    public Measurement(float temperature, float humidity, String location, Date date){
        this.temperature=temperature;
        this.humidity=humidity;
        this.location=location;
        this.date=date;
    }
}
