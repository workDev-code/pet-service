import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { authToken } from '../api/authToken';

export function ProtectedRoute() {
  const location = useLocation();
  if (!authToken.get()) {
    return <Navigate to="/login" replace state={{ from: location }} />;
  }
  return <Outlet />;
}
