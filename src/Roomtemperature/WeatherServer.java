package Roomtemperature;

import java.io.Serializable;
import java.math.RoundingMode;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.sql.*;
import java.util.Locale;

public class WeatherServer implements WeatherApi, Serializable {

    private Connection connection;

    public static void main(String[] args) {
        try {
            new WeatherServer();
        } catch (RemoteException e) {
            System.out.println("Something unusual happened");
        }
    }

    private void bindServerInstanceToRegistry() {
        try {
            int port = 55123;
            WeatherApi stub = (WeatherApi) UnicastRemoteObject.exportObject(this, port);
            Registry registry = LocateRegistry.createRegistry(port);
            registry.rebind("DESKTOP-RSJE3MT", this);
            System.out.println("Weather server running on port " + port + " :)");
        } catch (Exception e) {
            System.out.println("Could not start server :(");
        }
    }

    private void connectToDatabase() {
        StringBuilder sb = new StringBuilder().append("Establishing database connection: ");
        try {
            DoNotOpenThisSuperSecretCredentialClass luce = new DoNotOpenThisSuperSecretCredentialClass();
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://db.f4.htw-berlin.de:3306/_s0559625__Zimmertemperatur?serverTimezone=UTC", luce.getUsername(), luce.getPassword());
            sb.append("success");
        } catch (Exception e) {
            sb.append("failed");
        }
        System.out.println(sb.toString());

    }

    private void disconnectFromDatabase() {
        StringBuilder sb = new StringBuilder("Closing database connection: ");
        try {
            connection.close();
            sb.append("success");
        } catch (SQLException e) {
            sb.append("failed");
        }
        System.out.println(sb.toString());
    }

    private int[] getDateIntArrayFromString(String dateString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Date date = sdf.parse(dateString);
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return new int[]{year, month, day};
    }

    private String getSqlQueryFromString(String dateString) throws ParseException {
        int[] timeValues = getDateIntArrayFromString(dateString);
        int year = timeValues[0];
        int month = timeValues[1];
        int day = timeValues[2];
        return "SELECT * FROM `messungen` WHERE day = " + day +
                " AND month = " + month +
                " AND year = " + year + " ORDER BY `location` DESC, `year` DESC, `month` DESC, `day` DESC, `hour` DESC, `minute` DESC, `second` DESC";
    }

    private ResultSet getResultSetFromQuery(String sqlStatement) {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sqlStatement);
            return rs;
        } catch (Exception e) {
            System.out.println("Could not access database");
            return null;
        }
    }

    private WeatherServer() throws RemoteException {
        bindServerInstanceToRegistry();
    }

    @Override
    public int ping() {
        return 0;
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

    private float roundFloat(float value) {
        NumberFormat formatter = NumberFormat.getInstance(Locale.US);
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(2);
        formatter.setRoundingMode(RoundingMode.HALF_UP);
        return new Float(formatter.format(value));
    }

    @Override
    public String getWeatherValuesForDay(String day) {
        System.out.println("Getting values for day " + day);
        try {
            String query = getSqlQueryFromString(day);
            connectToDatabase();
            ResultSet resultSet = getResultSetFromQuery(query);
            ArrayList<Measurement> measurements = parseMeasurementsFromResultSet(resultSet);
            disconnectFromDatabase();
            if (measurements == null || measurements.size() == 0) {
                return "Sorry, but there are no measurements of this day";
            } else {
                System.out.println("Retrievd "+measurements.size()+ " rows");
                Measurement[] calculatedValues = calculateRequiredValues(measurements);
                return "max : " + calculatedValues[0].temperature + " °C" + System.lineSeparator() +
                        "min : " + calculatedValues[1].temperature + " °C" + System.lineSeparator() +
                        "max : " + calculatedValues[2].humidity + " % relative humidity" + System.lineSeparator() +
                        "min : " + calculatedValues[3].humidity + " % relative humidty" + System.lineSeparator() +
                        "average temperature : " + calculatedValues[4].temperature + " °C" + System.lineSeparator() +
                        "average humidity : " + calculatedValues[4].humidity + " % relative humidty" + System.lineSeparator();
            }
        } catch (ParseException e) {
            return "That is not a correct date.";
        }
    }

    private ArrayList<Measurement> parseMeasurementsFromResultSet(ResultSet rs) {
        String temperature, humidity, location, year, month, day, hour, minute, second;
        StringBuilder sb = new StringBuilder();
        ArrayList<Measurement> measurements = new ArrayList<>();
        try {
            while (rs.next()) {
                temperature = rs.getString("temperature");
                humidity = rs.getString("humidity");
                location = rs.getString("location");
                year = rs.getString("year");
                sb.append(year).append(" ");
                month = rs.getString("month");
                sb.append(month).append(" ");
                day = rs.getString("day");
                sb.append(day).append(" ");
                hour = rs.getString("hour");
                sb.append(hour).append(" ");
                minute = rs.getString("minute");
                sb.append(minute).append(" ");
                second = rs.getString("second");
                sb.append(second).append(" ");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy MM dd HH mm ss");
                Date date = sdf.parse(sb.toString());
                measurements.add(
                        new Measurement(
                                Float.parseFloat(temperature),
                                Float.parseFloat(humidity),
                                location,
                                date
                        )
                );
            }
            return measurements;
        } catch (Exception e) {
            return null;
        }
    }
}
