-- Minimal H2 schema for local development (used when spring.profiles.active=local)
-- Only create tables required by AuthService to avoid H2 "table not found" errors

CREATE TABLE IF NOT EXISTS users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  email VARCHAR(191) NOT NULL,
  phone VARCHAR(50),
  password_hash VARCHAR(255),
  status VARCHAR(50),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS roles (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  code VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS user_roles (
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  PRIMARY KEY(user_id, role_id)
);

CREATE TABLE IF NOT EXISTS profiles (
  user_id BIGINT PRIMARY KEY,
  skin_type VARCHAR(50),
  concerns VARCHAR(1000),
  allergies VARCHAR(1000),
  pregnant BOOLEAN,
  conditions VARCHAR(1000),
  lifestyle_json VARCHAR(2000),
  goals VARCHAR(1000)
);

-- Add simple foreign keys for clarity (H2 accepts them)
ALTER TABLE user_roles ADD CONSTRAINT IF NOT EXISTS fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id);
ALTER TABLE user_roles ADD CONSTRAINT IF NOT EXISTS fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id);
ALTER TABLE profiles ADD CONSTRAINT IF NOT EXISTS fk_profiles_user FOREIGN KEY (user_id) REFERENCES users(id);


INSERT INTO users (id, email, phone, password_hash, status, created_at) VALUES
    (1, 'admin@example.com', '+841234567890', '$2a$10$WNxyyP0plzSQ5jFoMpGx7.XU.xFJuRBRc/8rU43DbwYz87ebKQQ2q', 'ACTIVE', NOW());

INSERT INTO roles (id, code) VALUES
                                 (1, 'ADMIN'),
                                 (2, 'USER');

INSERT INTO user_roles (user_id, role_id) VALUES
                                              (1, 1),
                                              (1, 2);

INSERT INTO brands (id, name) VALUES
    (1, 'ABC');

INSERT INTO products (id, brand_id, name, upc_ean, category, image_url, country, created_at) VALUES
    (1, 1, 'Serum Niacinamide 10%', '8931234567890', 'Serum', '', 'VN', NOW());

INSERT INTO ingredients (id, inci_name, alias_vi, description_vi, functions, risk_level, banned_in, typical_range, sources) VALUES
                                                                                                                                (1, 'Niacinamide', 'Niacinamide', 'Niacinamide description', JSON_ARRAY('HYDRATION','ANTI_AGE'), 'GOOD', JSON_ARRAY(), JSON_ARRAY('5-10%'), JSON_ARRAY('synthetic')),
                                                                                                                                (2, 'Phenoxyethanol', 'Phenoxyethanol', 'Preservative', JSON_ARRAY('PRESERVATIVE'), 'RISKY', JSON_ARRAY(), JSON_ARRAY(), JSON_ARRAY('synthetic'));

INSERT INTO product_ingredients (product_id, ingredient_id, concentration_min, concentration_max, position_index) VALUES
                                                                                                                      (1, 1, 5.0, 10.0, 1),
                                                                                                                      (1, 2, NULL, NULL, 2);

INSERT INTO profiles (user_id, skin_type, concerns, allergies, pregnant, conditions, lifestyle_json, goals) VALUES
    (1, 'NORMAL', JSON_ARRAY('ACNE'), JSON_ARRAY('FRAGRANCE'), FALSE, JSON_ARRAY(), JSON_OBJECT('sleep','8h'), JSON_ARRAY('BRIGHTEN'));

-- Simple offer and retailer seed
INSERT INTO retailers (id, name, domain) VALUES (1, 'Retailer A', 'retailer-a.vn');
INSERT INTO offers (id, product_id, retailer_id, price, url, updated_at) VALUES (1, 1, 1, 199000.00, 'https://retailer-a.vn/p/1', NOW());

-- A few events
INSERT INTO events (user_id, type, payload_json) VALUES (1, 'SIGNIN', JSON_OBJECT('ip','127.0.0.1'));