--
-- File generated with SQLiteStudio v3.4.21 on Thu Jun 18 23:50:09 2026
--
-- Text encoding used: UTF-8
--
PRAGMA foreign_keys = off;
BEGIN TRANSACTION;

-- Table: ai_client_advisor
DROP TABLE IF EXISTS ai_client_advisor;
CREATE TABLE IF NOT EXISTS ai_client_advisor (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    ai_session_id INTEGER NOT NULL,
    voice_transcript TEXT,
    past_purchase_context TEXT,
    suggested_products TEXT,
    FOREIGN KEY (ai_session_id) REFERENCES ai_sessions(id) ON DELETE CASCADE
);

-- Table: ai_dermatologist
DROP TABLE IF EXISTS ai_dermatologist;
CREATE TABLE IF NOT EXISTS ai_dermatologist (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    ai_session_id INTEGER NOT NULL,
    image_url TEXT,
    skin_analysis TEXT,
    recommendation_skincare TEXT,
    confidence REAL,
    FOREIGN KEY (ai_session_id) REFERENCES ai_sessions(id) ON DELETE CASCADE
);

-- Table: ai_makeup_artist
DROP TABLE IF EXISTS ai_makeup_artist;
CREATE TABLE IF NOT EXISTS ai_makeup_artist (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    ai_session_id INTEGER NOT NULL,
    original_image_url TEXT,
    facial_landmarks TEXT,
    virtual_makeup_image_url TEXT,
    makeup_products_used TEXT,
    FOREIGN KEY (ai_session_id) REFERENCES ai_sessions(id) ON DELETE CASCADE
);

-- Table: ai_sessions
DROP TABLE IF EXISTS ai_sessions;
CREATE TABLE IF NOT EXISTS ai_sessions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER,
    session_id TEXT,
    ai_type TEXT NOT NULL,
    input_data TEXT,
    output_data TEXT,
    product_recommendations TEXT,
    device_info TEXT,
    processing_time_ms INTEGER,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (session_id) REFERENCES guest_sessions(session_id)
);

