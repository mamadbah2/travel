-- V1__init_travel_schema.sql
-- Initial database schema for travel-service

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Travels table (core aggregate)
CREATE TABLE IF NOT EXISTS travels (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    manager_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    duration INTEGER NOT NULL,
    price DOUBLE PRECISION NOT NULL,
    max_capacity INTEGER NOT NULL,
    current_bookings INTEGER NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'PUBLISHED', 'CANCELLED', 'COMPLETED')),
    accommodation_type VARCHAR(30) CHECK (accommodation_type IN ('HOTEL', 'HOSTEL', 'RESORT', 'APARTMENT', 'CAMPING', 'GUESTHOUSE', 'OTHER')),
    accommodation_name VARCHAR(255),
    transportation_type VARCHAR(30) CHECK (transportation_type IN ('FLIGHT', 'BUS', 'TRAIN', 'BOAT', 'CAR', 'MINIBUS', 'OTHER')),
    transportation_details VARCHAR(500),
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Destinations table
CREATE TABLE IF NOT EXISTS destinations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    travel_id UUID NOT NULL REFERENCES travels(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    country VARCHAR(100) NOT NULL,
    city VARCHAR(100),
    description TEXT,
    display_order INTEGER
);

-- Activities table
CREATE TABLE IF NOT EXISTS activities (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    travel_id UUID NOT NULL REFERENCES travels(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    location VARCHAR(255),
    display_order INTEGER
);

-- Subscriptions table
CREATE TABLE IF NOT EXISTS subscriptions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    traveler_id UUID NOT NULL,
    travel_id UUID NOT NULL REFERENCES travels(id) ON DELETE CASCADE,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING_PAYMENT' CHECK (status IN ('PENDING_PAYMENT', 'CONFIRMED', 'CANCELLED')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT uk_traveler_travel UNIQUE (traveler_id, travel_id)
);

-- Indexes for travels
CREATE INDEX IF NOT EXISTS idx_travel_manager ON travels(manager_id);
CREATE INDEX IF NOT EXISTS idx_travel_status ON travels(status);
CREATE INDEX IF NOT EXISTS idx_travel_start_date ON travels(start_date);

-- Indexes for destinations
CREATE INDEX IF NOT EXISTS idx_destination_travel ON destinations(travel_id);

-- Indexes for activities
CREATE INDEX IF NOT EXISTS idx_activity_travel ON activities(travel_id);

-- Indexes for subscriptions
CREATE INDEX IF NOT EXISTS idx_subscription_traveler ON subscriptions(traveler_id);
CREATE INDEX IF NOT EXISTS idx_subscription_travel ON subscriptions(travel_id);
CREATE INDEX IF NOT EXISTS idx_subscription_status ON subscriptions(status);
