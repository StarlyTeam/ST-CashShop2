package net.starly.cashshop.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Setter;
import net.starly.cashshop.executor.AsyncExecutors;
import net.starly.core.data.Config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;

public class ConnectionPoolManager {

    private static ConnectionPoolManager internalPool;
    private HikariDataSource dataSource;
    @Setter
    private String host;
    @Setter
    private String port;
    @Setter
    private String database;
    @Setter
    private String username;
    @Setter
    private String password;
    private int minimumConnections;
    private int maximumConnections;
    private long connectionTimeout;
    private String testQuery;

    public static void initializingPoolManager(Config config) {
        if (internalPool == null)
            internalPool = new ConnectionPoolManager(
                    config.getString("database.host"),
                    config.getString("database.port"),
                    config.getString("database.user"),
                    config.getString("database.password"),
                    config.getString("database.database"),
                    10,
                    20
            );
    }

    public static ConnectionPoolManager getInternalPool() {
        return internalPool;
    }

    public ConnectionPoolManager(String address, String port, String username, String password, String database) {
        this(address, port, username, password, database, 5, 10);
    }

    public ConnectionPoolManager(String address, String port, String username, String password, String database, int minimumConnections, int maximumConnections) {
        this.host = address;
        this.port = port;
        this.username = username;
        this.password = password;
        this.database = database;
        this.minimumConnections = minimumConnections;
        this.maximumConnections = maximumConnections;
        init();
        setupPool();
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    private void init() {
        connectionTimeout = 300000L;
        testQuery = "select 1";
    }

    private void setupPool() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&autoReconnect=true");
        config.setDriverClassName("com.mysql.jdbc.Driver");
        config.setUsername(username);
        config.setPassword(password);
        config.setMinimumIdle(minimumConnections);
        config.setMaximumPoolSize(maximumConnections);
        config.setConnectionTimeout(connectionTimeout);
        config.setConnectionTestQuery(testQuery);
        dataSource = new HikariDataSource(config);
    }

    public void close(Connection connection, PreparedStatement statement, ResultSet result) {
        if (connection != null) try {
            connection.close();
        } catch (SQLException ignored) {
        }

        if (statement != null) try {
            statement.close();
        } catch (SQLException ignored) {
        }

        if (result != null) try {
            result.close();
        } catch (SQLException ignored) {
        }
    }

    public void closePool() {
        if (dataSource != null)
            dataSource.close();
        internalPool = null;
    }

    public void getSQLThread(Consumer<Connection> consumer) {
        AsyncExecutors.run(() -> {
            try {
                Connection connection = getConnection();
                consumer.accept(connection);
                if (connection != null && !connection.isClosed())
                    connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

}
