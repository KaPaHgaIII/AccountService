package ru.kapahgaiii.client;

public class ClientFactory {
    public static Client createClient(CmdArguments cmdArguments){
        try {
            return new Client(cmdArguments.getrCount(), cmdArguments.getwCount(), cmdArguments.getIdList());
        }catch (Exception e) { //Catching RemoteException, NotBoundException, NoThreadsException, ArrayEmptyException
            System.err.println(e.toString());
            return null;
        }
    }
}
