-- Additional types of works and demo data

-- New services
INSERT INTO services (name, description, duration_minutes) VALUES
  ('Шиномонтаж', 'Замена/перебортировка шин', 40),
  ('Балансировка колес', 'Балансировка колес', 15),
  ('Замена масла', 'Замена моторного масла', 20),
  ('Антидефектогон ремонт', 'Специальная регламентная работа', 60);

-- New work options (для демонстрации)
INSERT INTO work_options (name, description, duration_minutes) VALUES
  ('Графитовая промывка АКПП', 'Промывка коробки передач', 30),
  ('Замена тормозных дисков', 'Замена тормозных дисков', 45);

-- Связи станций с новыми сервисами
-- Привяжем к существующим станциям 1 и 2
INSERT INTO station_services (station_id, service_id) VALUES
  (1, (SELECT id FROM services WHERE name='Шиномонтаж')),
  (1, (SELECT id FROM services WHERE name='Балансировка колес')),
  (2, (SELECT id FROM services WHERE name='Замена масла')),
  (2, (SELECT id FROM services WHERE name='Шиномонтаж'));
