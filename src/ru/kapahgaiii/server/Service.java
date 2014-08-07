package ru.kapahgaiii.server;


import ru.kapahgaiii.config.Config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.util.HashMap;
import java.util.Map;

public class Service implements AccountService {
    Map<Integer, Long> data = new HashMap<Integer, Long>();
    final Registry registry;
    private int requestsCount = 0;

    public Service() throws RemoteException, AlreadyBoundException {
        System.setProperty("java.rmi.server.hostname", Config.HOST_IP);
        registry = LocateRegistry.createRegistry(Config.PORT);
        Remote stub = UnicastRemoteObject.exportObject(this, 0);
        registry.bind(Config.BINDING_NAME, stub);
    }

    @Override
    public Long getAmount(Integer id) throws RemoteException {
        requestsCount++;
        if (data.containsKey(id)) {
            return data.get(id);
        } else {
            return (long) 0;
        }
    }

    @Override
    public void addAmount(Integer id, Long value) throws RemoteException {
        requestsCount++;
        if (data.containsKey(id)) {
            data.put(id, data.get(id) + value);
        } else {
            data.put(id, value);
        }
    }

    public void run() throws Exception {

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
                String s = reader.readLine();
                if (s.equals("shutdown")) {
                    shutdown();
                    break;
                } else if (s.equals("print")) {
                    System.out.println(data);
                } else if (s.equals("how much?")) {
                    System.out.println(requestsCount);
                }
            } catch (IOException e) {
                break;
            }
        }
    }

    public void shutdown() {
        System.out.print("Trying to shut down service... ");
        try {
            registry.unbind("AccountService");
            UnicastRemoteObject.unexportObject(this, true);
            System.out.println("Success");
        } catch (Exception e) { // catch RemoteException, NotBoundException, AccessException, NoSuchObjectException
            System.out.println("Failed");
            System.err.println(e.toString());
        }
    }

    public static void main(String[] args) throws Exception {
        Service service = ServiceFactory.createService();
        if(service!=null){
            service.run();
        }else{
            System.err.println("Could not start service");
        }
    }

}
