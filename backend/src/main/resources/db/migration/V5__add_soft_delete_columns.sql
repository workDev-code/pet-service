alter table users add column if not exists updated_at timestamptz;
alter table users add column if not exists deleted_at timestamptz;

update users
set created_at = coalesce(created_at, now()),
    updated_at = coalesce(updated_at, created_at, now()),
    deleted_at = null
where created_at is null
   or updated_at is null
   or deleted_at is not null;

alter table users alter column created_at set not null;
alter table users alter column updated_at set not null;

alter table pets add column if not exists created_at timestamptz;
alter table pets add column if not exists updated_at timestamptz;
alter table pets add column if not exists deleted_at timestamptz;

update pets
set created_at = coalesce(created_at, now()),
    updated_at = coalesce(updated_at, created_at, now()),
    deleted_at = null
where created_at is null
   or updated_at is null
   or deleted_at is not null;

alter table pets alter column created_at set not null;
alter table pets alter column updated_at set not null;

alter table bookings add column if not exists deleted_at timestamptz;

update bookings
set created_at = coalesce(created_at, now()),
    updated_at = coalesce(updated_at, created_at, now()),
    deleted_at = null
where created_at is null
   or updated_at is null
   or deleted_at is not null;

alter table bookings alter column created_at set not null;
alter table bookings alter column updated_at set not null;
