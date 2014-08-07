package ru.kapahgaiii.client;

/*
* Для клиента не будет задан ни один читатель/писатель, зачем нам такой клиент?
* */

public class NoThreadsException extends Exception {
    public NoThreadsException(String msg) {super(msg);}
    public NoThreadsException() {}
}