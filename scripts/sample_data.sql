-- D:/demo/scripts/sample_data.sql

-- Disable foreign key checks to allow inserting data in any order
SET FOREIGN_KEY_CHECKS = 0;

-- Clear existing data (optional, uncomment if you want to clear before inserting)
-- TRUNCATE TABLE alerts;
-- TRUNCATE TABLE brands;
-- TRUNCATE TABLE events;
-- TRUNCATE TABLE feedback;
-- TRUNCATE TABLE ingredient_labels;
-- TRUNCATE TABLE ingredients;
-- TRUNCATE TABLE journal_photos;
-- TRUNCATE TABLE journal_entries;
-- TRUNCATE TABLE offers;
-- TRUNCATE TABLE product_ingredients;
-- TRUNCATE TABLE products;
-- TRUNCATE TABLE profile_prefs;
-- TRUNCATE TABLE profiles;
-- TRUNCATE TABLE recs;
-- TRUNCATE TABLE regulatory_labels;
-- TRUNCATE TABLE retailers;
-- TRUNCATE TABLE roles;
-- TRUNCATE TABLE routine_items;
-- TRUNCATE TABLE routines;
-- TRUNCATE TABLE schedules;
-- TRUNCATE TABLE user_identities;
-- TRUNCATE TABLE user_roles;
-- TRUNCATE TABLE users;
-- TRUNCATE TABLE verification_tokens;

-- 1. Insert into `users` (Necessary for foreign key constraints in many tables)
INSERT INTO `users` (`id`, `email`, `phone`, `password_hash`, `status`, `created_at`) VALUES
(1, 'user1@example.com', '1234567890', '$2a$10$somehashedpassword1', 'ACTIVE', NOW()),
(2, 'user2@example.com', '0987654321', '$2a$10$somehashedpassword2', 'ACTIVE', NOW()),
(3, 'admin@example.com', '1122334455', '$2a$10$somehashedpassword3', 'ACTIVE', NOW());

-- 2. Insert into `roles`
INSERT INTO `roles` (`id`, `code`) VALUES
(1, 'USER'),
(2, 'ADMIN');

-- 3. Insert into `user_roles`
INSERT INTO `user_roles` (`user_id`, `role_id`) VALUES
(1, 1), -- user1 is a USER
(2, 1), -- user2 is a USER
(3, 1), -- admin is a USER
(3, 2); -- admin is also an ADMIN

-- 4. Insert into `brands`
INSERT INTO `brands` (`id`, `name`) VALUES
(1, 'The Ordinary'),
(2, 'Cerave'),
(3, 'La Roche-Posay');

-- 5. Insert into `products`
INSERT INTO `products` (`id`, `brand_id`, `name`, `upc_ean`, `category`, `image_url`, `country`, `created_at`) VALUES
(1, 1, 'Niacinamide 10% + Zinc 1%', '764072675001', 'Serum', 'https://example.com/niacinamide.jpg', 'CA', NOW()),
(2, 2, 'Hydrating Facial Cleanser', '3337875597397', 'Cleanser', 'https://example.com/cerave_cleanser.jpg', 'US', NOW()),
(3, 3, 'Anthelios Melt-in Milk Sunscreen SPF 60', '3606000532414', 'Sunscreen', 'https://example.com/anthelios.jpg', 'FR', NOW()),
(4, 1, 'Hyaluronic Acid 2% + B5', '764072675002', 'Serum', 'https://example.com/hyaluronic.jpg', 'CA', NOW());

