/**
 * 보드 목록 페이지
 *
 * 사용자의 모든 보드를 표시하고 새 보드를 생성할 수 있는 페이지입니다.
 */

import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { getAllBoards, createBoard, deleteBoard } from '../services/boardService';
import { Board } from '../types';

const BoardsPage: React.FC = () => {
  const navigate = useNavigate();
  const { username, logout } = useAuth();

  const [boards, setBoards] = useState<Board[]>([]);
  const [loading, setLoading] = useState(true);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [newBoardTitle, setNewBoardTitle] = useState('');
  const [newBoardDescription, setNewBoardDescription] = useState('');
  const [error, setError] = useState('');

  // 보드 목록 로드
  useEffect(() => {
    loadBoards();
  }, []);

  const loadBoards = async () => {
    try {
      setLoading(true);
      const data = await getAllBoards();
      setBoards(data);
    } catch (err: any) {
      setError('보드 목록을 불러오는데 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  // 보드 생성
  const handleCreateBoard = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    try {
      await createBoard({
        title: newBoardTitle,
        description: newBoardDescription || undefined,
      });

      setNewBoardTitle('');
      setNewBoardDescription('');
      setShowCreateModal(false);
      loadBoards();
    } catch (err: any) {
      setError(err.response?.data?.message || '보드 생성에 실패했습니다.');
    }
  };

  // 보드 삭제
  const handleDeleteBoard = async (id: number) => {
    if (!window.confirm('정말로 이 보드를 삭제하시겠습니까?')) {
      return;
    }

    try {
      await deleteBoard(id);
      loadBoards();
    } catch (err: any) {
      alert('보드 삭제에 실패했습니다.');
    }
  };

  // 로그아웃
  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-gray-600">로딩 중...</div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 to-slate-100">
      {/* 헤더 */}
      <header className="bg-white shadow-sm border-b border-gray-200">
        <div className="max-w-7xl mx-auto px-6 py-5 flex justify-between items-center">
          <div>
            <h1 className="text-3xl font-bold text-gray-900 mb-1">📊 내 보드</h1>
            <p className="text-sm text-gray-600">프로젝트를 관리하고 작업을 추적하세요</p>
          </div>
          <div className="flex items-center gap-4">
            <div className="text-right">
              <p className="text-sm text-gray-500">환영합니다</p>
              <p className="text-sm font-semibold text-gray-900">{username}님</p>
            </div>
            <button
              onClick={handleLogout}
              className="px-5 py-2.5 text-sm font-semibold text-white bg-red-600 rounded-lg hover:bg-red-700 shadow-md hover:shadow-lg transition-all"
            >
              로그아웃
            </button>
          </div>
        </div>
      </header>

      {/* 메인 컨텐츠 */}
      <main className="max-w-7xl mx-auto px-6 py-8">
        {error && (
          <div className="mb-6 bg-red-50 border-l-4 border-red-500 text-red-700 px-6 py-4 rounded-lg shadow-sm">
            <div className="flex items-center">
              <svg className="w-5 h-5 mr-2" fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clipRule="evenodd" />
              </svg>
              {error}
            </div>
          </div>
        )}

        {/* 보드 생성 버튼 */}
        <div className="mb-8">
          <button
            onClick={() => setShowCreateModal(true)}
            className="px-6 py-3 text-sm font-semibold text-white bg-indigo-600 rounded-xl hover:bg-indigo-700 shadow-lg hover:shadow-xl transition-all flex items-center gap-2"
          >
            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
            </svg>
            새 보드 만들기
          </button>
        </div>

        {/* 보드 목록 */}
        {boards.length === 0 ? (
          <div className="text-center py-20">
            <div className="inline-flex items-center justify-center w-16 h-16 bg-gray-200 rounded-full mb-4">
              <svg className="w-8 h-8 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
              </svg>
            </div>
            <h3 className="text-lg font-semibold text-gray-900 mb-2">아직 보드가 없습니다</h3>
            <p className="text-gray-600 mb-6">첫 번째 보드를 만들어 프로젝트를 시작하세요!</p>
            <button
              onClick={() => setShowCreateModal(true)}
              className="px-6 py-3 text-sm font-semibold text-white bg-indigo-600 rounded-xl hover:bg-indigo-700 shadow-lg hover:shadow-xl transition-all"
            >
              첫 보드 만들기
            </button>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {boards.map((board) => (
              <div
                key={board.id}
                className="bg-white rounded-xl shadow-md hover:shadow-xl transition-all duration-300 overflow-hidden border border-gray-100 group"
              >
                <div
                  onClick={() => navigate(`/boards/${board.id}`)}
                  className="p-6 cursor-pointer"
                >
                  {/* 보드 헤더 */}
                  <div className="flex items-start justify-between mb-3">
                    <div className="flex-1">
                      <h3 className="text-lg font-bold text-gray-900 mb-2 group-hover:text-indigo-600 transition-colors">
                        {board.title}
                      </h3>
                      {board.description && (
                        <p className="text-sm text-gray-600 line-clamp-2 mb-4">
                          {board.description}
                        </p>
                      )}
                    </div>
                  </div>

                  {/* 보드 정보 */}
                  <div className="flex items-center justify-between pt-4 border-t border-gray-100">
                    <div className="flex items-center gap-2">
                      <span className="inline-flex items-center px-3 py-1 rounded-full text-xs font-semibold bg-indigo-100 text-indigo-800">
                        <svg className="w-3.5 h-3.5 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4" />
                        </svg>
                        {board.taskCount}개
                      </span>
                    </div>
                    <div className="flex items-center gap-1 text-xs text-gray-500">
                      <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                      </svg>
                      {new Date(board.createdAt).toLocaleDateString('ko-KR', { month: 'short', day: 'numeric' })}
                    </div>
                  </div>
                </div>

                {/* 보드 액션 */}
                <div className="border-t border-gray-100 px-6 py-3 bg-gray-50 flex justify-end">
                  <button
                    onClick={(e) => {
                      e.stopPropagation();
                      handleDeleteBoard(board.id);
                    }}
                    className="text-red-600 hover:text-red-800 text-sm font-semibold flex items-center gap-1 transition-colors"
                  >
                    <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                    </svg>
                    삭제
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </main>

      {/* 보드 생성 모달 */}
      {showCreateModal && (
        <div className="fixed inset-0 bg-black bg-opacity-60 backdrop-blur-sm flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-2xl shadow-2xl p-8 max-w-lg w-full transform transition-all">
            <div className="flex items-center justify-between mb-6">
              <h2 className="text-2xl font-bold text-gray-900">새 보드 만들기</h2>
              <button
                onClick={() => {
                  setShowCreateModal(false);
                  setNewBoardTitle('');
                  setNewBoardDescription('');
                  setError('');
                }}
                className="text-gray-400 hover:text-gray-600 transition-colors"
              >
                <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>

            <form onSubmit={handleCreateBoard} className="space-y-5">
              <div>
                <label htmlFor="title" className="block text-sm font-semibold text-gray-700 mb-2">
                  보드 제목 <span className="text-red-500">*</span>
                </label>
                <input
                  id="title"
                  type="text"
                  required
                  value={newBoardTitle}
                  onChange={(e) => setNewBoardTitle(e.target.value)}
                  className="w-full px-4 py-3 border-2 border-gray-200 rounded-xl shadow-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all"
                  placeholder="예: 웹사이트 리뉴얼 프로젝트"
                />
              </div>

              <div>
                <label htmlFor="description" className="block text-sm font-semibold text-gray-700 mb-2">
                  설명
                </label>
                <textarea
                  id="description"
                  value={newBoardDescription}
                  onChange={(e) => setNewBoardDescription(e.target.value)}
                  rows={4}
                  className="w-full px-4 py-3 border-2 border-gray-200 rounded-xl shadow-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all resize-none"
                  placeholder="보드에 대한 자세한 설명을 입력하세요..."
                />
              </div>

              <div className="flex justify-end gap-3 pt-4">
                <button
                  type="button"
                  onClick={() => {
                    setShowCreateModal(false);
                    setNewBoardTitle('');
                    setNewBoardDescription('');
                    setError('');
                  }}
                  className="px-6 py-3 text-sm font-semibold text-gray-700 bg-gray-100 rounded-xl hover:bg-gray-200 transition-colors"
                >
                  취소
                </button>
                <button
                  type="submit"
                  className="px-6 py-3 text-sm font-semibold text-white bg-indigo-600 rounded-xl hover:bg-indigo-700 shadow-lg hover:shadow-xl transition-all"
                >
                  생성하기
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default BoardsPage;
