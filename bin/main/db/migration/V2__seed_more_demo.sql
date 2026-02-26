-- Additional demo data for local Postgres migrations
INSERT INTO stations (name, address, latitude, longitude, timezone) VALUES
  ('Demo СТО Москва 2', 'Москва, ул. Ленина 10', 55.76, 37.64, 'Europe/Moscow');

INSERT INTO work_options (name, description, duration_minutes) VALUES
  ('Диагностика двигателя', 'Комплексная диагностика двигателя', 30);

-- Link new station to existing service (id=1 assumed), adapt if necessary
INSERT INTO station_services (station_id, service_id) VALUES (2, 1);
