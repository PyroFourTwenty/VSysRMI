package RMI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {

    public static void main(String[] args) {
        try{
            String host = "DESKTOP-1D7T13P";
            Registry registry = LocateRegistry.getRegistry(host,55123);
            Hello stub = (Hello) registry.lookup(host);
            String response = stub.helloTo("Lincoln");
            System.out.println("response: "+response);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
