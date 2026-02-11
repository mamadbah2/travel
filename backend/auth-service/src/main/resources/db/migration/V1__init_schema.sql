-- V1__init_schema.sql

-- Enable pgcrypto for gen_random_uuid() if not already available
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Initial database schema for auth-service

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'MANAGER', 'TRAVELER')),
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'BANNED', 'PENDING_VERIFICATION')),
    performance_score FLOAT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    last_login_at TIMESTAMP
);

-- Create index on email for faster lookups
CREATE INDEX IF NOT EXISTS idx_user_email ON users(email);

-- Create index on role for filtering
CREATE INDEX IF NOT EXISTS idx_user_role ON users(role);

-- Create index on status for filtering
CREATE INDEX IF NOT EXISTS idx_user_status ON users(status);

-- Refresh tokens table
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    token VARCHAR(500) NOT NULL UNIQUE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    expiry_date TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE
);

-- Create index on token for faster lookups
CREATE INDEX IF NOT EXISTS idx_refresh_token ON refresh_tokens(token);

-- Create index on user_id for cascading operations
CREATE INDEX IF NOT EXISTS idx_refresh_token_user ON refresh_tokens(user_id);

-- Insert default admin user (password: Admin@123456)
-- BCrypt hash for 'Admin@123456'
INSERT INTO users (id, email, password, first_name, last_name, role, status)
VALUES (
    gen_random_uuid(),
    'admin@travel.sn',
    '$2a$10$8h/hLGFPGCLf1CJw8/nVxeK4XeZP8dHgGMHfNkiB3FKRJ9rPJHH7G',
    'System',
    'Administrator',
    'ADMIN',
    'ACTIVE'
) ON CONFLICT (email) DO NOTHING;
