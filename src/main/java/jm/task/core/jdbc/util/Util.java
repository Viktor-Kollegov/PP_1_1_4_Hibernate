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


/**
 public class ProxyConnection implements Connection {
    private final Connection connection;

    ProxyConnection(Connection connection) {    @Override
        this.connection = connection;
    }


    public void close() throws SQLException {    }
        CustomConnectionPool.getInstance().closeConnection(this);

}
public class CustomConnectionPool implements AutoCloseable {
    private static final Logger log = LogManager.getLogger(CustomConnectionPool.class);
    private static final Lock locking = new ReentrantLock();
    private static final AtomicBoolean isInitialized = new AtomicBoolean(false);
    private static CustomConnectionPool instance;
    private final BlockingQueue<ProxyConnection> freeConnections;
    private final Queue<ProxyConnection> givenAwayConnection;
    static final int POOL_SIZE = 4;

    private CustomConnectionPool() {
        freeConnections = new LinkedBlockingQueue<>();
        givenAwayConnection = new LinkedList<>();
        try {
            for (int i = 0; i < POOL_SIZE; i++) {
                Connection connection = ConnectionCreator.getConnection();
                ProxyConnection proxyConnection = new ProxyConnection(connection);
                freeConnections.add(proxyConnection);
            }
        } catch (SQLException e) {
            log.fatal(e);
            throw new InitializationException("Error in initialization connection pool");
        }
    }

    public Connection getConnection() {
        timerTaskLocker.readLock().lock();
        ProxyConnection connection = null;
        try {
            connection = freeConnections.take();
            givenAwayConnection.add(connection);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error(e);
        } finally {
            timerTaskLocker.readLock().unlock();
        }
        return connection;
    }

    void closeConnection(Connection connection) {
        timerTaskLocker.readLock().lock();
        try {
            if (connection instanceof ProxyConnection) {
                givenAwayConnection.remove(connection);
                freeConnections.add((ProxyConnection) connection);
            } else {
                log.warn();
            }
        } finally {
            timerTaskLocker.readLock().unlock();
        }

    }

 * Return connection to connection pool.
    Прокси делается отдельным классом пакетной видимости и лежит рядом с утилкой
 (нашим пулом коннекшенов) сделано для того чтобы остальные классы даже не могли им
 воспользоваться и не видели его. Коннекшн пул же изначально создается просто из прокси
 классов коннекшенов, таким образом достав из коллекции сразу возвращаем как коннекшн
 обычный (однако это наш завернутый прокси)

    Так как мы будем наследоваться от нашего интерфейса Connection, то да нам необходимо реализовать
 все его методы, реализуем мы их при помощи поля и параметра передаваемого в конструкторе,
 передается он в конструкторе так как он жизненно необходим для прокси, иначе его смысл теряется,
 ничего ему “обволакивать”.

*/