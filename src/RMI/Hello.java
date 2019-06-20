package RMI;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Hello extends Remote {
    public String helloTo(String name)throws RemoteException;
}
