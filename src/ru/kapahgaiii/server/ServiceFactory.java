package ru.kapahgaiii.server;

public class ServiceFactory {
    public static Service createService(){
        try {
            return new Service();
        }catch (Exception e) { //Catching RemoteException, AlreadyBoundException
            System.err.println(e.toString());
            return null;
        }
    }
}
