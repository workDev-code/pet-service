alter table bookings add column if not exists version bigint not null default 0;

create unique index if not exists ux_bookings_pet_scheduled_active
  on bookings (pet_id, scheduled_at)
  where deleted_at is null and status <> 'CANCELLED';

create unique index if not exists ux_bookings_service_scheduled_active
  on bookings (service_id, scheduled_at)
  where deleted_at is null and status <> 'CANCELLED';

create unique index if not exists ux_bookings_staff_scheduled_active
  on bookings (assigned_staff_id, scheduled_at)
  where deleted_at is null and assigned_staff_id is not null and status <> 'CANCELLED';
