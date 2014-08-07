package ru.kapahgaiii.client;

import ru.kapahgaiii.server.Service;

public class ClientFactory {
    public static Service createService(){
        try {
            return new Service();
        }catch (Exception e) { //Catching RemoteException, AlreadyBoundException
            System.err.println(e.toString());
            return null;
        }
    }
}
