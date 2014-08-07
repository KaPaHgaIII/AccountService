package ru.kapahgaiii.client;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import ru.kapahgaiii.config.Config;
import ru.kapahgaiii.server.AccountService;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Client {

    Registry registry;
    AccountService service;

    int rCount;
    int wCount;
    int[] idList;

    int activeReaders = 0;
    int activeWriters = 0;

    Random rand = new Random();

    BlockingQueue<Thread> queue = new LinkedBlockingQueue<Thread>();

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

    public void run() {
        for (int i = 0; i < rCount; i++) {
            Thread reader = new Thread(new Reader());
            reader.start();
            queue.offer(reader);
        }
        for (int i = 0; i < wCount; i++) {
            Thread writer = new Thread(new Writer());
            writer.start();
            queue.offer(writer);
        }
    }

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
        Client client = ClientFactory.createClient(cmdArguments);
        //если клиент создался, запускаем.
        if (client != null) {
            client.run();
        } else {
            System.err.println("Unable to start client");
        }

    }

    private class Reader implements Runnable {
        private final int readerId = activeReaders++;

        @Override
        public void run() {
            while (true) {
                try {
                    int id = idList[rand.nextInt(idList.length)];
                    System.out.println("Reader" + readerId + " says: id " + id + " now has amount " + service.getAmount(id));
                    Thread.yield();
                } catch (RemoteException e) {
                    break;
                }
            }
        }
    }

    private class Writer implements Runnable {
        private final int writerId = activeWriters++;

        @Override
        public void run() {
            while (true) {
                try {
                    int id = idList[rand.nextInt(idList.length)];
                    Long amount = (long) (rand.nextInt(1000) - rand.nextInt(1000)); //положительное или отрицательное
                    System.out.println("Writer" + writerId + " changes amount by " + amount + " where id= " + id);
                    service.addAmount(id, amount);
                    Thread.yield();
                } catch (RemoteException e) {
                    break;
                }
            }
        }
    }
}
