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
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Util {
    // реализуйте настройку соеденения с БД
    private static Connection conn;
    private static String url = "jdbc:mysql://localhost:3306/task1.4";
    private static String username = "admin";
    private static String password = "admin";
    private static SessionFactory sessionFactory;
    private static Metadata metadata;

    private Util() {
    }

    public static Connection getConnection() throws SQLException {
        if (null == conn || conn.isClosed()) {
            try {
                conn = DriverManager.getConnection(url, username, password);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return conn;
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
    
    public static void closeConnection() {
        if (null != sessionFactory) {
            sessionFactory.close();
        }
    }
}
