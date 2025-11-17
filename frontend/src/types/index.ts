/**
 * 타입 정의
 *
 * 애플리케이션 전체에서 사용하는 TypeScript 타입을 정의합니다.
 */

// 태스크 상태
export type TaskStatus = 'TO_DO' | 'IN_PROGRESS' | 'DONE';

// 사용자 정보
export interface User {
  id: number;
  username: string;
  email: string;
}

// 인증 응답
export interface AuthResponse {
  accessToken: string;
  tokenType: string;
  userId: number;
  username: string;
  email: string;
}

// 로그인 요청
export interface LoginRequest {
  usernameOrEmail: string;
  password: string;
}

// 회원가입 요청
export interface SignupRequest {
  username: string;
  email: string;
  password: string;
}

// 보드
export interface Board {
  id: number;
  title: string;
  description?: string;
  taskCount: number;
  createdAt: string;
  updatedAt: string;
}

// 보드 생성 요청
export interface CreateBoardRequest {
  title: string;
  description?: string;
}

// 보드 수정 요청
export interface UpdateBoardRequest {
  title: string;
  description?: string;
}

// 태스크
export interface Task {
  id: number;
  title: string;
  description?: string;
  status: TaskStatus;
  position: number;
  boardId: number;
  boardTitle: string;
  createdAt: string;
  updatedAt: string;
}

// 태스크 생성 요청
export interface CreateTaskRequest {
  title: string;
  description?: string;
  status: TaskStatus;
  position: number;
}

// 태스크 수정 요청
export interface UpdateTaskRequest {
  title: string;
  description?: string;
}

// 태스크 상태 변경 요청
export interface UpdateTaskStatusRequest {
  status: TaskStatus;
  position: number;
}

// 태스크 위치 변경 요청
export interface UpdateTaskPositionRequest {
  position: number;
}

// 에러 응답
export interface ErrorResponse {
  status: number;
  message: string;
}
