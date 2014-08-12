package ru.kapahgaiii.config;

import java.io.FileInputStream;
import java.util.Properties;

public class Config {
    public static int PORT;
    public static String HOST_IP;
    public static String BINDING_NAME;

    final public static Properties SQLConnInfo = new Properties();
    public static String JDBC_URL;

    static {
        try {
            Properties props = new Properties();
            props.loadFromXML(new FileInputStream("config.xml"));

            PORT = Integer.parseInt(props.getProperty("service.port"));
            HOST_IP = props.getProperty("service.host_ip");
            BINDING_NAME = props.getProperty("service.binding_name");

            SQLConnInfo.put("characterEncoding", "UTF8");
            SQLConnInfo.put("user", props.getProperty("jdbc.user"));
            SQLConnInfo.put("password", props.getProperty("jdbc.pass"));

            JDBC_URL = props.getProperty("jdbc.url");
        } catch (Exception e) {
            System.out.println("Error while reading config.xml");
            System.err.println(e.toString());
            System.exit(0);
        }
    }
}
