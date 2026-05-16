-- Password for all sample users: Password123!
insert into users (id, full_name, email, password_hash, role, created_at) values
  (1, 'Avery Customer', 'customer@example.com', '{noop}Password123!', 'CUSTOMER', now()),
  (2, 'Sam Staff', 'staff@example.com', '{noop}Password123!', 'STAFF', now()),
  (3, 'Morgan Admin', 'admin@example.com', '{noop}Password123!', 'ADMIN', now());

select setval('users_id_seq', 3, true);

insert into pets (id, owner_id, name, species, breed, weight_kg, notes) values
  (1, 1, 'Mochi', 'Dog', 'Poodle', 6.40, 'Nervous around dryers'),
  (2, 1, 'Luna', 'Cat', 'Domestic Shorthair', 4.20, 'Brush gently');

select setval('pets_id_seq', 2, true);

insert into service_catalog (id, name, description, price, duration_minutes, active) values
  (1, 'Basic Grooming', 'Bath, brush, nail trim, and ear cleaning.', 45.00, 60, true),
  (2, 'Full Grooming', 'Basic grooming plus haircut and styling.', 75.00, 120, true),
  (3, 'De-shedding Treatment', 'Deep brush-out and coat treatment.', 60.00, 90, true);

select setval('service_catalog_id_seq', 3, true);

insert into bookings (
  id, customer_id, pet_id, service_id, assigned_staff_id, scheduled_at, address, notes, status, created_at, updated_at
) values
  (1, 1, 1, 2, 2, now() + interval '2 days', '123 Paw Street', 'Please call on arrival.', 'ASSIGNED', now(), now()),
  (2, 1, 2, 1, null, now() + interval '5 days', '123 Paw Street', null, 'PENDING', now(), now());

select setval('bookings_id_seq', 2, true);
