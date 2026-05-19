#!/bin/sh
set -eu

APP_PASSWORD="${WCMS_MARIADB_APP_PASSWORD:-wcms_app_password}"

mariadb -uroot -p"${MARIADB_ROOT_PASSWORD}" <<SQL
CREATE DATABASE IF NOT EXISTS wcms_auth CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS wcms_user CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS wcms_organization CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS wcms_file CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS wcms_content CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS wcms_device CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS wcms_schedule CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'wcms_auth'@'%' IDENTIFIED BY '${APP_PASSWORD}';
CREATE USER IF NOT EXISTS 'wcms_user'@'%' IDENTIFIED BY '${APP_PASSWORD}';
CREATE USER IF NOT EXISTS 'wcms_organization'@'%' IDENTIFIED BY '${APP_PASSWORD}';
CREATE USER IF NOT EXISTS 'wcms_file'@'%' IDENTIFIED BY '${APP_PASSWORD}';
CREATE USER IF NOT EXISTS 'wcms_content'@'%' IDENTIFIED BY '${APP_PASSWORD}';
CREATE USER IF NOT EXISTS 'wcms_device'@'%' IDENTIFIED BY '${APP_PASSWORD}';
CREATE USER IF NOT EXISTS 'wcms_schedule'@'%' IDENTIFIED BY '${APP_PASSWORD}';

GRANT ALL PRIVILEGES ON wcms_auth.* TO 'wcms_auth'@'%';
GRANT ALL PRIVILEGES ON wcms_user.* TO 'wcms_user'@'%';
GRANT ALL PRIVILEGES ON wcms_organization.* TO 'wcms_organization'@'%';
GRANT ALL PRIVILEGES ON wcms_file.* TO 'wcms_file'@'%';
GRANT ALL PRIVILEGES ON wcms_content.* TO 'wcms_content'@'%';
GRANT ALL PRIVILEGES ON wcms_device.* TO 'wcms_device'@'%';
GRANT ALL PRIVILEGES ON wcms_schedule.* TO 'wcms_schedule'@'%';

FLUSH PRIVILEGES;
SQL
