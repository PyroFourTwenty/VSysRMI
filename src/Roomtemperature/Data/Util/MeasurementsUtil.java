package Roomtemperature.Data.Util;

import Roomtemperature.Data.Measurement;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MeasurementsUtil {

    public static float roundFloat(float value) {
        NumberFormat formatter = NumberFormat.getInstance(Locale.US);
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(2);
        formatter.setRoundingMode(RoundingMode.HALF_UP);
        return new Float(formatter.format(value));
    }

    private Measurement[] calculateRequiredValues(ArrayList<Measurement> measurements) {
        Measurement firstEntry = measurements.get(0), minTemp = firstEntry, maxTemp = firstEntry,
                minHumid = firstEntry, maxHumid = firstEntry, avg = new Measurement(0, 0, firstEntry.location, null);
        for (Measurement m : measurements) {
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
        avg.temperature = roundFloat(avg.temperature / measurements.size());
        avg.humidity = roundFloat(avg.humidity / measurements.size());
        return new Measurement[]{maxTemp, minTemp, maxHumid, minHumid, avg};
    }
}
