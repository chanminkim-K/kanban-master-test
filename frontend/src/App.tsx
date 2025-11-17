/**
 * App 컴포넌트
 *
 * 애플리케이션의 메인 컴포넌트입니다.
 * 라우팅 및 인증 컨텍스트를 설정합니다.
 */

import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './contexts/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import LoginPage from './pages/LoginPage';
import SignupPage from './pages/SignupPage';
import BoardsPage from './pages/BoardsPage';
import KanbanBoardPage from './pages/KanbanBoardPage';

const App: React.FC = () => {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          {/* 기본 경로 - 로그인 페이지로 리다이렉트 */}
          <Route path="/" element={<Navigate to="/login" replace />} />

          {/* 인증 페이지 */}
          <Route path="/login" element={<LoginPage />} />
          <Route path="/signup" element={<SignupPage />} />

          {/* 보호된 페이지 */}
          <Route
            path="/boards"
            element={
              <ProtectedRoute>
                <BoardsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/boards/:id"
            element={
              <ProtectedRoute>
                <KanbanBoardPage />
              </ProtectedRoute>
            }
          />

          {/* 404 페이지 */}
          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
};

export default App;
