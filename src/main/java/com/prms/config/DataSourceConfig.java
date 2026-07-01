package com.prms.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;

@Configuration
public class DataSourceConfig {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);

    @Value("${prms.db.mysql.url}")
    private String mysqlUrl;

    @Value("${prms.db.mysql.username}")
    private String mysqlUsername;

    @Value("${prms.db.mysql.password}")
    private String mysqlPassword;

    @Value("${prms.db.mysql.driver-class-name}")
    private String mysqlDriverClassName;

    @Value("${prms.db.h2.url}")
    private String h2Url;

    @Value("${prms.db.h2.username}")
    private String h2Username;

    @Value("${prms.db.h2.password}")
    private String h2Password;

    @Value("${prms.db.h2.driver-class-name}")
    private String h2DriverClassName;

    @Bean
    @Primary
    public DataSource dataSource() {
        if (isMySqlReachable()) {
            logger.info("MySQL is reachable and accepting connections. Configuring MySQL DataSource.");
            return DataSourceBuilder.create()
                    .url(mysqlUrl)
                    .username(mysqlUsername)
                    .password(mysqlPassword)
                    .driverClassName(mysqlDriverClassName)
                    .build();
        }

        logger.warn("MySQL is not available. Falling back to H2 database.");
        return DataSourceBuilder.create()
                .url(h2Url)
                .username(h2Username)
                .password(h2Password)
                .driverClassName(h2DriverClassName)
                .build();
    }

    private boolean isMySqlReachable() {
        try (Connection connection = DriverManager.getConnection(mysqlUrl, mysqlUsername, mysqlPassword)) {
            return true;
        } catch (Exception e) {
            logger.warn("MySQL connection test failed. Falling back to H2. Reason: {}", e.getMessage());
            return false;
        }
    }
}
