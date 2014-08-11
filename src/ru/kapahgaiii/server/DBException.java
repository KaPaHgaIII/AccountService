package ru.kapahgaiii.server;

/*
* Исключение, вызываемое при попытке загрузить данные с сервера
* */

public class DBException extends Exception {
    public DBException(String msg) {super(msg);}
    public DBException() {}
}