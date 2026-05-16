import { apiClient } from '../../shared/api/apiClient';
import { authToken } from '../../shared/api/authToken';
import { AuthResponse, Role, UserResponse } from '../../shared/types/domain';

export interface LoginPayload {
  email: string;
  password: string;
}

export interface RegisterPayload extends LoginPayload {
  fullName: string;
  role: Exclude<Role, 'ADMIN'>;
}

export async function login(payload: LoginPayload) {
  const { data } = await apiClient.post<AuthResponse>('/auth/login', payload);
  authToken.set(data.token);
  return data;
}

export async function register(payload: RegisterPayload) {
  const { data } = await apiClient.post<AuthResponse>('/auth/register', payload);
  authToken.set(data.token);
  return data;
}

export async function getMe() {
  const { data } = await apiClient.get<UserResponse>('/auth/me');
  return data;
}

export function logout() {
  authToken.clear();
}
