/**
 * 보드 서비스
 *
 * 보드 관련 API 호출을 처리합니다.
 */

import apiClient from './api';
import { Board, CreateBoardRequest, UpdateBoardRequest } from '../types';

/**
 * 모든 보드 조회
 */
export const getAllBoards = async (): Promise<Board[]> => {
  const response = await apiClient.get<Board[]>('/boards');
  return response.data;
};

/**
 * 특정 보드 조회
 */
export const getBoardById = async (id: number): Promise<Board> => {
  const response = await apiClient.get<Board>(`/boards/${id}`);
  return response.data;
};

/**
 * 보드 생성
 */
export const createBoard = async (data: CreateBoardRequest): Promise<Board> => {
  const response = await apiClient.post<Board>('/boards', data);
  return response.data;
};

/**
 * 보드 수정
 */
export const updateBoard = async (id: number, data: UpdateBoardRequest): Promise<Board> => {
  const response = await apiClient.put<Board>(`/boards/${id}`, data);
  return response.data;
};

/**
 * 보드 삭제
 */
export const deleteBoard = async (id: number): Promise<void> => {
  await apiClient.delete(`/boards/${id}`);
};
