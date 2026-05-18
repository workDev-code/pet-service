import { apiClient } from '../../shared/api/apiClient';
import { Role, UserResponse } from '../../shared/types/domain';

export interface CreateAdminUserPayload {
  fullName: string;
  email: string;
  password: string;
  role: Role;
}

export interface UpdateAdminUserPayload {
  fullName: string;
  email: string;
  password?: string;
  role: Role;
}

export interface DeletedAdminUserResponse extends UserResponse {
  updatedAt: string;
  deletedAt: string;
}

export async function listAdminUsers(role: Role) {
  const { data } = await apiClient.get<UserResponse[]>('/admin/users', { params: { role } });
  return data;
}

export async function listDeletedAdminUsers() {
  const { data } = await apiClient.get<DeletedAdminUserResponse[]>('/admin/users/deleted');
  return data;
}

export async function createAdminUser(payload: CreateAdminUserPayload) {
  const { data } = await apiClient.post<UserResponse>('/admin/users', payload);
  return data;
}

export async function updateAdminUser(id: number, payload: UpdateAdminUserPayload) {
  const { data } = await apiClient.put<UserResponse>(`/admin/users/${id}`, payload);
  return data;
}

export async function deleteAdminUser(id: number) {
  await apiClient.delete(`/admin/users/${id}`);
}

export async function restoreAdminUser(id: number) {
  const { data } = await apiClient.patch<UserResponse>(`/admin/users/${id}/restore`);
  return data;
}
