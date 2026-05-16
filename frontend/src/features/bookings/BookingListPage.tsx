import { useQuery } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { DataTable } from '../../shared/components/DataTable';
import { StatusBadge } from '../../shared/components/StatusBadge';
import { BookingResponse } from '../../shared/types/domain';
import { formatDateTime } from '../../shared/utils/format';
import { listBookings } from './bookingsApi';

export function BookingListPage() {
  const { data = [], isLoading } = useQuery({ queryKey: ['bookings'], queryFn: listBookings });

  return (
    <section className="grid gap-4">
      <div>
        <h2 className="text-xl font-semibold text-slate-900">Bookings</h2>
        <p className="text-sm text-slate-500">Visible bookings are filtered by your backend role.</p>
      </div>
      {isLoading ? <p className="text-sm text-slate-500">Loading...</p> : (
        <BookingTable bookings={data} />
      )}
    </section>
  );
}

export function BookingTable({ bookings }: { bookings: BookingResponse[] }) {
  return (
    <DataTable
      data={bookings}
      columns={[
        { key: 'id', header: 'ID', render: (row) => <Link className="font-medium text-brand-700" to={`/bookings/${row.id}`}>#{row.id}</Link> },
        { key: 'pet', header: 'Pet', render: (row) => row.pet.name },
        { key: 'service', header: 'Service', render: (row) => row.service.name },
        { key: 'scheduledAt', header: 'Scheduled', render: (row) => formatDateTime(row.scheduledAt) },
        { key: 'staff', header: 'Staff', render: (row) => row.assignedStaff?.fullName ?? 'Unassigned' },
        { key: 'status', header: 'Status', render: (row) => <StatusBadge status={row.status} /> },
      ]}
    />
  );
}
