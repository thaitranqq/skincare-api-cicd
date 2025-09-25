-- Manual MySQL setup + migrations for GENIE
-- NOTE: This script now assumes the database `geniedb` and user already exist.
-- It contains schema and seed statements only. Do NOT run as root to create users.

-- Use the existing database
USE geniedb;

-- Baseline schema (V1)
-- (aligned with src/main/resources/db/migration/V1__baseline.sql)
CREATE TABLE IF NOT EXISTS users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  email VARCHAR(255) UNIQUE,
  phone VARCHAR(50),
  password_hash TEXT,
  status VARCHAR(50),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS user_identities (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT,
  provider VARCHAR(100),
  provider_uid VARCHAR(255),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS roles (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  code VARCHAR(100) UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS user_roles (
  user_id BIGINT,
  role_id BIGINT,
  PRIMARY KEY(user_id, role_id),
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS brands (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS products (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  brand_id BIGINT,
  name VARCHAR(255) NOT NULL,
  upc_ean VARCHAR(64) UNIQUE,
  category VARCHAR(100),
  image_url TEXT,
  country VARCHAR(100),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (brand_id) REFERENCES brands(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS ingredients (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  inci_name VARCHAR(255) NOT NULL,
  alias_vi TEXT,
  description_vi TEXT,
  functions JSON,
  risk_level VARCHAR(50),
  banned_in JSON,
  typical_range JSON,
  sources JSON
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS product_ingredients (
  product_id BIGINT,
  ingredient_id BIGINT,
  concentration_min DECIMAL(10,4),
  concentration_max DECIMAL(10,4),
  position_index INT,
  PRIMARY KEY(product_id, ingredient_id),
  FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
  FOREIGN KEY (ingredient_id) REFERENCES ingredients(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS regulatory_labels (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  region VARCHAR(100),
  code VARCHAR(100),
  description TEXT,
  level VARCHAR(50)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS ingredient_labels (
  ingredient_id BIGINT,
  label_id BIGINT,
  PRIMARY KEY(ingredient_id, label_id),
  FOREIGN KEY (ingredient_id) REFERENCES ingredients(id) ON DELETE CASCADE,
  FOREIGN KEY (label_id) REFERENCES regulatory_labels(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS profiles (
  user_id BIGINT PRIMARY KEY,
  skin_type VARCHAR(50),
  concerns JSON,
  allergies JSON,
  pregnant BOOLEAN,
  conditions JSON,
  lifestyle_json JSON,
  goals JSON,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS profile_prefs (
  user_id BIGINT PRIMARY KEY,
  prefer_functions JSON,
  avoid_ingredients JSON,
  budget_min DECIMAL(10,2),
  budget_max DECIMAL(10,2),
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS recs (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT,
  product_id BIGINT,
  score DECIMAL(5,2),
  reason_json JSON,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (product_id) REFERENCES products(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS feedback (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT,
  product_id BIGINT,
  rating INT,
  reaction_tags JSON,
  note TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (product_id) REFERENCES products(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS routines (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT,
  title TEXT,
  FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS routine_items (
  routine_id BIGINT,
  product_id BIGINT,
  step INT,
  time_of_day VARCHAR(50),
  PRIMARY KEY(routine_id, product_id),
  FOREIGN KEY (routine_id) REFERENCES routines(id) ON DELETE CASCADE,
  FOREIGN KEY (product_id) REFERENCES products(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS journal_entries (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT,
  date DATE,
  text_note TEXT,
  FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS journal_photos (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  entry_id BIGINT,
  file_key VARCHAR(512),
  ai_features_json JSON,
  FOREIGN KEY (entry_id) REFERENCES journal_entries(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS alerts (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT,
  type VARCHAR(100),
  payload_json JSON,
  status VARCHAR(50),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS schedules (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT,
  product_id BIGINT,
  cron_expr VARCHAR(255),
  channel VARCHAR(50),
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (product_id) REFERENCES products(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS retailers (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255),
  domain VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS offers (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  product_id BIGINT,
  retailer_id BIGINT,
  price DECIMAL(10,2),
  url VARCHAR(1000),
  updated_at TIMESTAMP,
  FOREIGN KEY (product_id) REFERENCES products(id),
  FOREIGN KEY (retailer_id) REFERENCES retailers(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS events (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT,
  type VARCHAR(100),
  payload_json JSON,
  ts TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4) Seed data (copied from V2__seed_data.sql)
INSERT INTO users (id, email, phone, password_hash, status, created_at) VALUES
(1, 'admin@example.com', '+841234567890', '$2a$10$7s8q9Gm1KfYQePp1j6vG.uFQx8kz6bNqR9Yx1uTq3LwS0aZ4cH1iK', 'ACTIVE', NOW());

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

INSERT INTO retailers (id, name, domain) VALUES (1, 'Retailer A', 'retailer-a.vn');
INSERT INTO offers (id, product_id, retailer_id, price, url, updated_at) VALUES (1, 1, 1, 199000.00, 'https://retailer-a.vn/p/1', NOW());

INSERT INTO events (user_id, type, payload_json) VALUES (1, 'SIGNIN', JSON_OBJECT('ip','127.0.0.1'));

-- Done
COMMIT;
