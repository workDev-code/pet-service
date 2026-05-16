import { useMutation, useQuery } from '@tanstack/react-query';
import { FormEvent } from 'react';
import { queryClient } from '../../app/queryClient';
import { Button } from '../../shared/components/Button';
import { DataTable } from '../../shared/components/DataTable';
import { Select } from '../../shared/components/Select';
import { StatusBadge } from '../../shared/components/StatusBadge';
import { BookingResponse } from '../../shared/types/domain';
import { formatDateTime } from '../../shared/utils/format';
import { listStaff } from '../staff/staffApi';
import { assignBooking, listBookings } from './bookingsApi';

export function AdminBookingsPage() {
  const { data: bookings = [], isLoading } = useQuery({ queryKey: ['bookings'], queryFn: listBookings });
  const { data: staff = [] } = useQuery({ queryKey: ['staff'], queryFn: listStaff });
  const mutation = useMutation({
    mutationFn: ({ bookingId, staffId }: { bookingId: number; staffId: number }) => assignBooking(bookingId, staffId),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['bookings'] }),
  });

  function handleAssign(event: FormEvent<HTMLFormElement>, bookingId: number) {
    event.preventDefault();
    const formData = new FormData(event.currentTarget);
    const staffId = Number(formData.get('staffId'));
    if (staffId) mutation.mutate({ bookingId, staffId });
  }

  return (
    <section className="grid gap-4">
      <div>
        <h2 className="text-xl font-semibold text-slate-900">Admin Bookings</h2>
        <p className="text-sm text-slate-500">Assign staff and monitor every booking.</p>
      </div>
      {isLoading ? <p className="text-sm text-slate-500">Loading...</p> : (
        <DataTable
          data={bookings}
          columns={[
            { key: 'id', header: 'ID', render: (row) => `#${row.id}` },
            { key: 'customer', header: 'Customer', render: (row) => row.customer.fullName },
            { key: 'pet', header: 'Pet', render: (row) => row.pet.name },
            { key: 'scheduled', header: 'Scheduled', render: (row) => formatDateTime(row.scheduledAt) },
            { key: 'status', header: 'Status', render: (row) => <StatusBadge status={row.status} /> },
            {
              key: 'assign',
              header: 'Assign',
              render: (row: BookingResponse) => (
                <form className="flex min-w-64 gap-2" onSubmit={(event) => handleAssign(event, row.id)}>
                  <Select label="Staff" name="staffId" defaultValue={row.assignedStaff?.id ?? ''} className="min-w-36">
                    <option value="">Select</option>
                    {staff.map((person) => <option key={person.id} value={person.id}>{person.fullName}</option>)}
                  </Select>
                  <Button className="mt-6" type="submit" disabled={mutation.isPending}>Assign</Button>
                </form>
              ),
            },
          ]}
        />
      )}
    </section>
  );
}
