import { Navigate, createBrowserRouter } from 'react-router-dom';
import { AppLayout } from '../shared/components/AppLayout';
import { ProtectedRoute } from '../shared/components/ProtectedRoute';
import { RoleGuard } from '../shared/components/RoleGuard';
import { LoginPage } from '../features/auth/LoginPage';
import { RegisterPage } from '../features/auth/RegisterPage';
import { AdminDeletedUsersPage } from '../features/admin/AdminDeletedUsersPage';
import { AdminUsersPage } from '../features/admin/AdminUsersPage';
import { AdminBookingsPage } from '../features/bookings/AdminBookingsPage';
import { BookingDetailPage } from '../features/bookings/BookingDetailPage';
import { BookingListPage } from '../features/bookings/BookingListPage';
import { CreateBookingPage } from '../features/bookings/CreateBookingPage';
import { StaffBookingsPage } from '../features/bookings/StaffBookingsPage';
import { PetsPage } from '../features/pets/PetsPage';

export const router = createBrowserRouter([
  { path: '/', element: <Navigate to="/bookings" replace /> },
  { path: '/login', element: <LoginPage /> },
  { path: '/register', element: <RegisterPage /> },
  {
    element: <ProtectedRoute />,
    children: [
      {
        element: <AppLayout />,
        children: [
          { path: '/bookings', element: <BookingListPage /> },
          { path: '/bookings/:id', element: <BookingDetailPage /> },
          {
            element: <RoleGuard roles={['CUSTOMER']} />,
            children: [
              { path: '/bookings/new', element: <CreateBookingPage /> },
              { path: '/pets', element: <PetsPage /> },
            ],
          },
          {
            element: <RoleGuard roles={['ADMIN']} />,
            children: [
              { path: '/admin/bookings', element: <AdminBookingsPage /> },
              { path: '/admin/customers', element: <AdminUsersPage role="CUSTOMER" /> },
              { path: '/admin/staff', element: <AdminUsersPage role="STAFF" /> },
              { path: '/admin/deleted-users', element: <AdminDeletedUsersPage /> },
            ],
          },
          {
            element: <RoleGuard roles={['STAFF']} />,
            children: [{ path: '/staff/bookings', element: <StaffBookingsPage /> }],
          },
        ],
      },
    ],
  },
]);
