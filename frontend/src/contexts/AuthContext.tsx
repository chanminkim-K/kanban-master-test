/**
 * 인증 컨텍스트
 *
 * 사용자 인증 상태를 관리하고 애플리케이션 전체에서 접근할 수 있도록 합니다.
 */

import React, { createContext, useState, useContext, useEffect, ReactNode } from 'react';
import { login as loginApi, signup as signupApi, logout as logoutApi } from '../services/authService';
import { AuthResponse, LoginRequest, SignupRequest } from '../types';

interface AuthContextType {
  isAuthenticated: boolean;
  userId: number | null;
  username: string | null;
  login: (data: LoginRequest) => Promise<void>;
  signup: (data: SignupRequest) => Promise<void>;
  logout: () => Promise<void>;
  loading: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);
  const [userId, setUserId] = useState<number | null>(null);
  const [username, setUsername] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(true);

  // 컴포넌트 마운트 시 로컬 스토리지에서 토큰 확인
  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    const storedUserId = localStorage.getItem('userId');
    const storedUsername = localStorage.getItem('username');

    if (token && storedUserId && storedUsername) {
      setIsAuthenticated(true);
      setUserId(Number(storedUserId));
      setUsername(storedUsername);
    }

    setLoading(false);
  }, []);

  // 로그인
  const login = async (data: LoginRequest): Promise<void> => {
    const response: AuthResponse = await loginApi(data);

    // 토큰 및 사용자 정보 저장
    localStorage.setItem('accessToken', response.accessToken);
    localStorage.setItem('userId', response.userId.toString());
    localStorage.setItem('username', response.username);

    setIsAuthenticated(true);
    setUserId(response.userId);
    setUsername(response.username);
  };

  // 회원가입
  const signup = async (data: SignupRequest): Promise<void> => {
    const response: AuthResponse = await signupApi(data);

    // 토큰 및 사용자 정보 저장
    localStorage.setItem('accessToken', response.accessToken);
    localStorage.setItem('userId', response.userId.toString());
    localStorage.setItem('username', response.username);

    setIsAuthenticated(true);
    setUserId(response.userId);
    setUsername(response.username);
  };

  // 로그아웃
  const logout = async (): Promise<void> => {
    try {
      await logoutApi();
    } catch (error) {
      console.error('로그아웃 중 오류 발생:', error);
    } finally {
      // 로컬 스토리지 클리어
      localStorage.removeItem('accessToken');
      localStorage.removeItem('userId');
      localStorage.removeItem('username');

      setIsAuthenticated(false);
      setUserId(null);
      setUsername(null);
    }
  };

  return (
    <AuthContext.Provider value={{ isAuthenticated, userId, username, login, signup, logout, loading }}>
      {children}
    </AuthContext.Provider>
  );
};

// 커스텀 훅
export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth는 AuthProvider 내에서 사용해야 합니다.');
  }
  return context;
};
