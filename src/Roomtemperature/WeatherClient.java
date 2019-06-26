package Roomtemperature;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.Serializable;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class WeatherClient implements Serializable {

    public static String hostName = "DESKTOP-RSJE3MT";
    public static final int port = 55123;
    public static Registry registry;
    public static boolean connectionSuccessful = false;

    private static WeatherApi stub;

    public static void main(String[] args) {
        try {
            registry = LocateRegistry.getRegistry(hostName, port);
            stub = (WeatherApi) registry.lookup(hostName);
            connectionSuccessful = true;
            System.out.println("Server is reachable :)");
        } catch (Exception e) {
            System.out.println("Could not reach server :(");
        }
        if (connectionSuccessful) {
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

    }

    static boolean hasNeededFormat(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        try {
            Date date = sdf.parse(dateString);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }
    private static void bye(){
        try {
            stub.bye();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void pingWeatherServer() {
        try {
            long timestamp1 = System.currentTimeMillis(), timestamp2, delta;
            int repsonse = stub.ping();
            timestamp2 = System.currentTimeMillis();
            delta = (timestamp2 - timestamp1) / 2;
            System.out.println("Ping is " + delta + " ms");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printMainmenu() {
        StringBuilder sb = new StringBuilder();
        sb.append("-------Hello and welcome to the roomtemperature client------").append(System.lineSeparator());
        sb.append("-----------------Type in the option of your choice.----------").append(System.lineSeparator());
        sb.append("-------------- ping | ping the weatherserver ----------------").append(System.lineSeparator());
        sb.append("---- dd.MM.yyyy  | get a range of values for this day--------").append(System.lineSeparator());
        sb.append("----------------exit - exit the client ----------------------");
        System.out.println(sb.toString());
    }

    public static void weatherQuery(String dateString) {
        try {
            System.out.println(stub.weatherQuery(dateString));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
