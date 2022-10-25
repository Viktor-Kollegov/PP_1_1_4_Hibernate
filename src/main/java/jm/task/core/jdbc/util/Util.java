package jm.task.core.jdbc.util;

import jm.task.core.jdbc.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Util {
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/first_schema_on_local_instance?useSSL=false&serverTimezone=UTC";
    private static final String USER = "bestuser";
    private static final String PASS = "rootrootroot";
    private volatile static Connection connection;
    private volatile static SessionFactory factory;
    private static final Properties properties = new Properties();

    static {
        properties.put(Environment.DRIVER, DRIVER);
        properties.put(Environment.URL, URL);
        properties.put(Environment.USER, USER);
        properties.put(Environment.PASS, PASS);
        properties.put(Environment.DIALECT, "org.hibernate.dialect.MySQL8Dialect");
        properties.put(Environment.SHOW_SQL, "true");
        properties.put(Environment.AUTOCOMMIT, "false");
        properties.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
        properties.put(Environment.HBM2DDL_AUTO, "none");
    }

    public static Session getSession() {
        if (factory == null) {
            factory = new Configuration()
                    .addAnnotatedClass(User.class)
                    .setProperties(properties)
                    .buildSessionFactory();
        }
        return factory.openSession();
    }

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