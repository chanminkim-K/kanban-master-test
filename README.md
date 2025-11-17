# Kanban Board

최신 UI/UX 트렌드를 반영한 웹 기반 칸반 보드 애플리케이션

## 📖 프로젝트 설명

Kanban Board는 개인 및 팀의 작업 흐름을 시각적으로 관리할 수 있는 현대적인 웹 애플리케이션입니다. 직관적인 인터페이스와 효율적인 태스크 관리 기능을 통해 프로젝트의 생산성을 향상시키고, 작업의 진행 상황을 한눈에 파악할 수 있습니다. 사용자 인증 시스템을 통해 개인화된 작업 공간을 제공하며, 실시간으로 태스크를 생성, 수정, 삭제할 수 있는 완전한 CRUD 기능을 지원합니다.

## ✨ 주요 기능

- **사용자 인증**
  - 회원가입 및 로그인 기능
  - 안전한 사용자 세션 관리

- **칸반 보드 관리**
  - 칸반 보드 생성, 조회, 수정, 삭제 (CRUD)
  - 여러 보드를 통한 프로젝트 분리 관리

- **태스크 관리**
  - 태스크 생성, 조회, 수정, 삭제 (CRUD)
  - 태스크 상태 변경 (To Do, In Progress, Done)
  - 드래그 앤 드롭을 통한 직관적인 태스크 이동 (추가 예정)

## 🛠️ 기술 스택

### Frontend
- **React 18** - 모던 UI 라이브러리
- **TypeScript** - 타입 안정성을 갖춘 JavaScript 슈퍼셋
- **Vite** - 빠른 개발 서버 및 빌드 도구
- **Tailwind CSS v3** - 유틸리티 기반 CSS 프레임워크
- **Axios** - HTTP 클라이언트 (JWT 인터셉터)

### Backend
- **Java 17** - LTS 버전의 Java
- **Spring Boot 3.2.0** - 엔터프라이즈급 애플리케이션 프레임워크
- **Spring Security** - JWT 기반 인증 및 권한 관리
- **Spring Data JPA** - 데이터 접근 계층
- **Gradle** - 빌드 자동화 도구

### Database
- **H2 Database** - 임베디드 인메모리 데이터베이스 (개발/테스트용)

## 🚀 시작하기 (Getting Started)

### Prerequisites

프로젝트를 실행하기 위해 다음 도구들이 설치되어 있어야 합니다:

- **JDK 17** 이상
- **Node.js** 18.x 이상
- **npm** 또는 **yarn**
- **Git**

### Installation & Run

```bash
# 1. 프로젝트 클론
git clone https://github.com/chanminkim-K/kanban-master-test.git
cd kanban-master-test

# 2. 의존성 설치
npm install

# 3. 개발 서버 실행 (백엔드 + 프론트엔드 동시 실행)
npm run dev

# 백엔드 서버: http://localhost:8080
# 프론트엔드 서버: http://localhost:3000
```

### 개별 실행 (선택 사항)

백엔드와 프론트엔드를 개별적으로 실행하고 싶은 경우:

```bash
# 백엔드만 실행
npm run dev:backend

# 프론트엔드만 실행
npm run dev:frontend
```

## 📁 프로젝트 구조

```
kanban-master-test/
├── backend/                    # Spring Boot 백엔드 애플리케이션
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/          # Java 소스 코드
│   │   │   │   └── com/kanban/
│   │   │   │       ├── controller/    # REST API 컨트롤러
│   │   │   │       ├── service/       # 비즈니스 로직
│   │   │   │       ├── repository/    # 데이터 접근 계층
│   │   │   │       ├── model/         # 엔티티 (User, Board, Task)
│   │   │   │       ├── dto/           # 데이터 전송 객체
│   │   │   │       ├── security/      # JWT 인증 필터
│   │   │   │       └── config/        # 설정 파일 (Security, H2)
│   │   │   └── resources/     # 설정 파일 및 리소스
│   │   └── test/              # 테스트 코드
│   └── build.gradle           # Gradle 빌드 설정
│
├── frontend/                   # React 프론트엔드 애플리케이션
│   ├── src/
│   │   ├── components/        # 재사용 가능한 컴포넌트 (ProtectedRoute)
│   │   ├── contexts/          # Context API (AuthContext)
│   │   ├── pages/             # 페이지 컴포넌트
│   │   ├── services/          # API 호출 서비스
│   │   └── types/             # TypeScript 타입 정의
│   ├── public/                # 정적 파일
│   ├── vite.config.ts         # Vite 설정
│   ├── tailwind.config.js     # Tailwind CSS 설정
│   └── package.json           # 프로젝트 의존성
│
├── package.json                # 루트 패키지 (concurrently)
├── .gitignore                  # Git 무시 목록
└── README.md                   # 프로젝트 문서
```

## 📝 API 엔드포인트

### 인증 (Authentication)

```
POST   /api/auth/signup        # 회원가입
POST   /api/auth/login         # 로그인
POST   /api/auth/logout        # 로그아웃
GET    /api/auth/me            # 현재 사용자 정보 조회
```

### 칸반 보드 (Boards)

```
GET    /api/boards             # 모든 보드 조회
GET    /api/boards/{id}        # 특정 보드 조회
POST   /api/boards             # 새 보드 생성
PUT    /api/boards/{id}        # 보드 수정
DELETE /api/boards/{id}        # 보드 삭제
```

### 태스크 (Tasks)

```
GET    /api/boards/{boardId}/tasks       # 특정 보드의 모든 태스크 조회
GET    /api/tasks/{id}                   # 특정 태스크 조회
POST   /api/boards/{boardId}/tasks       # 새 태스크 생성
PUT    /api/tasks/{id}                   # 태스크 수정
PATCH  /api/tasks/{id}/status            # 태스크 상태 변경
DELETE /api/tasks/{id}                   # 태스크 삭제
```
---

**[메가존 클라우드 인턴십]** 칸반 보드 프로젝트
