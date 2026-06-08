CREATE TABLE user_account (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    display_name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    admin BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE cv (
    id BIGSERIAL PRIMARY KEY,
    owner_user_id BIGINT NOT NULL REFERENCES user_account(id),
    title VARCHAR(255) NOT NULL,
    uploaded_html_file_path VARCHAR(500) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_cv_owner_user_id ON cv(owner_user_id);

CREATE TABLE app_setting (
    setting_key VARCHAR(100) PRIMARY KEY,
    setting_value TEXT NOT NULL,
    description VARCHAR(500)
);

CREATE TABLE ai_suggestion (
    id BIGSERIAL PRIMARY KEY,
    cv_id BIGINT NOT NULL REFERENCES cv(id),
    action_type VARCHAR(100) NOT NULL,
    original_text TEXT NOT NULL,
    suggested_text TEXT NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_ai_suggestion_cv_id ON ai_suggestion(cv_id);
