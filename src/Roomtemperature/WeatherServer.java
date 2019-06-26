package Roomtemperature;

import java.io.Serializable;
import java.math.RoundingMode;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.sql.*;
import java.util.Locale;

public class WeatherServer implements WeatherApi, Serializable {

    private static Registry registry;
    private static int port = 55123;
    private static Connection connection;

    public static void main(String[] args) {
        try {
            WeatherServer obj = new WeatherServer();
            WeatherApi stub = (WeatherApi) UnicastRemoteObject.exportObject(obj, port);
            obj = new WeatherServer();
            registry = LocateRegistry.createRegistry(port);
            registry.rebind("DESKTOP-RSJE3MT", obj);
            System.out.println("Weather server ready ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void connectToDatabase() {
        StringBuilder sb = new StringBuilder().append("Establishing database connection: ");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://db.f4.htw-berlin.de:3306/_s0559625__Zimmertemperatur?serverTimezone=UTC", "username", "pw");
            sb.append("success");
        } catch (Exception e) {
            e.printStackTrace();
            sb.append("failed");
        }
        System.out.println(sb.toString());
    }

    private static void disconnectFromDatabase() {
        System.out.println("Closing database connection");
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static String getSqlQueryFromString(String dateString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Date date = sdf.parse(dateString);
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH)+1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        String query = "SELECT * FROM `messungen` WHERE day = "+day+
                " AND month = "+month+
                " AND year = "+year+" ORDER BY `location` DESC, `year` DESC, `month` DESC, `day` DESC, `hour` DESC, `minute` DESC, `second` DESC";
        return query;
    }

    private static ResultSet getResultSetFromQuery(String sqlStatement) {

        connectToDatabase();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sqlStatement);
            //  stmt.close();
            //disconnectFromDatabase();
            return rs;
        } catch (SQLException e) {
            e.printStackTrace();
            //disconnectFromDatabase();
            return null;
        }
    }


    protected WeatherServer() throws RemoteException {

    }

    @Override
    public int ping() {
        return 0;
    }

    private Measurement[] calculateRequiredValues(ArrayList<Measurement> measurements){
        Measurement firstEntry = measurements.get(0), minTemp = firstEntry, maxTemp = firstEntry,
                minHumid = firstEntry, maxHumid = firstEntry, avg = new Measurement(0, 0, firstEntry.location, null);
        for (Measurement m : measurements){
            if(m.temperature>maxTemp.temperature){
                maxTemp = m;
            }
            if(m.temperature<minTemp.temperature){
                minTemp = m;
            }
            if(m.humidity>maxHumid.humidity){
                maxHumid=m;
            }
            if(m.humidity<minHumid.humidity){
                minHumid=m;
            }
            avg.temperature+=m.temperature;
            avg.humidity+=m.humidity;
        }
        avg.temperature= roundFloat(avg.temperature/measurements.size(), 2);
        avg.humidity= roundFloat(avg.humidity/measurements.size(),2);
        return new Measurement[]{maxTemp, minTemp, maxHumid, minHumid, avg};
    }

    private float roundFloat(float value, int numberOfDecimalPlaces){
        NumberFormat formatter = NumberFormat.getInstance(Locale.US);
        formatter.setMaximumFractionDigits(numberOfDecimalPlaces);
        formatter.setMinimumFractionDigits(numberOfDecimalPlaces);
        formatter.setRoundingMode(RoundingMode.HALF_UP);
        return new Float(formatter.format(value));
    }

    @Override
    public String weatherQuery(String day) {
        System.out.println("weatherquery for "+day);

        try {
            String query = getSqlQueryFromString(day);
            ResultSet resultSet = getResultSetFromQuery(query);
            ArrayList<Measurement> measurements = parseMeasurementsFromResultSet(resultSet);
            if(measurements.size()==0){
                return "Sorry, but there are no measurements of this day";
            }else{
                Measurement[] calculatedValues = calculateRequiredValues(measurements);
                StringBuilder sb = new StringBuilder();
                sb.append("max : ").append(calculatedValues[0].temperature).append("°C").append(System.lineSeparator());
                sb.append("min : ").append(calculatedValues[1].temperature).append("°C").append(System.lineSeparator());
                sb.append("max : ").append(calculatedValues[2].humidity).append("% relative humidity").append(System.lineSeparator());
                sb.append("min : ").append(calculatedValues[3].humidity).append("% relative humidty").append(System.lineSeparator());
                sb.append("average temperature : ").append(calculatedValues[4].temperature).append("°C").append(System.lineSeparator());
                sb.append("average humidity : ").append(calculatedValues[4].humidity).append("").append(System.lineSeparator());
                disconnectFromDatabase();
                return sb.toString();
            }
        } catch (ParseException e) {
            return "That is not a correct date.";
        }     }

    public static ArrayList<Measurement> parseMeasurementsFromResultSet(ResultSet rs){
        String temperature, humidity, location, year, month, day,hour, minute, second;
        StringBuilder sb = new StringBuilder();
        ArrayList<Measurement> measurements = new ArrayList<>();
        try{
            while(rs.next()) {
                temperature = rs.getString("temperature");
                humidity = rs.getString("humidity");
                location = rs.getString("location");
                year = rs.getString("year"); sb.append(year+" ");
                month = rs.getString("month"); sb.append(month +" ");
                day  = rs.getString("day");sb.append(day +" ");
                hour  = rs.getString("hour");sb.append(hour +" ");
                minute = rs.getString("minute");sb.append( minute+" ");
                second = rs.getString("second");sb.append(second +" ");
                SimpleDateFormat sdf= new SimpleDateFormat("yyyy MM dd HH mm ss");
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
        }catch (SQLException sqle){
            sqle.printStackTrace();
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return measurements;
    }

    @Override
    public void bye() throws RemoteException {
        disconnectFromDatabase();
    }


}
