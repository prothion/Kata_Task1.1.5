package jm.task.core.jdbc.util;


import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Util {
    // реализуйте настройку соеденения с БД
    private static Connection conn = null;
    private static Util instance = null;
    private Util() {
        try {
            if (null == conn || conn.isClosed()) {
                Properties properties = getProperties();
                conn = DriverManager.getConnection(properties.getProperty("db.url"), properties.getProperty("db.username"), properties.getProperty("db.password"));
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
    public static Util getInstance() {
        if (null == instance) {
            instance = new Util();
        }
        return instance;
    }
    public Connection getConnection() {
        return conn;
    }
    private static Properties getProperties() throws IOException {
        Properties properties = new Properties();
        try (InputStream openStr = Files.newInputStream(Paths.get(Util.class.getResource("/prop.properties").toURI()))) {
            properties.load(openStr);
            return properties;
        } catch (IOException | URISyntaxException e) {
            throw new IOException("Не найден properties файл!", e);
        }
    }
}
