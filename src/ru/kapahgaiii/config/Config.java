package ru.kapahgaiii.config;

import java.util.Properties;

public class Config {
    final public static int PORT = 4568;
    final public static String HOST_IP = "178.62.30.124";
    //final public static String HOST_IP = "localhost";
    final public static String BINDING_NAME = "AccountService";

    final public static Properties SQLConnInfo = new Properties();

    static {
        SQLConnInfo.put("characterEncoding", "UTF8");
        SQLConnInfo.put("user", "root");
        SQLConnInfo.put("password", "951159");
    }
    final public static String JDBC_URL = "jdbc:mysql://localhost/account_service";
}
