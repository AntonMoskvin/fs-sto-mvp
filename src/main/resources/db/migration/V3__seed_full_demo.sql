-- Full demo data seed for Postgres migrations (MVP)
-- Add a second station
INSERT INTO stations (name, address, latitude, longitude, timezone) VALUES
  ('Demo СТО Москва 2', 'Москва, ул. Ленина 2', 55.75, 37.75, 'Europe/Moscow');

-- Link second station with existing service
INSERT INTO station_services (station_id, service_id) VALUES (2, 1);

-- Add additional work option for richer demo
INSERT INTO work_options (name, description, duration_minutes) VALUES
  ('Предпродажная диагностика', 'Проверка основных узлов перед продажей', 30);
