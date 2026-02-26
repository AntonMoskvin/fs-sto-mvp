-- Add optional customer contact fields to support applications view
ALTER TABLE users ADD COLUMN name VARCHAR(255);
ALTER TABLE users ADD COLUMN phone VARCHAR(20);

ALTER TABLE appointments ADD COLUMN customer_comment VARCHAR(1024);
