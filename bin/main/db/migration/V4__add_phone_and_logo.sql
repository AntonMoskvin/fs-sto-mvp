-- Add phone column to stations and logo_url to services; seed existing rows
ALTER TABLE stations ADD COLUMN IF NOT EXISTS phone VARCHAR(32);
ALTER TABLE services ADD COLUMN IF NOT EXISTS logo_url VARCHAR(255);

UPDATE stations SET phone = '+7 495 555-0101' WHERE id = 1;
UPDATE stations SET phone = '+7 495 555-0102' WHERE id = 2;
UPDATE services SET logo_url = 'https://example.com/logos/diagnostics.png' WHERE id = 1;
