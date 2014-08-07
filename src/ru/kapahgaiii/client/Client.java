package ru.kapahgaiii.client;

import ru.kapahgaiii.config.Config;
import ru.kapahgaiii.server.AccountService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
    public static void main(String[] args) {
        Registry registry;
        AccountService service;
        try {
            registry = LocateRegistry.getRegistry(Config.HOST_IP, Config.PORT);
            service = (AccountService) registry.lookup("AccountService");
        } catch (Exception e) {
            System.out.println("Unable to connect service");
            System.err.println(e.toString());
            return;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
                String s = reader.readLine();
                if (s.equals("exit")) {
                    break;
                } else if (s.equals("add")) {
                    service.addAmount(Integer.parseInt(reader.readLine()), Long.parseLong(reader.readLine()));
                } else if (s.equals("get")) {
                    System.out.println(service.getAmount(Integer.parseInt(reader.readLine())));
                }
            } catch (RemoteException e) {
                System.out.println("Server is off.");
                System.err.println(e.toString());
            } catch (IOException e) {
                System.out.println("IOException");
                System.err.println(e.toString());
                break;
            }
        }

    }
}
