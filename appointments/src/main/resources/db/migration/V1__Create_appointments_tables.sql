-- Clinics table
CREATE TABLE IF NOT EXISTS clinics (
    id VARCHAR(36) PRIMARY KEY,
    code VARCHAR(20) UNIQUE,
    name VARCHAR(200) NOT NULL,
    department VARCHAR(100),
    location VARCHAR(200),
    contact_phone VARCHAR(20),
    contact_email VARCHAR(100),
    slot_duration_minutes INTEGER DEFAULT 30,
    overbooking_threshold INTEGER DEFAULT 2,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version INTEGER DEFAULT 0
);

-- Clinic operating hours
CREATE TABLE IF NOT EXISTS clinic_hours (
    clinic_id VARCHAR(36) REFERENCES clinics(id),
    day_of_week VARCHAR(20),
    start_time TIME,
    end_time TIME,
    PRIMARY KEY (clinic_id, day_of_week)
);

-- Clinic blackout dates
CREATE TABLE IF NOT EXISTS clinic_blackout_dates (
    clinic_id VARCHAR(36) REFERENCES clinics(id),
    date DATE,
    PRIMARY KEY (clinic_id, date)
);

-- Providers table
CREATE TABLE IF NOT EXISTS providers (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(200) NOT NULL,
    title VARCHAR(50),
    specialty VARCHAR(100),
    department VARCHAR(100),
    license_number VARCHAR(50),
    email VARCHAR(100),
    phone VARCHAR(20),
    daily_capacity INTEGER,
    consultation_duration_minutes INTEGER DEFAULT 30,
    active BOOLEAN DEFAULT true,
    on_duty BOOLEAN DEFAULT false,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version INTEGER DEFAULT 0
);

-- Appointments table
CREATE TABLE IF NOT EXISTS appointments (
    id VARCHAR(36) PRIMARY KEY,
    appointment_number VARCHAR(50) UNIQUE,
    patient_id VARCHAR(36) NOT NULL,
    clinic_id VARCHAR(36) NOT NULL,
    provider_id VARCHAR(36),
    appointment_date TIMESTAMP NOT NULL,
    appointment_end_date TIMESTAMP,
    duration_minutes INTEGER DEFAULT 30,
    status VARCHAR(50) NOT NULL,
    type VARCHAR(50),
    source VARCHAR(50),
    chief_complaint VARCHAR(500),
    notes VARCHAR(1000),
    referral_id VARCHAR(36),
    sms_reminder_sent BOOLEAN DEFAULT false,
    reminder_sent_at TIMESTAMP,
    check_in_time TIMESTAMP,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version INTEGER DEFAULT 0
);

CREATE INDEX idx_appointments_patient ON appointments(patient_id);
CREATE INDEX idx_appointments_clinic ON appointments(clinic_id);
CREATE INDEX idx_appointments_provider ON appointments(provider_id);
CREATE INDEX idx_appointments_date ON appointments(appointment_date);
CREATE INDEX idx_appointments_status ON appointments(status);

-- Triage scores table
CREATE TABLE IF NOT EXISTS triage_scores (
    id VARCHAR(36) PRIMARY KEY,
    appointment_id VARCHAR(36) NOT NULL,
    patient_id VARCHAR(36) NOT NULL,
    temperature DECIMAL(5,2),
    systolic_bp INTEGER,
    diastolic_bp INTEGER,
    heart_rate INTEGER,
    respiratory_rate INTEGER,
    oxygen_saturation INTEGER,
    consciousness_level VARCHAR(50),
    pain_score INTEGER,
    pregnancy_flag BOOLEAN DEFAULT false,
    malaria_risk BOOLEAN DEFAULT false,
    other_symptoms VARCHAR(500),
    calculated_score INTEGER,
    priority_level VARCHAR(50),
    assessed_by VARCHAR(100),
    assessed_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version INTEGER DEFAULT 0
);

CREATE INDEX idx_triage_appointment ON triage_scores(appointment_id);
CREATE INDEX idx_triage_priority ON triage_scores(priority_level);

-- Visit queues table
CREATE TABLE IF NOT EXISTS visit_queues (
    id VARCHAR(36) PRIMARY KEY,
    clinic_id VARCHAR(36) NOT NULL,
    current_position INTEGER DEFAULT 0,
    total_waiting INTEGER DEFAULT 0,
    average_wait_time_minutes INTEGER DEFAULT 0,
    last_updated TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version INTEGER DEFAULT 0
);

CREATE INDEX idx_queue_clinic ON visit_queues(clinic_id);

-- Queue items table
CREATE TABLE IF NOT EXISTS queue_items (
    queue_id VARCHAR(36) REFERENCES visit_queues(id),
    patient_id VARCHAR(36),
    appointment_id VARCHAR(36),
    priority INTEGER,
    eta_minutes INTEGER,
    status VARCHAR(50),
    queued_at TIMESTAMP,
    estimated_start_time TIMESTAMP,
    PRIMARY KEY (queue_id, patient_id)
);

-- Referrals table
CREATE TABLE IF NOT EXISTS referrals (
    id VARCHAR(36) PRIMARY KEY,
    referral_number VARCHAR(50) UNIQUE,
    patient_id VARCHAR(36) NOT NULL,
    type VARCHAR(50) NOT NULL,
    from_provider_id VARCHAR(36),
    from_provider_name VARCHAR(200),
    to_clinic_id VARCHAR(36),
    to_provider_id VARCHAR(36),
    to_facility_name VARCHAR(200),
    reason VARCHAR(1000),
    clinical_summary TEXT,
    status VARCHAR(50) NOT NULL,
    priority VARCHAR(50),
    referral_date DATE,
    scheduled_date TIMESTAMP,
    completed_date TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version INTEGER DEFAULT 0
);

CREATE INDEX idx_referrals_patient ON referrals(patient_id);
CREATE INDEX idx_referrals_status ON referrals(status);

-- Referral attachments table
CREATE TABLE IF NOT EXISTS referral_attachments (
    referral_id VARCHAR(36) REFERENCES referrals(id),
    file_name VARCHAR(255),
    file_type VARCHAR(100),
    file_size BIGINT,
    storage_path VARCHAR(500),
    PRIMARY KEY (referral_id, file_name)
);

