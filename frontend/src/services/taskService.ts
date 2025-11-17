/**
 * 태스크 서비스
 *
 * 태스크 관련 API 호출을 처리합니다.
 */

import apiClient from './api';
import {
  Task,
  CreateTaskRequest,
  UpdateTaskRequest,
  UpdateTaskStatusRequest,
  UpdateTaskPositionRequest,
} from '../types';

/**
 * 보드의 모든 태스크 조회
 */
export const getTasksByBoardId = async (boardId: number): Promise<Task[]> => {
  const response = await apiClient.get<Task[]>(`/boards/${boardId}/tasks`);
  return response.data;
};

/**
 * 특정 태스크 조회
 */
export const getTaskById = async (id: number): Promise<Task> => {
  const response = await apiClient.get<Task>(`/tasks/${id}`);
  return response.data;
};

/**
 * 태스크 생성
 */
export const createTask = async (boardId: number, data: CreateTaskRequest): Promise<Task> => {
  const response = await apiClient.post<Task>(`/boards/${boardId}/tasks`, data);
  return response.data;
};

/**
 * 태스크 수정
 */
export const updateTask = async (id: number, data: UpdateTaskRequest): Promise<Task> => {
  const response = await apiClient.put<Task>(`/tasks/${id}`, data);
  return response.data;
};

/**
 * 태스크 상태 변경
 */
export const updateTaskStatus = async (id: number, data: UpdateTaskStatusRequest): Promise<Task> => {
  const response = await apiClient.patch<Task>(`/tasks/${id}/status`, data);
  return response.data;
};

/**
 * 태스크 위치 변경
 */
export const updateTaskPosition = async (id: number, data: UpdateTaskPositionRequest): Promise<Task> => {
  const response = await apiClient.patch<Task>(`/tasks/${id}/position`, data);
  return response.data;
};

/**
 * 태스크 삭제
 */
export const deleteTask = async (id: number): Promise<void> => {
  await apiClient.delete(`/tasks/${id}`);
};
