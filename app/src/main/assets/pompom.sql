--
-- File generated with SQLiteStudio v3.4.21 on Sun Jun 14 14:12:02 2026
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
INSERT INTO cart_items (cart_item_id, cart_id, product_id, variant_id, quantity, unit_price) VALUES (1, 1, 1, NULL, 1, 39.9);
INSERT INTO cart_items (cart_item_id, cart_id, product_id, variant_id, quantity, unit_price) VALUES (2, 1, 2, NULL, 2, 12.9);
INSERT INTO cart_items (cart_item_id, cart_id, product_id, variant_id, quantity, unit_price) VALUES (3, 2, 3, 1, 1, 22.0);
INSERT INTO cart_items (cart_item_id, cart_id, product_id, variant_id, quantity, unit_price) VALUES (4, 2, 5, NULL, 1, 7.0);
INSERT INTO cart_items (cart_item_id, cart_id, product_id, variant_id, quantity, unit_price) VALUES (5, 3, 10, NULL, 1, 29.9);
INSERT INTO cart_items (cart_item_id, cart_id, product_id, variant_id, quantity, unit_price) VALUES (6, 3, 4, 4, 2, 10.0);
INSERT INTO cart_items (cart_item_id, cart_id, product_id, variant_id, quantity, unit_price) VALUES (7, 4, 7, NULL, 1, 15.0);
INSERT INTO cart_items (cart_item_id, cart_id, product_id, variant_id, quantity, unit_price) VALUES (8, 4, 12, 6, 1, 12.0);
INSERT INTO cart_items (cart_item_id, cart_id, product_id, variant_id, quantity, unit_price) VALUES (9, 5, 15, NULL, 1, 8.0);
INSERT INTO cart_items (cart_item_id, cart_id, product_id, variant_id, quantity, unit_price) VALUES (10, 5, 18, NULL, 1, 17.0);

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
INSERT INTO community_posts (post_id, user_id, content, images, product_tag, like_count, comment_count, is_hidden, created_at, post_type) VALUES (1, 1, 'Thích quá trời bảng Unicorn Magic, lên màu cực xinh!', '', 1, 15, 3, 0, '2025-01-10 10:30:00', 'review');
INSERT INTO community_posts (post_id, user_id, content, images, product_tag, like_count, comment_count, is_hidden, created_at, post_type) VALUES (2, 2, 'Son Velvet Rose mướt, không khô môi. Recommend nha.', '', 2, 22, 5, 0, '2025-01-11 14:20:00', 'review');
INSERT INTO community_posts (post_id, user_id, content, images, product_tag, like_count, comment_count, is_hidden, created_at, post_type) VALUES (3, 3, 'Cushion Cloud che phủ tốt, da mình khô vẫn mịn.', '', 3, 18, 2, 0, '2025-01-12 09:15:00', 'review');
INSERT INTO community_posts (post_id, user_id, content, images, product_tag, like_count, comment_count, is_hidden, created_at, post_type) VALUES (4, 4, 'Má hồng Bunny Blush dễ thương nhưng hơi nhạt.', '', 4, 8, 1, 0, '2025-01-13 18:00:00', 'review');
INSERT INTO community_posts (post_id, user_id, content, images, product_tag, like_count, comment_count, is_hidden, created_at, post_type) VALUES (5, 5, 'Eyeliner Starry kẻ một đường là chuẩn, không lem.', '', 5, 12, 0, 0, '2025-01-14 12:00:00', 'review');
INSERT INTO community_posts (post_id, user_id, content, images, product_tag, like_count, comment_count, is_hidden, created_at, post_type) VALUES (6, 6, 'Set Sweet Bear làm quà tặng sinh nhật bạn thân, cô ấy thích lắm!', '', 10, 34, 7, 0, '2025-01-15 20:30:00', 'review');
INSERT INTO community_posts (post_id, user_id, content, images, product_tag, like_count, comment_count, is_hidden, created_at, post_type) VALUES (7, 7, 'Highlight Butterfly lên da căng bóng tự nhiên.', '', 7, 25, 4, 0, '2025-01-16 08:45:00', 'review');
INSERT INTO community_posts (post_id, user_id, content, images, product_tag, like_count, comment_count, is_hidden, created_at, post_type) VALUES (8, 8, 'Kẹp tóc Unicorn dễ thương, nhưng hơi yếu.', '', 8, 6, 2, 0, '2025-01-17 17:20:00', 'review');
INSERT INTO community_posts (post_id, user_id, content, images, product_tag, like_count, comment_count, is_hidden, created_at, post_type) VALUES (9, 9, 'Bộ cọ Heart mềm, không rụng lông.', '', 18, 10, 1, 0, '2025-01-18 10:00:00', 'review');
INSERT INTO community_posts (post_id, user_id, content, images, product_tag, like_count, comment_count, is_hidden, created_at, post_type) VALUES (10, 10, 'Mình thử AI Makeup Artist, kết quả shock luôn, hợp lắm.', '', NULL, 45, 12, 0, '2025-01-19 22:10:00', 'review');
INSERT INTO community_posts (post_id, user_id, content, images, product_tag, like_count, comment_count, is_hidden, created_at, post_type) VALUES (11, 1, 'Son Cherry Lip Tint lâu trôi nhưng hơi khó tán.', '', 15, 14, 3, 0, '2025-01-20 14:00:00', 'review');
INSERT INTO community_posts (post_id, user_id, content, images, product_tag, like_count, comment_count, is_hidden, created_at, post_type) VALUES (12, 2, 'Bảng Galaxy Eyeshadow nhũ đẹp mê hồn.', '', 22, 28, 6, 0, '2025-01-21 09:30:00', 'review');
INSERT INTO community_posts (post_id, user_id, content, images, product_tag, like_count, comment_count, is_hidden, created_at, post_type) VALUES (13, 3, 'Hướng dẫn makeup bằng PomPom set đây! Ai cần không?', '', 25, 52, 14, 0, '2025-01-22 16:00:00', 'review');
INSERT INTO community_posts (post_id, user_id, content, images, product_tag, like_count, comment_count, is_hidden, created_at, post_type) VALUES (14, 4, 'Xịt khóa lớp Angel Setting giữ được 8 tiếng.', '', 14, 11, 2, 0, '2025-01-23 11:15:00', 'review');
INSERT INTO community_posts (post_id, user_id, content, images, product_tag, like_count, comment_count, is_hidden, created_at, post_type) VALUES (15, 5, 'Highlight Mermaid lên chân mày cũng đẹp.', '', 16, 9, 1, 0, '2025-01-24 19:40:00', 'review');
INSERT INTO community_posts (post_id, user_id, content, images, product_tag, like_count, comment_count, is_hidden, created_at, post_type) VALUES (16, 6, 'Phấn phủ Peach Blur kiềm dầu tốt, da lì nhẹ.', '', 17, 17, 3, 0, '2025-01-25 13:00:00', 'review');
INSERT INTO community_posts (post_id, user_id, content, images, product_tag, like_count, comment_count, is_hidden, created_at, post_type) VALUES (17, 7, 'Son lì dạng nước Matte Liquid màu đỏ cam rất tôn da.', '', 12, 20, 4, 0, '2025-01-26 15:30:00', 'review');
INSERT INTO community_posts (post_id, user_id, content, images, product_tag, like_count, comment_count, is_hidden, created_at, post_type) VALUES (18, 8, 'Gương Cute Mirror siêu nhỏ gọn, bỏ túi được.', '', 20, 7, 1, 0, '2025-01-27 10:00:00', 'review');
INSERT INTO community_posts (post_id, user_id, content, images, product_tag, like_count, comment_count, is_hidden, created_at, post_type) VALUES (19, 9, 'Nước hoa Vanilla thơm ngọt, lưu hương 6 tiếng.', '', 19, 13, 2, 0, '2025-01-28 21:15:00', 'review');
INSERT INTO community_posts (post_id, user_id, content, images, product_tag, like_count, comment_count, is_hidden, created_at, post_type) VALUES (20, 10, 'Review từ A-Z set Ultimate Makeup, xứng đáng với giá sale.', '', 25, 38, 9, 0, '2025-01-29 08:00:00', 'review');

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
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (1, 1, 1, NULL, 1, 39.9);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (2, 1, 4, 4, 2, 10.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (3, 2, 10, NULL, 1, 29.9);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (4, 2, 5, NULL, 1, 7.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (5, 3, 3, 1, 1, 22.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (6, 4, 10, NULL, 1, 29.9);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (7, 4, 4, 5, 1, 10.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (8, 5, 7, NULL, 1, 15.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (9, 5, 15, NULL, 1, 8.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (10, 6, 18, NULL, 1, 17.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (11, 7, 1, NULL, 1, 39.9);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (12, 7, 2, NULL, 1, 12.9);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (13, 7, 22, NULL, 1, 28.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (14, 7, 14, NULL, 1, 10.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (15, 8, 12, 6, 1, 12.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (16, 8, 23, NULL, 1, 7.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (17, 9, 18, NULL, 1, 17.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (18, 9, 13, NULL, 1, 6.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (19, 10, 20, NULL, 1, 8.0);
INSERT INTO order_items (order_item_id, order_id, product_id, variant_id, quantity, price) VALUES (20, 10, 24, NULL, 1, 3.5);

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
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (1, 'PP-ORD-001', 1, NULL, 1, 39.9, 5.0, 0.0, 44.9, 'delivered', 'COD', 'paid', 'GHTK', 'GH0001', 'Gọi trước', '2025-01-10 10:00:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (2, 'PP-ORD-002', 2, NULL, 3, 29.9, 0.0, 0.0, 29.9, 'delivered', 'VISA', 'paid', 'GHN', 'GH0002', NULL, '2025-01-11 14:30:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (3, 'PP-ORD-003', NULL, 'guest_001', NULL, 22.0, 5.0, 10.0, 17.0, 'paid', 'VNPAY', 'paid', 'Viettel', 'VT001', 'Quà tặng', '2025-01-15 09:00:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (4, 'PP-ORD-004', 3, NULL, 5, 29.9, 5.0, 0.0, 34.9, 'shipping', 'COD', 'paid', 'GHTK', 'GH0004', NULL, '2025-01-18 16:00:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (5, 'PP-ORD-005', 4, NULL, 6, 15.0, 5.0, 5.0, 15.0, 'delivered', 'VISA', 'paid', 'GHN', 'GH0005', 'Để trước cửa', '2025-01-20 11:00:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (6, 'PP-ORD-006', 5, NULL, 7, 17.0, 0.0, 0.0, 17.0, 'cancelled', 'MOMO', 'failed', NULL, NULL, 'Khách hủy', '2025-01-22 19:00:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (7, 'PP-ORD-007', 6, NULL, 8, 44.9, 5.0, 0.0, 49.9, 'delivered', 'COD', 'paid', 'GHN', 'GH0007', NULL, '2025-01-24 08:30:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (8, 'PP-ORD-008', 7, NULL, 9, 12.0, 0.0, 0.0, 12.0, 'paid', 'VISA', 'paid', 'Viettel', 'VT008', NULL, '2025-01-25 13:00:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (9, 'PP-ORD-009', 8, NULL, 10, 17.0, 5.0, 2.0, 20.0, 'pending', 'COD', 'unpaid', NULL, NULL, NULL, '2025-01-26 21:00:00');
INSERT INTO orders (order_id, order_number, user_id, session_id, address_id, total_amount, shipping_fee, discount_amount, final_amount, status, payment_method, payment_status, shipping_carrier, tracking_number, note, created_at) VALUES (10, 'PP-ORD-010', 10, NULL, NULL, 8.0, 5.0, 0.0, 13.0, 'pending', 'VNPAY', 'unpaid', NULL, NULL, 'Thử đơn', '2025-01-28 10:15:00');

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
INSERT INTO payments (payment_id, order_id, transaction_id, amount, status, payment_method, paid_at) VALUES (1, 1, 'COD_TX_001', 44.9, 'success', 'COD', '2025-01-10 10:05:00');
INSERT INTO payments (payment_id, order_id, transaction_id, amount, status, payment_method, paid_at) VALUES (2, 2, 'VISA_TX_002', 29.9, 'success', 'VISA', '2025-01-11 14:32:00');
INSERT INTO payments (payment_id, order_id, transaction_id, amount, status, payment_method, paid_at) VALUES (3, 3, 'VNPAY_TX_003', 17.0, 'success', 'VNPAY', '2025-01-15 09:02:00');
INSERT INTO payments (payment_id, order_id, transaction_id, amount, status, payment_method, paid_at) VALUES (4, 4, 'COD_TX_004', 34.9, 'success', 'COD', '2025-01-18 16:05:00');
INSERT INTO payments (payment_id, order_id, transaction_id, amount, status, payment_method, paid_at) VALUES (5, 5, 'VISA_TX_005', 15.0, 'success', 'VISA', '2025-01-20 11:02:00');
INSERT INTO payments (payment_id, order_id, transaction_id, amount, status, payment_method, paid_at) VALUES (6, 6, 'MOMO_TX_006', 17.0, 'failed', 'MOMO', NULL);
INSERT INTO payments (payment_id, order_id, transaction_id, amount, status, payment_method, paid_at) VALUES (7, 7, 'COD_TX_007', 49.9, 'success', 'COD', '2025-01-24 08:35:00');
INSERT INTO payments (payment_id, order_id, transaction_id, amount, status, payment_method, paid_at) VALUES (8, 8, 'VISA_TX_008', 12.0, 'success', 'VISA', '2025-01-25 13:05:00');
INSERT INTO payments (payment_id, order_id, transaction_id, amount, status, payment_method, paid_at) VALUES (9, 9, NULL, 20.0, 'pending', 'COD', NULL);
INSERT INTO payments (payment_id, order_id, transaction_id, amount, status, payment_method, paid_at) VALUES (10, 10, NULL, 13.0, 'pending', 'VNPAY', NULL);

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
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (1, 1, 1, 1, 5, 'Bảng Unicorn Magic màu đẹp, lên chuẩn', '', 1, '2025-01-13 10:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (2, 2, 10, 2, 4, 'Set Sweet Bear dễ thương, son hơi nhạt', '', 1, '2025-01-15 14:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (3, 3, 3, 4, 5, 'Cushion mịn, che phủ tốt', '', 1, '2025-01-20 09:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (4, 4, 7, 5, 4, 'Highlight đẹp nhưng dễ vỡ', '', 1, '2025-01-22 10:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (5, 5, 18, 6, 2, 'Cọ hơi cứng, không thích lắm', '', 1, '2025-01-23 11:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (6, 6, 1, 7, 5, 'Palette này mình dùng hàng ngày', '', 1, '2025-01-26 12:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (7, 7, 22, 8, 5, 'Galaxy Eyeshadow nhũ siêu đẹp', '', 1, '2025-01-27 13:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (8, 8, 12, 9, 4, 'Son lì dạng nước màu đỏ cam rất tôn da', '', 0, '2025-01-28 14:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (9, 9, 15, 1, 5, 'Cherry tint lâu trôi, thích!', '', 0, '2025-01-29 15:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (10, 10, 2, 10, 5, 'Son Velvet Rose mềm, màu đẹp', '', 0, '2025-01-30 16:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (11, 1, 4, 1, 4, 'Má hồng Bunny dễ thương nhưng hơi phai', '', 1, '2025-01-14 17:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (12, 2, 5, 2, 5, 'Eyeliner kẻ một đường là chuẩn', '', 1, '2025-01-16 18:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (13, 3, 16, 4, 5, 'Mermaid highlighter lên da căng bóng', '', 1, '2025-01-21 19:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (14, 4, 14, 5, 5, 'Xịt khóa lớp giữ được cả ngày', '', 1, '2025-01-23 20:00:00');
INSERT INTO product_reviews (review_id, user_id, product_id, order_id, rating, comment, images, is_verified_purchase, created_at) VALUES (15, 5, 8, 6, 3, 'Kẹp tóc đẹp nhưng yếu', '', 1, '2025-01-24 21:00:00');

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
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (1, 3, 'Tone 01 – Sáng', 'PP-CU-003-L', 0.0, 50, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781356572/PP-CU-003-L_vh3nqy.webp');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (2, 3, 'Tone 02 – Trung bình', 'PP-CU-003-M', 0.0, 60, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781356571/PP-CU-003-M_ygcmie.webp');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (3, 3, 'Tone 03 – Ngăm', 'PP-CU-003-D', 0.0, 40, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781356573/PP-CU-003-D_hqvuxp.webp');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (4, 4, 'Màu Hồng Baby', 'PP-BL-004-PK', 0.0, 200, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781356570/PP-BL-004-PK_jkp86y.webp');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (5, 4, 'Màu Cam Đào', 'PP-BL-004-OR', 0.0, 200, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781356570/PP-BL-004-OR_myi3vw.webp');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (6, 12, 'Màu Đỏ Cam', 'PP-LL-012-RD', 0.0, 120, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781356570/PP-LL-012-RD_akkgi6.webp');
INSERT INTO product_variants (variant_id, product_id, variant_name, sku, additional_price, stock, image_url) VALUES (7, 12, 'Màu Hồng Nude', 'PP-LL-012-NU', 0.0, 130, 'https://res.cloudinary.com/dwu6e0ian/image/upload/v1781356569/PP-LL-012-NU_zny4ul.webp');

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
INSERT INTO vouchers (voucher_id, code, discount_type, discount_value, min_order_amount, max_discount, start_date, end_date, usage_limit, used_count, is_active) VALUES (1, 'WELCOME20', 'percent', 20.0, 0.0, 50.0, '2025-01-01 00:00:00', '2025-12-31 00:00:00', 1, 0, 1);
INSERT INTO vouchers (voucher_id, code, discount_type, discount_value, min_order_amount, max_discount, start_date, end_date, usage_limit, used_count, is_active) VALUES (2, 'FREESHIP25', 'fixed', 25.0, 200.0, 25.0, '2025-01-01 00:00:00', '2025-03-31 00:00:00', 100, 23, 1);
INSERT INTO vouchers (voucher_id, code, discount_type, discount_value, min_order_amount, max_discount, start_date, end_date, usage_limit, used_count, is_active) VALUES (3, 'UNICORN15', 'percent', 15.0, 300.0, 100.0, '2025-01-10 00:00:00', '2025-02-10 00:00:00', 50, 12, 1);
INSERT INTO vouchers (voucher_id, code, discount_type, discount_value, min_order_amount, max_discount, start_date, end_date, usage_limit, used_count, is_active) VALUES (4, 'MEMBER30', 'percent', 30.0, 500.0, 150.0, '2025-02-01 00:00:00', '2025-02-28 00:00:00', 30, 5, 1);
INSERT INTO vouchers (voucher_id, code, discount_type, discount_value, min_order_amount, max_discount, start_date, end_date, usage_limit, used_count, is_active) VALUES (5, 'FLASH10', 'fixed', 10.0, 100.0, 10.0, '2025-01-15 00:00:00', '2025-01-20 00:00:00', 200, 67, 1);
INSERT INTO vouchers (voucher_id, code, discount_type, discount_value, min_order_amount, max_discount, start_date, end_date, usage_limit, used_count, is_active) VALUES (6, 'VNPAY15', 'percent', 15.0, 150.0, 40.0, '2025-01-01 00:00:00', '2025-01-31 00:00:00', 500, 142, 1);
INSERT INTO vouchers (voucher_id, code, discount_type, discount_value, min_order_amount, max_discount, start_date, end_date, usage_limit, used_count, is_active) VALUES (7, 'BEARY50', 'fixed', 50.0, 350.0, 50.0, '2025-01-05 00:00:00', '2025-01-25 00:00:00', 20, 8, 1);
INSERT INTO vouchers (voucher_id, code, discount_type, discount_value, min_order_amount, max_discount, start_date, end_date, usage_limit, used_count, is_active) VALUES (8, 'NEWYEAR', 'percent', 12.0, 0.0, 30.0, '2025-01-01 00:00:00', '2025-01-31 00:00:00', 0, 0, 0);
INSERT INTO vouchers (voucher_id, code, discount_type, discount_value, min_order_amount, max_discount, start_date, end_date, usage_limit, used_count, is_active) VALUES (9, 'SHIPFREE', 'fixed', 20.0, 150.0, 20.0, '2025-02-01 00:00:00', '2025-12-31 00:00:00', 1000, 0, 1);
INSERT INTO vouchers (voucher_id, code, discount_type, discount_value, min_order_amount, max_discount, start_date, end_date, usage_limit, used_count, is_active) VALUES (10, 'LUCKY10', 'fixed', 10.0, 50.0, 10.0, '2025-01-01 00:00:00', '2025-12-31 00:00:00', 500, 78, 1);

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
