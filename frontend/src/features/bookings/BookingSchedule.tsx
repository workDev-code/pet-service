import { BookingResponse } from '../../shared/types/domain';
import { formatDateTime, formatDurationMinutes, formatTime, getEndDateTime } from '../../shared/utils/format';

type ScheduleSource = Pick<BookingResponse, 'scheduledAt' | 'service'>;

interface Props {
  booking: ScheduleSource;
}

export function BookingSchedule({ booking }: Props) {
  const endAt = getEndDateTime(booking.scheduledAt, booking.service.durationMinutes);

  return (
    <div className="min-w-56">
      <p className="text-xs font-medium uppercase tracking-wide text-slate-500">Start</p>
      <p className="font-medium text-slate-900">{formatDateTime(booking.scheduledAt)}</p>
      <div className="mt-2 grid grid-cols-2 gap-2 text-xs">
        <SchedulePill label="Duration" value={formatDurationMinutes(booking.service.durationMinutes)} />
        <SchedulePill label="End" value={formatTime(endAt)} />
      </div>
    </div>
  );
}

export function BookingScheduleTimeline({ booking }: Props) {
  const endAt = getEndDateTime(booking.scheduledAt, booking.service.durationMinutes);

  return (
    <div className="grid gap-3 sm:grid-cols-3">
      <ScheduleStep label="Start" value={formatDateTime(booking.scheduledAt)} />
      <ScheduleStep label="Duration" value={formatDurationMinutes(booking.service.durationMinutes)} />
      <ScheduleStep label="End" value={formatDateTime(endAt)} />
    </div>
  );
}

function SchedulePill({ label, value }: { label: string; value: string }) {
  return (
    <div className="rounded-md border border-slate-200 bg-slate-50 px-2 py-1.5">
      <span className="block text-slate-500">{label}</span>
      <span className="block font-medium text-slate-800">{value}</span>
    </div>
  );
}

function ScheduleStep({ label, value }: { label: string; value: string }) {
  return (
    <div className="border-l-2 border-brand-300 pl-3">
      <p className="text-xs font-medium uppercase tracking-wide text-slate-500">{label}</p>
      <p className="mt-1 font-semibold text-slate-900">{value}</p>
    </div>
  );
}
