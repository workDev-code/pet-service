import axios from 'axios';
import { authToken } from './authToken';

export const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api',
});

apiClient.interceptors.request.use((config) => {
  const token = authToken.get();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      authToken.clear();
    }
    return Promise.reject(error);
  },
);
