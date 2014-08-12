package ru.kapahgaiii.server;

import ru.kapahgaiii.config.Config;

import java.sql.*;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class DBConnection {
    Random random = new Random();
    private Connection conn = null;

    public DBConnection() throws SQLException {
        conn = DriverManager.getConnection(Config.JDBC_URL, Config.SQLConnInfo);
    }

    public static DBConnection createConnection() throws DBException {
        try {
            return new DBConnection();
        } catch (SQLException e) {
            System.err.println(e.toString());
            throw new DBException();
        }
    }

    public ConcurrentMap<Integer, AtomicLong> getData() {
        ConcurrentMap<Integer, AtomicLong> data = new ConcurrentHashMap<Integer, AtomicLong>();

        try {
            PreparedStatement pstmt = conn.prepareStatement("SELECT id, amount FROM accounts;");

            if (pstmt.execute()) {
                ResultSet rs = pstmt.getResultSet();

                while (rs.next()) {
                    data.put(rs.getInt("id"), new AtomicLong(rs.getLong("amount")));
                }
                return data;
            } else {
                return null;
            }

        } catch (SQLException e) {
            System.err.println(e.toString());
            return null;
        }

    }

    public void save(Set<Integer> saveSet, ConcurrentMap<Integer, AtomicLong> data ) throws SQLException {
        Statement s = conn.createStatement();

        Iterator<Integer> it = saveSet.iterator();
        String values = "";

        while (it.hasNext()) {
            Integer id=it.next();
            values += "(" + id + "," + data.get(id).get() + ")";
            if (it.hasNext()) {
                values += ",";
            }
        }

        s.executeUpdate("INSERT INTO accounts (id, amount) VALUES "+values+
                " ON DUPLICATE KEY UPDATE amount=VALUES(amount)");
    }


    /*public static void main(String[] args) throws Exception {
        DBConnection connection = DBConnection.createConnection();
        Set<Integer> set = new CopyOnWriteArraySet<Integer>();
        set.add(3);
        set.add(4);

        ConcurrentMap<Integer, AtomicLong> data = new ConcurrentHashMap<Integer, AtomicLong>();
        data.put(3,new AtomicLong(333));
        data.put(4,new AtomicLong(444));

        System.out.println(set);
        connection.save(set, data);
        System.out.println(set);
    }*/
}
