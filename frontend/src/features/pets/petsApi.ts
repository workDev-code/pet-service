import { apiClient } from '../../shared/api/apiClient';
import { PetResponse } from '../../shared/types/domain';

export interface CreatePetPayload {
  name: string;
  species: string;
  breed?: string;
  weightKg: number;
  notes?: string;
}

export type UpdatePetPayload = CreatePetPayload;

export async function listPets() {
  const { data } = await apiClient.get<PetResponse[]>('/pets');
  return data;
}

export async function createPet(payload: CreatePetPayload) {
  const { data } = await apiClient.post<PetResponse>('/pets', payload);
  return data;
}

export async function updatePet(id: number, payload: UpdatePetPayload) {
  const { data } = await apiClient.put<PetResponse>(`/pets/${id}`, payload);
  return data;
}

export async function deletePet(id: number) {
  await apiClient.delete(`/pets/${id}`);
}

export async function uploadPetPhoto(id: number, file: File) {
  const formData = new FormData();
  formData.append('file', file);
  const { data } = await apiClient.post<PetResponse>(`/pets/${id}/photo`, formData);
  return data;
}

export function getPetPhotoUrl(photoUrl?: string | null) {
  if (!photoUrl) {
    return undefined;
  }
  if (photoUrl.startsWith('http://') || photoUrl.startsWith('https://')) {
    return photoUrl;
  }
  const apiBaseUrl = import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api';
  return `${new URL(apiBaseUrl, window.location.origin).origin}${photoUrl}`;
}
