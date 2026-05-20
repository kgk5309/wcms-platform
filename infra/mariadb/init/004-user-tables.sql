USE wcms_user;

CREATE TABLE IF NOT EXISTS user_profiles (
    id BINARY(16) NOT NULL,
    auth_account_id BINARY(16) NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone_number VARCHAR(40) NULL,
    role VARCHAR(40) NOT NULL,
    scope_type VARCHAR(20) NOT NULL,
    tenant_id BINARY(16) NULL,
    client_id BINARY(16) NULL,
    status VARCHAR(20) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_profiles_auth_account_id (auth_account_id),
    UNIQUE KEY uk_user_profiles_email (email),
    KEY idx_user_profiles_role (role),
    KEY idx_user_profiles_scope (scope_type, tenant_id, client_id),
    KEY idx_user_profiles_status (status)
);