-- 6. Insert into `ingredients`
INSERT INTO `ingredients` (`id`, `alias_vi`, `description_vi`, `functions`, `risk_level`, `banned_in`, `typical_range`, `sources`) VALUES
(1, 'Niacinamide', 'Vitamin B3, giúp cải thiện tông màu da, giảm mụn và se khít lỗ chân lông.', '["Skin-restoring", "Anti-acne", "Antioxidant"]', 'LOW', '[]', '{"min": 1, "max": 10}', '["Synthetic"]'),
(2, 'Zinc PCA', 'Muối kẽm của axit pyrrolidone carboxylic, giúp kiểm soát dầu và giảm viêm.', '["Sebum control", "Anti-inflammatory"]', 'LOW', '[]', '{"min": 0.1, "max": 1}', '["Synthetic"]'),
(3, 'Hyaluronic Acid', 'Chất giữ ẩm mạnh mẽ, giúp da ngậm nước và căng mọng.', '["Hydration", "Humectant"]', 'LOW', '[]', '{"min": 0.1, "max": 2}', '["Fermentation"]'),
(4, 'Retinol', 'Dạng vitamin A, giúp chống lão hóa, giảm nếp nhăn và cải thiện kết cấu da.', '["Anti-aging", "Cell turnover"]', 'HIGH', '["EU (above certain concentration)"]', '{"min": 0.01, "max": 1}', '["Synthetic"]'),
(5, 'Fragrance', 'Hương liệu tổng hợp, có thể gây kích ứng cho da nhạy cảm.', '["Masking", "Aesthetic"]', 'MEDIUM', '[]', '{"min": 0.01, "max": 1}', '["Synthetic"]'),
(6, 'Alcohol Denat.', 'Cồn biến tính, có thể làm khô da.', '["Solvent", "Astringent"]', 'MEDIUM', '[]', '{"min": 1, "max": 10}', '["Synthetic"]');

-- 7. Insert into `product_ingredients`
INSERT INTO `product_ingredients` (`product_id`, `ingredient_id`, `concentration_min`, `concentration_max`, `position_index`) VALUES
(1, 1, 0.10, 0.10, 1), -- Niacinamide 10% + Zinc 1%
(1, 2, 0.01, 0.01, 2),
(4, 3, 0.02, 0.02, 1); -- Hyaluronic Acid 2% + B5

-- 8. Insert into `retailers`
INSERT INTO `retailers` (`id`, `name`, `domain`) VALUES
(1, 'Sephora', 'sephora.com'),
(2, 'Ulta Beauty', 'ulta.com'),
(3, 'Amazon', 'amazon.com');

-- 9. Insert into `offers`
INSERT INTO `offers` (`id`, `product_id`, `retailer_id`, `price`, `url`, `updated_at`) VALUES
(1, 1, 1, 5.90, 'https://sephora.com/niacinamide', NOW()),
(2, 1, 2, 5.90, 'https://ulta.com/niacinamide', NOW()),
(3, 2, 1, 12.99, 'https://sephora.com/cerave-cleanser', NOW()),
(4, 3, 3, 24.99, 'https://amazon.com/anthelios', NOW());

-- 10. Insert into `profiles`
INSERT INTO `profiles` (`user_id`, `skin_type`, `concerns`, `allergies`, `pregnant`, `conditions`, `lifestyle_json`, `goals`) VALUES
(1, 'OILY', '["Acne", "Pores"]', '["Fragrance"]', FALSE, '[]', '{"budget_min": 10, "budget_max": 50}', '["ANTI_ACNE", "SEBUM_CONTROL"]'),
(2, 'DRY', '["Dehydration", "Fine Lines"]', '[]', FALSE, '[]', '{"budget_min": 20, "budget_max": 100}', '["HYDRATION", "ANTI_AGING"]'),
(3, 'NORMAL', '[]', '[]', TRUE, '[]', '{"budget_min": 0, "budget_max": 200}', '[]');

-- 11. Insert into `profile_prefs` (This table might be redundant if `profiles.lifestyle_json` and `profiles.goals` are used directly)
-- Assuming `profile_prefs` is still used for some legacy reason or specific preferences not in `profiles`
INSERT INTO `profile_prefs` (`user_id`, `prefer_functions`, `avoid_ingredients`, `budget_min`, `budget_max`) VALUES
(1, '["ANTI_ACNE", "SEBUM_CONTROL"]', '["Fragrance"]', 10.00, 50.00),
(2, '["HYDRATION", "ANTI_AGING"]', '[]', 20.00, 100.00),
(3, '[]', '[]', 0.00, 200.00);

-- 12. Insert into `feedback`
INSERT INTO `feedback` (`id`, `user_id`, `product_id`, `rating`, `reaction_tags`, `note`, `created_at`) VALUES
(1, 1, 1, 5, '["Effective", "Non-irritating"]', 'Sản phẩm tuyệt vời cho da dầu mụn!', NOW()),
(2, 2, 2, 4, '["Hydrating"]', 'Làm sạch nhẹ nhàng, không gây khô da.', NOW()),
(3, 1, 3, 2, '["Irritating"]', 'Gây kích ứng nhẹ cho da nhạy cảm của tôi.', NOW());

