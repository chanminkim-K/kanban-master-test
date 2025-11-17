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

  // 상태별 색상
  const statusColors: Record<TaskStatus, string> = {
    TO_DO: 'bg-gray-100',
    IN_PROGRESS: 'bg-blue-100',
    DONE: 'bg-green-100',
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
    <div className="min-h-screen bg-gray-50">
      {/* 헤더 */}
      <header className="bg-white shadow">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
          <div className="flex justify-between items-center">
            <div>
              <button
                onClick={() => navigate('/boards')}
                className="text-indigo-600 hover:text-indigo-800 mb-2 text-sm"
              >
                ← 보드 목록으로
              </button>
              <h1 className="text-2xl font-bold text-gray-900">{board.title}</h1>
              {board.description && <p className="text-gray-600 mt-1">{board.description}</p>}
            </div>
            <button
              onClick={() => {
                setSelectedStatus('TO_DO');
                setShowCreateModal(true);
              }}
              className="px-4 py-2 text-sm font-medium text-white bg-indigo-600 rounded-md hover:bg-indigo-700"
            >
              + 태스크 추가
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

        {/* 칸반 보드 컬럼 */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {(['TO_DO', 'IN_PROGRESS', 'DONE'] as TaskStatus[]).map((status) => (
            <div key={status} className={`${statusColors[status]} rounded-lg p-4`}>
              <h2 className="text-lg font-semibold text-gray-900 mb-4">
                {statusNames[status]} ({getTasksByStatus(status).length})
              </h2>

              <div className="space-y-3">
                {getTasksByStatus(status).map((task) => (
                  <div key={task.id} className="bg-white rounded-lg shadow p-4">
                    <h3 className="font-medium text-gray-900 mb-2">{task.title}</h3>
                    {task.description && (
                      <p className="text-sm text-gray-600 mb-3">{task.description}</p>
                    )}

                    <div className="flex items-center justify-between text-xs">
                      <div className="flex space-x-2">
                        <button
                          onClick={() => openEditModal(task)}
                          className="text-indigo-600 hover:text-indigo-800"
                        >
                          수정
                        </button>
                        <button
                          onClick={() => handleDeleteTask(task.id)}
                          className="text-red-600 hover:text-red-800"
                        >
                          삭제
                        </button>
                      </div>

                      {/* 상태 변경 버튼 */}
                      <div className="flex space-x-1">
                        {status !== 'TO_DO' && (
                          <button
                            onClick={() =>
                              handleMoveTask(
                                task.id,
                                status === 'DONE' ? 'IN_PROGRESS' : 'TO_DO'
                              )
                            }
                            className="text-gray-600 hover:text-gray-800"
                          >
                            ←
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
                            className="text-gray-600 hover:text-gray-800"
                          >
                            →
                          </button>
                        )}
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
                className="mt-3 w-full py-2 text-sm text-gray-600 hover:text-gray-900 hover:bg-white/50 rounded transition-colors"
              >
                + 태스크 추가
              </button>
            </div>
          ))}
        </div>
      </main>

      {/* 태스크 생성 모달 */}
      {showCreateModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-lg p-6 max-w-md w-full">
            <h2 className="text-xl font-bold text-gray-900 mb-4">새 태스크 만들기</h2>
            <form onSubmit={handleCreateTask} className="space-y-4">
              <div>
                <label htmlFor="status" className="block text-sm font-medium text-gray-700">
                  상태
                </label>
                <select
                  id="status"
                  value={selectedStatus}
                  onChange={(e) => setSelectedStatus(e.target.value as TaskStatus)}
                  className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                >
                  <option value="TO_DO">할 일</option>
                  <option value="IN_PROGRESS">진행 중</option>
                  <option value="DONE">완료</option>
                </select>
              </div>

              <div>
                <label htmlFor="title" className="block text-sm font-medium text-gray-700">
                  제목
                </label>
                <input
                  id="title"
                  type="text"
                  required
                  value={taskTitle}
                  onChange={(e) => setTaskTitle(e.target.value)}
                  className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                  placeholder="태스크 제목을 입력하세요"
                />
              </div>

              <div>
                <label htmlFor="description" className="block text-sm font-medium text-gray-700">
                  설명 (선택사항)
                </label>
                <textarea
                  id="description"
                  value={taskDescription}
                  onChange={(e) => setTaskDescription(e.target.value)}
                  rows={3}
                  className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                  placeholder="태스크에 대한 설명을 입력하세요"
                />
              </div>

              <div className="flex justify-end space-x-3">
                <button
                  type="button"
                  onClick={() => {
                    setShowCreateModal(false);
                    setTaskTitle('');
                    setTaskDescription('');
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

      {/* 태스크 수정 모달 */}
      {showEditModal && selectedTask && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-lg p-6 max-w-md w-full">
            <h2 className="text-xl font-bold text-gray-900 mb-4">태스크 수정</h2>
            <form onSubmit={handleUpdateTask} className="space-y-4">
              <div>
                <label htmlFor="edit-title" className="block text-sm font-medium text-gray-700">
                  제목
                </label>
                <input
                  id="edit-title"
                  type="text"
                  required
                  value={taskTitle}
                  onChange={(e) => setTaskTitle(e.target.value)}
                  className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                />
              </div>

              <div>
                <label htmlFor="edit-description" className="block text-sm font-medium text-gray-700">
                  설명
                </label>
                <textarea
                  id="edit-description"
                  value={taskDescription}
                  onChange={(e) => setTaskDescription(e.target.value)}
                  rows={3}
                  className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                />
              </div>

              <div className="flex justify-end space-x-3">
                <button
                  type="button"
                  onClick={() => {
                    setShowEditModal(false);
                    setSelectedTask(null);
                    setTaskTitle('');
                    setTaskDescription('');
                  }}
                  className="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-md hover:bg-gray-200"
                >
                  취소
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 text-sm font-medium text-white bg-indigo-600 rounded-md hover:bg-indigo-700"
                >
                  저장
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
