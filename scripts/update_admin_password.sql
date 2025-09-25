-- Update admin password to '123456' (BCrypt hash)
-- Run with: mysql -u root -prootpass geniedb < scripts/update_admin_password.sql

UPDATE users SET password_hash = '$2a$10$7s8q9Gm1KfYQePp1j6vG.uFQx8kz6bNqR9Yx1uTq3LwS0aZ4cH1iK' WHERE email = 'admin@example.com';

