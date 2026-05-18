import { CalendarDays, LogOut, PawPrint, Plus, RotateCcw, Scissors, UserCog, Users } from 'lucide-react';
import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { queryClient } from '../../app/queryClient';
import { logout } from '../../features/auth/authApi';
import { useCurrentUser } from '../hooks/useCurrentUser';
import { Button } from './Button';

export function AppLayout() {
  const navigate = useNavigate();
  const { data: user } = useCurrentUser();

  function handleLogout() {
    logout();
    queryClient.clear();
    navigate('/login');
  }

  const linkClass = ({ isActive }: { isActive: boolean }) =>
    `inline-flex items-center gap-2 rounded-md px-3 py-2 text-sm font-medium ${
      isActive ? 'bg-brand-100 text-brand-700' : 'text-slate-600 hover:bg-slate-100'
    }`;

  return (
    <div className="min-h-screen">
      <header className="border-b border-slate-200 bg-white">
        <div className="mx-auto flex max-w-6xl items-center justify-between px-4 py-4">
          <div>
            <h1 className="text-lg font-semibold text-slate-900">Pet Service Booking</h1>
            <p className="text-sm text-slate-500">{user?.fullName} · {user?.role}</p>
          </div>
          <Button variant="secondary" onClick={handleLogout}>
            <LogOut className="mr-2 h-4 w-4" />
            Logout
          </Button>
        </div>
      </header>

      <div className="mx-auto grid max-w-6xl gap-6 px-4 py-6 md:grid-cols-[220px_1fr]">
        <nav className="flex gap-2 md:flex-col">
          <NavLink className={linkClass} to="/bookings">
            <CalendarDays className="h-4 w-4" />
            My Bookings
          </NavLink>
          {user?.role === 'CUSTOMER' ? (
            <>
              <NavLink className={linkClass} to="/bookings/new">
                <Plus className="h-4 w-4" />
                New Booking
              </NavLink>
              <NavLink className={linkClass} to="/pets">
                <PawPrint className="h-4 w-4" />
                My Pets
              </NavLink>
            </>
          ) : null}
          {user?.role === 'ADMIN' ? (
            <>
              <NavLink className={linkClass} to="/admin/bookings">
                <Users className="h-4 w-4" />
                Bookings
              </NavLink>
              <NavLink className={linkClass} to="/admin/customers">
                <Users className="h-4 w-4" />
                Customers
              </NavLink>
              <NavLink className={linkClass} to="/admin/staff">
                <UserCog className="h-4 w-4" />
                Staff
              </NavLink>
              <NavLink className={linkClass} to="/admin/deleted-users">
                <RotateCcw className="h-4 w-4" />
                Deleted Users
              </NavLink>
            </>
          ) : null}
          {user?.role === 'STAFF' ? (
            <NavLink className={linkClass} to="/staff/bookings">
              <Scissors className="h-4 w-4" />
              Staff
            </NavLink>
          ) : null}
        </nav>
        <main>
          <Outlet />
        </main>
      </div>
    </div>
  );
}
