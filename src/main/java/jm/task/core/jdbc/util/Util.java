package jm.task.core.jdbc.util;


import jm.task.core.jdbc.model.User;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.service.ServiceRegistry;

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
    private static Connection conn;
    private static SessionFactory sessionFactory;
    private static Metadata metadata;

    private Util() {
    }

    public static Connection getConnection() throws SQLException {
        if (null == conn || conn.isClosed()) {
            try {
                Properties props = getProperties();
                conn = DriverManager.getConnection(props.getProperty("db.url"), props.getProperty("db.username"), props.getProperty("db.password"));
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        }
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

    public static SessionFactory getSessionFactory() throws HibernateException {
        if (null == sessionFactory) {
            try {
                ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                        .applySetting(Environment.USE_SQL_COMMENTS, false)
                        .applySetting(Environment.SHOW_SQL, false)
                        .applySetting(Environment.HBM2DDL_AUTO, "none") //update create-drop
                        .build();
                MetadataSources metadataSources = new MetadataSources(serviceRegistry);
                metadataSources.addAnnotatedClass(User.class);
                metadata = metadataSources.getMetadataBuilder().build();
                sessionFactory = metadata.buildSessionFactory();
            } catch (HibernateException e) {
                throw new HibernateException("Not connecting to database", e);
            }
        }
        return sessionFactory;
    }

    public static String getTableName(String entityName) throws NullPointerException {
        if (null == metadata) {
            throw new NullPointerException("Metadata is null");
        }
        for (PersistentClass persistentClass : Util.metadata.getEntityBindings()) {
            if (entityName.equals(persistentClass.getJpaEntityName()) || entityName.equals(persistentClass.getClassName())) {
                return persistentClass.getTable().getName();
            }
        }
        throw new NullPointerException(String.format("Entity {%s} not found", entityName));
    }
}
