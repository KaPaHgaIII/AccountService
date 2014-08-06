package ru.kapahgaiii.client;

import ru.kapahgaiii.config.Config;
import ru.kapahgaiii.server.AccountService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
    public static void main(String[] args) throws Exception {
        Registry registry = LocateRegistry.getRegistry(Config.HOST_IP, Config.PORT);
        AccountService service = (AccountService) registry.lookup("AccountService");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
                String s = reader.readLine();
                if (s.equals("exit")) {
                    break;
                } else if (s.equals("add")) {
                    service.addAmount(Integer.parseInt(reader.readLine()),Long.parseLong(reader.readLine()));
                } else if (s.equals("get")) {
                    System.out.println(service.getAmount(Integer.parseInt(reader.readLine())));
                }
            } catch (IOException e) {
                break;
            }
        }

    }
}
