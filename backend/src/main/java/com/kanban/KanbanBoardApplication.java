package com.kanban;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 칸반 보드 애플리케이션 - 메인 진입점
 *
 * 칸반 보드 프로젝트의 메인 Spring Boot 애플리케이션 클래스입니다.
 * 필요한 모든 설정과 함께 Spring 애플리케이션 컨텍스트를 초기화하고 실행합니다.
 *
 * 기술 스택:
 * - Java 17
 * - Spring Boot 3.2.0
 * - Spring Data JPA
 * - Spring Security
 * - H2 Database
 *
 * @author 메가존 클라우드 인턴십
 * @version 1.0
 */
@SpringBootApplication
public class KanbanBoardApplication {

    public static void main(String[] args) {
        SpringApplication.run(KanbanBoardApplication.class, args);
        System.out.println("===========================================");
        System.out.println("   칸반 보드 애플리케이션이 시작되었습니다!");
        System.out.println("   백엔드 서버: http://localhost:8080");
        System.out.println("   H2 콘솔: http://localhost:8080/h2-console");
        System.out.println("===========================================");
    }
}
