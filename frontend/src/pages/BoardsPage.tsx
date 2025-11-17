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
    <div className="min-h-screen bg-gray-50">
      {/* 헤더 */}
      <header className="bg-white shadow">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4 flex justify-between items-center">
          <h1 className="text-2xl font-bold text-gray-900">내 보드</h1>
          <div className="flex items-center space-x-4">
            <span className="text-gray-700">안녕하세요, {username}님</span>
            <button
              onClick={handleLogout}
              className="px-4 py-2 text-sm font-medium text-white bg-red-600 rounded-md hover:bg-red-700"
            >
              로그아웃
            </button>
          </div>
        </div>
      </header>

      {/* 메인 컨텐츠 */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {error && (
          <div className="mb-4 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
            {error}
          </div>
        )}

        {/* 보드 생성 버튼 */}
        <div className="mb-6">
          <button
            onClick={() => setShowCreateModal(true)}
            className="px-4 py-2 text-sm font-medium text-white bg-indigo-600 rounded-md hover:bg-indigo-700"
          >
            + 새 보드 만들기
          </button>
        </div>

        {/* 보드 목록 */}
        {boards.length === 0 ? (
          <div className="text-center py-12">
            <p className="text-gray-500">아직 보드가 없습니다. 새 보드를 만들어보세요!</p>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {boards.map((board) => (
              <div
                key={board.id}
                className="bg-white rounded-lg shadow hover:shadow-lg transition-shadow cursor-pointer"
              >
                <div onClick={() => navigate(`/boards/${board.id}`)} className="p-6">
                  <h3 className="text-xl font-semibold text-gray-900 mb-2">{board.title}</h3>
                  {board.description && (
                    <p className="text-gray-600 mb-4 line-clamp-2">{board.description}</p>
                  )}
                  <div className="flex items-center justify-between text-sm text-gray-500">
                    <span>태스크 {board.taskCount}개</span>
                    <span>{new Date(board.createdAt).toLocaleDateString()}</span>
                  </div>
                </div>
                <div className="border-t border-gray-200 px-6 py-3 flex justify-end">
                  <button
                    onClick={(e) => {
                      e.stopPropagation();
                      handleDeleteBoard(board.id);
                    }}
                    className="text-red-600 hover:text-red-800 text-sm font-medium"
                  >
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
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-lg p-6 max-w-md w-full">
            <h2 className="text-xl font-bold text-gray-900 mb-4">새 보드 만들기</h2>
            <form onSubmit={handleCreateBoard} className="space-y-4">
              <div>
                <label htmlFor="title" className="block text-sm font-medium text-gray-700">
                  보드 제목
                </label>
                <input
                  id="title"
                  type="text"
                  required
                  value={newBoardTitle}
                  onChange={(e) => setNewBoardTitle(e.target.value)}
                  className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                  placeholder="보드 제목을 입력하세요"
                />
              </div>

              <div>
                <label htmlFor="description" className="block text-sm font-medium text-gray-700">
                  설명 (선택사항)
                </label>
                <textarea
                  id="description"
                  value={newBoardDescription}
                  onChange={(e) => setNewBoardDescription(e.target.value)}
                  rows={3}
                  className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                  placeholder="보드에 대한 설명을 입력하세요"
                />
              </div>

              <div className="flex justify-end space-x-3">
                <button
                  type="button"
                  onClick={() => {
                    setShowCreateModal(false);
                    setNewBoardTitle('');
                    setNewBoardDescription('');
                    setError('');
                  }}
                  className="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-md hover:bg-gray-200"
                >
                  취소
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 text-sm font-medium text-white bg-indigo-600 rounded-md hover:bg-indigo-700"
                >
                  생성
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
