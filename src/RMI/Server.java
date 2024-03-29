package RMI;

import com.sun.corba.se.spi.activation.ServerOperations;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server implements Hello {

    public static Registry registry;

    protected Server() throws RemoteException{
    }

    @Override
    public String helloTo(String name) throws RemoteException {
        System.err.println(name + "is trying to contact!");
        return "Server2 greets "+name;
    }

    public static void main(String[] args) {
        try {
            Server obj = new Server();
            Hello stub = (Hello) UnicastRemoteObject.exportObject(obj, 0);

            registry = LocateRegistry.createRegistry(1099);
            registry.rebind("DESKTOP-1D7T13P", obj);
            System.err.println("Server2 ready");
        }catch (Exception e){
            System.err.println("Server2 exception: "+e.toString());
            e.printStackTrace();
        }
    }
}
