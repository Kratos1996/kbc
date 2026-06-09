-- Initial DB settings. Runs only on first container start.
-- The kbc database and user are created by MYSQL_DATABASE / MYSQL_USER env vars.

-- Ensure full UTF-8 and Indian timezone
SET NAMES utf8mb4;
SET time_zone = '+05:30';

-- (Optional) extensions placeholder for future use
-- CREATE EXTENSION IF NOT EXISTS ...;
