package jm.task.core.jdbc.util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class Util {
    private static final String URL = "jdbc:mysql://localhost:3306/first_schema_on_local_instance";
    private static final String USER = "root";
    private static final String PASS = "root";
    private volatile static Connection connection;
    public static Connection getConnection() {
        if (connection == null) {
            synchronized (Connection.class) {
                if (connection == null) {
                    try {
                        Connection connection = DriverManager.getConnection(URL, USER, PASS);
                        if (!connection.isClosed()) {
                            //System.out.println("Соединение с БД установлено");
                            return connection;
                        }
                    } catch (SQLException e) {
                        System.out.println("Ошибка соединения с БД");
                        e.printStackTrace();
                    }
                }
            }
        }
        return connection;
    }
}