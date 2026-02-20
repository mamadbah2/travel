-- ===========================================================
-- V1__init_notification_schema.sql
-- Notification Service - Initial Schema
-- ===========================================================

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS notifications (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    traveler_id       UUID NOT NULL,
    travel_id         UUID NOT NULL,
    subscription_id   UUID NOT NULL,
    recipient_email   VARCHAR(255) NOT NULL,
    subject           VARCHAR(500) NOT NULL,
    body              TEXT,
    type              VARCHAR(30) NOT NULL CHECK (type IN ('SUBSCRIPTION_CREATED', 'PAYMENT_SUCCESS', 'PAYMENT_FAILED')),
    status            VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'SENT', 'FAILED')),
    failure_reason    VARCHAR(500),
    created_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP
);

-- Indexes for efficient querying
CREATE INDEX IF NOT EXISTS idx_notification_traveler ON notifications(traveler_id);
CREATE INDEX IF NOT EXISTS idx_notification_travel ON notifications(travel_id);
CREATE INDEX IF NOT EXISTS idx_notification_subscription ON notifications(subscription_id);
CREATE INDEX IF NOT EXISTS idx_notification_type ON notifications(type);
CREATE INDEX IF NOT EXISTS idx_notification_status ON notifications(status);
CREATE INDEX IF NOT EXISTS idx_notification_created_at ON notifications(created_at DESC);
