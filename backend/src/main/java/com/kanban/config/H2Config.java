package com.kanban.config;

import org.h2.tools.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.sql.SQLException;

/**
 * H2 Database Configuration
 *
 * This configuration enables the H2 database console for development and testing purposes.
 * The H2 console provides a web-based interface to interact with the database.
 *
 * Access the H2 console at: http://localhost:8080/h2-console
 *
 * Default connection settings:
 * - JDBC URL: jdbc:h2:mem:kanbandb
 * - Username: sa
 * - Password: (empty)
 *
 * Note: This configuration is for development only and should not be used in production.
 *
 * @author Megazone Cloud Internship
 */
@Configuration
@Profile({"default", "dev"})
public class H2Config {

    /**
     * Starts the H2 TCP server for remote database connections.
     * This allows external tools to connect to the H2 database.
     *
     * @return H2 Server instance
     * @throws SQLException if the server fails to start
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server h2Server() throws SQLException {
        return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092");
    }

    /**
     * Starts the H2 Web Console server.
     * This provides a web-based interface to manage the database.
     *
     * @return H2 Web Server instance
     * @throws SQLException if the server fails to start
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server h2WebServer() throws SQLException {
        return Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082");
    }
}
