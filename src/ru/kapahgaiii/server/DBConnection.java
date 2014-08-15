package ru.kapahgaiii.server;

import ru.kapahgaiii.config.Config;

import java.sql.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class DBConnection {
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

    //метод для загрузки данных из базы данных при старте клиента
    public ConcurrentMap<Integer, AtomicLong> getData() {
        ConcurrentMap<Integer, AtomicLong> data = new ConcurrentHashMap<Integer, AtomicLong>();

        try {
            //нужно создать таблицу, если её ещё нет.
            Statement s = conn.createStatement();
            s.executeUpdate("CREATE TABLE IF NOT EXISTS accounts (" +
                    "  id int(5) NOT NULL AUTO_INCREMENT," +
                    "  amount bigint(20) NOT NULL," +
                    "  PRIMARY KEY (id)" +
                    ") ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;");

            //вытаскиваем данные
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

    //метод сохранения данных в бд
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

}
