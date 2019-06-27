package Roomtemperature;

import java.util.Date;

class Measurement
{
    float temperature, humidity;
    String location;
    private Date date;

    Measurement(float temperature, float humidity, String location, Date date){
        this.temperature=temperature;
        this.humidity=humidity;
        this.location=location;
        this.date=date;
    }
}
