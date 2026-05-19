USE wcms_auth;

CREATE TABLE IF NOT EXISTS auth_accounts (
    id BINARY(16) NOT NULL,
    username VARCHAR(80) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(40) NOT NULL,
    status VARCHAR(20) NOT NULL,
    password_change_required BIT NOT NULL,
    token_version BIGINT NOT NULL,
    failed_login_count INT NOT NULL,
    locked_until DATETIME(6) NULL,
    last_login_at DATETIME(6) NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_auth_accounts_username (username),
    UNIQUE KEY uk_auth_accounts_email (email)
);

CREATE TABLE IF NOT EXISTS refresh_token_sessions (
    id BINARY(16) NOT NULL,
    account_id BINARY(16) NOT NULL,
    token_hash VARCHAR(128) NOT NULL,
    expires_at DATETIME(6) NOT NULL,
    revoked_at DATETIME(6) NULL,
    created_by_ip VARCHAR(64) NULL,
    user_agent VARCHAR(512) NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_refresh_token_sessions_token_hash (token_hash),
    KEY idx_refresh_token_sessions_account_id (account_id),
    KEY idx_refresh_token_sessions_expires_at (expires_at)
);
