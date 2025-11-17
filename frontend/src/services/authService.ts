/**
 * 인증 서비스
 *
 * 사용자 인증(회원가입, 로그인, 로그아웃)과 관련된 API 호출을 처리합니다.
 */

import apiClient from './api';
import { AuthResponse, LoginRequest, SignupRequest, User } from '../types';

/**
 * 회원가입
 */
export const signup = async (data: SignupRequest): Promise<AuthResponse> => {
  const response = await apiClient.post<AuthResponse>('/auth/signup', data);
  return response.data;
};

/**
 * 로그인
 */
export const login = async (data: LoginRequest): Promise<AuthResponse> => {
  const response = await apiClient.post<AuthResponse>('/auth/login', data);
  return response.data;
};

/**
 * 로그아웃
 */
export const logout = async (): Promise<void> => {
  await apiClient.post('/auth/logout');
};

/**
 * 현재 사용자 정보 조회
 */
export const getCurrentUser = async (): Promise<User> => {
  const response = await apiClient.get<User>('/auth/me');
  return response.data;
};
