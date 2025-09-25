-- MySQL-compatible baseline schema for GENIE (reduced)

CREATE TABLE users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  email VARCHAR(191) UNIQUE,
  phone VARCHAR(50),
  password_hash TEXT,
  status VARCHAR(50),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE user_identities (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT,
  provider VARCHAR(100),
  provider_uid VARCHAR(255),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE roles (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  code VARCHAR(100) UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE user_roles (
  user_id BIGINT,
  role_id BIGINT,
  PRIMARY KEY(user_id, role_id),
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE brands (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(191) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE products (
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

CREATE TABLE ingredients (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  alias_vi TEXT,
  description_vi TEXT,
  functions JSON,
  risk_level VARCHAR(50),
  banned_in JSON,
  typical_range JSON,
  sources JSON
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE product_ingredients (
  product_id BIGINT,
  ingredient_id BIGINT,
  concentration_min DECIMAL(10,4),
  concentration_max DECIMAL(10,4),
  position_index INT,
  PRIMARY KEY(product_id, ingredient_id),
  FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
  FOREIGN KEY (ingredient_id) REFERENCES ingredients(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE regulatory_labels (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  region VARCHAR(100),
  code VARCHAR(100),
  description TEXT,
  level VARCHAR(50)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE ingredient_labels (
  ingredient_id BIGINT,
  label_id BIGINT,
  PRIMARY KEY(ingredient_id, label_id),
  FOREIGN KEY (ingredient_id) REFERENCES ingredients(id) ON DELETE CASCADE,
  FOREIGN KEY (label_id) REFERENCES regulatory_labels(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE profiles (
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

CREATE TABLE profile_prefs (
  user_id BIGINT PRIMARY KEY,
  prefer_functions JSON,
  avoid_ingredients JSON,
  budget_min DECIMAL(10,2),
  budget_max DECIMAL(10,2),
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE recs (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT,
  product_id BIGINT,
  score DECIMAL(5,2),
  reason_json JSON,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (product_id) REFERENCES products(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE feedback (
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

CREATE TABLE routines (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT,
  title TEXT,
  FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE routine_items (
  routine_id BIGINT,
  product_id BIGINT,
  step INT,
  time_of_day VARCHAR(50),
  PRIMARY KEY(routine_id, product_id),
  FOREIGN KEY (routine_id) REFERENCES routines(id) ON DELETE CASCADE,
  FOREIGN KEY (product_id) REFERENCES products(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE journal_entries (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT,
  date DATE,
  text_note TEXT,
  FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE journal_photos (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  entry_id BIGINT,
  file_key VARCHAR(512),
  ai_features_json JSON,
  FOREIGN KEY (entry_id) REFERENCES journal_entries(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE alerts (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT,
  type VARCHAR(100),
  payload_json JSON,
  status VARCHAR(50),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE schedules (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT,
  product_id BIGINT,
  cron_expr VARCHAR(255),
  channel VARCHAR(50),
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (product_id) REFERENCES products(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE retailers (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255),
  domain VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE offers (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  product_id BIGINT,
  retailer_id BIGINT,
  price DECIMAL(10,2),
  url VARCHAR(1000),
  updated_at TIMESTAMP,
  FOREIGN KEY (product_id) REFERENCES products(id),
  FOREIGN KEY (retailer_id) REFERENCES retailers(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE events (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT,
  type VARCHAR(100),
  payload_json JSON,
  ts TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
