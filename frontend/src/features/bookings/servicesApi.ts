import { apiClient } from '../../shared/api/apiClient';
import { ServiceCatalogResponse } from '../../shared/types/domain';

export async function listServices() {
  const { data } = await apiClient.get<ServiceCatalogResponse[]>('/services');
  return data;
}
