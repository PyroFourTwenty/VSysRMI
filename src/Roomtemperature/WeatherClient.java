package Roomtemperature;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class WeatherClient implements Serializable {

    private boolean connectionSuccessful = false;
    private WeatherApi stub;

    public static void main(String[] args) {
        new WeatherClient();
    }

     private WeatherClient() {
        createWeatherApiStub();
        if (connectionSuccessful) {
            commandLineLoop();
        }
    }

    private void commandLineLoop() {
        Scanner sc = new Scanner(System.in);
        String input;
        do {
            printMainmenu();
            input = sc.nextLine().trim();
            if (input.equals("ping")) {
                pingWeatherServer();
            } else if (hasNeededFormat(input)) {
                weatherQuery(input);
            }
        } while (!input.equals("exit"));
    }

    private void createWeatherApiStub() {
        try {
            String hostName = "DESKTOP-RSJE3MT";
            int port = 55123;
            Registry registry = LocateRegistry.getRegistry(hostName, port);
            stub = (WeatherApi) registry.lookup(hostName);
            connectionSuccessful = true;
            System.out.println("Server is reachable :)");
        } catch (Exception e) {
            System.out.println("Could not reach server :(");
        }
    }

    private boolean hasNeededFormat(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        try {
            Date date = sdf.parse(dateString);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private void pingWeatherServer() {
        try {
            long timestamp1 = System.currentTimeMillis(), timestamp2, delta;
            int repsonse = stub.ping();
            timestamp2 = System.currentTimeMillis();
            delta = calculatePassedTimeInMillis(timestamp1, timestamp2);
            System.out.println("Ping is " + delta + " ms");
        } catch (Exception e) {
            System.out.println("Ping is currently not available");
        }
    }

    private long calculatePassedTimeInMillis(long firstTimestamp, long secondTimestamo) {
        return (secondTimestamo - firstTimestamp) / 2;
    }

    private static void printMainmenu() {
        StringBuilder sb = new StringBuilder();
        sb.append("-------Hello and welcome to the roomtemperature client-------").append(System.lineSeparator());
        sb.append("-----------------Type in the option of your choice.----------").append(System.lineSeparator());
        sb.append("-------------- ping | ping the weatherserver ----------------").append(System.lineSeparator());
        sb.append("---- dd.MM.yyyy  | get a range of values for this day--------").append(System.lineSeparator());
        sb.append("----------------exit - exit the client ----------------------");
        System.out.println(sb.toString());
    }

    private void weatherQuery(String dateString) {
        try {
            System.out.println("Please wait");
            System.out.println(stub.getWeatherValuesForDay(dateString));
        } catch (RemoteException e) {
            System.out.println("Weather query is currently not available");
        }
    }
}
