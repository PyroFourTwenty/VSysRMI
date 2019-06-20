package RMI2;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Hello2 extends Remote {
    public String helloTo(String name)throws RemoteException;
}
