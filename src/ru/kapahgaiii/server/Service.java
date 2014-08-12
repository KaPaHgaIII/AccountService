package ru.kapahgaiii.server;


import ru.kapahgaiii.config.Config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class Service implements AccountService {
    ConcurrentMap<Integer, AtomicLong> data = new ConcurrentHashMap<Integer, AtomicLong>();
    Set<Integer> saveSet = new HashSet<Integer>();

    final Registry registry;
    final DBConnection connection;
    final Thread DBThread;
    private int requestsCount = 0;

    public Service() throws RemoteException, AlreadyBoundException, DBException {

        connection = DBConnection.createConnection();
        data = connection.getData();

        System.setProperty("java.rmi.server.hostname", Config.HOST_IP);
        registry = LocateRegistry.createRegistry(Config.PORT);
        Remote stub = UnicastRemoteObject.exportObject(this, 0);
        registry.bind(Config.BINDING_NAME, stub);

        DBThread = new Thread(new DBProcessor());
        DBThread.start();
    }

    public static Service createService() {
        try {
            return new Service();
        } catch (Exception e) { //Catching RemoteException, AlreadyBoundException, DBException
            System.err.println(e.toString());
            return null;
        }
    }

    @Override
    public Long getAmount(Integer id) throws RemoteException {
        requestsCount++;
        if (data.containsKey(id)) {
            return data.get(id).get();
        } else {
            return (long) 0;
        }
    }

    @Override
    public void addAmount(Integer id, Long value) throws RemoteException {
        //для MySQL
        saveSet.add(id);
        //делаем то, ради чего вообще вызывался метод
        data.putIfAbsent(id, new AtomicLong(0));
        data.get(id).getAndAdd(value);
        //для статистики
        requestsCount++;
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
            DBThread.interrupt();
            DBThread.join();
            System.out.println("Success");
        } catch (Exception e) { // catch RemoteException, NotBoundException, AccessException, NoSuchObjectException
            System.out.println("Failed");
            System.err.println(e.toString());
        }
    }

    public static void main(String[] args) throws Exception {
        Service service = Service.createService();
        if (service != null) {
            service.run();
        } else {
            System.err.println("Could not start service");
        }
    }

    private class DBProcessor implements Runnable {
        @Override
        public void run() {
            Boolean interrupted = false; //этот флаг нужен чтобы не потерять данные
            while (true) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                if (Thread.currentThread().isInterrupted()) {
                    interrupted = true;
                }

                if (saveSet.size() > 0) {
                    try {
                        Set<Integer> completeSaveSet = saveSet; //сейчас есть 2 ссылки на мой сет
                        saveSet = new HashSet<Integer>(); //новый айдишники теперь будут сохранятся в новый сет
                        connection.save(completeSaveSet, data); //спокойно работаем со старым сетом
                    } catch (SQLException e) {
                        System.err.println("MySQL: Could not update DB");
                        System.err.println(e.toString());
                    }
                }
                //Thread.currentThread().isInterrupted()
                //Такую проверку нельзя выполнить тут, потому что тогда есть шанс, что потеряется часть данных.
                if (interrupted) {
                    break;
                }
            }
        }
    }
}
