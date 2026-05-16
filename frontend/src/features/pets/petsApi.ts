import { apiClient } from '../../shared/api/apiClient';
import { PetResponse } from '../../shared/types/domain';

export interface CreatePetPayload {
  name: string;
  species: string;
  breed?: string;
  weightKg: number;
  notes?: string;
}

export async function listPets() {
  const { data } = await apiClient.get<PetResponse[]>('/pets');
  return data;
}

export async function createPet(payload: CreatePetPayload) {
  const { data } = await apiClient.post<PetResponse>('/pets', payload);
  return data;
}
