import { useQuery } from '@tanstack/react-query';
import { authToken } from '../api/authToken';
import { getMe } from '../../features/auth/authApi';

export function useCurrentUser() {
  return useQuery({
    queryKey: ['me'],
    queryFn: getMe,
    enabled: Boolean(authToken.get()),
  });
}
