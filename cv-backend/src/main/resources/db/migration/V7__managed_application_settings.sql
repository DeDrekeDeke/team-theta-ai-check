INSERT INTO app_setting (setting_key, setting_value, description)
VALUES
    ('application.displayName', 'CV Manager', 'Display name shown in the application UI.')
ON CONFLICT (setting_key) DO UPDATE
SET description = EXCLUDED.description;

INSERT INTO app_setting (setting_key, setting_value, description)
VALUES
    (
        'ai.toolsetEnabled',
        COALESCE((SELECT setting_value FROM app_setting WHERE setting_key = 'ai.mockProviderEnabled'), 'true'),
        'Controls whether AI-assisted CV actions can run.'
    )
ON CONFLICT (setting_key) DO UPDATE
SET description = EXCLUDED.description;

DELETE FROM app_setting
WHERE setting_key IN ('ai.mockProviderEnabled', 'admin.email');
