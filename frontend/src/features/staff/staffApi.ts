import { apiClient } from '../../shared/api/apiClient';
import { StaffResponse } from '../../shared/types/domain';

export async function listStaff() {
  const { data } = await apiClient.get<StaffResponse[]>('/staff');
  return data;
}
