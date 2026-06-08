INSERT INTO user_account (id, email, display_name, password, admin, created_at)
VALUES
    (1, 'admin@example.com', 'Admin User', 'admin123', TRUE, CURRENT_TIMESTAMP),
    (2, 'alice@example.com', 'Alice Student', 'user123', FALSE, CURRENT_TIMESTAMP),
    (3, 'bob@example.com', 'Bob Student', 'user123', FALSE, CURRENT_TIMESTAMP);

INSERT INTO cv (id, owner_user_id, title, uploaded_html_file_path, created_at, updated_at)
VALUES
    (1, 2, 'Alice Demo CV', 'uploads/alice-cv.html', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 3, 'Bob Demo CV', 'uploads/bob-cv.html', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO app_setting (setting_key, setting_value, description)
VALUES
    ('application.displayName', 'CV Manager', 'Display name shown by clients.'),
    ('ai.mockProviderEnabled', 'true', 'Whether the starter app uses mock AI suggestions.'),
    ('admin.email', 'admin@example.com', 'Simple AS-IS admin indicator.');

SELECT setval(pg_get_serial_sequence('user_account', 'id'), (SELECT MAX(id) FROM user_account));
SELECT setval(pg_get_serial_sequence('cv', 'id'), (SELECT MAX(id) FROM cv));
