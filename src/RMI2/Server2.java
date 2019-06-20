package RMI2;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server2 implements Hello2 {

    public static Registry registry;

    protected Server2() throws RemoteException{
    }

    @Override
    public String helloTo(String name) throws RemoteException {
        System.err.println(name + "is trying to contact!");
        return "Server2 greets "+name;
    }

    public static void main(String[] args) {
        try {

            int port = (args.length<1)? null : Integer.parseInt(args[0]);
            Server2 obj = new Server2();
            Hello2 stub = (Hello2) UnicastRemoteObject.exportObject(obj, 0);

            registry = LocateRegistry.createRegistry(port);
            registry.rebind("DESKTOP-1D7T13P", obj);
            System.err.println("Server2 ready");
        }catch (Exception e){
            System.err.println("Server2 exception: "+e.toString());
            e.printStackTrace();
        }
    }
}
