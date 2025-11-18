/**
 * 칸반 보드 페이지
 *
 * 태스크를 칸반 보드 형식으로 표시하고 관리하는 페이지입니다.
 */

import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getBoardById } from '../services/boardService';
import {
  getTasksByBoardId,
  createTask,
  updateTask,
  updateTaskStatus,
  deleteTask,
} from '../services/taskService';
import { Board, Task, TaskStatus } from '../types';

const KanbanBoardPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const [board, setBoard] = useState<Board | null>(null);
  const [tasks, setTasks] = useState<Task[]>([]);
  const [loading, setLoading] = useState(true);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [selectedStatus, setSelectedStatus] = useState<TaskStatus>('TO_DO');
  const [selectedTask, setSelectedTask] = useState<Task | null>(null);

  const [taskTitle, setTaskTitle] = useState('');
  const [taskDescription, setTaskDescription] = useState('');
  const [error, setError] = useState('');

  // 데이터 로드
  useEffect(() => {
    loadData();
  }, [id]);

  const loadData = async () => {
    if (!id) return;

    try {
      setLoading(true);
      const [boardData, tasksData] = await Promise.all([
        getBoardById(Number(id)),
        getTasksByBoardId(Number(id)),
      ]);
      setBoard(boardData);
      setTasks(tasksData);
    } catch (err: any) {
      setError('데이터를 불러오는데 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  // 태스크 생성
  const handleCreateTask = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!id) return;

    try {
      const maxPosition = tasks
        .filter((t) => t.status === selectedStatus)
        .reduce((max, t) => Math.max(max, t.position), -1);

      await createTask(Number(id), {
        title: taskTitle,
        description: taskDescription || undefined,
        status: selectedStatus,
        position: maxPosition + 1,
      });

      setTaskTitle('');
      setTaskDescription('');
      setShowCreateModal(false);
      loadData();
    } catch (err: any) {
      setError(err.response?.data?.message || '태스크 생성에 실패했습니다.');
    }
  };

  // 태스크 수정
  const handleUpdateTask = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedTask) return;

    try {
      await updateTask(selectedTask.id, {
        title: taskTitle,
        description: taskDescription || undefined,
      });

      setTaskTitle('');
      setTaskDescription('');
      setSelectedTask(null);
      setShowEditModal(false);
      loadData();
    } catch (err: any) {
      setError(err.response?.data?.message || '태스크 수정에 실패했습니다.');
    }
  };

  // 태스크 상태 변경
  const handleMoveTask = async (taskId: number, newStatus: TaskStatus) => {
    try {
      const maxPosition = tasks
        .filter((t) => t.status === newStatus)
        .reduce((max, t) => Math.max(max, t.position), -1);

      await updateTaskStatus(taskId, {
        status: newStatus,
        position: maxPosition + 1,
      });

      loadData();
    } catch (err: any) {
      alert('태스크 이동에 실패했습니다.');
    }
  };

  // 태스크 삭제
  const handleDeleteTask = async (taskId: number) => {
    if (!window.confirm('정말로 이 태스크를 삭제하시겠습니까?')) {
      return;
    }

    try {
      await deleteTask(taskId);
      loadData();
    } catch (err: any) {
      alert('태스크 삭제에 실패했습니다.');
    }
  };

  // 태스크 편집 모달 열기
  const openEditModal = (task: Task) => {
    setSelectedTask(task);
    setTaskTitle(task.title);
    setTaskDescription(task.description || '');
    setShowEditModal(true);
  };

  // 상태별 태스크 필터링
  const getTasksByStatus = (status: TaskStatus) => {
    return tasks.filter((task) => task.status === status).sort((a, b) => a.position - b.position);
  };

  // 상태별 한글 이름
  const statusNames: Record<TaskStatus, string> = {
    TO_DO: '할 일',
    IN_PROGRESS: '진행 중',
    DONE: '완료',
  };

  // 상태별 색상 - ClickUp 스타일
  const statusColors: Record<TaskStatus, string> = {
    TO_DO: '#FFF0F5', // 연한 핑크
    IN_PROGRESS: '#E8F4FD', // 연한 파랑
    DONE: '#E8F5E9', // 연한 초록
  };

  const statusBadgeColors: Record<TaskStatus, string> = {
    TO_DO: 'bg-pink-200 text-pink-800',
    IN_PROGRESS: 'bg-blue-200 text-blue-800',
    DONE: 'bg-green-200 text-green-800',
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-gray-600">로딩 중...</div>
      </div>
    );
  }

  if (!board) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-gray-600">보드를 찾을 수 없습니다.</div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 to-slate-100">
      {/* 헤더 */}
      <header className="bg-white shadow-sm border-b border-gray-200">
        <div className="max-w-full mx-auto px-6 py-4">
          <div className="flex justify-between items-center">
            <div>
              <button
                onClick={() => navigate('/boards')}
                className="text-indigo-600 hover:text-indigo-800 mb-2 text-sm font-medium flex items-center gap-1"
              >
                ← 보드 목록으로
              </button>
              <h1 className="text-3xl font-bold text-gray-900">{board.title}</h1>
              {board.description && <p className="text-gray-600 mt-1">{board.description}</p>}
            </div>
            <button
              onClick={() => {
                setSelectedStatus('TO_DO');
                setShowCreateModal(true);
              }}
              className="px-6 py-2.5 text-sm font-semibold text-white bg-indigo-600 rounded-lg hover:bg-indigo-700 shadow-md hover:shadow-lg transition-all"
            >
              + 태스크 추가
            </button>
          </div>
        </div>
      </header>

      {/* 메인 컨텐츠 */}
      <main className="max-w-full mx-auto px-6 py-6">
        {error && (
          <div className="mb-4 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg">
            {error}
          </div>
        )}

        {/* 칸반 보드 컬럼 */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
          {(['TO_DO', 'IN_PROGRESS', 'DONE'] as TaskStatus[]).map((status) => (
            <div key={status} className="flex flex-col" style={{ minHeight: '500px' }}>
              {/* 컬럼 헤더 */}
              <div
                className="rounded-t-xl p-4 border-b-2"
                style={{
                  backgroundColor: statusColors[status],
                  borderBottomColor: status === 'TO_DO' ? '#ec4899' : status === 'IN_PROGRESS' ? '#3b82f6' : '#10b981'
                }}
              >
                <div className="flex items-center justify-between">
                  <h2 className="text-base font-bold text-gray-900 flex items-center gap-2">
                    <span className={`inline-block w-3 h-3 rounded-full ${status === 'TO_DO' ? 'bg-pink-500' : status === 'IN_PROGRESS' ? 'bg-blue-500' : 'bg-green-500'}`}></span>
                    {statusNames[status]}
                  </h2>
                  <span className={`px-2.5 py-1 rounded-full text-xs font-bold ${statusBadgeColors[status]}`}>
                    {getTasksByStatus(status).length}
                  </span>
                </div>
              </div>

              {/* 컬럼 내용 */}
              <div
                className="flex-1 p-4 rounded-b-xl"
                style={{ backgroundColor: statusColors[status] }}
              >
                <div className="space-y-3">
                  {getTasksByStatus(status).map((task) => (
                    <div
                      key={task.id}
                      className="bg-white rounded-xl shadow-sm hover:shadow-md transition-all duration-200 p-4 border border-gray-200 group cursor-pointer"
                    >
                      {/* 태스크 제목 */}
                      <h3 className="font-semibold text-gray-900 mb-2 text-sm leading-tight">
                        {task.title}
                      </h3>

                      {/* 태스크 설명 */}
                      {task.description && (
                        <p className="text-xs text-gray-600 mb-3 line-clamp-2">
                          {task.description}
                        </p>
                      )}

                      {/* 하단 정보 */}
                      <div className="flex items-center justify-between mt-3 pt-3 border-t border-gray-100">
                        {/* 날짜 정보 */}
                        <div className="flex items-center gap-1 text-xs text-gray-500">
                          <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                          </svg>
                          <span>{new Date(task.createdAt).toLocaleDateString('ko-KR', { month: 'short', day: 'numeric' })}</span>
                        </div>

                        {/* 액션 버튼들 */}
                        <div className="flex items-center gap-1">
                          {/* 이동 버튼 */}
                          {status !== 'TO_DO' && (
                            <button
                              onClick={() =>
                                handleMoveTask(
                                  task.id,
                                  status === 'DONE' ? 'IN_PROGRESS' : 'TO_DO'
                                )
                              }
                              className="p-1 text-gray-400 hover:text-gray-600 hover:bg-gray-100 rounded transition-colors"
                              title="이전 단계로"
                            >
                              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
                              </svg>
                            </button>
                          )}
                          {status !== 'DONE' && (
                            <button
                              onClick={() =>
                                handleMoveTask(
                                  task.id,
                                  status === 'TO_DO' ? 'IN_PROGRESS' : 'DONE'
                                )
                              }
                              className="p-1 text-gray-400 hover:text-gray-600 hover:bg-gray-100 rounded transition-colors"
                              title="다음 단계로"
                            >
                              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
                              </svg>
                            </button>
                          )}

                          {/* 수정 버튼 */}
                          <button
                            onClick={() => openEditModal(task)}
                            className="p-1 text-gray-400 hover:text-blue-600 hover:bg-blue-50 rounded transition-colors"
                            title="수정"
                          >
                            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                            </svg>
                          </button>

                          {/* 삭제 버튼 */}
                          <button
                            onClick={() => handleDeleteTask(task.id)}
                            className="p-1 text-gray-400 hover:text-red-600 hover:bg-red-50 rounded transition-colors"
                            title="삭제"
                          >
                            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                            </svg>
                          </button>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>

                {/* 새 태스크 추가 버튼 */}
                <button
                  onClick={() => {
                    setSelectedStatus(status);
                    setShowCreateModal(true);
                  }}
                  className="mt-3 w-full py-3 text-sm font-medium text-gray-600 hover:text-gray-900 hover:bg-white/60 rounded-lg transition-all flex items-center justify-center gap-2"
                >
                  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
                  </svg>
                  태스크 추가
                </button>
              </div>
            </div>
          ))}
        </div>
      </main>

      {/* 태스크 생성 모달 */}
      {showCreateModal && (
        <div className="fixed inset-0 bg-black bg-opacity-60 backdrop-blur-sm flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-2xl shadow-2xl p-8 max-w-lg w-full transform transition-all">
            <div className="flex items-center justify-between mb-6">
              <h2 className="text-2xl font-bold text-gray-900">새 태스크 만들기</h2>
              <button
                onClick={() => {
                  setShowCreateModal(false);
                  setTaskTitle('');
                  setTaskDescription('');
                }}
                className="text-gray-400 hover:text-gray-600 transition-colors"
              >
                <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>

            <form onSubmit={handleCreateTask} className="space-y-5">
              <div>
                <label htmlFor="status" className="block text-sm font-semibold text-gray-700 mb-2">
                  상태
                </label>
                <select
                  id="status"
                  value={selectedStatus}
                  onChange={(e) => setSelectedStatus(e.target.value as TaskStatus)}
                  className="w-full px-4 py-3 border-2 border-gray-200 rounded-xl shadow-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all"
                >
                  <option value="TO_DO">📋 할 일</option>
                  <option value="IN_PROGRESS">⚡ 진행 중</option>
                  <option value="DONE">✅ 완료</option>
                </select>
              </div>

              <div>
                <label htmlFor="title" className="block text-sm font-semibold text-gray-700 mb-2">
                  제목 <span className="text-red-500">*</span>
                </label>
                <input
                  id="title"
                  type="text"
                  required
                  value={taskTitle}
                  onChange={(e) => setTaskTitle(e.target.value)}
                  className="w-full px-4 py-3 border-2 border-gray-200 rounded-xl shadow-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all"
                  placeholder="예: 사용자 인증 기능 구현"
                />
              </div>

              <div>
                <label htmlFor="description" className="block text-sm font-semibold text-gray-700 mb-2">
                  설명
                </label>
                <textarea
                  id="description"
                  value={taskDescription}
                  onChange={(e) => setTaskDescription(e.target.value)}
                  rows={4}
                  className="w-full px-4 py-3 border-2 border-gray-200 rounded-xl shadow-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all resize-none"
                  placeholder="태스크에 대한 자세한 설명을 입력하세요..."
                />
              </div>

              <div className="flex justify-end gap-3 pt-4">
                <button
                  type="button"
                  onClick={() => {
                    setShowCreateModal(false);
                    setTaskTitle('');
                    setTaskDescription('');
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

      {/* 태스크 수정 모달 */}
      {showEditModal && selectedTask && (
        <div className="fixed inset-0 bg-black bg-opacity-60 backdrop-blur-sm flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-2xl shadow-2xl p-8 max-w-lg w-full transform transition-all">
            <div className="flex items-center justify-between mb-6">
              <h2 className="text-2xl font-bold text-gray-900">태스크 수정</h2>
              <button
                onClick={() => {
                  setShowEditModal(false);
                  setSelectedTask(null);
                  setTaskTitle('');
                  setTaskDescription('');
                }}
                className="text-gray-400 hover:text-gray-600 transition-colors"
              >
                <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>

            <form onSubmit={handleUpdateTask} className="space-y-5">
              <div>
                <label htmlFor="edit-title" className="block text-sm font-semibold text-gray-700 mb-2">
                  제목 <span className="text-red-500">*</span>
                </label>
                <input
                  id="edit-title"
                  type="text"
                  required
                  value={taskTitle}
                  onChange={(e) => setTaskTitle(e.target.value)}
                  className="w-full px-4 py-3 border-2 border-gray-200 rounded-xl shadow-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all"
                />
              </div>

              <div>
                <label htmlFor="edit-description" className="block text-sm font-semibold text-gray-700 mb-2">
                  설명
                </label>
                <textarea
                  id="edit-description"
                  value={taskDescription}
                  onChange={(e) => setTaskDescription(e.target.value)}
                  rows={4}
                  className="w-full px-4 py-3 border-2 border-gray-200 rounded-xl shadow-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all resize-none"
                />
              </div>

              <div className="flex justify-end gap-3 pt-4">
                <button
                  type="button"
                  onClick={() => {
                    setShowEditModal(false);
                    setSelectedTask(null);
                    setTaskTitle('');
                    setTaskDescription('');
                  }}
                  className="px-6 py-3 text-sm font-semibold text-gray-700 bg-gray-100 rounded-xl hover:bg-gray-200 transition-colors"
                >
                  취소
                </button>
                <button
                  type="submit"
                  className="px-6 py-3 text-sm font-semibold text-white bg-indigo-600 rounded-xl hover:bg-indigo-700 shadow-lg hover:shadow-xl transition-all"
                >
                  저장하기
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default KanbanBoardPage;
