import { useMutation, useQuery } from '@tanstack/react-query';
import { useNavigate, useParams } from 'react-router-dom';
import { queryClient } from '../../app/queryClient';
import { Button } from '../../shared/components/Button';
import { StatusBadge } from '../../shared/components/StatusBadge';
import { useCurrentUser } from '../../shared/hooks/useCurrentUser';
import { BookingStatus } from '../../shared/types/domain';
import { formatCurrency } from '../../shared/utils/format';
import { BookingScheduleTimeline } from './BookingSchedule';
import { getBooking, updateBookingStatus } from './bookingsApi';

export function BookingDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const bookingId = Number(id);
  const { data: user } = useCurrentUser();
  const { data: booking, isLoading } = useQuery({
    queryKey: ['booking', bookingId],
    queryFn: () => getBooking(bookingId),
    enabled: Number.isFinite(bookingId),
  });
  const mutation = useMutation({
    mutationFn: (status: BookingStatus) => updateBookingStatus(bookingId, status),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['bookings'] });
      queryClient.invalidateQueries({ queryKey: ['booking', bookingId] });
    },
  });

  if (isLoading) return <p className="text-sm text-slate-500">Loading...</p>;
  if (!booking) return <p className="text-sm text-slate-500">Booking not found.</p>;

  return (
    <section className="grid gap-4">
      <Button className="w-fit" variant="secondary" onClick={() => navigate(-1)}>Back</Button>
      <div className="rounded-md border border-slate-200 bg-white p-5">
        <div className="mb-4 flex items-start justify-between gap-4">
          <div>
            <h2 className="text-xl font-semibold text-slate-900">Booking #{booking.id}</h2>
            <p className="text-sm text-slate-500">{booking.pet.name} · {booking.service.name}</p>
          </div>
          <StatusBadge status={booking.status} />
        </div>
        <div className="mb-5">
          <BookingScheduleTimeline booking={booking} />
        </div>
        <dl className="grid gap-3 text-sm md:grid-cols-2">
          <Detail label="Customer" value={booking.customer.fullName} />
          <Detail label="Staff" value={booking.assignedStaff?.fullName ?? 'Unassigned'} />
          <Detail label="Price" value={formatCurrency(booking.service.price)} />
          <Detail label="Address" value={booking.address} />
          <Detail label="Notes" value={booking.notes ?? 'None'} />
        </dl>
        <div className="mt-5 flex flex-wrap gap-2">
          {user?.role === 'CUSTOMER' && booking.status !== 'CANCELLED' && booking.status !== 'COMPLETED' ? (
            <Button variant="danger" onClick={() => mutation.mutate('CANCELLED')}>Cancel booking</Button>
          ) : null}
          {user?.role === 'STAFF' && booking.status === 'ASSIGNED' ? (
            <>
              <Button onClick={() => mutation.mutate('COMPLETED')}>Mark completed</Button>
              <Button variant="danger" onClick={() => mutation.mutate('CANCELLED')}>Cancel</Button>
            </>
          ) : null}
        </div>
      </div>
    </section>
  );
}

function Detail({ label, value }: { label: string; value: string }) {
  return (
    <div>
      <dt className="font-medium text-slate-500">{label}</dt>
      <dd className="mt-1 text-slate-900">{value}</dd>
    </div>
  );
}
