package ru.kapahgaiii.client;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import ru.kapahgaiii.config.Config;
import ru.kapahgaiii.server.AccountService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

public class Client {

    Registry registry;
    AccountService service;

    int rCount;
    int wCount;
    int[] idList;

    Random rand = new Random();

    List<Thread> threads = new LinkedList<Thread>();
    public Client(int rCount, int wCount, int[] idList) throws RemoteException, NotBoundException,
            NoThreadsException, ArrayEmptyException {
        if (rCount == 0 && wCount == 0) {
            throw new NoThreadsException();
        }
        if (idList.length == 0) {
            throw new ArrayEmptyException();
        }

        registry = LocateRegistry.getRegistry(Config.HOST_IP, Config.PORT);
        service = (AccountService) registry.lookup("AccountService");

        this.rCount = rCount;
        this.wCount = wCount;
        this.idList = idList;

    }

    public static Client createClient(CmdArguments cmdArguments) {
        try {
            return new Client(cmdArguments.getrCount(), cmdArguments.getwCount(), cmdArguments.getIdList());
        } catch (Exception e) { //Catching RemoteException, NotBoundException, NoThreadsException, ArrayEmptyException
            System.err.println(e.toString());
            return null;
        }
    }

    //метод запускает клиент
    public void run() {
        for (int i = 0; i < rCount; i++) { //запускаем потоки читателей
            Thread reader = new Thread(new Reader());
            reader.start();
            threads.add(reader);
        }
        for (int i = 0; i < wCount; i++) { //запускаем потоки писателей
            Thread writer = new Thread(new Writer());
            writer.start();
            threads.add(writer);
        }
        //Запуск следилку за потоками, она нам скажет, что сервер отрубился.
        new Thread(new ThreadManager()).start();

        //Запуск читателя команд с консоли
        Thread commandReader = new Thread(new CommandReader());
        commandReader.setDaemon(true); // чтобы не ждать команды на выключение клиента, если сервер отрубился
        commandReader.start();
    }

    public void shutdown() {
        for (Thread thread : threads) {
            thread.interrupt();
        }

    }

    //метод для чтения и парсинга агруметов командной строки
    private static CmdArguments getArguments(String[] args) throws CmdLineException {
        CmdArguments cmdArguments = new CmdArguments();
        CmdLineParser parser = new CmdLineParser(cmdArguments);
        parser.parseArgument(args);
        return cmdArguments;
    }

    public static void main(String[] args) {

        //пробуем парсить агрументы командной строки
        CmdArguments cmdArguments;
        try {
            cmdArguments = getArguments(args);
        } catch (CmdLineException e) {
            System.err.println("An error occurred while parsing command line args");
            return;
        }
        //если всё хорошо, пробуем создать клиент
        Client client = Client.createClient(cmdArguments);
        //если клиент создался, запускаем.
        if (client != null) {
            client.run();
        } else {
            System.out.println("Unable to start client");
        }

    }

    //класс вызывает метод getAmount
    private class Reader implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    int id = idList[rand.nextInt(idList.length)];
                    service.getAmount(id);
                    Thread.yield();
                } catch (RemoteException e) {
                    System.err.println(e.toString());
                    Thread.currentThread().interrupt();
                }
                if(Thread.currentThread().isInterrupted()){
                    threads.remove(Thread.currentThread());
                    break;
                }
            }
        }
    }

    //класс вызывает метод addAmount
    private class Writer implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    int id = idList[rand.nextInt(idList.length)];
                    Long amount = (long) (rand.nextInt(1000) - rand.nextInt(1000)); //положительное или отрицательное
                    service.addAmount(id, amount);
                    Thread.yield();
                } catch (RemoteException e) {
                    System.err.println(e.toString());
                    Thread.currentThread().interrupt();
                }
                if(Thread.currentThread().isInterrupted()){
                    threads.remove(Thread.currentThread());
                    break;
                }
            }
        }
    }

    //задача класса - следить за активными потоками и уведомить, если они все завершились
    private class ThreadManager implements Runnable {
        @Override
        public void run() {
            System.out.println("Client started: ");
            System.out.println("rCount: "+rCount);
            System.out.println("wCount: "+wCount);
            System.out.println("IdList: "+ Arrays.toString(idList));
            while (!threads.isEmpty()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }
                Thread.yield();
            }
            System.out.println("Client stopped.");
        }
    }

    //класс считывает команды с консоли. точнее команду - завершить работу клиента.
    private class CommandReader implements Runnable {
        @Override
        public void run() {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                try {
                    String s = reader.readLine();
                    if (s.equals("shutdown")) {
                        shutdown();
                        break;
                    }
                } catch (IOException e) {
                    break;
                }
            }
        }
    }
}
