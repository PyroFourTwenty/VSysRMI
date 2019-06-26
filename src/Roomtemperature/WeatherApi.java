package Roomtemperature;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface WeatherApi extends Remote {
    int ping() throws RemoteException;

    String weatherQuery(String day) throws RemoteException;

    void bye() throws RemoteException;
}
