package ru.kapahgaiii.server;


import ru.kapahgaiii.config.Config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class Service implements AccountService {
    static Map<Integer, Long> data = new HashMap<Integer, Long>();

    public Long getAmount(Integer id) throws RemoteException {
        if (data.containsKey(id)) {
            return data.get(id);
        } else {
            return (long) 0;
        }
    }

    public void addAmount(Integer id, Long value) throws RemoteException {
        if (data.containsKey(id)) {
            data.put(id, data.get(id) + value);
        } else {
            data.put(id, value);
        }
    }

    public static void main(String[] args) throws Exception {
        System.setProperty("java.rmi.server.hostname", Config.HOST_IP);
        final Registry registry = LocateRegistry.createRegistry(Config.PORT);
        final AccountService service = new Service();
        Remote stub = UnicastRemoteObject.exportObject(service, 0);
        registry.bind("AccountService", stub);

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
                String s = reader.readLine();
                if (s.equals("shutdown")) {
                    System.out.println("Выключаюсь...");
                    registry.unbind("AccountService");
                    UnicastRemoteObject.unexportObject(service, true);
                    break;
                } else if (s.equals("print")) {
                    System.out.println(data);
                }
            } catch (IOException e) {
                break;
            }
        }
    }

}
