create table users (
  id bigserial primary key,
  full_name varchar(120) not null,
  email varchar(160) not null unique,
  password_hash varchar(255) not null,
  role varchar(30) not null check (role in ('CUSTOMER', 'STAFF', 'ADMIN')),
  created_at timestamptz not null default now()
);

create table pets (
  id bigserial primary key,
  owner_id bigint not null references users(id),
  name varchar(80) not null,
  species varchar(60) not null,
  breed varchar(80),
  weight_kg numeric(5,2) not null,
  notes text
);

create table service_catalog (
  id bigserial primary key,
  name varchar(120) not null,
  description text,
  price numeric(10,2) not null,
  duration_minutes integer not null,
  active boolean not null default true
);

create table bookings (
  id bigserial primary key,
  customer_id bigint not null references users(id),
  pet_id bigint not null references pets(id),
  service_id bigint not null references service_catalog(id),
  assigned_staff_id bigint references users(id),
  scheduled_at timestamptz not null,
  address text not null,
  notes text,
  status varchar(30) not null check (status in ('PENDING', 'ASSIGNED', 'COMPLETED', 'CANCELLED')),
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create index idx_pets_owner_id on pets(owner_id);
create index idx_bookings_customer_id on bookings(customer_id);
create index idx_bookings_assigned_staff_id on bookings(assigned_staff_id);
create index idx_bookings_status on bookings(status);
create index idx_bookings_scheduled_at on bookings(scheduled_at);
