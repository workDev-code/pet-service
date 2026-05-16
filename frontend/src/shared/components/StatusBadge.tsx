import { clsx } from 'clsx';
import { BookingStatus } from '../types/domain';

const styles: Record<BookingStatus, string> = {
  PENDING: 'bg-amber-100 text-amber-800',
  ASSIGNED: 'bg-sky-100 text-sky-800',
  COMPLETED: 'bg-emerald-100 text-emerald-800',
  CANCELLED: 'bg-rose-100 text-rose-800',
};

export function StatusBadge({ status }: { status: BookingStatus }) {
  return (
    <span className={clsx('inline-flex rounded-full px-2.5 py-1 text-xs font-semibold', styles[status])}>
      {status}
    </span>
  );
}
