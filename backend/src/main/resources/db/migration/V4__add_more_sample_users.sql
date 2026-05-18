-- Password for all additional sample users: Password123!
insert into users (full_name, email, password_hash, role, created_at) values
  ('Jenny Groomer', 'jenny.groomer@example.com', '{noop}Password123!', 'STAFF', now()),
  ('Mike Groomer', 'mike.groomer@example.com', '{noop}Password123!', 'STAFF', now()),
  ('Lisa Groomer', 'lisa.groomer@example.com', '{noop}Password123!', 'STAFF', now()),
  ('David Groomer', 'david.groomer@example.com', '{noop}Password123!', 'STAFF', now()),
  ('Customer 2', 'customer2@example.com', '{noop}Password123!', 'CUSTOMER', now()),
  ('Customer 3', 'customer3@example.com', '{noop}Password123!', 'CUSTOMER', now()),
  ('Customer 4', 'customer4@example.com', '{noop}Password123!', 'CUSTOMER', now()),
  ('Customer 5', 'customer5@example.com', '{noop}Password123!', 'CUSTOMER', now()),
  ('Customer 6', 'customer6@example.com', '{noop}Password123!', 'CUSTOMER', now()),
  ('Customer 7', 'customer7@example.com', '{noop}Password123!', 'CUSTOMER', now()),
  ('Customer 8', 'customer8@example.com', '{noop}Password123!', 'CUSTOMER', now()),
  ('Customer 9', 'customer9@example.com', '{noop}Password123!', 'CUSTOMER', now()),
  ('Customer 10', 'customer10@example.com', '{noop}Password123!', 'CUSTOMER', now())
on conflict (email) do nothing;

select setval('users_id_seq', (select max(id) from users), true);
