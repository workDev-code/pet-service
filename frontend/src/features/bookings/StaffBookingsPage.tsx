import { useMutation, useQuery } from '@tanstack/react-query';
import { queryClient } from '../../app/queryClient';
import { Button } from '../../shared/components/Button';
import { DataTable } from '../../shared/components/DataTable';
import { StatusBadge } from '../../shared/components/StatusBadge';
import { BookingResponse, BookingStatus } from '../../shared/types/domain';
import { BookingSchedule } from './BookingSchedule';
import { listBookings, updateBookingStatus } from './bookingsApi';

export function StaffBookingsPage() {
  const { data: bookings = [], isLoading } = useQuery({ queryKey: ['bookings'], queryFn: listBookings });
  const mutation = useMutation({
    mutationFn: ({ id, status }: { id: number; status: BookingStatus }) => updateBookingStatus(id, status),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['bookings'] }),
  });

  return (
    <section className="grid gap-4">
      <div>
        <h2 className="text-xl font-semibold text-slate-900">Assigned Work</h2>
        <p className="text-sm text-slate-500">Update bookings assigned to you.</p>
      </div>
      {isLoading ? <p className="text-sm text-slate-500">Loading...</p> : (
        <DataTable
          data={bookings}
          columns={[
            { key: 'id', header: 'ID', render: (row) => `#${row.id}` },
            { key: 'customer', header: 'Customer', render: (row) => row.customer.fullName },
            { key: 'pet', header: 'Pet', render: (row) => `${row.pet.name} (${row.pet.species})` },
            { key: 'service', header: 'Service', render: (row) => row.service.name },
            { key: 'schedule', header: 'Schedule', render: (row) => <BookingSchedule booking={row} /> },
            { key: 'status', header: 'Status', render: (row) => <StatusBadge status={row.status} /> },
            {
              key: 'actions',
              header: 'Actions',
              render: (row: BookingResponse) => row.status === 'ASSIGNED' ? (
                <div className="flex gap-2">
                  <Button onClick={() => mutation.mutate({ id: row.id, status: 'COMPLETED' })}>Complete</Button>
                  <Button variant="danger" onClick={() => mutation.mutate({ id: row.id, status: 'CANCELLED' })}>Cancel</Button>
                </div>
              ) : 'No action',
            },
          ]}
        />
      )}
    </section>
  );
}
