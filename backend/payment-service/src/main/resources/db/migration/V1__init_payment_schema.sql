-- V1__init_payment_schema.sql
-- Initial database schema for payment-service

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Payments table
CREATE TABLE IF NOT EXISTS payments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    subscription_id UUID NOT NULL UNIQUE,
    travel_id UUID NOT NULL,
    traveler_id UUID NOT NULL,
    travel_title VARCHAR(255),
    amount DOUBLE PRECISION NOT NULL,
    currency VARCHAR(10) NOT NULL DEFAULT 'XOF',
    method VARCHAR(20) NOT NULL DEFAULT 'SIMULATED' CHECK (method IN ('STRIPE', 'PAYPAL', 'WAVE', 'SIMULATED')),
    transaction_id VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'SUCCESS', 'FAILED')),
    failure_reason VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_payment_subscription ON payments(subscription_id);
CREATE INDEX IF NOT EXISTS idx_payment_traveler ON payments(traveler_id);
CREATE INDEX IF NOT EXISTS idx_payment_travel ON payments(travel_id);
CREATE INDEX IF NOT EXISTS idx_payment_status ON payments(status);
CREATE INDEX IF NOT EXISTS idx_payment_created_at ON payments(created_at);
