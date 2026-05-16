import { Navigate, Outlet } from 'react-router-dom';
import { Role } from '../types/domain';
import { useCurrentUser } from '../hooks/useCurrentUser';

export function RoleGuard({ roles }: { roles: Role[] }) {
  const { data: user, isLoading } = useCurrentUser();

  if (isLoading) {
    return <div className="p-8 text-sm text-slate-500">Loading...</div>;
  }

  if (!user || !roles.includes(user.role)) {
    return <Navigate to="/bookings" replace />;
  }

  return <Outlet />;
}
