USE wcms_auth;

INSERT INTO auth_accounts (
    id,
    username,
    email,
    password_hash,
    role,
    status,
    password_change_required,
    token_version,
    failed_login_count,
    locked_until,
    last_login_at,
    created_at,
    updated_at
) VALUES (
    UNHEX(REPLACE('00000000-0000-4000-8000-000000000001', '-', '')),
    'platform-admin',
    'platform-admin@wcms.local',
    '$2a$10$SkPZeTo6hdwyry7vOAidPuh9SHHwi5aMLiszS7VngxTUAlwXpuxIm',
    'SUPER_ADMIN',
    'ACTIVE',
    TRUE,
    0,
    0,
    NULL,
    NULL,
    CURRENT_TIMESTAMP(6),
    CURRENT_TIMESTAMP(6)
) ON DUPLICATE KEY UPDATE
    email = VALUES(email),
    password_hash = VALUES(password_hash),
    role = VALUES(role),
    status = VALUES(status),
    password_change_required = VALUES(password_change_required),
    token_version = VALUES(token_version),
    failed_login_count = VALUES(failed_login_count),
    locked_until = VALUES(locked_until),
    updated_at = CURRENT_TIMESTAMP(6);
