
-- V1__Create_initial_schema.sql

-- Bảng lưu trữ hồ sơ người dùng
CREATE TABLE profiles (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    skin_type VARCHAR(255),
    concerns TEXT,
    allergies TEXT,
    pregnant BOOLEAN,
    goals TEXT,
    lifestyle_json TEXT
);

-- Bảng thương hiệu sản phẩm
CREATE TABLE brands (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- Bảng sản phẩm
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    upc_ean VARCHAR(255) UNIQUE,
    category VARCHAR(255),
    image_url VARCHAR(255),
    country VARCHAR(255),
    brand_id BIGINT,
    created_at TIMESTAMP WITH TIME ZONE,
    FOREIGN KEY (brand_id) REFERENCES brands(id)
);

-- Bảng thành phần
CREATE TABLE ingredients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    inci_name VARCHAR(255),
    alias_vi VARCHAR(255),
    description_vi TEXT,
    functions TEXT,
    risk_level VARCHAR(255),
    banned_in TEXT,
    typical_range TEXT,
    sources TEXT
);

-- Bảng liên kết sản phẩm và thành phần
CREATE TABLE product_ingredients (
    product_id BIGINT NOT NULL,
    ingredient_id BIGINT NOT NULL,
    concentration_min DECIMAL(10, 5),
    concentration_max DECIMAL(10, 5),
    position_index INT,
    PRIMARY KEY (product_id, ingredient_id),
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (ingredient_id) REFERENCES ingredients(id)
);

-- Bảng quy trình chăm sóc da
CREATE TABLE routines (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    title TEXT
);

-- Bảng các mục trong quy trình chăm sóc da
CREATE TABLE routine_items (
    routine_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    step INT,
    time_of_day VARCHAR(255),
    PRIMARY KEY (routine_id, product_id),
    FOREIGN KEY (routine_id) REFERENCES routines(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Bảng nhật ký
CREATE TABLE journal_entries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    date DATE,
    text_note TEXT
);

-- Bảng ảnh trong nhật ký
CREATE TABLE journal_photos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entry_id BIGINT,
    file_key VARCHAR(255),
    ai_features_json VARCHAR(255),
    FOREIGN KEY (entry_id) REFERENCES journal_entries(id)
);

-- Bảng cảnh báo
CREATE TABLE alerts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    type VARCHAR(255),
    payload_json JSON,
    status VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE
);

-- Bảng lịch trình
CREATE TABLE schedules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    product_id BIGINT,
    cron_expr VARCHAR(255),
    channel VARCHAR(255)
);

-- Bảng phản hồi
CREATE TABLE feedback (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    product_id BIGINT,
    rating INT,
    note TEXT,
    reaction_tags JSON,
    created_at TIMESTAMP WITH TIME ZONE,
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Bảng đề xuất
CREATE TABLE recs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    product_id BIGINT,
    score DECIMAL(10, 5),
    reason_json JSON,
    created_at TIMESTAMP WITH TIME ZONE
);

-- Bảng sự kiện
CREATE TABLE events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    type VARCHAR(255),
    payload_json JSON,
    ts TIMESTAMP WITH TIME ZONE
);

-- Bảng nhãn quy định
CREATE TABLE regulatory_labels (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    region VARCHAR(255),
    code VARCHAR(255),
    description TEXT,
    level VARCHAR(255)
);

-- Bảng nhà bán lẻ
CREATE TABLE retailers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    domain VARCHAR(255)
);

-- Bảng ưu đãi
CREATE TABLE offers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT,
    retailer_id BIGINT,
    price DECIMAL(19, 4),
    currency VARCHAR(255),
    url VARCHAR(2048),
    last_checked TIMESTAMP WITH TIME ZONE,
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (retailer_id) REFERENCES retailers(id)
);
