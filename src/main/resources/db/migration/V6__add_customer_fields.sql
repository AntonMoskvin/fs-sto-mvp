-- Add optional customer contact fields to support applications view
ALTER TABLE public.users ADD COLUMN name VARCHAR(255);
ALTER TABLE public.users ADD COLUMN phone VARCHAR(20);

ALTER TABLE public.appointments ADD COLUMN customer_comment VARCHAR(1024);
