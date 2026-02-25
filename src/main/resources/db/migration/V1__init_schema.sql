-- Flyway migration: initial schema for SFSTO MVP (PostgreSQL / H2 compatibility)

CREATE TABLE stations (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  name VARCHAR(255),
  address VARCHAR(255),
  latitude DOUBLE PRECISION,
  longitude DOUBLE PRECISION,
  timezone VARCHAR(100)
);

CREATE TABLE services (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  name VARCHAR(255),
  description VARCHAR(1024),
  duration_minutes INT,
  price DOUBLE PRECISION
);

CREATE TABLE station_services (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  station_id BIGINT,
  service_id BIGINT,
  FOREIGN KEY (station_id) REFERENCES stations(id),
  FOREIGN KEY (service_id) REFERENCES services(id)
);

CREATE TABLE users (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  email VARCHAR(255),
  password_hash VARCHAR(255),
  role VARCHAR(50)
);

CREATE TABLE vehicles (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  vin VARCHAR(64),
  make VARCHAR(100),
  model VARCHAR(100),
  year INT,
  customer_id BIGINT,
  FOREIGN KEY (customer_id) REFERENCES users(id)
);

CREATE TABLE appointments (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  station_id BIGINT,
  vehicle_id BIGINT,
  start_time TIMESTAMP,
  end_time TIMESTAMP,
  status VARCHAR(50),
  FOREIGN KEY (station_id) REFERENCES stations(id),
  FOREIGN KEY (vehicle_id) REFERENCES vehicles(id)
);

CREATE TABLE work_options (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  name VARCHAR(255),
  description VARCHAR(1024),
  duration_minutes INT
);

CREATE TABLE appointment_works (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  appointment_id BIGINT,
  work_option_id BIGINT,
  duration_override INT,
  FOREIGN KEY (appointment_id) REFERENCES appointments(id),
  FOREIGN KEY (work_option_id) REFERENCES work_options(id)
);

CREATE TABLE history_entries (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  vehicle_id BIGINT,
  workorder_id BIGINT,
  description VARCHAR(1024),
  timestamp TIMESTAMP,
  FOREIGN KEY (vehicle_id) REFERENCES vehicles(id),
  FOREIGN KEY (workorder_id) REFERENCES appointments(id)
);

CREATE TABLE work_orders (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  appointment_id BIGINT,
  status VARCHAR(50),
  description VARCHAR(1024),
  FOREIGN KEY (appointment_id) REFERENCES appointments(id)
);

CREATE TABLE notifications (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  user_id BIGINT,
  type VARCHAR(50),
  message TEXT,
  sent_at TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Demo data (seed)
INSERT INTO stations (name, address, latitude, longitude, timezone) VALUES ('Demo СТО Москва','Москва, Красная площадь',55.7558,37.6173,'Europe/Moscow');
INSERT INTO services (name, description, duration_minutes, price) VALUES ('Компьютерная диагностика','Диагностика',45,1500);
INSERT INTO work_options (name, description, duration_minutes) VALUES ('ТО','Техническое обслуживание',60);
INSERT INTO station_services (station_id, service_id) VALUES (1, 1);