-- Table: banners
DROP TABLE IF EXISTS banners;
CREATE TABLE IF NOT EXISTS banners (
    banner_id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT,
    image_url TEXT NOT NULL,
    target_type TEXT,
    target_id INTEGER,
    target_url TEXT,
    sort_order INTEGER DEFAULT 0,
    is_active INTEGER DEFAULT 1,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP
);
INSERT INTO banners (banner_id, title, image_url, target_type, target_id, target_url, sort_order, is_active, created_at) VALUES (1, 'Bộ sưu tập Unicorn Magic', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781357354/Bo_suu_tap_Unicorn_Magic_jub3uc.png', 'product', 1, NULL, 1, 1, '2026-06-13 14:24:49');
INSERT INTO banners (banner_id, title, image_url, target_type, target_id, target_url, sort_order, is_active, created_at) VALUES (2, 'Giảm 20% toàn bộ son môi', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781357353/Giam_20_toan_bo_son_moi_czatit.png', 'category', 3, NULL, 2, 1, '2026-06-13 14:24:49');
INSERT INTO banners (banner_id, title, image_url, target_type, target_id, target_url, sort_order, is_active, created_at) VALUES (3, 'Set quà tặng Sweet Bear', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781357353/Set_qua_tang_Sweet_Bear_p3tekw.png', 'product', 10, NULL, 3, 1, '2026-06-13 14:24:49');
INSERT INTO banners (banner_id, title, image_url, target_type, target_id, target_url, sort_order, is_active, created_at) VALUES (4, 'New Arrival: Galaxy Eyeshadow', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781357352/New_Arrival_Galaxy_Eyeshadow_fjygjh.png', 'product', 22, NULL, 4, 1, '2026-06-13 14:24:49');
INSERT INTO banners (banner_id, title, image_url, target_type, target_id, target_url, sort_order, is_active, created_at) VALUES (5, 'Free ship đơn từ 300k', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781357354/Free_ship_don_tu_300k_pcsqib.png', 'url', NULL, '/promotion/freeship', 5, 1, '2026-06-13 14:24:49');
INSERT INTO banners (banner_id, title, image_url, target_type, target_id, target_url, sort_order, is_active, created_at) VALUES (6, 'Mua 1 tặng 1 chổi trang điểm', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781357353/Mua_1_tang_1_choi_trang_diem_t1pjgf.png', 'product', 18, NULL, 6, 1, '2026-06-13 14:24:49');
INSERT INTO banners (banner_id, title, image_url, target_type, target_id, target_url, sort_order, is_active, created_at) VALUES (7, 'Thử AI Makeup Artist ngay', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781357352/Thu_AI_Makeup_Artist_ngay_axljn1.png', 'url', NULL, '/ai/makeup-artist', 7, 1, '2026-06-13 14:24:49');
INSERT INTO banners (banner_id, title, image_url, target_type, target_id, target_url, sort_order, is_active, created_at) VALUES (8, 'Hội viên nhận voucher đặc biệt', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781357355/Hoi_vien_nhan_voucher_dac_biet_pv6zkg.png', 'url', NULL, '/vip', 8, 1, '2026-06-13 14:24:49');
INSERT INTO banners (banner_id, title, image_url, target_type, target_id, target_url, sort_order, is_active, created_at) VALUES (9, 'Mẹo trang điểm từ cộng đồng', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781357352/Thu_AI_Makeup_Artist_ngay_axljn1.png', 'url', NULL, '/community', 9, 1, '2026-06-13 14:24:49');
INSERT INTO banners (banner_id, title, image_url, target_type, target_id, target_url, sort_order, is_active, created_at) VALUES (10, 'Flash sale 12h trưa', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781357352/Flash_sale_12h_trua_th68tt.png', 'url', NULL, '/flash-sale', 10, 1, '2026-06-13 14:24:49');

-- Table: cart_items
DROP TABLE IF EXISTS cart_items;
CREATE TABLE IF NOT EXISTS cart_items (
    cart_item_id INTEGER PRIMARY KEY AUTOINCREMENT,
    cart_id INTEGER NOT NULL,
    product_id INTEGER NOT NULL,
    variant_id INTEGER,
    quantity INTEGER NOT NULL,
    unit_price REAL,
    FOREIGN KEY (cart_id) REFERENCES carts(cart_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id),
    FOREIGN KEY (variant_id) REFERENCES product_variants(variant_id)
);
INSERT INTO cart_items (cart_item_id, cart_id, product_id, variant_id, quantity, unit_price) VALUES (1, 1, 1, 8, 1, 997500.0);
INSERT INTO cart_items (cart_item_id, cart_id, product_id, variant_id, quantity, unit_price) VALUES (2, 1, 2, 11, 2, 322500.0);
INSERT INTO cart_items (cart_item_id, cart_id, product_id, variant_id, quantity, unit_price) VALUES (3, 2, 3, 1, 1, 550000.0);
INSERT INTO cart_items (cart_item_id, cart_id, product_id, variant_id, quantity, unit_price) VALUES (4, 2, 5, 13, 1, 175000.0);
INSERT INTO cart_items (cart_item_id, cart_id, product_id, variant_id, quantity, unit_price) VALUES (5, 3, 10, 25, 1, 747500.0);
INSERT INTO cart_items (cart_item_id, cart_id, product_id, variant_id, quantity, unit_price) VALUES (6, 3, 4, 4, 2, 250000.0);
INSERT INTO cart_items (cart_item_id, cart_id, product_id, variant_id, quantity, unit_price) VALUES (7, 4, 7, 18, 1, 375000.0);
INSERT INTO cart_items (cart_item_id, cart_id, product_id, variant_id, quantity, unit_price) VALUES (8, 4, 12, 6, 1, 300000.0);
INSERT INTO cart_items (cart_item_id, cart_id, product_id, variant_id, quantity, unit_price) VALUES (9, 5, 15, 34, 1, 200000.0);
INSERT INTO cart_items (cart_item_id, cart_id, product_id, variant_id, quantity, unit_price) VALUES (10, 5, 18, 41, 1, 425000.0);

-- Table: carts
DROP TABLE IF EXISTS carts;
CREATE TABLE IF NOT EXISTS carts (
    cart_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER,
    session_id TEXT,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    updated_at TEXT DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (session_id) REFERENCES guest_sessions(session_id)
);
INSERT INTO carts (cart_id, user_id, session_id, created_at, updated_at) VALUES (1, 1, NULL, '2025-01-20 09:00:00', '2025-01-20 09:30:00');
INSERT INTO carts (cart_id, user_id, session_id, created_at, updated_at) VALUES (2, NULL, 'guest_001', '2025-01-21 14:00:00', '2025-01-21 14:20:00');
INSERT INTO carts (cart_id, user_id, session_id, created_at, updated_at) VALUES (3, 3, NULL, '2025-01-22 18:00:00', '2025-01-22 18:15:00');
INSERT INTO carts (cart_id, user_id, session_id, created_at, updated_at) VALUES (4, NULL, 'guest_003', '2025-01-23 10:00:00', '2025-01-23 10:10:00');
INSERT INTO carts (cart_id, user_id, session_id, created_at, updated_at) VALUES (5, 5, NULL, '2025-01-24 20:30:00', '2025-01-24 20:45:00');

-- Table: categories
DROP TABLE IF EXISTS categories;
CREATE TABLE IF NOT EXISTS categories (
    category_id INTEGER PRIMARY KEY AUTOINCREMENT,
    category_name TEXT NOT NULL,
    parent_id INTEGER,
    sort_order INTEGER,
    image_url TEXT,
    FOREIGN KEY (parent_id) REFERENCES categories(category_id) ON DELETE CASCADE
);
INSERT INTO categories (category_id, category_name, parent_id, sort_order, image_url) VALUES (1, 'Trang điểm mặt', NULL, 1, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781340916/face_cpqlrz.webp');
INSERT INTO categories (category_id, category_name, parent_id, sort_order, image_url) VALUES (2, 'Trang điểm mắt', NULL, 2, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781340914/eyes_d0850n.webp');
INSERT INTO categories (category_id, category_name, parent_id, sort_order, image_url) VALUES (3, 'Trang điểm môi', NULL, 3, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781340915/lips_glkuhk.webp');
INSERT INTO categories (category_id, category_name, parent_id, sort_order, image_url) VALUES (4, 'Dụng cụ & Phụ kiện', NULL, 4, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781340914/tools_q0ftuk.webp');
INSERT INTO categories (category_id, category_name, parent_id, sort_order, image_url) VALUES (5, 'Set & Quà tặng', NULL, 5, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781340911/set_gift_ygfenz.webp');
INSERT INTO categories (category_id, category_name, parent_id, sort_order, image_url) VALUES (6, 'Phấn mắt', 2, 6, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781340911/phan_mat_xtlxgb.jpg');
INSERT INTO categories (category_id, category_name, parent_id, sort_order, image_url) VALUES (7, 'Má hồng', 1, 7, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781340911/ma_hong_r2373s.webp');
INSERT INTO categories (category_id, category_name, parent_id, sort_order, image_url) VALUES (8, 'Son lì', 3, 8, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781340910/son_li_w5gt1v.webp');
INSERT INTO categories (category_id, category_name, parent_id, sort_order, image_url) VALUES (9, 'Son bóng', 3, 9, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781340910/son_bong_p77qzw.webp');
INSERT INTO categories (category_id, category_name, parent_id, sort_order, image_url) VALUES (10, 'Cọ trang điểm', 4, 10, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781340910/co_trang_diem_zgi4dz.webp');

-- Table: chatbot_conversations
DROP TABLE IF EXISTS chatbot_conversations;
CREATE TABLE IF NOT EXISTS chatbot_conversations (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER,
    session_id TEXT,
    started_at TEXT DEFAULT CURRENT_TIMESTAMP,
    ended_at TEXT,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (session_id) REFERENCES guest_sessions(session_id)
);

-- Table: chatbot_messages
DROP TABLE IF EXISTS chatbot_messages;
CREATE TABLE IF NOT EXISTS chatbot_messages (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    conversation_id INTEGER NOT NULL,
    sender TEXT NOT NULL,
    message TEXT,
    intent TEXT,
    response_time_ms INTEGER,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (conversation_id) REFERENCES chatbot_conversations(id) ON DELETE CASCADE
);

-- Table: comments
DROP TABLE IF EXISTS comments;
CREATE TABLE IF NOT EXISTS comments (
    comment_id INTEGER PRIMARY KEY AUTOINCREMENT,
    post_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    parent_id INTEGER,
    content TEXT,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES community_posts(post_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (parent_id) REFERENCES comments(comment_id) ON DELETE CASCADE
);
INSERT INTO comments (comment_id, post_id, user_id, parent_id, content, created_at) VALUES (1, 1, 2, NULL, 'Bảng này mình cũng mua, rất ưng nè.', '2025-01-10 10:35:00');
INSERT INTO comments (comment_id, post_id, user_id, parent_id, content, created_at) VALUES (2, 1, 3, 1, 'Màu nào lên chuẩn nhất bạn?', '2025-01-10 10:40:00');
INSERT INTO comments (comment_id, post_id, user_id, parent_id, content, created_at) VALUES (3, 2, 4, NULL, 'Son này có say màu không ạ?', '2025-01-11 14:25:00');
INSERT INTO comments (comment_id, post_id, user_id, parent_id, content, created_at) VALUES (4, 2, 2, 3, 'Có dính một chút nhưng không đáng kể.', '2025-01-11 14:30:00');
INSERT INTO comments (comment_id, post_id, user_id, parent_id, content, created_at) VALUES (5, 3, 5, NULL, 'Da dầu dùng có bị trôi không?', '2025-01-12 09:20:00');
INSERT INTO comments (comment_id, post_id, user_id, parent_id, content, created_at) VALUES (6, 3, 3, 5, 'Mình da dầu vẫn ổn, có phủ phấn nhé.', '2025-01-12 09:25:00');
INSERT INTO comments (comment_id, post_id, user_id, parent_id, content, created_at) VALUES (7, 6, 7, NULL, 'Set này có đủ dùng để cưới không?', '2025-01-15 20:35:00');
INSERT INTO comments (comment_id, post_id, user_id, parent_id, content, created_at) VALUES (8, 6, 6, 7, 'Đủ nha, có cả phấn mắt, má hồng, son.', '2025-01-15 20:40:00');
INSERT INTO comments (comment_id, post_id, user_id, parent_id, content, created_at) VALUES (9, 10, 1, NULL, 'AI đó có miễn phí không bạn?', '2025-01-19 22:15:00');
INSERT INTO comments (comment_id, post_id, user_id, parent_id, content, created_at) VALUES (10, 10, 10, 9, 'Có, free cho thành viên.', '2025-01-19 22:20:00');
INSERT INTO comments (comment_id, post_id, user_id, parent_id, content, created_at) VALUES (11, 12, 8, NULL, 'Bảng này có nhũ rơi không?', '2025-01-21 09:35:00');
INSERT INTO comments (comment_id, post_id, user_id, parent_id, content, created_at) VALUES (12, 12, 2, 11, 'Mình đánh vài lần thấy ổn.', '2025-01-21 09:40:00');
INSERT INTO comments (comment_id, post_id, user_id, parent_id, content, created_at) VALUES (13, 13, 9, NULL, 'Bạn có thể làm video hướng dẫn chi tiết được không?', '2025-01-22 16:05:00');
INSERT INTO comments (comment_id, post_id, user_id, parent_id, content, created_at) VALUES (14, 13, 3, 13, 'Mình sẽ làm sớm, cảm ơn bạn.', '2025-01-22 16:10:00');
INSERT INTO comments (comment_id, post_id, user_id, parent_id, content, created_at) VALUES (15, 17, 10, NULL, 'Màu này có hợp da ngăm không?', '2025-01-26 15:35:00');
INSERT INTO comments (comment_id, post_id, user_id, parent_id, content, created_at) VALUES (16, 17, 7, 15, 'Rất hợp, da ngăm tông cam đỏ cực đẹp.', '2025-01-26 15:40:00');
INSERT INTO comments (comment_id, post_id, user_id, parent_id, content, created_at) VALUES (17, 20, 1, NULL, 'Set này nặng bao nhiêu kg vậy ạ?', '2025-01-29 08:05:00');
INSERT INTO comments (comment_id, post_id, user_id, parent_id, content, created_at) VALUES (18, 20, 10, 17, 'Khoảng 1.5kg, bao bì đẹp lắm.', '2025-01-29 08:10:00');

-- Table: community_posts
DROP TABLE IF EXISTS community_posts;
CREATE TABLE IF NOT EXISTS community_posts (
    post_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    content TEXT,
    images TEXT,
    product_tag INTEGER,
    like_count INTEGER DEFAULT 0,
    comment_count INTEGER DEFAULT 0,
    is_hidden INTEGER DEFAULT 0,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP, post_type TEXT DEFAULT 'review',
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (product_tag) REFERENCES products(product_id)
);
INSERT INTO community_posts (post_id, user_id, content, images, product_tag, like_count, comment_count, is_hidden, created_at, post_type) VALUES (1, 1, 'Thích quá trời bảng Unicorn Magic, lên màu cực xinh!', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781529924/pos1_zvo164.png', 1, 15, 3, 0, '2025-01-10 10:30:00', 'review');
INSERT INTO community_posts (post_id, user_id, content, images, product_tag, like_count, comment_count, is_hidden, created_at, post_type) VALUES (2, 2, 'Son Velvet Rose mướt, không khô môi. Recommend nha.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781532226/pos2_z8pge2.png', 2, 22, 5, 0, '2025-01-11 14:20:00', 'review');
INSERT INTO community_posts (post_id, user_id, content, images, product_tag, like_count, comment_count, is_hidden, created_at, post_type) VALUES (3, 3, 'Cushion Cloud che phủ tốt, da mình khô vẫn mịn.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781532228/pos3_lllu1n.png', 3, 18, 2, 0, '2025-01-12 09:15:00', 'review');
INSERT INTO community_posts (post_id, user_id, content, images, product_tag, like_count, comment_count, is_hidden, created_at, post_type) VALUES (4, 4, 'Má hồng Bunny Blush dễ thương nhưng hơi nhạt.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781532236/pos4_a1ojqr.png', 4, 8, 1, 0, '2025-01-13 18:00:00', 'review');
INSERT INTO community_posts (post_id, user_id, content, images, product_tag, like_count, comment_count, is_hidden, created_at, post_type) VALUES (5, 5, 'Eyeliner Starry kẻ một đường là chuẩn, không lem.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781532230/pos5_rje33p.png', 5, 12, 0, 0, '2025-01-14 12:00:00', 'review');
INSERT INTO community_posts (post_id, user_id, content, images, product_tag, like_count, comment_count, is_hidden, created_at, post_type) VALUES (6, 6, 'Set Sweet Bear làm quà tặng sinh nhật bạn thân, cô ấy thích lắm!', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781532231/pos6_kmabag.png', 10, 34, 7, 0, '2025-01-15 20:30:00', 'review');
INSERT INTO community_posts (post_id, user_id, content, images, product_tag, like_count, comment_count, is_hidden, created_at, post_type) VALUES (7, 7, 'Highlight Butterfly lên da căng bóng tự nhiên.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781529924/pos1_zvo164.png', 7, 25, 4, 0, '2025-01-16 08:45:00', 'review');
INSERT INTO community_posts (post_id, user_id, content, images, product_tag, like_count, comment_count, is_hidden, created_at, post_type) VALUES (8, 8, 'Kẹp tóc Unicorn dễ thương, nhưng hơi yếu.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781532226/pos2_z8pge2.png', 8, 6, 2, 0, '2025-01-17 17:20:00', 'review');
INSERT INTO community_posts (post_id, user_id, content, images, product_tag, like_count, comment_count, is_hidden, created_at, post_type) VALUES (9, 9, 'Bộ cọ Heart mềm, không rụng lông.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781532228/pos3_lllu1n.png', 18, 10, 1, 0, '2025-01-18 10:00:00', 'review');
INSERT INTO community_posts (post_id, user_id, content, images, product_tag, like_count, comment_count, is_hidden, created_at, post_type) VALUES (10, 10, 'Mình thử AI Makeup Artist, kết quả shock luôn, hợp lắm.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781532236/pos4_a1ojqr.png', NULL, 45, 12, 0, '2025-01-19 22:10:00', 'review');
INSERT INTO community_posts (post_id, user_id, content, images, product_tag, like_count, comment_count, is_hidden, created_at, post_type) VALUES (11, 1, 'Son Cherry Lip Tint lâu trôi nhưng hơi khó tán.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781532230/pos5_rje33p.png', 15, 14, 3, 0, '2025-01-20 14:00:00', 'review');
INSERT INTO community_posts (post_id, user_id, content, images, product_tag, like_count, comment_count, is_hidden, created_at, post_type) VALUES (12, 2, 'Bảng Galaxy Eyeshadow nhũ đẹp mê hồn.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781532231/pos6_kmabag.png', 22, 28, 6, 0, '2025-01-21 09:30:00', 'review');
INSERT INTO community_posts (post_id, user_id, content, images, product_tag, like_count, comment_count, is_hidden, created_at, post_type) VALUES (13, 3, 'Hướng dẫn makeup bằng PomPom set đây! Ai cần không?', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781529924/pos1_zvo164.png', 25, 52, 14, 0, '2025-01-22 16:00:00', 'review');
INSERT INTO community_posts (post_id, user_id, content, images, product_tag, like_count, comment_count, is_hidden, created_at, post_type) VALUES (14, 4, 'Xịt khóa lớp Angel Setting giữ được 8 tiếng.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781532226/pos2_z8pge2.png', 14, 11, 2, 0, '2025-01-23 11:15:00', 'review');
INSERT INTO community_posts (post_id, user_id, content, images, product_tag, like_count, comment_count, is_hidden, created_at, post_type) VALUES (15, 5, 'Highlight Mermaid lên chân mày cũng đẹp.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781532228/pos3_lllu1n.png', 16, 9, 1, 0, '2025-01-24 19:40:00', 'review');
INSERT INTO community_posts (post_id, user_id, content, images, product_tag, like_count, comment_count, is_hidden, created_at, post_type) VALUES (16, 6, 'Phấn phủ Peach Blur kiềm dầu tốt, da lì nhẹ.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781532236/pos4_a1ojqr.png', 17, 17, 3, 0, '2025-01-25 13:00:00', 'review');
INSERT INTO community_posts (post_id, user_id, content, images, product_tag, like_count, comment_count, is_hidden, created_at, post_type) VALUES (17, 7, 'Son lì dạng nước Matte Liquid màu đỏ cam rất tôn da.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781532230/pos5_rje33p.png', 12, 20, 4, 0, '2025-01-26 15:30:00', 'review');
INSERT INTO community_posts (post_id, user_id, content, images, product_tag, like_count, comment_count, is_hidden, created_at, post_type) VALUES (18, 8, 'Gương Cute Mirror siêu nhỏ gọn, bỏ túi được.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781532231/pos6_kmabag.png', 20, 7, 1, 0, '2025-01-27 10:00:00', 'review');
INSERT INTO community_posts (post_id, user_id, content, images, product_tag, like_count, comment_count, is_hidden, created_at, post_type) VALUES (19, 9, 'Nước hoa Vanilla thơm ngọt, lưu hương 6 tiếng.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781529924/pos1_zvo164.png', 19, 13, 2, 0, '2025-01-28 21:15:00', 'review');
INSERT INTO community_posts (post_id, user_id, content, images, product_tag, like_count, comment_count, is_hidden, created_at, post_type) VALUES (20, 10, 'Review từ A-Z set Ultimate Makeup, xứng đáng với giá sale.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781532226/pos2_z8pge2.png', 25, 38, 9, 0, '2025-01-29 08:00:00', 'review');

-- Table: follows
DROP TABLE IF EXISTS follows;
CREATE TABLE IF NOT EXISTS follows (
    follower_id INTEGER NOT NULL,
    followed_id INTEGER NOT NULL,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (follower_id, followed_id),
    FOREIGN KEY (follower_id) REFERENCES users(user_id),
    FOREIGN KEY (followed_id) REFERENCES users(user_id)
);
INSERT INTO follows (follower_id, followed_id, created_at) VALUES (1, 2, '2025-01-12 08:00:00');
INSERT INTO follows (follower_id, followed_id, created_at) VALUES (2, 1, '2025-01-12 09:00:00');
INSERT INTO follows (follower_id, followed_id, created_at) VALUES (3, 6, '2025-01-13 10:00:00');
INSERT INTO follows (follower_id, followed_id, created_at) VALUES (4, 6, '2025-01-14 11:00:00');
INSERT INTO follows (follower_id, followed_id, created_at) VALUES (5, 10, '2025-01-15 12:00:00');
INSERT INTO follows (follower_id, followed_id, created_at) VALUES (6, 1, '2025-01-16 13:00:00');
INSERT INTO follows (follower_id, followed_id, created_at) VALUES (7, 10, '2025-01-17 14:00:00');
INSERT INTO follows (follower_id, followed_id, created_at) VALUES (8, 9, '2025-01-18 15:00:00');
INSERT INTO follows (follower_id, followed_id, created_at) VALUES (9, 10, '2025-01-19 16:00:00');
INSERT INTO follows (follower_id, followed_id, created_at) VALUES (10, 1, '2025-01-20 17:00:00');

-- Table: guest_sessions
DROP TABLE IF EXISTS guest_sessions;
CREATE TABLE IF NOT EXISTS guest_sessions (
    session_id TEXT PRIMARY KEY,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    last_activity TEXT DEFAULT CURRENT_TIMESTAMP
);
INSERT INTO guest_sessions (session_id, created_at, last_activity) VALUES ('guest_001', '2025-01-15 08:00:00', '2025-01-15 09:30:00');
INSERT INTO guest_sessions (session_id, created_at, last_activity) VALUES ('guest_002', '2025-01-16 14:00:00', '2025-01-16 14:45:00');
INSERT INTO guest_sessions (session_id, created_at, last_activity) VALUES ('guest_003', '2025-01-20 19:00:00', '2025-01-20 20:15:00');
INSERT INTO guest_sessions (session_id, created_at, last_activity) VALUES ('guest_004', '2025-01-22 11:00:00', '2025-01-22 11:30:00');
INSERT INTO guest_sessions (session_id, created_at, last_activity) VALUES ('guest_005', '2025-01-25 22:00:00', '2025-01-25 22:20:00');

-- Table: likes
DROP TABLE IF EXISTS likes;
CREATE TABLE IF NOT EXISTS likes (
    like_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    target_type TEXT NOT NULL,
    target_id INTEGER NOT NULL,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, target_type, target_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);
INSERT INTO likes (like_id, user_id, target_type, target_id, created_at) VALUES (1, 2, 'post', 1, '2025-01-10 10:32:00');
INSERT INTO likes (like_id, user_id, target_type, target_id, created_at) VALUES (2, 3, 'post', 1, '2025-01-10 10:33:00');
INSERT INTO likes (like_id, user_id, target_type, target_id, created_at) VALUES (3, 4, 'post', 2, '2025-01-11 14:22:00');
INSERT INTO likes (like_id, user_id, target_type, target_id, created_at) VALUES (4, 5, 'post', 2, '2025-01-11 14:24:00');
INSERT INTO likes (like_id, user_id, target_type, target_id, created_at) VALUES (5, 6, 'post', 3, '2025-01-12 09:16:00');
INSERT INTO likes (like_id, user_id, target_type, target_id, created_at) VALUES (6, 7, 'post', 6, '2025-01-15 20:32:00');
INSERT INTO likes (like_id, user_id, target_type, target_id, created_at) VALUES (7, 8, 'post', 6, '2025-01-15 20:33:00');
INSERT INTO likes (like_id, user_id, target_type, target_id, created_at) VALUES (8, 9, 'post', 6, '2025-01-15 20:34:00');
INSERT INTO likes (like_id, user_id, target_type, target_id, created_at) VALUES (9, 1, 'post', 10, '2025-01-19 22:12:00');
INSERT INTO likes (like_id, user_id, target_type, target_id, created_at) VALUES (10, 2, 'post', 10, '2025-01-19 22:13:00');

-- Table: membership_history
DROP TABLE IF EXISTS membership_history;
CREATE TABLE IF NOT EXISTS membership_history (
    membership_id INTEGER PRIMARY KEY,
    user_id INTEGER NOT NULL,
    level TEXT NOT NULL, -- Bronze, Silver, Gold, Diamond
    points INTEGER NOT NULL,
    changed_at TEXT,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);
INSERT INTO membership_history (user_id, level, points, changed_at) VALUES (1, 'Diamond Member', 3250, '2025-01-20 10:00:00');
INSERT INTO membership_history (user_id, level, points, changed_at) VALUES (2, 'Gold Member', 1500, '2025-01-21 14:30:00');
INSERT INTO membership_history (user_id, level, points, changed_at) VALUES (3, 'Silver Member', 800, '2025-01-19 09:15:00');


-- Table: notifications
DROP TABLE IF EXISTS notifications;
CREATE TABLE IF NOT EXISTS notifications (
    notif_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    type TEXT NOT NULL,
    title TEXT,
    message TEXT,
    image_url TEXT,
    action_url TEXT,
    is_read INTEGER DEFAULT 0,
    reference_id INTEGER,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);
INSERT INTO notifications (notif_id, user_id, type, title, message, image_url, action_url, is_read, reference_id, created_at) VALUES (1, 1, 'order', 'Đơn hàng PP-ORD-001 đã giao', 'Cảm ơn bạn đã mua sắm', '', '/orders/1', 0, 1, '2025-01-12 15:05:00');
INSERT INTO notifications (notif_id, user_id, type, title, message, image_url, action_url, is_read, reference_id, created_at) VALUES (2, 2, 'order', 'Đơn hàng PP-ORD-002 đã giao', 'Đánh giá sản phẩm ngay', '', '/orders/2', 0, 2, '2025-01-14 11:10:00');
INSERT INTO notifications (notif_id, user_id, type, title, message, image_url, action_url, is_read, reference_id, created_at) VALUES (3, 1, 'comment', 'Bình luận mới', 'Trần Minh Anh đã bình luận bài viết của bạn', '', '/post/1', 0, 1, '2025-01-11 14:30:00');
INSERT INTO notifications (notif_id, user_id, type, title, message, image_url, action_url, is_read, reference_id, created_at) VALUES (4, 2, 'like', 'Ai đó đã thích bài viết', 'Lê Bảo Ngọc thích bài viết của bạn', '', '/post/2', 0, 2, '2025-01-13 08:00:00');
INSERT INTO notifications (notif_id, user_id, type, title, message, image_url, action_url, is_read, reference_id, created_at) VALUES (5, 3, 'follow', 'Bạn có follower mới', 'Nguyễn Thảo Nguyên đã theo dõi bạn', '', '/profile/1', 0, NULL, '2025-01-15 10:00:00');
INSERT INTO notifications (notif_id, user_id, type, title, message, image_url, action_url, is_read, reference_id, created_at) VALUES (6, 4, 'ai_result', 'Kết quả phân tích da', 'AI Dermatologist đã có kết quả', '', '/ai/dermatologist/1', 0, 1, '2025-01-20 09:00:00');
INSERT INTO notifications (notif_id, user_id, type, title, message, image_url, action_url, is_read, reference_id, created_at) VALUES (7, 5, 'order', 'Đơn hàng PP-ORD-006 bị hủy', 'Đơn hàng của bạn đã bị hủy', '', '/orders/6', 0, 6, '2025-01-22 19:35:00');
INSERT INTO notifications (notif_id, user_id, type, title, message, image_url, action_url, is_read, reference_id, created_at) VALUES (8, 6, 'comment', 'Bình luận mới', 'Hoàng Tuấn Anh đã trả lời bình luận của bạn', '', '/post/6', 0, 6, '2025-01-23 16:00:00');
INSERT INTO notifications (notif_id, user_id, type, title, message, image_url, action_url, is_read, reference_id, created_at) VALUES (9, 7, 'voucher', 'Nhận voucher giảm giá', 'Bạn nhận được mã SHIPFREE giảm 20k', '', '/vouchers', 0, 9, '2025-01-25 09:00:00');
INSERT INTO notifications (notif_id, user_id, type, title, message, image_url, action_url, is_read, reference_id, created_at) VALUES (10, 8, 'ai_result', 'AI Makeup Artist gợi ý', 'Hãy thử bảng Unicorn Magic', '', '/product/1', 0, 1, '2025-01-26 14:00:00');
INSERT INTO notifications (notif_id, user_id, type, title, message, image_url, action_url, is_read, reference_id, created_at) VALUES (11, 9, 'order', 'Đơn hàng PP-ORD-009 chờ thanh toán', 'Vui lòng thanh toán để xác nhận', '', '/orders/9', 0, 9, '2025-01-26 21:05:00');
INSERT INTO notifications (notif_id, user_id, type, title, message, image_url, action_url, is_read, reference_id, created_at) VALUES (12, 10, 'promotion', 'Flash sale 50%', 'Chỉ còn 2 giờ, săn ngay!', '', '/flash-sale', 0, NULL, '2025-01-27 10:00:00');
INSERT INTO notifications (notif_id, user_id, type, title, message, image_url, action_url, is_read, reference_id, created_at) VALUES (13, 1, 'community', 'Bài viết của bạn được ghim', 'Bài viết của bạn được chọn làm nổi bật', '', '/post/1', 0, 1, '2025-01-28 11:00:00');
INSERT INTO notifications (notif_id, user_id, type, title, message, image_url, action_url, is_read, reference_id, created_at) VALUES (14, 2, 'ai', 'Chatbot PomPom có thông báo', 'Bạn muốn tư vấn sản phẩm hôm nay?', '', '/chatbot', 0, NULL, '2025-01-28 15:00:00');
INSERT INTO notifications (notif_id, user_id, type, title, message, image_url, action_url, is_read, reference_id, created_at) VALUES (15, 3, 'system', 'Cập nhật tính năng mới', 'AI Client Advisor đã có mặt', '', '/ai/client-advisor', 0, NULL, '2025-01-29 09:00:00');
INSERT INTO notifications (notif_id, user_id, type, title, message, image_url, action_url, is_read, reference_id, created_at) VALUES (16, 4, 'review', 'Đánh giá của bạn được duyệt', 'Cảm ơn bạn đã đánh giá sản phẩm', '', '/products/7', 0, 7, '2025-01-30 10:00:00');
INSERT INTO notifications (notif_id, user_id, type, title, message, image_url, action_url, is_read, reference_id, created_at) VALUES (17, 5, 'order', 'Đơn hàng PP-ORD-008 đã thanh toán', 'Đơn hàng đang được xử lý', '', '/orders/8', 0, 8, '2025-01-25 13:10:00');
INSERT INTO notifications (notif_id, user_id, type, title, message, image_url, action_url, is_read, reference_id, created_at) VALUES (18, 6, 'follow', 'Bạn có follower mới', 'Đặng Hoàng Nam đã theo dõi bạn', '', '/profile/7', 0, NULL, '2025-01-26 08:00:00');
INSERT INTO notifications (notif_id, user_id, type, title, message, image_url, action_url, is_read, reference_id, created_at) VALUES (19, 7, 'voucher', 'Mã FLASH10 sắp hết hạn', 'Dùng ngay kẻo lỡ!', '', '/vouchers/5', 0, 5, '2025-01-19 18:00:00');
INSERT INTO notifications (notif_id, user_id, type, title, message, image_url, action_url, is_read, reference_id, created_at) VALUES (20, 8, 'ai', 'AI Makeup Artist có ưu đãi', 'Thử đồ ảo miễn phí hôm nay', '', '/ai/makeup-artist', 0, NULL, '2025-01-30 08:00:00');

-- Table: order_items
DROP TABLE IF EXISTS order_items;
CREATE TABLE IF NOT EXISTS order_items (
    order_item_id INTEGER PRIMARY KEY AUTOINCREMENT,
    order_id INTEGER NOT NULL,
    product_id INTEGER NOT NULL,
    variant_id INTEGER,
    quantity INTEGER NOT NULL,
    price REAL NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id),
    FOREIGN KEY (variant_id) REFERENCES product_variants(variant_id)
);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (1, 1, 1, 8, 1, 1125000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (2, 2, 2, 11, 1, 375000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (3, 3, 3, 1, 1, 625000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (4, 4, 4, 4, 1, 300000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (5, 5, 5, 13, 1, 200000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (6, 6, 6, 15, 1, 250000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (7, 7, 7, 18, 1, 450000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (8, 8, 8, 20, 1, 150000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (9, 9, 9, 22, 1, 225000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (10, 10, 10, 25, 1, 875000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (11, 11, 11, 27, 1, 700000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (12, 12, 12, 6, 1, 350000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (13, 13, 13, 29, 1, 175000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (14, 14, 14, 32, 1, 300000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (15, 15, 15, 34, 1, 225000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (16, 16, 16, 36, 1, 550000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (17, 17, 17, 39, 1, 400000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (18, 18, 18, 41, 1, 500000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (19, 19, 19, 43, 1, 750000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (20, 20, 20, 46, 1, 200000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (21, 21, 21, 48, 1, 275000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (22, 22, 22, 50, 1, 800000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (23, 23, 23, 53, 1, 200000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (24, 24, 24, 55, 1, 100000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (25, 25, 25, 57, 1, 1625000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (26, 26, 1, 8, 1, 1125000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (27, 27, 2, 11, 1, 375000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (28, 28, 3, 1, 1, 625000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (29, 29, 4, 4, 1, 300000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (30, 30, 5, 13, 1, 200000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (31, 31, 6, 15, 1, 250000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (32, 32, 7, 18, 1, 450000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (33, 33, 8, 20, 1, 150000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (34, 34, 9, 22, 1, 225000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (35, 35, 10, 25, 1, 875000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (36, 36, 11, 27, 1, 700000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (37, 37, 12, 6, 1, 350000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (38, 38, 13, 29, 1, 175000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (39, 39, 14, 32, 1, 300000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (40, 40, 15, 34, 1, 225000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (41, 41, 16, 36, 1, 550000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (42, 42, 17, 39, 1, 400000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (43, 43, 18, 41, 1, 500000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (44, 44, 19, 43, 1, 750000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (45, 45, 20, 46, 1, 200000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (46, 46, 21, 48, 1, 275000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (47, 47, 22, 50, 1, 800000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (48, 48, 23, 53, 1, 200000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (49, 49, 24, 55, 1, 100000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (50, 50, 25, 57, 1, 1625000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (51, 51, 1, 8, 1, 1125000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (52, 52, 2, 11, 1, 375000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (53, 53, 3, 1, 1, 625000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (54, 54, 4, 4, 1, 300000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (55, 55, 5, 13, 1, 200000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (56, 56, 6, 15, 1, 250000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (57, 57, 7, 18, 1, 450000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (58, 58, 8, 20, 1, 150000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (59, 59, 9, 22, 1, 225000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (60, 60, 10, 25, 1, 875000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (61, 61, 11, 27, 1, 700000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (62, 62, 12, 6, 1, 350000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (63, 63, 13, 29, 1, 175000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (64, 64, 14, 32, 1, 300000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (65, 65, 15, 34, 1, 225000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (66, 66, 16, 36, 1, 550000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (67, 67, 17, 39, 1, 400000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (68, 68, 18, 41, 1, 500000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (69, 69, 19, 43, 1, 750000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (70, 70, 20, 46, 1, 200000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (71, 71, 21, 48, 1, 275000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (72, 72, 22, 50, 1, 800000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (73, 73, 23, 53, 1, 200000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (74, 74, 24, 55, 1, 100000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (75, 75, 25, 57, 1, 1625000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (76, 76, 1, 8, 1, 1125000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (77, 77, 2, 11, 1, 375000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (78, 78, 3, 1, 1, 625000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (79, 79, 4, 4, 1, 300000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (80, 80, 5, 13, 1, 200000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (81, 81, 6, 15, 1, 250000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (82, 82, 7, 18, 1, 450000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (83, 83, 8, 20, 1, 150000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (84, 84, 9, 22, 1, 225000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (85, 85, 10, 25, 1, 875000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (86, 86, 11, 27, 1, 700000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (87, 87, 12, 6, 1, 350000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (88, 88, 13, 29, 1, 175000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (89, 89, 14, 32, 1, 300000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (90, 90, 15, 34, 1, 225000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (91, 91, 16, 36, 1, 550000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (92, 92, 17, 39, 1, 400000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (93, 93, 18, 41, 1, 500000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (94, 94, 19, 43, 1, 750000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (95, 95, 20, 46, 1, 200000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (96, 96, 21, 48, 1, 275000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (97, 97, 22, 50, 1, 800000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (98, 98, 23, 53, 1, 200000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (99, 99, 24, 55, 1, 100000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (100, 100, 25, 57, 1, 1625000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (101, 101, 1, 8, 1, 1125000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (102, 102, 2, 11, 1, 375000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (103, 103, 3, 1, 1, 625000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (104, 104, 4, 4, 1, 300000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (105, 105, 5, 13, 1, 200000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (106, 106, 6, 15, 1, 250000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (107, 107, 7, 18, 1, 450000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (108, 108, 8, 20, 1, 150000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (109, 109, 9, 22, 1, 225000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (110, 110, 10, 25, 1, 875000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (111, 111, 11, 27, 1, 700000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (112, 112, 12, 6, 1, 350000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (113, 113, 13, 29, 1, 175000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (114, 114, 14, 32, 1, 300000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (115, 115, 15, 34, 1, 225000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (116, 116, 16, 36, 1, 550000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (117, 117, 17, 39, 1, 400000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (118, 118, 18, 41, 1, 500000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (119, 119, 19, 43, 1, 750000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (120, 120, 20, 46, 1, 200000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (121, 121, 21, 48, 1, 275000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (122, 122, 22, 50, 1, 800000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (123, 123, 23, 53, 1, 200000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (124, 124, 24, 55, 1, 100000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (125, 125, 25, 57, 1, 1625000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (126, 126, 1, 8, 1, 1125000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (127, 127, 2, 11, 1, 375000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (128, 128, 3, 1, 1, 625000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (129, 129, 4, 4, 1, 300000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (130, 130, 5, 13, 1, 200000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (131, 131, 6, 15, 1, 250000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (132, 132, 7, 18, 1, 450000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (133, 133, 8, 20, 1, 150000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (134, 134, 9, 22, 1, 225000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (135, 135, 10, 25, 1, 875000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (136, 136, 11, 27, 1, 700000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (137, 137, 12, 6, 1, 350000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (138, 138, 13, 29, 1, 175000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (139, 139, 14, 32, 1, 300000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (140, 140, 15, 34, 1, 225000.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (141, 141, 16, 36, 1, 550000.0);

-- Table: order_status_history
DROP TABLE IF EXISTS order_status_history;
CREATE TABLE IF NOT EXISTS order_status_history (
    history_id INTEGER PRIMARY KEY AUTOINCREMENT,
    order_id INTEGER NOT NULL,
    status TEXT NOT NULL,
    note TEXT,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE
);
INSERT INTO order_status_history (history_id, order_id, status, note, created_at) VALUES (1, 1, 'pending', 'Tạo đơn', '2025-01-10 10:00:00');
INSERT INTO order_status_history (history_id, order_id, status, note, created_at) VALUES (2, 1, 'paid', 'COD thành công', '2025-01-10 10:05:00');
INSERT INTO order_status_history (history_id, order_id, status, note, created_at) VALUES (3, 1, 'preparing', 'Đang đóng gói', '2025-01-10 14:00:00');
INSERT INTO order_status_history (history_id, order_id, status, note, created_at) VALUES (4, 1, 'shipping', 'Giao vận chuyển', '2025-01-11 08:00:00');
INSERT INTO order_status_history (history_id, order_id, status, note, created_at) VALUES (5, 1, 'delivered', 'Giao thành công', '2025-01-12 15:00:00');
INSERT INTO order_status_history (history_id, order_id, status, note, created_at) VALUES (6, 2, 'pending', 'Tạo đơn', '2025-01-11 14:30:00');
INSERT INTO order_status_history (history_id, order_id, status, note, created_at) VALUES (7, 2, 'paid', 'VISA', '2025-01-11 14:32:00');
INSERT INTO order_status_history (history_id, order_id, status, note, created_at) VALUES (8, 2, 'delivered', 'Giao thành công', '2025-01-14 11:00:00');
INSERT INTO order_status_history (history_id, order_id, status, note, created_at) VALUES (9, 3, 'pending', 'Guest đặt', '2025-01-15 09:00:00');
INSERT INTO order_status_history (history_id, order_id, status, note, created_at) VALUES (10, 3, 'paid', 'VNPAY', '2025-01-15 09:02:00');

-- Table: orders
DROP TABLE IF EXISTS orders;
CREATE TABLE IF NOT EXISTS orders (
    order_id INTEGER PRIMARY KEY AUTOINCREMENT,
    order_number TEXT UNIQUE,
    user_id INTEGER,
    session_id TEXT,
    address_id INTEGER,
    total_amount REAL,
    shipping_fee REAL DEFAULT 0,
    discount_amount REAL DEFAULT 0,
    final_amount REAL,
    status TEXT DEFAULT 'pending',
    payment_method TEXT,
    payment_status TEXT DEFAULT 'unpaid',
    shipping_carrier TEXT,
    tracking_number TEXT,
    note TEXT,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (session_id) REFERENCES guest_sessions(session_id),
    FOREIGN KEY (address_id) REFERENCES user_addresses(address_id)
);
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (1, 'PP-ORD-001', 1, NULL, 1, 1125000.0, 380000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-10 10:00:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (2, 'PP-ORD-002', 2, NULL, 3, 375000.0, 355000.0, 205000.0, 205000.0, 'delivered', 'VISA', 'paid', 'GHN', 'GH0002', NULL, '2025-01-11 14:30:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (3, 'PP-ORD-003', NULL, 'guest_001', NULL, 625000.0, 180000.0, 205000.0, 205000.0, 'paid', 'VNPAY', 'paid', 'Viettel', 'VT001', 'Quà tặng', '2025-01-15 09:00:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (4, 'PP-ORD-004', 3, NULL, 5, 300000.0, 305000.0, 205000.0, 205000.0, 'shipping', 'COD', 'paid', 'GHTK', 'GH0004', NULL, '2025-01-18 16:00:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (5, 'PP-ORD-005', 4, NULL, 6, 200000.0, 230000.0, 205000.0, 205000.0, 'delivered', 'VISA', 'paid', 'GHN', 'GH0005', 'Để trước cửa', '2025-01-20 11:00:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (6, 'PP-ORD-006', 5, NULL, 7, 250000.0, 555000.0, 205000.0, 205000.0, 'cancelled', 'MOMO', 'failed', NULL, NULL, 'Khách hủy', '2025-01-22 19:00:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (7, 'PP-ORD-007', 6, NULL, 8, 450000.0, 405000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHN', 'GH0007', NULL, '2025-01-24 08:30:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (8, 'PP-ORD-008', 7, NULL, 9, 150000.0, 505000.0, 205000.0, 205000.0, 'paid', 'VISA', 'paid', 'Viettel', 'VT008', NULL, '2025-01-25 13:00:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (9, 'PP-ORD-009', 8, NULL, 10, 225000.0, 755000.0, 205000.0, 205000.0, 'pending', 'COD', 'unpaid', NULL, NULL, NULL, '2025-01-26 21:00:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (10, 'PP-ORD-010', 10, NULL, NULL, 875000.0, 705000.0, 205000.0, 205000.0, 'pending', 'VNPAY', 'unpaid', NULL, NULL, 'Thử đơn', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (11, 'PP-ORD-011', 1, NULL, 1, 700000.0, 705000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (12, 'PP-ORD-012', 2, NULL, 2, 350000.0, 355000.0, 205000.0, 205000.0, 'delivered', 'VISA', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (13, 'PP-ORD-013', 3, NULL, 3, 175000.0, 180000.0, 205000.0, 205000.0, 'delivered', 'VNPAY', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (14, 'PP-ORD-014', 4, NULL, 4, 300000.0, 305000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (15, 'PP-ORD-015', 5, NULL, 5, 225000.0, 230000.0, 205000.0, 205000.0, 'delivered', 'VISA', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (16, 'PP-ORD-016', 6, NULL, 6, 550000.0, 555000.0, 205000.0, 205000.0, 'delivered', 'MOMO', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (17, 'PP-ORD-017', 7, NULL, 7, 400000.0, 405000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (18, 'PP-ORD-018', 8, NULL, 8, 500000.0, 505000.0, 205000.0, 205000.0, 'delivered', 'VISA', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (19, 'PP-ORD-019', 9, NULL, 9, 750000.0, 755000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (20, 'PP-ORD-020', 10, NULL, 10, 200000.0, 205000.0, 205000.0, 205000.0, 'delivered', 'VNPAY', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (21, 'PP-ORD-021', 1, NULL, 1, 275000.0, 280000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (22, 'PP-ORD-022', 2, NULL, 2, 800000.0, 805000.0, 205000.0, 205000.0, 'delivered', 'VISA', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (23, 'PP-ORD-023', 3, NULL, 3, 200000.0, 205000.0, 205000.0, 205000.0, 'delivered', 'VNPAY', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (24, 'PP-ORD-024', 4, NULL, 4, 100000.0, 105000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (25, 'PP-ORD-025', 5, NULL, 5, 1625000.0, 1630000.0, 205000.0, 205000.0, 'delivered', 'VISA', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (26, 'PP-ORD-026', 6, NULL, 6, 1125000.0, 1130000.0, 205000.0, 205000.0, 'delivered', 'MOMO', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (27, 'PP-ORD-027', 7, NULL, 7, 375000.0, 380000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (28, 'PP-ORD-028', 8, NULL, 8, 625000.0, 630000.0, 205000.0, 205000.0, 'delivered', 'VISA', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (29, 'PP-ORD-029', 9, NULL, 9, 300000.0, 305000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (30, 'PP-ORD-030', 10, NULL, 10, 200000.0, 205000.0, 205000.0, 205000.0, 'delivered', 'VNPAY', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (31, 'PP-ORD-031', 1, NULL, 1, 250000.0, 255000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (32, 'PP-ORD-032', 2, NULL, 2, 450000.0, 455000.0, 205000.0, 205000.0, 'delivered', 'VISA', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (33, 'PP-ORD-033', 3, NULL, 3, 150000.0, 155000.0, 205000.0, 205000.0, 'delivered', 'VNPAY', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (34, 'PP-ORD-034', 4, NULL, 4, 225000.0, 230000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (35, 'PP-ORD-035', 5, NULL, 5, 875000.0, 880000.0, 205000.0, 205000.0, 'delivered', 'VISA', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (36, 'PP-ORD-036', 6, NULL, 6, 700000.0, 705000.0, 205000.0, 205000.0, 'delivered', 'MOMO', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (37, 'PP-ORD-037', 7, NULL, 7, 350000.0, 355000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (38, 'PP-ORD-038', 8, NULL, 8, 175000.0, 180000.0, 205000.0, 205000.0, 'delivered', 'VISA', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (39, 'PP-ORD-039', 9, NULL, 9, 300000.0, 305000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (40, 'PP-ORD-040', 10, NULL, 10, 225000.0, 230000.0, 205000.0, 205000.0, 'delivered', 'VNPAY', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (41, 'PP-ORD-041', 1, NULL, 1, 550000.0, 555000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (42, 'PP-ORD-042', 2, NULL, 2, 400000.0, 405000.0, 205000.0, 205000.0, 'delivered', 'VISA', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (43, 'PP-ORD-043', 3, NULL, 3, 500000.0, 505000.0, 205000.0, 205000.0, 'delivered', 'VNPAY', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (44, 'PP-ORD-044', 4, NULL, 4, 750000.0, 755000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (45, 'PP-ORD-045', 5, NULL, 5, 200000.0, 205000.0, 205000.0, 205000.0, 'delivered', 'VISA', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (46, 'PP-ORD-046', 6, NULL, 6, 275000.0, 280000.0, 205000.0, 205000.0, 'delivered', 'MOMO', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (47, 'PP-ORD-047', 7, NULL, 7, 800000.0, 805000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (48, 'PP-ORD-048', 8, NULL, 8, 200000.0, 205000.0, 205000.0, 205000.0, 'delivered', 'VISA', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (49, 'PP-ORD-049', 9, NULL, 9, 100000.0, 105000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (50, 'PP-ORD-050', 10, NULL, 10, 1625000.0, 1630000.0, 205000.0, 205000.0, 'delivered', 'VNPAY', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (51, 'PP-ORD-051', 1, NULL, 1, 1125000.0, 1130000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (52, 'PP-ORD-052', 2, NULL, 2, 375000.0, 380000.0, 205000.0, 205000.0, 'delivered', 'VISA', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (53, 'PP-ORD-053', 3, NULL, 3, 625000.0, 630000.0, 205000.0, 205000.0, 'delivered', 'VNPAY', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (54, 'PP-ORD-054', 4, NULL, 4, 300000.0, 305000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (55, 'PP-ORD-055', 5, NULL, 5, 200000.0, 205000.0, 205000.0, 205000.0, 'delivered', 'VISA', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (56, 'PP-ORD-056', 6, NULL, 6, 250000.0, 255000.0, 205000.0, 205000.0, 'delivered', 'MOMO', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (57, 'PP-ORD-057', 7, NULL, 7, 450000.0, 455000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (58, 'PP-ORD-058', 8, NULL, 8, 150000.0, 155000.0, 205000.0, 205000.0, 'delivered', 'VISA', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (59, 'PP-ORD-059', 9, NULL, 9, 225000.0, 230000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (60, 'PP-ORD-060', 10, NULL, 10, 875000.0, 880000.0, 205000.0, 205000.0, 'delivered', 'VNPAY', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (61, 'PP-ORD-061', 1, NULL, 1, 700000.0, 705000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (62, 'PP-ORD-062', 2, NULL, 2, 350000.0, 355000.0, 205000.0, 205000.0, 'delivered', 'VISA', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (63, 'PP-ORD-063', 3, NULL, 3, 175000.0, 180000.0, 205000.0, 205000.0, 'delivered', 'VNPAY', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (64, 'PP-ORD-064', 4, NULL, 4, 300000.0, 305000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (65, 'PP-ORD-065', 5, NULL, 5, 225000.0, 230000.0, 205000.0, 205000.0, 'delivered', 'VISA', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (66, 'PP-ORD-066', 6, NULL, 6, 550000.0, 555000.0, 205000.0, 205000.0, 'delivered', 'MOMO', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (67, 'PP-ORD-067', 7, NULL, 7, 400000.0, 405000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (68, 'PP-ORD-068', 8, NULL, 8, 500000.0, 505000.0, 205000.0, 205000.0, 'delivered', 'VISA', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (69, 'PP-ORD-069', 9, NULL, 9, 750000.0, 755000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (70, 'PP-ORD-070', 10, NULL, 10, 200000.0, 205000.0, 205000.0, 205000.0, 'delivered', 'VNPAY', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (71, 'PP-ORD-071', 1, NULL, 1, 275000.0, 280000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (72, 'PP-ORD-072', 2, NULL, 2, 800000.0, 805000.0, 205000.0, 205000.0, 'delivered', 'VISA', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (73, 'PP-ORD-073', 3, NULL, 3, 200000.0, 205000.0, 205000.0, 205000.0, 'delivered', 'VNPAY', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (74, 'PP-ORD-074', 4, NULL, 4, 100000.0, 105000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (75, 'PP-ORD-075', 5, NULL, 5, 1625000.0, 1630000.0, 205000.0, 205000.0, 'delivered', 'VISA', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (76, 'PP-ORD-076', 6, NULL, 6, 1125000.0, 1130000.0, 205000.0, 205000.0, 'delivered', 'MOMO', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (77, 'PP-ORD-077', 7, NULL, 7, 375000.0, 380000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (78, 'PP-ORD-078', 8, NULL, 8, 625000.0, 630000.0, 205000.0, 205000.0, 'delivered', 'VISA', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (79, 'PP-ORD-079', 9, NULL, 9, 300000.0, 305000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (80, 'PP-ORD-080', 10, NULL, 10, 200000.0, 205000.0, 205000.0, 205000.0, 'delivered', 'VNPAY', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (81, 'PP-ORD-081', 1, NULL, 1, 250000.0, 255000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (82, 'PP-ORD-082', 2, NULL, 2, 450000.0, 455000.0, 205000.0, 205000.0, 'delivered', 'VISA', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (83, 'PP-ORD-083', 3, NULL, 3, 150000.0, 155000.0, 205000.0, 205000.0, 'delivered', 'VNPAY', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (84, 'PP-ORD-084', 4, NULL, 4, 225000.0, 230000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (85, 'PP-ORD-085', 5, NULL, 5, 875000.0, 880000.0, 205000.0, 205000.0, 'delivered', 'VISA', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (86, 'PP-ORD-086', 6, NULL, 6, 700000.0, 705000.0, 205000.0, 205000.0, 'delivered', 'MOMO', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (87, 'PP-ORD-087', 7, NULL, 7, 350000.0, 355000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (88, 'PP-ORD-088', 8, NULL, 8, 175000.0, 180000.0, 205000.0, 205000.0, 'delivered', 'VISA', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (89, 'PP-ORD-089', 9, NULL, 9, 300000.0, 305000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (90, 'PP-ORD-090', 10, NULL, 10, 225000.0, 230000.0, 205000.0, 205000.0, 'delivered', 'VNPAY', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (91, 'PP-ORD-091', 1, NULL, 1, 550000.0, 555000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (92, 'PP-ORD-092', 2, NULL, 2, 400000.0, 405000.0, 205000.0, 205000.0, 'delivered', 'VISA', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (93, 'PP-ORD-093', 3, NULL, 3, 500000.0, 505000.0, 205000.0, 205000.0, 'delivered', 'VNPAY', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (94, 'PP-ORD-094', 4, NULL, 4, 750000.0, 755000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (95, 'PP-ORD-095', 5, NULL, 5, 200000.0, 205000.0, 205000.0, 205000.0, 'delivered', 'VISA', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (96, 'PP-ORD-096', 6, NULL, 6, 275000.0, 280000.0, 205000.0, 205000.0, 'delivered', 'MOMO', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (97, 'PP-ORD-097', 7, NULL, 7, 800000.0, 805000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (98, 'PP-ORD-098', 8, NULL, 8, 200000.0, 205000.0, 205000.0, 205000.0, 'delivered', 'VISA', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (99, 'PP-ORD-099', 9, NULL, 9, 100000.0, 105000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (100, 'PP-ORD-100', 10, NULL, 10, 1625000.0, 1630000.0, 205000.0, 205000.0, 'delivered', 'VNPAY', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (101, 'PP-ORD-101', 1, NULL, 1, 1125000.0, 1130000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (102, 'PP-ORD-102', 2, NULL, 2, 375000.0, 380000.0, 205000.0, 205000.0, 'delivered', 'VISA', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (103, 'PP-ORD-103', 3, NULL, 3, 625000.0, 630000.0, 205000.0, 205000.0, 'delivered', 'VNPAY', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (104, 'PP-ORD-104', 4, NULL, 4, 300000.0, 305000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (105, 'PP-ORD-105', 5, NULL, 5, 200000.0, 205000.0, 205000.0, 205000.0, 'delivered', 'VISA', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (106, 'PP-ORD-106', 6, NULL, 6, 250000.0, 255000.0, 205000.0, 205000.0, 'delivered', 'MOMO', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (107, 'PP-ORD-107', 7, NULL, 7, 450000.0, 455000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (108, 'PP-ORD-108', 8, NULL, 8, 150000.0, 155000.0, 205000.0, 205000.0, 'delivered', 'VISA', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (109, 'PP-ORD-109', 9, NULL, 9, 225000.0, 230000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (110, 'PP-ORD-110', 10, NULL, 10, 875000.0, 880000.0, 205000.0, 205000.0, 'delivered', 'VNPAY', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (111, 'PP-ORD-111', 1, NULL, 1, 700000.0, 705000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (112, 'PP-ORD-112', 2, NULL, 2, 350000.0, 355000.0, 205000.0, 205000.0, 'delivered', 'VISA', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (113, 'PP-ORD-113', 3, NULL, 3, 175000.0, 180000.0, 205000.0, 205000.0, 'delivered', 'VNPAY', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (114, 'PP-ORD-114', 4, NULL, 4, 300000.0, 305000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (115, 'PP-ORD-115', 5, NULL, 5, 225000.0, 230000.0, 205000.0, 205000.0, 'delivered', 'VISA', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (116, 'PP-ORD-116', 6, NULL, 6, 550000.0, 555000.0, 205000.0, 205000.0, 'delivered', 'MOMO', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (117, 'PP-ORD-117', 7, NULL, 7, 400000.0, 405000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (118, 'PP-ORD-118', 8, NULL, 8, 500000.0, 505000.0, 205000.0, 205000.0, 'delivered', 'VISA', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (119, 'PP-ORD-119', 9, NULL, 9, 750000.0, 755000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (120, 'PP-ORD-120', 10, NULL, 10, 200000.0, 205000.0, 205000.0, 205000.0, 'delivered', 'VNPAY', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (121, 'PP-ORD-121', 1, NULL, 1, 275000.0, 280000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (122, 'PP-ORD-122', 2, NULL, 2, 800000.0, 805000.0, 205000.0, 205000.0, 'delivered', 'VISA', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (123, 'PP-ORD-123', 3, NULL, 3, 200000.0, 205000.0, 205000.0, 205000.0, 'delivered', 'VNPAY', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (124, 'PP-ORD-124', 4, NULL, 4, 100000.0, 105000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (125, 'PP-ORD-125', 5, NULL, 5, 1625000.0, 1630000.0, 205000.0, 205000.0, 'delivered', 'VISA', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (126, 'PP-ORD-126', 6, NULL, 6, 1125000.0, 1130000.0, 205000.0, 205000.0, 'delivered', 'MOMO', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (127, 'PP-ORD-127', 7, NULL, 7, 375000.0, 380000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (128, 'PP-ORD-128', 8, NULL, 8, 625000.0, 630000.0, 205000.0, 205000.0, 'delivered', 'VISA', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (129, 'PP-ORD-129', 9, NULL, 9, 300000.0, 305000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (130, 'PP-ORD-130', 10, NULL, 10, 200000.0, 205000.0, 205000.0, 205000.0, 'delivered', 'VNPAY', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (131, 'PP-ORD-131', 1, NULL, 1, 250000.0, 255000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (132, 'PP-ORD-132', 2, NULL, 2, 450000.0, 455000.0, 205000.0, 205000.0, 'delivered', 'VISA', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (133, 'PP-ORD-133', 3, NULL, 3, 150000.0, 155000.0, 205000.0, 205000.0, 'delivered', 'VNPAY', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (134, 'PP-ORD-134', 4, NULL, 4, 225000.0, 230000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (135, 'PP-ORD-135', 5, NULL, 5, 875000.0, 880000.0, 205000.0, 205000.0, 'delivered', 'VISA', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (136, 'PP-ORD-136', 6, NULL, 6, 700000.0, 705000.0, 205000.0, 205000.0, 'delivered', 'MOMO', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (137, 'PP-ORD-137', 7, NULL, 7, 350000.0, 355000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (138, 'PP-ORD-138', 8, NULL, 8, 175000.0, 180000.0, 205000.0, 205000.0, 'delivered', 'VISA', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (139, 'PP-ORD-139', 9, NULL, 9, 300000.0, 305000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (140, 'PP-ORD-140', 10, NULL, 10, 225000.0, 230000.0, 205000.0, 205000.0, 'delivered', 'VNPAY', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (141, 'PP-ORD-141', 1, NULL, 1, 550000.0, 555000.0, 205000.0, 205000.0, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-28 10:15:00');

-- Table: payments
DROP TABLE IF EXISTS payments;
CREATE TABLE IF NOT EXISTS payments (
    payment_id INTEGER PRIMARY KEY AUTOINCREMENT,
    order_id INTEGER NOT NULL,
    transaction_id TEXT,
    amount REAL,
    status TEXT DEFAULT 'pending',
    payment_method TEXT,
    paid_at TEXT,
    FOREIGN KEY (order_id) REFERENCES orders(order_id)
);
INSERT INTO payments (payment_id, order_id, transaction_id, amount, status, payment_method, paid_at) VALUES (1, 1, 'COD_TX_001', 1122500.0, 'success', 'COD', '2025-01-10 10:05:00');
INSERT INTO payments (payment_id, order_id, transaction_id, amount, status, payment_method, paid_at) VALUES (2, 2, 'VISA_TX_002', 747500.0, 'success', 'VISA', '2025-01-11 14:32:00');
INSERT INTO payments (payment_id, order_id, transaction_id, amount, status, payment_method, paid_at) VALUES (3, 3, 'VNPAY_TX_003', 425000.0, 'success', 'VNPAY', '2025-01-15 09:02:00');
INSERT INTO payments (payment_id, order_id, transaction_id, amount, status, payment_method, paid_at) VALUES (4, 4, 'COD_TX_004', 872500.0, 'success', 'COD', '2025-01-18 16:05:00');
INSERT INTO payments (payment_id, order_id, transaction_id, amount, status, payment_method, paid_at) VALUES (5, 5, 'VISA_TX_005', 375000.0, 'success', 'VISA', '2025-01-20 11:02:00');
INSERT INTO payments (payment_id, order_id, transaction_id, amount, status, payment_method, paid_at) VALUES (6, 6, 'MOMO_TX_006', 425000.0, 'failed', 'MOMO', NULL);
INSERT INTO payments (payment_id, order_id, transaction_id, amount, status, payment_method, paid_at) VALUES (7, 7, 'COD_TX_007', 1247500.0, 'success', 'COD', '2025-01-24 08:35:00');
INSERT INTO payments (payment_id, order_id, transaction_id, amount, status, payment_method, paid_at) VALUES (8, 8, 'VISA_TX_008', 300000.0, 'success', 'VISA', '2025-01-25 13:05:00');
INSERT INTO payments (payment_id, order_id, transaction_id, amount, status, payment_method, paid_at) VALUES (9, 9, NULL, 500000.0, 'pending', 'COD', NULL);
INSERT INTO payments (payment_id, order_id, transaction_id, amount, status, payment_method, paid_at) VALUES (10, 10, NULL, 325000.0, 'pending', 'VNPAY', NULL);

-- Table: points_transactions
DROP TABLE IF EXISTS points_transactions;
CREATE TABLE IF NOT EXISTS points_transactions (
    transaction_id INTEGER PRIMARY KEY,
    user_id INTEGER NOT NULL,
    points_change INTEGER NOT NULL, -- có thể âm nếu tiêu điểm
    reason TEXT, -- 'earn_from_order', 'redeem_voucher', ...
    reference_id INTEGER, -- order_id hoặc voucher_id
    created_at TEXT,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Table: product_images
DROP TABLE IF EXISTS product_images;
CREATE TABLE IF NOT EXISTS product_images (
    image_id INTEGER PRIMARY KEY AUTOINCREMENT,
    product_id INTEGER NOT NULL,
    image_url TEXT NOT NULL,
    sort_order INTEGER DEFAULT 0,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (1, 1, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344448/PomPom_Unicorn_Magic_Palette_p82y28.webp', 1);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (2, 1, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344447/PomPom_Unicorn_Magic_Palette_2_wlgaci.webp', 2);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (3, 1, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344444/PomPom_Unicorn_Magic_Palette_3_zvw9ag.jpg', 3);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (4, 1, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344437/PomPom_Unicorn_Magic_Palette_4_t10p94.webp', 4);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (5, 1, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344428/PomPom_Unicorn_Magic_Palette_5_t23qmi.webp', 5);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (6, 1, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344424/PomPom_Unicorn_Magic_Palette_6_f5id5k.webp', 6);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (7, 2, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344406/PomPom_Velvet_Rose_Lipstick_vjgkow.webp', 1);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (8, 2, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344406/PomPom_Velvet_Rose_Lipstick_2_sjwmae.webp', 2);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (9, 2, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344405/PomPom_Velvet_Rose_Lipstick_3_fqm5vm.webp', 3);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (10, 2, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344283/PomPom_Velvet_Rose_Lipstick_4_gfkusb.webp', 4);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (11, 2, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344279/PomPom_Velvet_Rose_Lipstick_5_x9bibe.webp', 5);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (12, 3, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344277/PomPom_Cloud_Cushion_kwewua.webp', 1);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (13, 3, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344275/PomPom_Cloud_Cushion_2_rjvlas.webp', 2);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (14, 3, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344274/PomPom_Cloud_Cushion_3_kclee4.webp', 3);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (15, 3, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344272/PomPom_Cloud_Cushion_4_oktuc7.webp', 4);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (16, 3, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344270/PomPom_Cloud_Cushion_5_hegax2.webp', 5);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (17, 4, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344269/PomPom_Bunny_Blush_fuyqdx.webp', 1);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (18, 4, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344266/PomPom_Bunny_Blush_2_qz8ehi.webp', 2);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (19, 4, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344265/PomPom_Bunny_Blush_3_xh89xd.webp', 3);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (20, 4, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344264/PomPom_Bunny_Blush_4_ya17i6.webp', 4);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (21, 4, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344261/PomPom_Bunny_Blush_5_tkxbdb.webp', 5);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (22, 5, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344259/PomPom_Garden_Angled_Blush_Brush_lapivi.webp', 1);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (23, 5, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344258/PomPom_Garden_Angled_Blush_Brush_2_vcgert.webp', 2);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (24, 5, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344256/PomPom_Garden_Angled_Blush_Brush_3_flytyu.webp', 3);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (25, 6, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344254/PomPom_Berry_Lip_Gloss_ovgbeh.webp', 1);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (26, 6, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344253/PomPom_Berry_Lip_Gloss_2_g0y0ec.webp', 2);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (27, 6, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344249/PomPom_Berry_Lip_Gloss_3_kx5bqz.webp', 3);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (28, 6, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344249/PomPom_Berry_Lip_Gloss_4_lugcnl.webp', 4);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (29, 6, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344248/PomPom_Berry_Lip_Gloss_5_sdund4.webp', 5);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (30, 7, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344245/PomPom_Butterfly_Highlighter_h52wfb.webp', 1);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (31, 7, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344243/PomPom_Butterfly_Highlighter_2_ygs4kt.webp', 2);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (32, 7, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344242/PomPom_Butterfly_Highlighter_3_wolhca.webp', 3);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (33, 7, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344239/PomPom_Butterfly_Highlighter_4_jllacd.webp', 4);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (34, 7, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344238/PomPom_Butterfly_Highlighter_5_rljdb3.webp', 5);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (35, 7, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344235/PomPom_Butterfly_Highlighter_6_swy3ro.webp', 6);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (36, 8, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344234/PomPom_Unicorn_Hair_Clip_ixv73h.webp', 1);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (37, 8, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344233/PomPom_Unicorn_Hair_Clip_2_sof1te.webp', 2);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (38, 9, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344230/PomPom_Makeup_Sponge_Set_a0qzcx.webp', 1);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (39, 9, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344229/PomPom_Makeup_Sponge_Set_2_dvlqig.webp', 2);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (40, 9, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344228/PomPom_Makeup_Sponge_Set_3_mvg0m4.webp', 3);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (41, 9, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344225/PomPom_Makeup_Sponge_Set_4_eterwy.webp', 4);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (42, 9, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344223/PomPom_Makeup_Sponge_Set_5_ujetq6.webp', 5);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (43, 10, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344222/PomPom_Sweet_Bear_Gift_Set_vtz4iv.webp', 1);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (44, 10, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344218/PomPom_Sweet_Bear_Gift_Set_2_hqx3jb.webp', 2);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (45, 10, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344218/PomPom_Sweet_Bear_Gift_Set_3_njcmz8.webp', 3);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (46, 10, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344213/PomPom_Sweet_Bear_Gift_Set_4_acu0p9.webp', 4);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (47, 10, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344214/PomPom_Sweet_Bear_Gift_Set_5_zgjk7v.webp', 5);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (48, 11, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344209/PomPom_Glitter_Eyeshadow_Palette_t4dk1z.webp', 1);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (49, 11, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344208/PomPom_Glitter_Eyeshadow_Palette_2_iyiqav.webp', 2);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (50, 11, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344204/PomPom_Glitter_Eyeshadow_Palette_3_jerfgx.webp', 3);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (51, 11, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344203/PomPom_Glitter_Eyeshadow_Palette_4_eqygza.jpg', 4);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (52, 11, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344202/PomPom_Glitter_Eyeshadow_Palette_5_ivsz4e.webp', 5);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (53, 12, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344199/PomPom_Matte_Liquid_Lipstick_hjktfe.webp', 1);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (54, 12, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344197/PomPom_Matte_Liquid_Lipstick_2_fbqrr5.webp', 2);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (55, 12, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344197/PomPom_Matte_Liquid_Lipstick_3_b0txid.webp', 3);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (56, 12, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344193/PomPom_Matte_Liquid_Lipstick_4_aghe2c.webp', 4);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (57, 12, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344191/PomPom_Matte_Liquid_Lipstick_5_vruriy.webp', 5);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (58, 13, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344190/PomPom_Strawberry_Cupid_Hair_Clip_omkqzm.webp', 1);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (59, 13, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344187/PomPom_Strawberry_Cupid_Hair_Clip_2_tysuwq.webp', 2);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (60, 13, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344186/PomPom_Strawberry_Cupid_Hair_Clip_3_icq1ta.webp', 3);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (61, 14, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344183/PomPom_Bunny_Garden_Cushion_Cream_Foundation_vir7xr.webp', 1);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (62, 14, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344182/PomPom_Bunny_Garden_Cushion_Cream_Foundation_2_swhxto.webp', 2);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (63, 14, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344179/PomPom_Bunny_Garden_Cushion_Cream_Foundation_3_qkbext.webp', 3);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (64, 14, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344178/PomPom_Bunny_Garden_Cushion_Cream_Foundation_4_mo1y2e.webp', 4);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (65, 14, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344175/PomPom_Bunny_Garden_Cushion_Cream_Foundation_5_cnnzhh.webp', 5);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (66, 15, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344174/PomPom_Cherry_Lip_Tint_srvvxe.webp', 1);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (67, 15, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344170/PomPom_Cherry_Lip_Tint_2_igkwvp.webp', 2);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (68, 15, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344169/PomPom_Cherry_Lip_Tint_3_l1jh7f.webp', 3);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (69, 15, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344166/PomPom_Cherry_Lip_Tint_4_ts90lr.webp', 4);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (70, 16, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344162/PomPom_Mermaid_Highlighter_cpw9h2.webp', 1);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (71, 16, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344159/PomPom_Mermaid_Highlighter_2_mvzzuy.webp', 2);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (72, 16, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344155/PomPom_Mermaid_Highlighter_3_w03xn9.webp', 3);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (73, 16, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344153/PomPom_Mermaid_Highlighter_4_kql9jv.jpg', 4);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (74, 16, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344152/PomPom_Mermaid_Highlighter_5_aemy9l.webp', 5);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (75, 17, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344150/PomPom_Peach_Blur_Powder_jds5ix.webp', 1);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (76, 17, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344149/PomPom_Peach_Blur_Powder_2_hfl6iu.webp', 2);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (77, 17, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344146/PomPom_Peach_Blur_Powder_3_o6qm6g.webp', 3);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (78, 17, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344145/PomPom_Peach_Blur_Powder_4_jk9kjf.webp', 4);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (79, 17, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344142/PomPom_Peach_Blur_Powder_5_ttkshk.webp', 5);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (80, 18, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344141/PomPom_Heart_Brush_Set_dq1u4o.webp', 1);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (81, 18, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344138/PomPom_Heart_Brush_Set_2_trbjkk.webp', 2);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (82, 18, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344137/PomPom_Heart_Brush_Set_3_ll8vm0.webp', 3);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (83, 18, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344136/PomPom_Heart_Brush_Set_4_hxw2ui.webp', 4);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (84, 18, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344133/PomPom_Heart_Brush_Set_5_oeflce.webp', 5);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (85, 18, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344132/PomPom_Heart_Brush_Set_6_hyta51.webp', 6);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (86, 19, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344130/PomPom_Midsummer_Fairytales_Liquid_Highlighter_zhbkdz.webp', 1);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (87, 19, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344129/PomPom_Midsummer_Fairytales_Liquid_Highlighter_2_hbulot.webp', 2);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (88, 19, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344126/PomPom_Midsummer_Fairytales_Liquid_Highlighter_3_awa083.webp', 3);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (89, 19, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344124/PomPom_Midsummer_Fairytales_Liquid_Highlighter_4_ywxaul.webp', 4);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (90, 19, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344124/PomPom_Midsummer_Fairytales_Liquid_Highlighter_5_xfdti0.webp', 5);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (91, 20, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344121/PomPom_Cute_Mirror_jqsdxf.webp', 1);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (92, 20, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344120/PomPom_Cute_Mirror_2_vihpl1.webp', 2);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (93, 20, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344118/PomPom_Cute_Mirror_3_vnpfet.webp', 3);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (94, 21, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344117/PomPom_Midsummer_Fairytales_Makeup_Cleansing_Balm_ggn2cc.webp', 1);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (95, 21, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344116/PomPom_Midsummer_Fairytales_Makeup_Cleansing_Balm_2_o8qe41.webp', 2);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (96, 21, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344114/PomPom_Midsummer_Fairytales_Makeup_Cleansing_Balm_3_fxupm7.webp', 3);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (97, 21, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344112/PomPom_Midsummer_Fairytales_Makeup_Cleansing_Balm_4_jknyeb.webp', 4);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (98, 21, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344111/PomPom_Midsummer_Fairytales_Makeup_Cleansing_Balm_5_uxxb6d.webp', 5);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (99, 22, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344109/PomPom_Galaxy_Eyeshadow_vfawze.webp', 1);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (100, 22, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344108/PomPom_Galaxy_Eyeshadow_2_dyf6n0.webp', 2);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (101, 22, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344107/PomPom_Galaxy_Eyeshadow_3_cbnj4o.webp', 3);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (102, 22, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344105/PomPom_Galaxy_Eyeshadow_4_lnt2ay.webp', 4);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (103, 22, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344103/PomPom_Galaxy_Eyeshadow_5_uujsw1.webp', 5);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (104, 22, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344102/PomPom_Galaxy_Eyeshadow_6_iy0vwr.webp', 6);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (105, 23, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344101/PomPom_Honey_Lip_Mask_knj4ek.webp', 1);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (106, 23, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344099/PomPom_Honey_Lip_Mask_2_bni4kn.webp', 2);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (107, 23, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344098/PomPom_Honey_Lip_Mask_3_fj22hg.webp', 3);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (108, 23, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344097/PomPom_Honey_Lip_Mask_4_fmhkhf.webp', 4);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (109, 23, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344096/PomPom_Honey_Lip_Mask_5_dyv19x.webp', 5);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (110, 24, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344095/PomPom_Fluffy_Powder_Puff_k2z4y2.webp', 1);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (111, 24, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344094/PomPom_Fluffy_Powder_Puff_2_nujd5b.webp', 2);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (112, 24, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344093/PomPom_Fluffy_Powder_Puff_3_ujx9as.webp', 3);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (113, 24, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344092/PomPom_Fluffy_Powder_Puff_4_sjvx47.webp', 4);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (114, 24, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344091/PomPom_Fluffy_Powder_Puff_5_gy6nja.webp', 5);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (115, 25, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344090/PomPom_Ultimate_Makeup_Set_mb1gw9.webp', 1);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (116, 25, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344090/PomPom_Ultimate_Makeup_Set_2_ubyybb.webp', 2);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (117, 25, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344089/PomPom_Ultimate_Makeup_Set_3_xt3tbg.webp', 3);
INSERT INTO product_images (image_id, product_id, image_url, sort_order) VALUES (118, 25, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344089/PomPom_Ultimate_Makeup_Set_4_nzh1qn.webp', 4);

-- Table: product_reviews
DROP TABLE IF EXISTS product_reviews;
CREATE TABLE IF NOT EXISTS product_reviews (
    review_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    product_id INTEGER NOT NULL,
    order_id INTEGER NOT NULL,
    rating INTEGER,
    comment TEXT,
    images TEXT,
    is_verified_purchase INTEGER DEFAULT 0,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, product_id, order_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id),
    FOREIGN KEY (order_id) REFERENCES orders(order_id)
);
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (1, 1, 1, 1, 5, 'Bảng Unicorn Magic màu đẹp, lên chuẩn', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344283/PomPom_Velvet_Rose_Lipstick_4_gfkusb.webp', 1, '2025-01-13 10:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (2, 2, 10, 2, 4, 'Set Sweet Bear dễ thương, son hơi nhạt', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344265/PomPom_Bunny_Blush_3_xh89xd.webp', 1, '2025-01-15 14:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (3, 3, 3, 4, 5, 'Cushion mịn, che phủ tốt', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344249/PomPom_Berry_Lip_Gloss_4_lugcnl.webp', 1, '2025-01-20 09:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (4, 4, 7, 5, 4, 'Highlight đẹp nhưng dễ vỡ', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344235/PomPom_Butterfly_Highlighter_6_swy3ro.webp', 1, '2025-01-22 10:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (5, 5, 18, 6, 2, 'Cọ hơi cứng, không thích lắm', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344208/PomPom_Glitter_Eyeshadow_Palette_2_iyiqav.webp', 1, '2025-01-23 11:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (6, 6, 1, 7, 5, 'Palette này mình dùng hàng ngày', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344283/PomPom_Velvet_Rose_Lipstick_4_gfkusb.webp', 1, '2025-01-26 12:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (7, 7, 22, 8, 5, 'Galaxy Eyeshadow nhũ siêu đẹp', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344265/PomPom_Bunny_Blush_3_xh89xd.webp', 1, '2025-01-27 13:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (8, 8, 12, 9, 4, 'Son lì dạng nước màu đỏ cam rất tôn da', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344249/PomPom_Berry_Lip_Gloss_4_lugcnl.webp', 1, '2025-01-28 14:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (9, 9, 15, 10, 5, 'Cherry tint lâu trôi, thích!', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344235/PomPom_Butterfly_Highlighter_6_swy3ro.webp', 1, '2025-01-29 15:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (10, 10, 2, 1, 5, 'Son Velvet Rose mềm, màu đẹp', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344208/PomPom_Glitter_Eyeshadow_Palette_2_iyiqav.webp', 1, '2025-01-30 16:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (11, 1, 4, 2, 4, 'Má hồng Bunny dễ thương nhưng hơi phai', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344283/PomPom_Velvet_Rose_Lipstick_4_gfkusb.webp', 1, '2025-01-14 17:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (12, 2, 5, 4, 5, 'Eyeliner kẻ một đường là chuẩn', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344265/PomPom_Bunny_Blush_3_xh89xd.webp', 1, '2025-01-16 18:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (13, 3, 16, 5, 5, 'Mermaid highlighter lên da căng bóng', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344249/PomPom_Berry_Lip_Gloss_4_lugcnl.webp', 1, '2025-01-21 19:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (14, 4, 14, 6, 5, 'Xịt khóa lớp giữ được cả ngày', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344235/PomPom_Butterfly_Highlighter_6_swy3ro.webp', 1, '2025-01-23 20:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (15, 5, 8, 11, 3, 'Kẹp tóc đẹp nhưng yếu', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344208/PomPom_Glitter_Eyeshadow_Palette_2_iyiqav.webp', 1, '2025-01-24 21:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (16, 11, 1, 12, 5, 'Bảng màu rất đẹp, chất phấn mịn, màu lên chuẩn ngay từ lần quẹt đầu tiên. Mình dùng cả đi học lẫn đi tiệc đều ổn.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344283/PomPom_Velvet_Rose_Lipstick_4_gfkusb.webp', 1, '2025-02-01 09:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (17, 12, 1, 13, 4, 'Packaging xinh, màu pastel đúng như hình.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344265/PomPom_Bunny_Blush_3_xh89xd.webp', 1, '2025-02-02 10:15:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (18, 13, 1, 14, 5, 'Mình đã mua lần thứ hai. Các màu nhũ bắt sáng tốt và không bị rơi phấn quá nhiều.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344249/PomPom_Berry_Lip_Gloss_4_lugcnl.webp', 1, '2025-02-03 14:30:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (19, 14, 1, 15, 5, 'Đẹp xuất sắc.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344235/PomPom_Butterfly_Highlighter_6_swy3ro.webp', 1, '2025-02-04 08:20:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (20, 15, 1, 16, 4, 'Màu sắc đa dạng, phù hợp cho người mới tập trang điểm.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344208/PomPom_Glitter_Eyeshadow_Palette_2_iyiqav.webp', 1, '2025-02-05 16:40:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (21, 16, 1, 17, 5, 'Đây là bảng mắt yêu thích nhất của mình từ trước tới nay. Phấn dễ tán, ít bị bết màu.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344283/PomPom_Velvet_Rose_Lipstick_4_gfkusb.webp', 1, '2025-02-06 11:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (22, 11, 2, 18, 5, 'Chất son mềm mượt, màu rose rất tôn da và không làm lộ vân môi.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344265/PomPom_Bunny_Blush_3_xh89xd.webp', 1, '2025-02-01 10:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (23, 12, 2, 19, 4, 'Màu đẹp nhưng mình mong son bám lâu hơn một chút.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344249/PomPom_Berry_Lip_Gloss_4_lugcnl.webp', 1, '2025-02-02 11:20:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (24, 13, 2, 20, 5, 'Mê màu này luôn.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344235/PomPom_Butterfly_Highlighter_6_swy3ro.webp', 1, '2025-02-03 13:45:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (25, 14, 2, 21, 5, 'Son lên màu chuẩn, không bị khô môi sau nhiều giờ sử dụng.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344208/PomPom_Glitter_Eyeshadow_Palette_2_iyiqav.webp', 1, '2025-02-04 15:10:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (26, 15, 2, 22, 4, 'Thiết kế sang trọng, cầm chắc tay.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344283/PomPom_Velvet_Rose_Lipstick_4_gfkusb.webp', 1, '2025-02-05 18:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (27, 16, 2, 23, 5, 'Mình mua tặng bạn và bạn rất thích. Chắc chắn sẽ thử thêm các màu khác trong bộ sưu tập.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344265/PomPom_Bunny_Blush_3_xh89xd.webp', 1, '2025-02-06 20:15:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (28, 11, 3, 24, 5, 'Cushion che phủ tốt, lớp nền tự nhiên và không bị xuống tông sau nhiều giờ.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344249/PomPom_Berry_Lip_Gloss_4_lugcnl.webp', 1, '2025-02-07 09:30:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (29, 12, 3, 25, 4, 'Phù hợp với da hỗn hợp, độ che phủ ổn.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344235/PomPom_Butterfly_Highlighter_6_swy3ro.webp', 1, '2025-02-08 10:45:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (30, 13, 3, 26, 5, 'Rất hài lòng.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344208/PomPom_Glitter_Eyeshadow_Palette_2_iyiqav.webp', 1, '2025-02-09 12:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (31, 14, 3, 27, 5, 'Mình dùng đi làm hằng ngày, cảm giác nhẹ mặt và không bí da.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344283/PomPom_Velvet_Rose_Lipstick_4_gfkusb.webp', 1, '2025-02-10 14:20:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (32, 15, 3, 28, 4, 'Hộp cushion đẹp, bông mút mềm.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344265/PomPom_Bunny_Blush_3_xh89xd.webp', 1, '2025-02-11 16:35:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (33, 16, 3, 29, 5, 'Sau khi thử nhiều loại cushion thì đây là sản phẩm mình thấy cân bằng tốt giữa độ che phủ và độ tự nhiên.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344249/PomPom_Berry_Lip_Gloss_4_lugcnl.webp', 1, '2025-02-12 19:10:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (34, 11, 4, 30, 4, 'Má hồng có thiết kế đáng yêu, màu lên nhẹ nhàng.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344235/PomPom_Butterfly_Highlighter_6_swy3ro.webp', 1, '2025-02-13 08:50:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (35, 12, 4, 31, 5, 'Màu rất xinh trên da sáng.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344208/PomPom_Glitter_Eyeshadow_Palette_2_iyiqav.webp', 1, '2025-02-14 11:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (36, 13, 4, 32, 4, 'Hạt phấn mịn, dễ tán đều.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344283/PomPom_Velvet_Rose_Lipstick_4_gfkusb.webp', 1, '2025-02-15 13:15:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (37, 14, 4, 33, 5, 'Bao bì hình thỏ cực kỳ dễ thương, mình mua chỉ vì thiết kế nhưng chất lượng cũng rất tốt.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344265/PomPom_Bunny_Blush_3_xh89xd.webp', 1, '2025-02-16 15:40:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (38, 15, 4, 34, 3, 'Màu đẹp nhưng độ bám chưa cao trên da dầu.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344249/PomPom_Berry_Lip_Gloss_4_lugcnl.webp', 1, '2025-02-17 17:30:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (39, 16, 4, 35, 5, 'Sản phẩm phù hợp để trang điểm hàng ngày, tạo hiệu ứng tự nhiên và trẻ trung.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344235/PomPom_Butterfly_Highlighter_6_swy3ro.webp', 1, '2025-02-18 20:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (40, 11, 5, 36, 5, 'Đầu bút mảnh, dễ điều khiển, rất phù hợp cho người mới tập kẻ mắt.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344208/PomPom_Glitter_Eyeshadow_Palette_2_iyiqav.webp', 1, '2025-02-19 09:10:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (41, 12, 5, 37, 5, 'Kẻ một đường là sắc nét ngay.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344283/PomPom_Velvet_Rose_Lipstick_4_gfkusb.webp', 1, '2025-02-20 11:25:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (42, 13, 5, 38, 4, 'Khả năng chống lem khá tốt.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344265/PomPom_Bunny_Blush_3_xh89xd.webp', 1, '2025-02-21 13:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (43, 14, 5, 39, 5, 'Mình đã dùng suốt một ngày ngoài trời nhưng đường kẻ vẫn giữ được gần như nguyên vẹn.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344249/PomPom_Berry_Lip_Gloss_4_lugcnl.webp', 1, '2025-02-22 15:20:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (44, 15, 5, 40, 4, 'Giá hợp lý so với chất lượng nhận được.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344235/PomPom_Butterfly_Highlighter_6_swy3ro.webp', 1, '2025-02-23 18:15:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (45, 16, 5, 41, 5, 'Đây là eyeliner tốt nhất trong tầm giá mà mình từng sử dụng. Mực đều, không bị tắc đầu bút và rất dễ thao tác.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344208/PomPom_Glitter_Eyeshadow_Palette_2_iyiqav.webp', 1, '2025-02-24 20:30:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (46, 17, 6, 42, 5, 'Kem lót giúp lớp nền bám tốt hơn hẳn, da mịn cả ngày.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344283/PomPom_Velvet_Rose_Lipstick_4_gfkusb.webp', 1, '2025-02-25 09:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (47, 18, 6, 43, 4, 'Chất kem nhẹ mặt, không gây bí da.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344265/PomPom_Bunny_Blush_3_xh89xd.webp', 1, '2025-02-25 11:20:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (48, 19, 6, 44, 5, 'Mình rất thích mùi hương dịu nhẹ của sản phẩm này.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344249/PomPom_Berry_Lip_Gloss_4_lugcnl.webp', 1, '2025-02-25 14:10:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (49, 20, 6, 45, 5, 'Sau khi dùng kem lót này, lớp makeup giữ được lâu hơn và ít đổ dầu hơn.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344235/PomPom_Butterfly_Highlighter_6_swy3ro.webp', 1, '2025-02-26 08:45:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (50, 21, 6, 46, 4, 'Giá hơi cao nhưng chất lượng tốt.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344208/PomPom_Glitter_Eyeshadow_Palette_2_iyiqav.webp', 1, '2025-02-26 15:30:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (51, 17, 7, 47, 5, 'Highlight bắt sáng cực đẹp, đi tiệc rất nổi bật.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344283/PomPom_Velvet_Rose_Lipstick_4_gfkusb.webp', 1, '2025-02-27 09:15:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (52, 18, 7, 48, 4, 'Màu champagne phù hợp nhiều tông da.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344265/PomPom_Bunny_Blush_3_xh89xd.webp', 1, '2025-02-27 10:50:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (53, 19, 7, 49, 5, 'Nhũ mịn và không bị lộ hạt quá nhiều.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344249/PomPom_Berry_Lip_Gloss_4_lugcnl.webp', 1, '2025-02-27 13:40:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (54, 20, 7, 50, 5, 'Mình dùng cho sống mũi và gò má, hiệu ứng rất tự nhiên.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344235/PomPom_Butterfly_Highlighter_6_swy3ro.webp', 1, '2025-02-28 11:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (55, 21, 7, 51, 3, 'Đẹp nhưng hộp hơi dễ trầy xước.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344208/PomPom_Glitter_Eyeshadow_Palette_2_iyiqav.webp', 1, '2025-02-28 16:25:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (56, 17, 8, 52, 4, 'Kẹp tóc dễ thương, hợp với phong cách học sinh sinh viên.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344283/PomPom_Velvet_Rose_Lipstick_4_gfkusb.webp', 1, '2025-03-01 08:20:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (57, 18, 8, 53, 5, 'Chắc chắn hơn mình nghĩ, đeo cả ngày không tuột.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344265/PomPom_Bunny_Blush_3_xh89xd.webp', 1, '2025-03-01 10:10:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (58, 19, 8, 54, 5, 'Màu pastel rất xinh.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344249/PomPom_Berry_Lip_Gloss_4_lugcnl.webp', 1, '2025-03-01 13:30:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (59, 20, 8, 55, 4, 'Đóng gói cẩn thận, giao hàng nhanh.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344235/PomPom_Butterfly_Highlighter_6_swy3ro.webp', 1, '2025-03-02 09:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (60, 21, 8, 56, 5, 'Mình mua hai chiếc để phối đồ, chất lượng tốt hơn mong đợi.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344208/PomPom_Glitter_Eyeshadow_Palette_2_iyiqav.webp', 1, '2025-03-02 15:15:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (61, 17, 9, 57, 5, 'Son dưỡng làm mềm môi rất nhanh.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344283/PomPom_Velvet_Rose_Lipstick_4_gfkusb.webp', 1, '2025-03-03 09:40:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (62, 18, 9, 58, 4, 'Mùi thơm nhẹ, dễ chịu.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344265/PomPom_Bunny_Blush_3_xh89xd.webp', 1, '2025-03-03 11:10:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (63, 19, 9, 59, 5, 'Dùng ban đêm sáng hôm sau môi mềm rõ rệt.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344249/PomPom_Berry_Lip_Gloss_4_lugcnl.webp', 1, '2025-03-03 14:20:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (64, 20, 9, 60, 5, 'Không gây cảm giác dính khó chịu như một số loại son dưỡng khác.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344235/PomPom_Butterfly_Highlighter_6_swy3ro.webp', 1, '2025-03-04 08:50:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (65, 21, 9, 61, 4, 'Chất lượng ổn trong tầm giá.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344208/PomPom_Glitter_Eyeshadow_Palette_2_iyiqav.webp', 1, '2025-03-04 17:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (66, 17, 10, 62, 5, 'Set Sweet Bear cực kỳ đáng yêu, phù hợp làm quà tặng.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344283/PomPom_Velvet_Rose_Lipstick_4_gfkusb.webp', 1, '2025-03-05 09:30:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (67, 18, 10, 63, 5, 'Mình mua vì bao bì nhưng chất son cũng rất ổn.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344265/PomPom_Bunny_Blush_3_xh89xd.webp', 1, '2025-03-05 11:45:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (68, 19, 10, 64, 4, 'Thiết kế đẹp, màu son dễ dùng hằng ngày.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344249/PomPom_Berry_Lip_Gloss_4_lugcnl.webp', 1, '2025-03-05 13:50:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (69, 20, 10, 65, 5, 'Bộ sản phẩm được hoàn thiện rất tốt, nhìn cao cấp hơn giá bán.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344235/PomPom_Butterfly_Highlighter_6_swy3ro.webp', 1, '2025-03-06 10:15:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (70, 21, 10, 66, 5, 'Đây là một trong những set quà dễ thương nhất mình từng mua.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344208/PomPom_Glitter_Eyeshadow_Palette_2_iyiqav.webp', 1, '2025-03-06 10:17:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (71, 22, 11, 67, 5, 'Phấn phủ kiềm dầu rất tốt, phù hợp thời tiết nóng.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344283/PomPom_Velvet_Rose_Lipstick_4_gfkusb.webp', 1, '2025-03-07 09:20:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (72, 23, 11, 68, 4, 'Hạt phấn mịn, không bị mốc nền.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344265/PomPom_Bunny_Blush_3_xh89xd.webp', 1, '2025-03-07 12:10:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (73, 24, 11, 69, 5, 'Sẽ mua lại.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344249/PomPom_Berry_Lip_Gloss_4_lugcnl.webp', 1, '2025-03-07 15:40:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (74, 25, 11, 70, 5, 'Da mình khá dầu nhưng sản phẩm giữ được lớp nền đẹp trong nhiều giờ.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344235/PomPom_Butterfly_Highlighter_6_swy3ro.webp', 1, '2025-03-08 09:50:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (75, 26, 11, 71, 4, 'Bao bì nhỏ gọn, dễ mang theo.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344208/PomPom_Glitter_Eyeshadow_Palette_2_iyiqav.webp', 1, '2025-03-08 09:52:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (76, 22, 12, 72, 5, 'Son đỏ cam lên màu cực đẹp và tôn da.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344283/PomPom_Velvet_Rose_Lipstick_4_gfkusb.webp', 1, '2025-03-09 08:40:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (77, 23, 12, 73, 4, 'Chất son nhẹ môi, không bị bột.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344265/PomPom_Bunny_Blush_3_xh89xd.webp', 1, '2025-03-09 11:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (78, 24, 12, 74, 5, 'Màu yêu thích của mình hiện tại.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344249/PomPom_Berry_Lip_Gloss_4_lugcnl.webp', 1, '2025-03-09 14:10:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (79, 25, 12, 75, 5, 'Bám màu khá lâu dù mình ăn uống nhiều lần.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344235/PomPom_Butterfly_Highlighter_6_swy3ro.webp', 1, '2025-03-10 09:30:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (80, 26, 12, 76, 4, 'Rất đáng tiền.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344208/PomPom_Glitter_Eyeshadow_Palette_2_iyiqav.webp', 1, '2025-03-10 09:32:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (81, 27, 13, 77, 5, 'Chì kẻ mày dễ sử dụng, màu tự nhiên.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344283/PomPom_Velvet_Rose_Lipstick_4_gfkusb.webp', 1, '2025-03-11 09:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (82, 28, 13, 78, 4, 'Đầu chì mảnh, dễ tạo sợi.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344265/PomPom_Bunny_Blush_3_xh89xd.webp', 1, '2025-03-11 14:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (83, 29, 13, 79, 5, 'Kẻ rất nhanh mỗi sáng.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344249/PomPom_Berry_Lip_Gloss_4_lugcnl.webp', 1, '2025-03-11 18:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (84, 30, 13, 80, 5, 'Giữ màu cả ngày mà không bị lem khi đổ mồ hôi.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344235/PomPom_Butterfly_Highlighter_6_swy3ro.webp', 1, '2025-03-12 09:20:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (85, 31, 13, 81, 4, 'Giá hợp lý, chất lượng tốt.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344208/PomPom_Glitter_Eyeshadow_Palette_2_iyiqav.webp', 1, '2025-03-12 16:30:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (86, 27, 14, 82, 5, 'Xịt khóa nền giúp lớp makeup bền hơn rất nhiều.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344283/PomPom_Velvet_Rose_Lipstick_4_gfkusb.webp', 1, '2025-03-13 09:10:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (87, 28, 14, 83, 4, 'Mùi hương nhẹ nhàng.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344265/PomPom_Bunny_Blush_3_xh89xd.webp', 1, '2025-03-13 12:15:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (88, 29, 14, 84, 5, 'Dùng trong thời tiết nóng vẫn giữ nền đẹp.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344249/PomPom_Berry_Lip_Gloss_4_lugcnl.webp', 1, '2025-03-13 18:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (89, 30, 14, 85, 5, 'Một trong những chai setting spray tốt nhất mình từng dùng.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344235/PomPom_Butterfly_Highlighter_6_swy3ro.webp', 1, '2025-03-14 08:50:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (90, 31, 14, 86, 4, 'Thiết kế chai đẹp, vòi xịt đều.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344208/PomPom_Glitter_Eyeshadow_Palette_2_iyiqav.webp', 1, '2025-03-14 15:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (91, 27, 15, 87, 5, 'Tint Cherry bám màu cực lâu.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344283/PomPom_Velvet_Rose_Lipstick_4_gfkusb.webp', 1, '2025-03-15 09:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (92, 28, 15, 88, 5, 'Màu đỏ cherry rất xinh.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344265/PomPom_Bunny_Blush_3_xh89xd.webp', 1, '2025-03-15 11:45:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (93, 29, 15, 89, 4, 'Chất tint nhẹ môi.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344249/PomPom_Berry_Lip_Gloss_4_lugcnl.webp', 1, '2025-03-15 16:30:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (94, 30, 15, 90, 5, 'Ăn uống vẫn còn màu trên môi khá rõ.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344235/PomPom_Butterfly_Highlighter_6_swy3ro.webp', 1, '2025-03-16 10:20:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (95, 31, 15, 91, 5, 'Đây là màu son mình dùng nhiều nhất trong tháng vừa rồi.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344208/PomPom_Glitter_Eyeshadow_Palette_2_iyiqav.webp', 1, '2025-03-16 18:10:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (96, 32, 16, 92, 5, 'Mermaid Highlighter tạo hiệu ứng da căng bóng rất đẹp.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344283/PomPom_Velvet_Rose_Lipstick_4_gfkusb.webp', 1, '2025-03-17 08:40:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (97, 33, 16, 93, 4, 'Nhũ mịn, dễ tán.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344265/PomPom_Bunny_Blush_3_xh89xd.webp', 1, '2025-03-17 13:20:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (98, 34, 16, 94, 5, 'Màu sắc rất độc đáo.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344249/PomPom_Berry_Lip_Gloss_4_lugcnl.webp', 1, '2025-03-17 17:50:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (99, 35, 16, 95, 5, 'Dùng đi tiệc lên ảnh rất nổi bật.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344235/PomPom_Butterfly_Highlighter_6_swy3ro.webp', 1, '2025-03-18 09:30:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (100, 36, 16, 96, 4, 'Hộp sản phẩm đẹp mắt.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344208/PomPom_Glitter_Eyeshadow_Palette_2_iyiqav.webp', 1, '2025-03-18 15:10:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (101, 32, 17, 97, 5, 'Kem che khuyết điểm che phủ tốt các nốt mụn.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344283/PomPom_Velvet_Rose_Lipstick_4_gfkusb.webp', 1, '2025-03-19 09:15:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (102, 33, 17, 98, 4, 'Không bị cakey khi dùng lượng vừa phải.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344265/PomPom_Bunny_Blush_3_xh89xd.webp', 1, '2025-03-19 11:20:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (103, 34, 17, 99, 5, 'Mình rất hài lòng với sản phẩm này.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344249/PomPom_Berry_Lip_Gloss_4_lugcnl.webp', 1, '2025-03-19 16:40:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (104, 35, 17, 100, 5, 'Che quầng thâm mắt khá tốt.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344235/PomPom_Butterfly_Highlighter_6_swy3ro.webp', 1, '2025-03-20 09:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (105, 36, 17, 101, 4, 'Tông màu hợp da châu Á.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344208/PomPom_Glitter_Eyeshadow_Palette_2_iyiqav.webp', 1, '2025-03-20 17:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (106, 32, 18, 102, 3, 'Bộ cọ nhìn đẹp nhưng hơi cứng lúc mới dùng.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344283/PomPom_Velvet_Rose_Lipstick_4_gfkusb.webp', 1, '2025-03-21 08:30:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (107, 33, 18, 103, 4, 'Sau khi giặt vài lần thì mềm hơn.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344265/PomPom_Bunny_Blush_3_xh89xd.webp', 1, '2025-03-21 12:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (108, 34, 18, 104, 5, 'Đầy đủ các loại cọ cần thiết.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344249/PomPom_Berry_Lip_Gloss_4_lugcnl.webp', 1, '2025-03-21 18:15:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (109, 35, 18, 105, 4, 'Tay cầm chắc chắn.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344235/PomPom_Butterfly_Highlighter_6_swy3ro.webp', 1, '2025-03-22 10:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (110, 36, 18, 106, 5, 'Rất phù hợp cho người mới bắt đầu học makeup.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344208/PomPom_Glitter_Eyeshadow_Palette_2_iyiqav.webp', 1, '2025-03-22 15:40:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (111, 37, 19, 107, 5, 'Mặt nạ dưỡng ẩm giúp da mềm mịn ngay sau khi dùng.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344283/PomPom_Velvet_Rose_Lipstick_4_gfkusb.webp', 1, '2025-03-23 09:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (112, 38, 19, 108, 4, 'Tinh chất khá nhiều.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344265/PomPom_Bunny_Blush_3_xh89xd.webp', 1, '2025-03-23 13:20:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (113, 39, 19, 109, 5, 'Đắp rất thư giãn.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344249/PomPom_Berry_Lip_Gloss_4_lugcnl.webp', 1, '2025-03-23 18:30:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (114, 40, 19, 110, 5, 'Da căng mọng hơn vào sáng hôm sau.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344235/PomPom_Butterfly_Highlighter_6_swy3ro.webp', 1, '2025-03-24 08:40:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (115, 41, 19, 111, 4, 'Sẽ mua lại khi dùng hết.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344208/PomPom_Glitter_Eyeshadow_Palette_2_iyiqav.webp', 1, '2025-03-24 16:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (116, 37, 20, 112, 5, 'Kem tay thấm nhanh và không nhờn rít.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344283/PomPom_Velvet_Rose_Lipstick_4_gfkusb.webp', 1, '2025-03-25 09:10:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (117, 38, 20, 113, 4, 'Mùi hương dễ chịu.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344265/PomPom_Bunny_Blush_3_xh89xd.webp', 1, '2025-03-25 12:40:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (118, 39, 20, 114, 5, 'Dưỡng ẩm khá tốt.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344249/PomPom_Berry_Lip_Gloss_4_lugcnl.webp', 1, '2025-03-25 17:50:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (119, 40, 20, 115, 5, 'Mình để trong túi mang theo mỗi ngày.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344235/PomPom_Butterfly_Highlighter_6_swy3ro.webp', 1, '2025-03-26 09:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (120, 41, 20, 116, 4, 'Bao bì nhỏ gọn, đẹp mắt.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344208/PomPom_Glitter_Eyeshadow_Palette_2_iyiqav.webp', 1, '2025-03-26 15:15:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (121, 42, 21, 117, 5, 'Nước tẩy trang làm sạch tốt mà không khô da.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344283/PomPom_Velvet_Rose_Lipstick_4_gfkusb.webp', 1, '2025-03-27 09:20:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (122, 43, 21, 118, 4, 'Không cay mắt.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344265/PomPom_Bunny_Blush_3_xh89xd.webp', 1, '2025-03-27 11:45:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (123, 44, 21, 119, 5, 'Dịu nhẹ với da nhạy cảm.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344249/PomPom_Berry_Lip_Gloss_4_lugcnl.webp', 1, '2025-03-27 18:10:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (124, 45, 21, 120, 5, 'Loại bỏ lớp makeup khá nhanh.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344235/PomPom_Butterfly_Highlighter_6_swy3ro.webp', 1, '2025-03-28 09:40:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (125, 46, 21, 121, 4, 'Giá thành hợp lý.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344208/PomPom_Glitter_Eyeshadow_Palette_2_iyiqav.webp', 1, '2025-03-28 16:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (126, 42, 22, 122, 5, 'Galaxy Eyeshadow nhũ đẹp ngoài mong đợi.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344283/PomPom_Velvet_Rose_Lipstick_4_gfkusb.webp', 1, '2025-03-29 08:30:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (127, 43, 22, 123, 5, 'Màu sắc lung linh dưới ánh đèn.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344265/PomPom_Bunny_Blush_3_xh89xd.webp', 1, '2025-03-29 11:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (128, 44, 22, 124, 4, 'Độ bám khá tốt.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344249/PomPom_Berry_Lip_Gloss_4_lugcnl.webp', 1, '2025-03-29 16:20:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (129, 45, 22, 125, 5, 'Đây là bảng nhũ đẹp nhất mình từng sở hữu.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344235/PomPom_Butterfly_Highlighter_6_swy3ro.webp', 1, '2025-03-30 09:15:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (130, 46, 22, 126, 4, 'Thiết kế hộp rất đẹp.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344208/PomPom_Glitter_Eyeshadow_Palette_2_iyiqav.webp', 1, '2025-03-30 14:45:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (131, 47, 23, 127, 5, 'Phấn tạo khối dễ tán, không bị quá đậm.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344283/PomPom_Velvet_Rose_Lipstick_4_gfkusb.webp', 1, '2025-03-31 09:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (132, 48, 23, 128, 4, 'Màu sắc tự nhiên.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344265/PomPom_Bunny_Blush_3_xh89xd.webp', 1, '2025-03-31 13:10:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (133, 49, 23, 129, 5, 'Rất phù hợp cho người mới tập contour.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344249/PomPom_Berry_Lip_Gloss_4_lugcnl.webp', 1, '2025-03-31 18:30:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (134, 50, 23, 130, 5, 'Chất phấn mịn và dễ kiểm soát lượng sản phẩm.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344235/PomPom_Butterfly_Highlighter_6_swy3ro.webp', 1, '2025-04-01 10:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (135, 51, 23, 131, 4, 'Đóng gói chắc chắn.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344208/PomPom_Glitter_Eyeshadow_Palette_2_iyiqav.webp', 1, '2025-04-01 16:20:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (136, 47, 24, 132, 5, 'Serum cấp ẩm tốt, da mềm hơn sau một tuần.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344283/PomPom_Velvet_Rose_Lipstick_4_gfkusb.webp', 1, '2025-04-02 09:10:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (137, 48, 24, 133, 4, 'Kết cấu lỏng nhẹ, thấm nhanh.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344265/PomPom_Bunny_Blush_3_xh89xd.webp', 1, '2025-04-02 12:20:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (138, 49, 24, 134, 5, 'Không gây kích ứng da.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344249/PomPom_Berry_Lip_Gloss_4_lugcnl.webp', 1, '2025-04-02 17:40:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (139, 50, 24, 135, 5, 'Hiệu quả dưỡng ẩm rất rõ rệt khi sử dụng đều đặn.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344235/PomPom_Butterfly_Highlighter_6_swy3ro.webp', 1, '2025-04-03 09:50:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (140, 51, 24, 136, 4, 'Chai thủy tinh nhìn khá sang trọng.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344208/PomPom_Glitter_Eyeshadow_Palette_2_iyiqav.webp', 1, '2025-04-03 15:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (141, 52, 25, 137, 5, 'Bộ quà Unicorn Gift Box quá dễ thương.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344283/PomPom_Velvet_Rose_Lipstick_4_gfkusb.webp', 1, '2025-04-04 08:45:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (142, 53, 25, 138, 5, 'Thích hợp làm quà sinh nhật cho bạn bè.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344265/PomPom_Bunny_Blush_3_xh89xd.webp', 1, '2025-04-04 11:30:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (143, 54, 25, 139, 4, 'Đóng gói đẹp và cẩn thận.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344249/PomPom_Berry_Lip_Gloss_4_lugcnl.webp', 1, '2025-04-04 17:20:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (144, 55, 25, 140, 5, 'Nhận hàng xong thấy đẹp hơn cả ảnh quảng cáo.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344235/PomPom_Butterfly_Highlighter_6_swy3ro.webp', 1, '2025-04-05 09:15:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (145, 56, 25, 141, 5, 'Một bộ quà rất đáng tiền, người nhận cực kỳ thích.', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344208/PomPom_Glitter_Eyeshadow_Palette_2_iyiqav.webp', 1, '2025-04-05 15:40:00');

-- Table: product_variants
DROP TABLE IF EXISTS product_variants;
CREATE TABLE IF NOT EXISTS product_variants (
    variant_id INTEGER PRIMARY KEY AUTOINCREMENT,
    product_id INTEGER NOT NULL,
    variant_name TEXT,
    sku TEXT UNIQUE,
    additional_price REAL DEFAULT 0,
    stock INTEGER,
    image_url TEXT,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
);
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (1, 3, 'Tone 01 – Sáng', 'PP-CU-003-L', 0.0, 50, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_pink_dream_vr3mtq.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (2, 3, 'Tone 02 – Trung bình', 'PP-CU-003-M', 0.0, 60, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_fairy_rose_s5eo5q.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (3, 3, 'Tone 03 – Ngăm', 'PP-CU-003-D', 0.0, 40, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_sweet_peach_zdwlh8.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (4, 4, 'Màu Hồng Baby', 'PP-BL-004-PK', 0.0, 200, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_pink_dream_vr3mtq.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (5, 4, 'Màu Cam Đào', 'PP-BL-004-OR', 0.0, 200, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_fairy_rose_s5eo5q.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (6, 12, 'Màu Đỏ Cam', 'PP-LL-012-RD', 0.0, 120, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_pink_dream_vr3mtq.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (7, 12, 'Màu Hồng Nude', 'PP-LL-012-NU', 0.0, 130, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_fairy_rose_s5eo5q.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (8, 1, 'Tone 01 – Sáng', 'PP-CU-001-L', 0.0, 50, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_pink_dream_vr3mtq.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (9, 1, 'Tone 02 – Trung bình', 'PP-CU-001-M', 0.0, 60, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_fairy_rose_s5eo5q.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (10, 1, 'Tone 03 – Ngăm', 'PP-CU-001-D', 0.0, 40, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_sweet_peach_zdwlh8.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (11, 2, 'Màu Hồng Baby', 'PP-BL-002-PK', 0.0, 200, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_pink_dream_vr3mtq.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (12, 2, 'Màu Cam Đào', 'PP-BL-002-OR', 0.0, 200, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_fairy_rose_s5eo5q.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (13, 5, 'Màu Đỏ Cam', 'PP-LL-005-RD', 0.0, 120, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_pink_dream_vr3mtq.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (14, 5, 'Màu Hồng Nude', 'PP-LL-005-NU', 0.0, 130, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_fairy_rose_s5eo5q.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (15, 6, 'Tone 01 – Sáng', 'PP-CU-006-L', 0.0, 50, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_pink_dream_vr3mtq.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (16, 6, 'Tone 02 – Trung bình', 'PP-CU-006-M', 0.0, 60, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_fairy_rose_s5eo5q.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (17, 6, 'Tone 03 – Ngăm', 'PP-CU-006-D', 0.0, 40, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_sweet_peach_zdwlh8.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (18, 7, 'Màu Hồng Baby', 'PP-BL-007-PK', 0.0, 200, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_pink_dream_vr3mtq.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (19, 7, 'Màu Cam Đào', 'PP-BL-007-OR', 0.0, 200, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_fairy_rose_s5eo5q.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (20, 8, 'Màu Đỏ Cam', 'PP-LL-008-RD', 0.0, 120, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_pink_dream_vr3mtq.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (21, 8, 'Màu Hồng Nude', 'PP-LL-008-NU', 0.0, 130, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_fairy_rose_s5eo5q.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (22, 9, 'Tone 01 – Sáng', 'PP-CU-009-L', 0.0, 50, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_pink_dream_vr3mtq.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (23, 9, 'Tone 02 – Trung bình', 'PP-CU-009-M', 0.0, 60, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_fairy_rose_s5eo5q.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (24, 9, 'Tone 03 – Ngăm', 'PP-CU-009-D', 0.0, 40, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_sweet_peach_zdwlh8.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (25, 10, 'Màu Hồng Baby', 'PP-BL-010-PK', 0.0, 200, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_pink_dream_vr3mtq.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (26, 10, 'Màu Cam Đào', 'PP-BL-010-OR', 0.0, 200, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_fairy_rose_s5eo5q.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (27, 11, 'Màu Đỏ Cam', 'PP-LL-011-RD', 0.0, 120, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_pink_dream_vr3mtq.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (28, 11, 'Màu Hồng Nude', 'PP-LL-011-NU', 0.0, 130, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_fairy_rose_s5eo5q.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (29, 13, 'Tone 01 – Sáng', 'PP-CU-013-L', 0.0, 50, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_pink_dream_vr3mtq.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (30, 13, 'Tone 02 – Trung bình', 'PP-CU-013-M', 0.0, 60, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_fairy_rose_s5eo5q.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (31, 13, 'Tone 03 – Ngăm', 'PP-CU-013-D', 0.0, 40, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_sweet_peach_zdwlh8.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (32, 14, 'Màu Hồng Baby', 'PP-BL-014-PK', 0.0, 200, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_pink_dream_vr3mtq.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (33, 14, 'Màu Cam Đào', 'PP-BL-014-OR', 0.0, 200, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_fairy_rose_s5eo5q.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (34, 15, 'Màu Đỏ Cam', 'PP-LL-015-RD', 0.0, 120, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_pink_dream_vr3mtq.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (35, 15, 'Màu Hồng Nude', 'PP-LL-015-NU', 0.0, 130, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_fairy_rose_s5eo5q.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (36, 16, 'Tone 01 – Sáng', 'PP-CU-016-L', 0.0, 50, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_pink_dream_vr3mtq.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (37, 16, 'Tone 02 – Trung bình', 'PP-CU-016-M', 0.0, 60, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_fairy_rose_s5eo5q.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (38, 16, 'Tone 03 – Ngăm', 'PP-CU-061-D', 0.0, 40, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_sweet_peach_zdwlh8.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (39, 17, 'Màu Hồng Baby', 'PP-BL-017-PK', 0.0, 200, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_pink_dream_vr3mtq.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (40, 17, 'Màu Cam Đào', 'PP-BL-017-OR', 0.0, 200, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_fairy_rose_s5eo5q.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (41, 18, 'Màu Đỏ Cam', 'PP-LL-018-RD', 0.0, 120, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_pink_dream_vr3mtq.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (42, 18, 'Màu Hồng Nude', 'PP-LL-018-NU', 0.0, 130, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_fairy_rose_s5eo5q.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (43, 19, 'Tone 01 – Sáng', 'PP-CU-019-L', 0.0, 50, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_pink_dream_vr3mtq.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (44, 19, 'Tone 02 – Trung bình', 'PP-CU-019-M', 0.0, 60, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_fairy_rose_s5eo5q.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (45, 19, 'Tone 03 – Ngăm', 'PP-CU-019-D', 0.0, 40, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_sweet_peach_zdwlh8.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (46, 20, 'Màu Hồng Baby', 'PP-BL-020-PK', 0.0, 200, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_pink_dream_vr3mtq.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (47, 20, 'Màu Cam Đào', 'PP-BL-020-OR', 0.0, 200, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_fairy_rose_s5eo5q.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (48, 21, 'Màu Đỏ Cam', 'PP-LL-021-RD', 0.0, 120, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_pink_dream_vr3mtq.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (49, 21, 'Màu Hồng Nude', 'PP-LL-021-NU', 0.0, 130, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_fairy_rose_s5eo5q.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (50, 22, 'Tone 01 – Sáng', 'PP-CU-022-L', 0.0, 50, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_pink_dream_vr3mtq.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (51, 22, 'Tone 02 – Trung bình', 'PP-CU-022-M', 0.0, 60, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_fairy_rose_s5eo5q.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (52, 22, 'Tone 03 – Ngăm', 'PP-CU-022-D', 0.0, 40, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_sweet_peach_zdwlh8.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (53, 23, 'Màu Hồng Baby', 'PP-BL-023-PK', 0.0, 200, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_pink_dream_vr3mtq.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (54, 23, 'Màu Cam Đào', 'PP-BL-023-OR', 0.0, 200, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_fairy_rose_s5eo5q.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (55, 24, 'Màu Đỏ Cam', 'PP-LL-024-RD', 0.0, 120, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_pink_dream_vr3mtq.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (56, 24, 'Màu Hồng Nude', 'PP-LL-024-NU', 0.0, 130, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_fairy_rose_s5eo5q.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (57, 25, 'Tone 01 – Sáng', 'PP-CU-025-L', 0.0, 50, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_pink_dream_vr3mtq.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (58, 25, 'Tone 02 – Trung bình', 'PP-CU-025-M', 0.0, 60, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_fairy_rose_s5eo5q.png');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (59, 25, 'Tone 03 – Ngăm', 'PP-CU-025-D', 0.0, 40, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781799726/variant_sweet_peach_zdwlh8.png');

-- Table: products
DROP TABLE IF EXISTS products;
CREATE TABLE IF NOT EXISTS products (
    product_id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    slug TEXT UNIQUE,
    description TEXT,
    price REAL,
    sale_price REAL,
    stock INTEGER,
    sku TEXT UNIQUE,
    category_id INTEGER,
    brand TEXT DEFAULT 'PomPom',
    thumbnail_url TEXT,
    is_active INTEGER DEFAULT 1,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(category_id)
);
INSERT INTO products (product_id, name, slug, description, price, sale_price, stock, sku, category_id, brand, thumbnail_url, is_active, created_at) VALUES (1, 'PomPom Unicorn Magic Palette', 'pompom-unicorn-magic', 'Bảng phấn mắt 8 màu hồng tím', 125000.0, 997500.0, 200, 'PP-MP-001', 6, 'PomPom', NULL, 1, '2026-06-13 14:20:36');
INSERT INTO products (product_id, name, slug, description, price, sale_price, stock, sku, category_id, brand, thumbnail_url, is_active, created_at) VALUES (2, 'PomPom Velvet Rose Lipstick', 'pompom-velvet-rose', 'Son lì mềm mượt màu hồng đất', 375000.0, 322500.0, 300, 'PP-LS-002', 8, 'PomPom', NULL, 1, '2026-06-13 14:20:36');
INSERT INTO products (product_id, name, slug, description, price, sale_price, stock, sku, category_id, brand, thumbnail_url, is_active, created_at) VALUES (3, 'PomPom Cloud Cushion', 'pompom-cloud-cushion', 'Kem nền cushion 3 tông', 625000.0, 550000.0, 150, 'PP-CU-003', 1, 'PomPom', NULL, 1, '2026-06-13 14:20:36');
INSERT INTO products (product_id, name, slug, description, price, sale_price, stock, sku, category_id, brand, thumbnail_url, is_active, created_at) VALUES (4, 'PomPom Bunny Blush', 'pompom-bunny-blush', 'Má hồng dạng nén hình thỏ', 300000.0, 250000.0, 400, 'PP-BL-004', 7, 'PomPom', NULL, 1, '2026-06-13 14:20:36');
INSERT INTO products (product_id, name, slug, description, price, sale_price, stock, sku, category_id, brand, thumbnail_url, is_active, created_at) VALUES (5, 'PomPom Garden Angled Blush Brush', 'pompom-garden-angled-blush-brush', 'Cọ má hồng góc cạnh Garden', 200000.0, 175000.0, 500, 'PP-EY-005', 4, 'PomPom', NULL, 1, '2026-06-13 14:20:36');
INSERT INTO products (product_id, name, slug, description, price, sale_price, stock, sku, category_id, brand, thumbnail_url, is_active, created_at) VALUES (6, 'PomPom Berry Lip Gloss', 'pompom-berry-lip-gloss', 'Son bóng dưỡng môi hương dâu', 250000.0, 212500.0, 350, 'PP-LG-006', 9, 'PomPom', NULL, 1, '2026-06-13 14:20:36');
INSERT INTO products (product_id, name, slug, description, price, sale_price, stock, sku, category_id, brand, thumbnail_url, is_active, created_at) VALUES (7, 'PomPom Butterfly Highlighter', 'pompom-butterfly-highlighter', 'Highlight dạng bột ánh vàng hồng', 450000.0, 375000.0, 200, 'PP-HL-007', 1, 'PomPom', NULL, 1, '2026-06-13 14:20:36');
INSERT INTO products (product_id, name, slug, description, price, sale_price, stock, sku, category_id, brand, thumbnail_url, is_active, created_at) VALUES (8, 'PomPom Unicorn Hair Clip', 'pompom-unicorn-hair-clip', 'Kẹp tóc hình kỳ lân', 150000.0, 125000.0, 800, 'PP-AC-008', 4, 'PomPom', NULL, 1, '2026-06-13 14:20:36');
INSERT INTO products (product_id, name, slug, description, price, sale_price, stock, sku, category_id, brand, thumbnail_url, is_active, created_at) VALUES (9, 'PomPom Makeup Sponge Set', 'pompom-sponge-set', 'Bộ 3 mút trang điểm', 225000.0, 187500.0, 600, 'PP-AC-009', 4, 'PomPom', NULL, 1, '2026-06-13 14:20:36');
INSERT INTO products (product_id, name, slug, description, price, sale_price, stock, sku, category_id, brand, thumbnail_url, is_active, created_at) VALUES (10, 'PomPom Sweet Bear Gift Set', 'pompom-sweet-bear-set', 'Set gấu: son, má hồng, phấn mắt mini', 875000.0, 747500.0, 100, 'PP-GS-010', 5, 'PomPom', NULL, 1, '2026-06-13 14:20:36');
INSERT INTO products (product_id, name, slug, description, price, sale_price, stock, sku, category_id, brand, thumbnail_url, is_active, created_at) VALUES (11, 'PomPom Glitter Eyeshadow Palette', 'pompom-glitter-palette', 'Bảng phấn nhũ 6 màu', 700000.0, 600000.0, 180, 'PP-MP-011', 6, 'PomPom', NULL, 1, '2026-06-13 14:20:36');
INSERT INTO products (product_id, name, slug, description, price, sale_price, stock, sku, category_id, brand, thumbnail_url, is_active, created_at) VALUES (12, 'PomPom Matte Liquid Lipstick', 'pompom-matte-liquid', 'Son lì dạng nước đỏ cam', 350000.0, 300000.0, 250, 'PP-LL-012', 8, 'PomPom', NULL, 1, '2026-06-13 14:20:36');
INSERT INTO products (product_id, name, slug, description, price, sale_price, stock, sku, category_id, brand, thumbnail_url, is_active, created_at) VALUES (13, 'PomPom Strawberry Cupid Hair Clip', 'pompom-strawberry-cupid-hair-clip', 'Kẹp tóc hình trái tim dâu tây', 175000.0, 150000.0, 400, 'PP-AC-013', 4, 'PomPom', NULL, 1, '2026-06-13 14:20:36');
INSERT INTO products (product_id, name, slug, description, price, sale_price, stock, sku, category_id, brand, thumbnail_url, is_active, created_at) VALUES (14, 'PomPom Bunny Garden Cushion Cream Foundation', 'pompom-bunny_garden_cushion_cream_foundation', 'Kem nền dạng cushion Bunny Garden', 300000.0, 250000.0, 300, 'PP-SP-014', 1, 'PomPom', NULL, 1, '2026-06-13 14:20:36');
INSERT INTO products (product_id, name, slug, description, price, sale_price, stock, sku, category_id, brand, thumbnail_url, is_active, created_at) VALUES (15, 'PomPom Cherry Lip Tint', 'pompom-cherry-tint', 'Màu nước môi lâu trôi', 225000.0, 200000.0, 500, 'PP-LT-015', 8, 'PomPom', NULL, 1, '2026-06-13 14:20:36');
INSERT INTO products (product_id, name, slug, description, price, sale_price, stock, sku, category_id, brand, thumbnail_url, is_active, created_at) VALUES (16, 'PomPom Mermaid Highlighter', 'pompom-mermaid-highlighter', 'Bảng highlight 4 màu', 550000.0, 475000.0, 140, 'PP-HL-016', 1, 'PomPom', NULL, 1, '2026-06-13 14:20:36');
INSERT INTO products (product_id, name, slug, description, price, sale_price, stock, sku, category_id, brand, thumbnail_url, is_active, created_at) VALUES (17, 'PomPom Peach Blur Powder', 'pompom-peach-powder', 'Phấn phủ kiềm dầu', 400000.0, 350000.0, 220, 'PP-PW-017', 1, 'PomPom', NULL, 1, '2026-06-13 14:20:36');
INSERT INTO products (product_id, name, slug, description, price, sale_price, stock, sku, category_id, brand, thumbnail_url, is_active, created_at) VALUES (18, 'PomPom Heart Brush Set', 'pompom-heart-brush-set', 'Bộ 5 cọ trang điểm hình tim', 500000.0, 425000.0, 180, 'PP-AC-018', 10, 'PomPom', NULL, 1, '2026-06-13 14:20:36');
INSERT INTO products (product_id, name, slug, description, price, sale_price, stock, sku, category_id, brand, thumbnail_url, is_active, created_at) VALUES (19, 'PomPom Midsummer Fairytales Liquid Highlighter', 'pompom-midsummer-fairytales-liquid-highlighter', 'Phấn bắt sáng dạng lỏng Midsummer Fairytales', 750000.0, 675000.0, 120, 'PP-PF-019', 1, 'PomPom', NULL, 1, '2026-06-13 14:20:36');
INSERT INTO products (product_id, name, slug, description, price, sale_price, stock, sku, category_id, brand, thumbnail_url, is_active, created_at) VALUES (20, 'PomPom Cute Mirror', 'pompom-cute-mirror', 'Gương bỏ túi viền thú', 200000.0, 162500.0, 600, 'PP-AC-020', 4, 'PomPom', NULL, 1, '2026-06-13 14:20:36');
INSERT INTO products (product_id, name, slug, description, price, sale_price, stock, sku, category_id, brand, thumbnail_url, is_active, created_at) VALUES (21, 'PomPom Midsummer Fairytales Makeup Cleansing Balm', 'pompom-midsummer-fairytales-makeup-cleansing-balm', 'Sáp tẩy trang Midsummer Fairytales', 275000.0, 225000.0, 350, 'PP-SP-021', 1, 'PomPom', NULL, 1, '2026-06-13 14:20:36');
INSERT INTO products (product_id, name, slug, description, price, sale_price, stock, sku, category_id, brand, thumbnail_url, is_active, created_at) VALUES (22, 'PomPom Galaxy Eyeshadow', 'pompom-galaxy-eyeshadow', 'Bảng phấn mắt màu thiên hà', 800000.0, 700000.0, 130, 'PP-MP-022', 6, 'PomPom', NULL, 1, '2026-06-13 14:20:36');
INSERT INTO products (product_id, name, slug, description, price, sale_price, stock, sku, category_id, brand, thumbnail_url, is_active, created_at) VALUES (23, 'PomPom Honey Lip Mask', 'pompom-honey-lip-mask', 'Mặt nạ môi dưỡng ẩm', 200000.0, 175000.0, 400, 'PP-LM-023', 8, 'PomPom', NULL, 1, '2026-06-13 14:20:36');
INSERT INTO products (product_id, name, slug, description, price, sale_price, stock, sku, category_id, brand, thumbnail_url, is_active, created_at) VALUES (24, 'PomPom Fluffy Powder Puff', 'pompom-fluffy-puff', 'Bông phấn mềm', 100000.0, 87500.0, 900, 'PP-AC-024', 4, 'PomPom', NULL, 1, '2026-06-13 14:20:36');
INSERT INTO products (product_id, name, slug, description, price, sale_price, stock, sku, category_id, brand, thumbnail_url, is_active, created_at) VALUES (25, 'PomPom Ultimate Makeup Set', 'pompom-ultimate-set', 'Set full face 12 món', 1625000.0, 1375000.0, 80, 'PP-GS-025', 5, 'PomPom', NULL, 1, '2026-06-13 14:20:36');

-- Table: promotion_details
DROP TABLE IF EXISTS promotion_details;
CREATE TABLE IF NOT EXISTS promotion_details (
    detail_id INTEGER PRIMARY KEY,
    promotion_id INTEGER NOT NULL,
    product_id INTEGER, -- sản phẩm được áp dụng (null nếu tất cả)
    buy_quantity INTEGER DEFAULT 1,
    get_product_id INTEGER, -- sản phẩm tặng
    get_quantity INTEGER DEFAULT 1,
    discount_percent REAL, -- nếu type là percent
    discount_amount REAL, -- nếu type là fixed
    FOREIGN KEY (promotion_id) REFERENCES promotions(promotion_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id),
    FOREIGN KEY (get_product_id) REFERENCES products(product_id)
);
INSERT INTO promotion_details (detail_id, promotion_id, product_id, buy_quantity, get_product_id, get_quantity, discount_percent, discount_amount) VALUES (1, 1, 18, 1, 18, 1, NULL, NULL);
INSERT INTO promotion_details (detail_id, promotion_id, product_id, buy_quantity, get_product_id, get_quantity, discount_percent, discount_amount) VALUES (2, 2, 1, 1, NULL, NULL, 30.0, NULL);
INSERT INTO promotion_details (detail_id, promotion_id, product_id, buy_quantity, get_product_id, get_quantity, discount_percent, discount_amount) VALUES (3, 3, NULL, 1, NULL, NULL, 15.0, NULL);
INSERT INTO promotion_details (detail_id, promotion_id, product_id, buy_quantity, get_product_id, get_quantity, discount_percent, discount_amount) VALUES (4, 4, NULL, 1, NULL, NULL, NULL, 0.0);
INSERT INTO promotion_details (detail_id, promotion_id, product_id, buy_quantity, get_product_id, get_quantity, discount_percent, discount_amount) VALUES (5, 5, 2, 1, 2, 1, NULL, NULL);
INSERT INTO promotion_details (detail_id, promotion_id, product_id, buy_quantity, get_product_id, get_quantity, discount_percent, discount_amount) VALUES (6, 5, 12, 1, 12, 1, NULL, NULL);

-- Table: promotions
DROP TABLE IF EXISTS promotions;
CREATE TABLE IF NOT EXISTS promotions (
    promotion_id INTEGER PRIMARY KEY,
    name TEXT NOT NULL, -- 'Mua 1 tặng 1 chổi trang điểm'
    type TEXT NOT NULL, -- 'bogo', 'flash_sale', 'percent_discount', ...
    start_date TEXT,
    end_date TEXT,
    is_active INTEGER DEFAULT 1
);
INSERT INTO promotions (promotion_id, name, type, start_date, end_date, is_active) VALUES (1, 'Mua 1 tặng 1 chổi trang điểm', 'bogo', '2025-06-01 00:00:00', '2025-06-30 23:59:59', 1);
INSERT INTO promotions (promotion_id, name, type, start_date, end_date, is_active) VALUES (2, 'Flash sale 12h trưa', 'flash_sale', '2025-01-15 12:00:00', '2025-01-15 13:00:00', 0);
INSERT INTO promotions (promotion_id, name, type, start_date, end_date, is_active) VALUES (3, 'Giảm 15% cho hội viên', 'percent_discount', '2025-06-01 00:00:00', '2025-06-30 23:59:59', 1);
INSERT INTO promotions (promotion_id, name, type, start_date, end_date, is_active) VALUES (4, 'Freeship toàn quốc', 'free_shipping', '2025-06-01 00:00:00', '2025-06-25 23:59:59', 1);
INSERT INTO promotions (promotion_id, name, type, start_date, end_date, is_active) VALUES (5, 'Mua 1 tặng 1 son môi', 'bogo', '2025-07-01 00:00:00', '2025-07-31 23:59:59', 1);

-- Table: recently_viewed
DROP TABLE IF EXISTS recently_viewed;
CREATE TABLE IF NOT EXISTS recently_viewed (
    view_id INTEGER PRIMARY KEY,
    user_id INTEGER NOT NULL,
    product_id INTEGER NOT NULL,
    viewed_at TEXT,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id),
    UNIQUE(user_id, product_id) -- chỉ lưu một lần, cập nhật thời gian
);

-- Table: search_history
DROP TABLE IF EXISTS search_history;
CREATE TABLE IF NOT EXISTS search_history (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER,
    session_id TEXT,
    keyword TEXT NOT NULL,
    searched_at TEXT DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (session_id) REFERENCES guest_sessions(session_id)
);

-- Table: user_addresses
DROP TABLE IF EXISTS user_addresses;
CREATE TABLE IF NOT EXISTS user_addresses (
    address_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    label TEXT,
    recipient_name TEXT,
    phone TEXT,
    address_line TEXT,
    city TEXT,
    district TEXT,
    ward TEXT,
    is_default INTEGER DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);
INSERT INTO user_addresses (address_id, user_id, label, recipient_name, phone, address_line, city, district, ward, is_default) VALUES (1, 1, 'Nhà', 'Nguyễn Thảo Nguyên', '901234567', '123 Đường Láng', 'Hà Nội', 'Đống Đa', 'Láng Thượng', 1);
INSERT INTO user_addresses (address_id, user_id, label, recipient_name, phone, address_line, city, district, ward, is_default) VALUES (2, 1, 'Công ty', 'Nguyễn Thảo Nguyên', '901234567', '456 Nguyễn Trãi', 'Hà Nội', 'Thanh Xuân', 'Thanh Xuân Trung', 0);
INSERT INTO user_addresses (address_id, user_id, label, recipient_name, phone, address_line, city, district, ward, is_default) VALUES (3, 2, 'Nhà', 'Trần Minh Anh', '902345678', '789 Lê Lợi', 'TP. HCM', 'Quận 1', 'Bến Nghé', 1);
INSERT INTO user_addresses (address_id, user_id, label, recipient_name, phone, address_line, city, district, ward, is_default) VALUES (4, 2, 'Công ty', 'Trần Minh Anh', '902345678', '101 Võ Văn Kiệt', 'TP. HCM', 'Quận 5', 'Phường 2', 0);
INSERT INTO user_addresses (address_id, user_id, label, recipient_name, phone, address_line, city, district, ward, is_default) VALUES (5, 3, 'Nhà', 'Lê Bảo Ngọc', '903456789', '22 Hùng Vương', 'Đà Nẵng', 'Hải Châu', 'Hải Châu 1', 1);
INSERT INTO user_addresses (address_id, user_id, label, recipient_name, phone, address_line, city, district, ward, is_default) VALUES (6, 4, 'Nhà', 'Phạm Quỳnh Hương', '904567890', '33 Lý Tự Trọng', 'Cần Thơ', 'Ninh Kiều', 'Tân An', 1);
INSERT INTO user_addresses (address_id, user_id, label, recipient_name, phone, address_line, city, district, ward, is_default) VALUES (7, 5, 'Nhà', 'Hoàng Tuấn Anh', '905678901', '44 Nguyễn Huệ', 'Nha Trang', 'Lộc Thọ', 'Phước Tân', 1);
INSERT INTO user_addresses (address_id, user_id, label, recipient_name, phone, address_line, city, district, ward, is_default) VALUES (8, 6, 'Nhà', 'Vũ Lan Chi', '906789012', '55 Trần Phú', 'Huế', 'Phú Nhuận', 'Trường An', 1);
INSERT INTO user_addresses (address_id, user_id, label, recipient_name, phone, address_line, city, district, ward, is_default) VALUES (9, 7, 'Nhà', 'Đặng Hoàng Nam', '907890123', '66 Điện Biên Phủ', 'Hải Phòng', 'Ngô Quyền', 'Cầu Đất', 1);
INSERT INTO user_addresses (address_id, user_id, label, recipient_name, phone, address_line, city, district, ward, is_default) VALUES (10, 8, 'Nhà', 'Bùi Thanh Huyền', '908901234', '77 Nguyễn Du', 'Buôn Ma Thuột', 'Thắng Lợi', 'Tân Lợi', 1);

-- Table: user_vouchers
DROP TABLE IF EXISTS user_vouchers;
CREATE TABLE IF NOT EXISTS user_vouchers (
    user_id INTEGER NOT NULL,
    voucher_id INTEGER NOT NULL,
    used_at TEXT,
    assigned_at TEXT DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, voucher_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (voucher_id) REFERENCES vouchers(voucher_id)
);
INSERT INTO user_vouchers (user_id, voucher_id, used_at, assigned_at) VALUES (1, 1, NULL, '2025-01-02 10:00:00');
INSERT INTO user_vouchers (user_id, voucher_id, used_at, assigned_at) VALUES (2, 2, '2025-01-05 14:00:00', '2025-01-03 09:00:00');
INSERT INTO user_vouchers (user_id, voucher_id, used_at, assigned_at) VALUES (3, 3, NULL, '2025-01-10 08:00:00');
INSERT INTO user_vouchers (user_id, voucher_id, used_at, assigned_at) VALUES (4, 4, '2025-01-15 20:00:00', '2025-01-12 12:00:00');
INSERT INTO user_vouchers (user_id, voucher_id, used_at, assigned_at) VALUES (5, 5, NULL, '2025-01-16 09:00:00');
INSERT INTO user_vouchers (user_id, voucher_id, used_at, assigned_at) VALUES (6, 1, '2025-01-20 18:00:00', '2025-01-05 11:00:00');
INSERT INTO user_vouchers (user_id, voucher_id, used_at, assigned_at) VALUES (7, 2, NULL, '2025-01-08 14:00:00');
INSERT INTO user_vouchers (user_id, voucher_id, used_at, assigned_at) VALUES (8, 3, NULL, '2025-01-18 16:00:00');
INSERT INTO user_vouchers (user_id, voucher_id, used_at, assigned_at) VALUES (9, 4, NULL, '2025-01-22 10:00:00');
INSERT INTO user_vouchers (user_id, voucher_id, used_at, assigned_at) VALUES (10, 5, '2025-01-25 09:00:00', '2025-01-17 15:00:00');
INSERT INTO user_vouchers (user_id, voucher_id, used_at, assigned_at) VALUES (1, 6, NULL, '2025-01-15 10:00:00');
INSERT INTO user_vouchers (user_id, voucher_id, used_at, assigned_at) VALUES (2, 7, '2025-01-18 11:00:00', '2025-01-06 09:00:00');
INSERT INTO user_vouchers (user_id, voucher_id, used_at, assigned_at) VALUES (3, 8, NULL, '2025-01-20 12:00:00');
INSERT INTO user_vouchers (user_id, voucher_id, used_at, assigned_at) VALUES (4, 9, NULL, '2025-01-25 08:00:00');
INSERT INTO user_vouchers (user_id, voucher_id, used_at, assigned_at) VALUES (5, 10, '2025-01-28 14:00:00', '2025-01-10 15:00:00');

-- Table: users
DROP TABLE IF EXISTS users;
CREATE TABLE IF NOT EXISTS "users" (
    user_id INTEGER PRIMARY KEY AUTOINCREMENT,
    full_name TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    phone_number TEXT,
    avatar_url TEXT,
    bio TEXT,
    join_date TEXT,
    role TEXT DEFAULT 'user',
    status TEXT DEFAULT 'active',
    last_login TEXT,
    birth_date TEXT,
    gender TEXT,
    skin_type TEXT,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    updated_at TEXT DEFAULT CURRENT_TIMESTAMP
);
INSERT INTO users (user_id, full_name, email, password_hash, phone_number, avatar_url, bio, join_date, role, status, last_login, birth_date, gender, skin_type, created_at, updated_at) VALUES (1, 'Nguyễn Thảo Nguyên', 'nguyen@example.com', 'nguyenkhung1802', '901234567', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1779803535/an_bnlp0l.jpg', 'Yêu thích makeup', '2025-01-01 00:00:00', 'user', 'active', '2025-01-20 10:00:00', '1995-05-10 00:00:00', 'female', 'combination', '2025-01-01 00:00:00', '2025-01-20 10:00:00');
INSERT INTO users (user_id, full_name, email, password_hash, phone_number, avatar_url, bio, join_date, role, status, last_login, birth_date, gender, skin_type, created_at, updated_at) VALUES (2, 'Trần Minh Anh', 'anh.tran@example.com', 'anhtran1802', '902345678', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1779803534/binh_blilfz.jpg', 'Mê son môi', '2025-01-02 00:00:00', 'user', 'active', '2025-01-21 14:30:00', '1998-08-22 00:00:00', 'female', 'oily', '2025-01-02 00:00:00', '2025-01-21 14:30:00');
INSERT INTO users (user_id, full_name, email, password_hash, phone_number, avatar_url, bio, join_date, role, status, last_login, birth_date, gender, skin_type, created_at, updated_at) VALUES (3, 'Lê Bảo Ngọc', 'ngoc.le@example.com', 'ngocle1802', '903456789', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1779803534/hung_jvvgug.jpg', 'Thích phấn mắt', '2025-01-03 00:00:00', 'user', 'active', '2025-01-19 09:15:00', '2000-12-01 00:00:00', 'female', 'normal', '2025-01-03 00:00:00', '2025-01-19 09:15:00');
INSERT INTO users (user_id, full_name, email, password_hash, phone_number, avatar_url, bio, join_date, role, status, last_login, birth_date, gender, skin_type, created_at, updated_at) VALUES (4, 'Phạm Quỳnh Hương', 'huong.pham@example.com', 'huongpham1802', '904567890', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1779803534/ctydulichhalong_fbmsy6.jpg', 'Newbie', '2025-01-04 00:00:00', 'user', 'active', '2025-01-18 18:00:00', '2002-03-15 00:00:00', 'female', 'dry', '2025-01-04 00:00:00', '2025-01-18 18:00:00');
INSERT INTO users (user_id, full_name, email, password_hash, phone_number, avatar_url, bio, join_date, role, status, last_login, birth_date, gender, skin_type, created_at, updated_at) VALUES (5, 'Hoàng Tuấn Anh', 'tuananh@example.com', 'anhhoang1802', '905678901', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1779803534/hoa_ibecs8.jpg', 'Thích skincare', '2025-01-05 00:00:00', 'user', 'active', '2025-01-22 11:20:00', '1993-07-19 00:00:00', 'male', 'oily', '2025-01-05 00:00:00', '2025-01-22 11:20:00');
INSERT INTO users (user_id, full_name, email, password_hash, phone_number, avatar_url, bio, join_date, role, status, last_login, birth_date, gender, skin_type, created_at, updated_at) VALUES (6, 'Vũ Lan Chi', 'chi.vu@example.com', 'chivu1802', '906789012', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1779803534/lan_nycuvs.jpg', 'Chuyên gia review', '2025-01-06 00:00:00', 'user', 'active', '2025-01-23 16:45:00', '1997-11-02 00:00:00', 'female', 'sensitive', '2025-01-06 00:00:00', '2025-01-23 16:45:00');
INSERT INTO users (user_id, full_name, email, password_hash, phone_number, avatar_url, bio, join_date, role, status, last_login, birth_date, gender, skin_type, created_at, updated_at) VALUES (7, 'Đặng Hoàng Nam', 'nam.dang@example.com', 'namdang1802', '907890123', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1779803534/cuong_ibjfdf.jpg', 'Thích màu nude', '2025-01-07 00:00:00', 'user', 'active', '2025-01-17 08:30:00', '1996-04-25 00:00:00', 'male', 'normal', '2025-01-07 00:00:00', '2025-01-17 08:30:00');
INSERT INTO users (user_id, full_name, email, password_hash, phone_number, avatar_url, bio, join_date, role, status, last_login, birth_date, gender, skin_type, created_at, updated_at) VALUES (8, 'Bùi Thanh Huyền', 'huyen.bui@example.com', 'huyenbui1802', '908901234', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1779803533/phuong_abwihc.jpg', 'Đam mê làm đẹp', '2025-01-08 00:00:00', 'user', 'active', '2025-01-24 20:00:00', '1999-09-09 00:00:00', 'female', 'combination', '2025-01-08 00:00:00', '2025-01-24 20:00:00');
INSERT INTO users (user_id, full_name, email, password_hash, phone_number, avatar_url, bio, join_date, role, status, last_login, birth_date, gender, skin_type, created_at, updated_at) VALUES (9, 'Ngô Minh Khôi', 'khoi.ngo@example.com', 'khoingo1802', '909012345', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1779803533/quyen_i2eylz.jpg', 'Thích thử đồ mới', '2025-01-09 00:00:00', 'admin', 'active', '2025-01-25 12:00:00', '1991-12-30 00:00:00', 'male', 'normal', '2025-01-09 00:00:00', '2025-01-25 12:00:00');
INSERT INTO users (user_id, full_name, email, password_hash, phone_number, avatar_url, bio, join_date, role, status, last_login, birth_date, gender, skin_type, created_at, updated_at) VALUES (10, 'Trịnh Yến Nhi', 'nhi.trinh@example.com', 'yentrinh1802', '910123456', 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1779803533/ngoc_rj5aqq.jpg', 'Thích AI làm đẹp', '2025-01-10 00:00:00', 'user', 'active', '2025-01-26 09:30:00', '2001-05-20 00:00:00', 'female', 'dry', '2025-01-10 00:00:00', '2025-01-26 09:30:00');

-- Table: vouchers
DROP TABLE IF EXISTS vouchers;
CREATE TABLE IF NOT EXISTS vouchers (
    voucher_id INTEGER PRIMARY KEY AUTOINCREMENT,
    code TEXT UNIQUE,
    discount_type TEXT NOT NULL,
    discount_value REAL,
    min_order_amount REAL,
    max_discount REAL,
    start_date TEXT,
    end_date TEXT,
    usage_limit INTEGER DEFAULT 1,
    used_count INTEGER DEFAULT 0,
    is_active INTEGER DEFAULT 1
);
INSERT INTO vouchers (voucher_id, code, discount_type, discount_value, min_order_amount, max_discount, start_date, end_date, usage_limit, used_count, is_active) VALUES (1, 'WELCOME20', 'percent', 20.0, 0.0, 1250000.0, '2025-01-01', '2025-12-31', 1, 0, 1);
INSERT INTO vouchers (voucher_id, code, discount_type, discount_value, min_order_amount, max_discount, start_date, end_date, usage_limit, used_count, is_active) VALUES (2, 'FREESHIP25', 'fixed', 625000.0, 5000000.0, 625000.0, '2025-01-01', '2025-03-31', 100, 23, 1);
INSERT INTO vouchers (voucher_id, code, discount_type, discount_value, min_order_amount, max_discount, start_date, end_date, usage_limit, used_count, is_active) VALUES (3, 'UNICORN15', 'percent', 15.0, 7500000.0, 2500000.0, '2025-01-10', '2025-02-10', 50, 12, 1);
INSERT INTO vouchers (voucher_id, code, discount_type, discount_value, min_order_amount, max_discount, start_date, end_date, usage_limit, used_count, is_active) VALUES (4, 'MEMBER30', 'percent', 30.0, 12500000.0, 3750000.0, '2025-02-01', '2025-02-28', 30, 5, 1);
INSERT INTO vouchers (voucher_id, code, discount_type, discount_value, min_order_amount, max_discount, start_date, end_date, usage_limit, used_count, is_active) VALUES (5, 'FLASH10', 'fixed', 250000.0, 2500000.0, 250000.0, '2025-01-15', '2025-01-20', 200, 67, 1);
INSERT INTO vouchers (voucher_id, code, discount_type, discount_value, min_order_amount, max_discount, start_date, end_date, usage_limit, used_count, is_active) VALUES (6, 'VNPAY15', 'percent', 15.0, 3750000.0, 1000000.0, '2025-01-01', '2025-01-31', 500, 142, 1);
INSERT INTO vouchers (voucher_id, code, discount_type, discount_value, min_order_amount, max_discount, start_date, end_date, usage_limit, used_count, is_active) VALUES (7, 'BEARY50', 'fixed', 1250000.0, 8750000.0, 1250000.0, '2025-01-05', '2025-01-25', 20, 8, 1);
INSERT INTO vouchers (voucher_id, code, discount_type, discount_value, min_order_amount, max_discount, start_date, end_date, usage_limit, used_count, is_active) VALUES (8, 'NEWYEAR', 'percent', 12.0, 0.0, 750000.0, '2025-01-01', '2025-01-31', 0, 0, 0);
INSERT INTO vouchers (voucher_id, code, discount_type, discount_value, min_order_amount, max_discount, start_date, end_date, usage_limit, used_count, is_active) VALUES (9, 'SHIPFREE', 'fixed', 500000.0, 3750000.0, 500000.0, '2025-02-01', '2025-12-31', 1000, 0, 1);
INSERT INTO vouchers (voucher_id, code, discount_type, discount_value, min_order_amount, max_discount, start_date, end_date, usage_limit, used_count, is_active) VALUES (10, 'LUCKY10', 'fixed', 250000.0, 1250000.0, 250000.0, '2025-01-01', '2025-12-31', 500, 78, 1);

-- Table: wishlists
DROP TABLE IF EXISTS wishlists;
CREATE TABLE IF NOT EXISTS wishlists (
    wishlist_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    product_id INTEGER NOT NULL,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, product_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
);
INSERT INTO wishlists (wishlist_id, user_id, product_id, created_at) VALUES (1, 1, 2, '2025-01-12 08:00:00');
INSERT INTO wishlists (wishlist_id, user_id, product_id, created_at) VALUES (2, 1, 7, '2025-01-13 09:00:00');
INSERT INTO wishlists (wishlist_id, user_id, product_id, created_at) VALUES (3, 2, 1, '2025-01-14 10:00:00');
INSERT INTO wishlists (wishlist_id, user_id, product_id, created_at) VALUES (4, 2, 10, '2025-01-15 11:00:00');
INSERT INTO wishlists (wishlist_id, user_id, product_id, created_at) VALUES (5, 3, 22, '2025-01-16 12:00:00');
INSERT INTO wishlists (wishlist_id, user_id, product_id, created_at) VALUES (6, 4, 5, '2025-01-17 13:00:00');
INSERT INTO wishlists (wishlist_id, user_id, product_id, created_at) VALUES (7, 5, 16, '2025-01-18 14:00:00');
INSERT INTO wishlists (wishlist_id, user_id, product_id, created_at) VALUES (8, 6, 25, '2025-01-19 15:00:00');
INSERT INTO wishlists (wishlist_id, user_id, product_id, created_at) VALUES (9, 7, 3, '2025-01-20 16:00:00');
INSERT INTO wishlists (wishlist_id, user_id, product_id, created_at) VALUES (10, 8, 11, '2025-01-21 17:00:00');
INSERT INTO wishlists (wishlist_id, user_id, product_id, created_at) VALUES (11, 9, 18, '2025-01-22 18:00:00');
INSERT INTO wishlists (wishlist_id, user_id, product_id, created_at) VALUES (12, 10, 1, '2025-01-23 19:00:00');
INSERT INTO wishlists (wishlist_id, user_id, product_id, created_at) VALUES (13, 10, 4, '2025-01-24 20:00:00');
INSERT INTO wishlists (wishlist_id, user_id, product_id, created_at) VALUES (14, 2, 12, '2025-01-25 21:00:00');
INSERT INTO wishlists (wishlist_id, user_id, product_id, created_at) VALUES (15, 3, 8, '2025-01-26 22:00:00');

-- Index: idx_reviews_product
DROP INDEX IF EXISTS idx_reviews_product;
CREATE INDEX IF NOT EXISTS idx_reviews_product ON product_reviews(product_id);

-- Trigger: carts_check_trigger
DROP TRIGGER IF EXISTS carts_check_trigger;
CREATE TRIGGER IF NOT EXISTS carts_check_trigger
BEFORE INSERT ON carts
FOR EACH ROW
BEGIN
    SELECT CASE
        WHEN NEW.user_id IS NULL AND NEW.session_id IS NULL
        THEN RAISE(ABORT, 'Either user_id or session_id must be not null')
    END;
END;

COMMIT TRANSACTION;
PRAGMA foreign_keys = on;
