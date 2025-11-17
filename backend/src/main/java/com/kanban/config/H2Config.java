package com.kanban.config;

import org.h2.tools.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.sql.SQLException;

/**
 * H2 데이터베이스 설정
 *
 * 개발 및 테스트 목적으로 H2 데이터베이스 콘솔을 활성화하는 설정입니다.
 * H2 콘솔은 데이터베이스와 상호작용할 수 있는 웹 기반 인터페이스를 제공합니다.
 *
 * H2 콘솔 접속: http://localhost:8080/h2-console
 *
 * 기본 연결 설정:
 * - JDBC URL: jdbc:h2:mem:kanbandb
 * - 사용자명: sa
 * - 비밀번호: (비어있음)
 *
 * 참고: 이 설정은 개발 전용이며 프로덕션 환경에서는 사용하지 않아야 합니다.
 * 현재 H2 Server 클래스 이슈로 임시 비활성화 (application.properties에서 H2 콘솔 활성화)
 *
 * @author 메가존 클라우드 인턴십
 */
//@Configuration
//@Profile({"default", "dev"})
public class H2Config {

    /**
     * 원격 데이터베이스 연결을 위한 H2 TCP 서버를 시작합니다.
     * 외부 도구에서 H2 데이터베이스에 연결할 수 있도록 허용합니다.
     *
     * @return H2 서버 인스턴스
     * @throws SQLException 서버 시작 실패 시
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server h2Server() throws SQLException {
        return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092");
    }

    /**
     * H2 웹 콘솔 서버를 시작합니다.
     * 데이터베이스 관리를 위한 웹 기반 인터페이스를 제공합니다.
     *
     * @return H2 웹 서버 인스턴스
     * @throws SQLException 서버 시작 실패 시
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server h2WebServer() throws SQLException {
        return Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082");
    }
}
