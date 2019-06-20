package RMI2;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client2 {

    public static void main(String[] args) {
        try{
            int port = (args.length<1)? null : Integer.parseInt(args[0]);
            //Laptop: DESKTOP-RSJE3MT
            //Desktop: DESKTOP-1D7T13P
            String host = "DESKTOP-1D7T13P";
            Registry registry = LocateRegistry.getRegistry(host,port);
            Hello2 stub = (Hello2) registry.lookup(host);
            String response = stub.helloTo("Lincoln2");
            System.out.println("response: "+response);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
