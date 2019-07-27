package Roomtemperature.Data;

import Roomtemperature.Data.Util.MeasurementsUtil;

import java.util.ArrayList;

public class Measurements {
    private ArrayList<Measurement> measurements;

    public Measurements() {
        this.measurements = new ArrayList<>();
    }

    public void add(Measurement measurement) {
        this.measurements.add(measurement);
    }

    public void remove(Measurement measurement) {
        this.measurements.remove(measurement);
    }

    public ArrayList<Measurement> getMeasurements() {
        return this.measurements;
    }

    private Measurement[] calculateRequiredValues(Measurements measurements) {
        Measurement firstEntry = measurements.getMeasurements().get(0), minTemp = firstEntry, maxTemp = firstEntry,
                minHumid = firstEntry, maxHumid = firstEntry, avg = new Measurement(0, 0, firstEntry.location, null);
        for (Measurement m : measurements.getMeasurements()) {
            if (m.temperature > maxTemp.temperature) {
                maxTemp = m;
            }
            if (m.temperature < minTemp.temperature) {
                minTemp = m;
            }
            if (m.humidity > maxHumid.humidity) {
                maxHumid = m;
            }
            if (m.humidity < minHumid.humidity) {
                minHumid = m;
            }
            avg.temperature += m.temperature;
            avg.humidity += m.humidity;
        }
        avg.temperature = MeasurementsUtil.roundFloat(avg.temperature / measurements.getMeasurements().size());
        avg.humidity = MeasurementsUtil.roundFloat(avg.humidity / measurements.getMeasurements().size());
        return new Measurement[]{maxTemp, minTemp, maxHumid, minHumid, avg};
    }
}
