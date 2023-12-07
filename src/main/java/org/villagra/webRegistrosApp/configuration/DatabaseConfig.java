package org.villagra.webRegistrosApp.configuration;

import org.apache.commons.dbcp2.BasicDataSource;

public class DatabaseConfig {
    private static BasicDataSource dataSource;

    static {
        dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/registro_desafioweb");
        dataSource.setUsername("root");
        dataSource.setPassword("7897");
        dataSource.setMaxTotal(100);
        dataSource.setMaxIdle(30);
        dataSource.setMaxWaitMillis(10000);

        dataSource.setRemoveAbandonedTimeout(60);
        dataSource.setLogAbandoned(true);
    }

    public static BasicDataSource getInstance() {
        return dataSource;
    }
}
