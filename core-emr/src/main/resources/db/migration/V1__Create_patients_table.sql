-- Patients table for EMR
CREATE TABLE IF NOT EXISTS patients (
    id VARCHAR(36) PRIMARY KEY,
    mrn VARCHAR(20) UNIQUE NOT NULL,
    nin VARCHAR(11) UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    date_of_birth DATE,
    gender VARCHAR(20),
    title VARCHAR(10),
    primary_phone VARCHAR(20),
    email VARCHAR(100),
    residential_address VARCHAR(500),
    lga VARCHAR(100),
    state VARCHAR(50) NOT NULL,
    nationality VARCHAR(50),
    marital_status VARCHAR(20),
    occupation VARCHAR(100),
    blood_group VARCHAR(10),
    genotype VARCHAR(10),
    insurance_number VARCHAR(100),
    insurance_provider VARCHAR(100),
    emergency_contact_name VARCHAR(100),
    emergency_contact_phone VARCHAR(20),
    emergency_contact_relationship VARCHAR(50),
    consent_to_treatment BOOLEAN DEFAULT false,
    consent_to_data_sharing BOOLEAN DEFAULT false,
    active BOOLEAN DEFAULT true,
    deleted BOOLEAN DEFAULT false,
    deleted_reason VARCHAR(255),
    deleted_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version INTEGER DEFAULT 0
);

CREATE INDEX idx_patients_mrn ON patients(mrn);
CREATE INDEX idx_patients_nin ON patients(nin);
CREATE INDEX idx_patients_phone ON patients(primary_phone);

-- Patient phones table
CREATE TABLE IF NOT EXISTS patient_phones (
    patient_id VARCHAR(36) REFERENCES patients(id),
    phone VARCHAR(20),
    PRIMARY KEY (patient_id, phone)
);

-- Patient addresses table
CREATE TABLE IF NOT EXISTS patient_addresses (
    patient_id VARCHAR(36) REFERENCES patients(id),
    type VARCHAR(50),
    street VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(50),
    postal_code VARCHAR(20),
    PRIMARY KEY (patient_id, type)
);

-- Patient identifiers table
CREATE TABLE IF NOT EXISTS patient_identifiers (
    patient_id VARCHAR(36) REFERENCES patients(id),
    type VARCHAR(50),
    value VARCHAR(100),
    issuer VARCHAR(100),
    PRIMARY KEY (patient_id, type)
);

-- Patient contacts table
CREATE TABLE IF NOT EXISTS patient_contacts (
    patient_id VARCHAR(36) REFERENCES patients(id),
    type VARCHAR(50),
    name VARCHAR(100),
    phone VARCHAR(20),
    relationship VARCHAR(50),
    address VARCHAR(500),
    PRIMARY KEY (patient_id, type)
);

