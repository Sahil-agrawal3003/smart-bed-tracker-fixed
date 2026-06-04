-- ============================================================
-- Smart Hospital Bed Tracker — PostgreSQL Schema
-- Run once, or let Hibernate ddl-auto=update handle it.
-- This file is provided as a reference / manual setup option.
-- ============================================================

-- Create sequences (used by JPA @SequenceGenerator)
CREATE SEQUENCE IF NOT EXISTS ward_sequence    START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS bed_sequence     START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS patient_sequence START WITH 1 INCREMENT BY 1;

-- Wards
CREATE TABLE IF NOT EXISTS wards (
    id          BIGINT       PRIMARY KEY DEFAULT nextval('ward_sequence'),
    name        VARCHAR(100) NOT NULL UNIQUE,
    total_beds  INT          NOT NULL CHECK (total_beds > 0),
    floor       VARCHAR(50)  NOT NULL,
    description VARCHAR(255)
);

-- Beds
CREATE TABLE IF NOT EXISTS beds (
    id           BIGINT      PRIMARY KEY DEFAULT nextval('bed_sequence'),
    bed_number   VARCHAR(20) NOT NULL UNIQUE,
    status       VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE'
                     CHECK (status IN ('AVAILABLE','OCCUPIED','MAINTENANCE','RESERVED')),
    ward_id      BIGINT      REFERENCES wards(id) ON DELETE SET NULL,
    last_updated TIMESTAMP
);

-- Patients
CREATE TABLE IF NOT EXISTS patients (
    id                BIGINT       PRIMARY KEY DEFAULT nextval('patient_sequence'),
    name              VARCHAR(150) NOT NULL,
    age               INT          NOT NULL CHECK (age >= 0),
    diagnosis         VARCHAR(255),
    doctor_assigned   VARCHAR(150),
    bed_id            BIGINT       UNIQUE REFERENCES beds(id) ON DELETE SET NULL,
    admission_date    TIMESTAMP,
    discharge_date    TIMESTAMP,
    status            VARCHAR(20)  NOT NULL DEFAULT 'ADMITTED'
                          CHECK (status IN ('ADMITTED','DISCHARGED','TRANSFERRED')),
    contact_number    VARCHAR(20),
    emergency_contact VARCHAR(20)
);

-- Indexes for common queries
CREATE INDEX IF NOT EXISTS idx_beds_status     ON beds(status);
CREATE INDEX IF NOT EXISTS idx_beds_ward       ON beds(ward_id);
CREATE INDEX IF NOT EXISTS idx_patients_status ON patients(status);
CREATE INDEX IF NOT EXISTS idx_patients_bed    ON patients(bed_id);

-- Login Audit (stores every login event with username, role, and timestamp)
CREATE SEQUENCE IF NOT EXISTS login_audit_sequence START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS login_audit (
    id          BIGINT       PRIMARY KEY DEFAULT nextval('login_audit_sequence'),
    username    VARCHAR(100) NOT NULL,
    role        VARCHAR(50),
    action      VARCHAR(50)  NOT NULL DEFAULT 'LOGIN',
    action_time TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_login_audit_user ON login_audit(username);
CREATE INDEX IF NOT EXISTS idx_login_audit_time ON login_audit(action_time);
