package com.kanban;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Kanban Board Application - Main Entry Point
 *
 * This is the main Spring Boot application class for the Kanban Board project.
 * It initializes and runs the Spring application context with all required configurations.
 *
 * Technology Stack:
 * - Java 17
 * - Spring Boot 3.2.0
 * - Spring Data JPA
 * - Spring Security
 * - H2 Database
 *
 * @author Megazone Cloud Internship
 * @version 1.0
 */
@SpringBootApplication
public class KanbanBoardApplication {

    public static void main(String[] args) {
        SpringApplication.run(KanbanBoardApplication.class, args);
        System.out.println("===========================================");
        System.out.println("   Kanban Board Application Started!");
        System.out.println("   Backend Server: http://localhost:8080");
        System.out.println("   H2 Console: http://localhost:8080/h2-console");
        System.out.println("===========================================");
    }
}