-- 13. Insert into `journal_entries`
INSERT INTO `journal_entries` (`id`, `user_id`, `date`, `text_note`) VALUES
(1, 1, '2023-10-01', 'Da hôm nay khá ổn, ít mụn hơn.'),
(2, 1, '2023-10-05', 'Bắt đầu dùng sản phẩm mới, cảm thấy hơi châm chích.'),
(3, 2, '2023-09-28', 'Da khô hơn bình thường, cần cấp ẩm nhiều hơn.');

-- 14. Insert into `journal_photos`
INSERT INTO `journal_photos` (`id`, `entry_id`, `file_key`, `ai_features_json`) VALUES
(1, 1, 'journal/user1/20231001_selfie.jpg', '{"acne_count": 5, "redness_score": 0.3}'),
(2, 1, 'journal/user1/20231001_product.jpg', '{"product_detected": "Niacinamide 10% + Zinc 1%"}');

-- 15. Insert into `routines`
INSERT INTO `routines` (`id`, `user_id`, `title`) VALUES
(1, 1, 'Quy trình sáng cho da dầu mụn'),
(2, 2, 'Quy trình tối cấp ẩm');

-- 16. Insert into `routine_items`
INSERT INTO `routine_items` (`routine_id`, `product_id`, `step`, `time_of_day`) VALUES
(1, 2, 1, 'MORNING'), -- Cleanser
(1, 1, 2, 'MORNING'), -- Niacinamide
(1, 3, 3, 'MORNING'), -- Sunscreen
(2, 2, 1, 'NIGHT'), -- Cleanser
(2, 4, 2, 'NIGHT'); -- Hyaluronic Acid

-- 17. Insert into `schedules`
INSERT INTO `schedules` (`id`, `user_id`, `product_id`, `cron_expr`, `channel`) VALUES
(1, 1, 1, '0 0 9 * * ?', 'EMAIL'), -- Email reminder for Niacinamide at 9 AM daily
(2, 2, 4, '0 30 21 * * ?', 'PUSH'); -- Push notification for Hyaluronic Acid at 9:30 PM daily

-- 18. Insert into `alerts`
INSERT INTO `alerts` (`id`, `user_id`, `type`, `payload_json`, `status`, `created_at`) VALUES
(1, 1, 'PRODUCT_RECALL', '{"product_id": 1, "reason": "Batch recall"}', 'NEW', NOW()),
(2, 2, 'PRICE_DROP', '{"product_id": 4, "old_price": 10.00, "new_price": 8.00}', 'SENT', NOW());

-- 19. Insert into `events`
INSERT INTO `events` (`id`, `user_id`, `type`, `payload_json`, `ts`) VALUES
(1, 1, 'PRODUCT_VIEW', '{"product_id": 1, "source": "homepage"}', NOW()),
(2, 1, 'ADD_TO_CART', '{"product_id": 1, "quantity": 1}', NOW()),
(3, 2, 'PROFILE_UPDATE', '{"field": "skin_type", "old_value": "NORMAL", "new_value": "DRY"}', NOW());

-- 20. Insert into `recs`
INSERT INTO `recs` (`id`, `user_id`, `product_id`, `score`, `reason_json`, `created_at`) VALUES
(1, 1, 1, 0.95, '{"match_goals": ["ANTI_ACNE"], "avoid_allergies": []}', NOW()),
(2, 2, 4, 0.90, '{"match_goals": ["HYDRATION"]}', NOW());

-- 21. Insert into `regulatory_labels`
INSERT INTO `regulatory_labels` (`id`, `region`, `code`, `description`, `level`) VALUES
(1, 'EU', 'EU1223/2009', 'European Cosmetics Regulation', 'HIGH'),
(2, 'US', 'FDA-GRAS', 'Generally Recognized As Safe by FDA', 'MEDIUM');

-- 22. Insert into `ingredient_labels`
INSERT INTO `ingredient_labels` (`ingredient_id`, `label_id`) VALUES
(1, 2), -- Niacinamide is FDA-GRAS
(3, 2); -- Hyaluronic Acid is FDA-GRAS

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;
