-- Allergies table
CREATE TABLE IF NOT EXISTS allergies (
    id VARCHAR(36) PRIMARY KEY,
    patient_id VARCHAR(36) NOT NULL REFERENCES patients(id),
    substance VARCHAR(200) NOT NULL,
    reaction VARCHAR(500) NOT NULL,
    severity VARCHAR(50),
    status VARCHAR(50),
    onset_date DATE,
    last_observed DATE,
    notes VARCHAR(1000),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version INTEGER DEFAULT 0
);

CREATE INDEX idx_allergies_patient ON allergies(patient_id);

-- Encounters table
CREATE TABLE IF NOT EXISTS encounters (
    id VARCHAR(36) PRIMARY KEY,
    patient_id VARCHAR(36) NOT NULL REFERENCES patients(id),
    encounter_number VARCHAR(50) UNIQUE,
    type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP,
    reason VARCHAR(500),
    location VARCHAR(100),
    clinician_id VARCHAR(100),
    clinician_name VARCHAR(200),
    triage_level VARCHAR(20),
    admission_id VARCHAR(36),
    visit_type VARCHAR(50),
    referral_source VARCHAR(200),
    final_diagnosis VARCHAR(1000),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version INTEGER DEFAULT 0
);

CREATE INDEX idx_encounters_patient ON encounters(patient_id);
CREATE INDEX idx_encounters_status ON encounters(status);

-- Vital signs table
CREATE TABLE IF NOT EXISTS vital_signs (
    id VARCHAR(36) PRIMARY KEY,
    patient_id VARCHAR(36) NOT NULL REFERENCES patients(id),
    encounter_id VARCHAR(36) REFERENCES encounters(id),
    systolic_bp INTEGER,
    diastolic_bp INTEGER,
    heart_rate INTEGER,
    respiratory_rate INTEGER,
    temperature_celsius DECIMAL(5,2),
    spo2 INTEGER,
    weight_kg DECIMAL(5,2),
    height_cm DECIMAL(5,2),
    bmi DECIMAL(5,2),
    taken_at TIMESTAMP NOT NULL,
    device_id VARCHAR(100),
    notes VARCHAR(500),
    taken_by VARCHAR(100),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version INTEGER DEFAULT 0
);

CREATE INDEX idx_vitals_encounter ON vital_signs(encounter_id);
CREATE INDEX idx_vitals_patient ON vital_signs(patient_id);

-- Diagnoses table
CREATE TABLE IF NOT EXISTS diagnoses (
    id VARCHAR(36) PRIMARY KEY,
    patient_id VARCHAR(36) NOT NULL REFERENCES patients(id),
    encounter_id VARCHAR(36) NOT NULL REFERENCES encounters(id),
    code VARCHAR(20) NOT NULL,
    system VARCHAR(50),
    description VARCHAR(500) NOT NULL,
    type VARCHAR(50) NOT NULL,
    certainty VARCHAR(50),
    onset_date DATE,
    resolved_date DATE,
    status VARCHAR(50),
    notes VARCHAR(1000),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version INTEGER DEFAULT 0
);

CREATE INDEX idx_diagnoses_encounter ON diagnoses(encounter_id);
CREATE INDEX idx_diagnoses_patient ON diagnoses(patient_id);
CREATE INDEX idx_diagnoses_code ON diagnoses(code);

-- Clinical notes table
CREATE TABLE IF NOT EXISTS clinical_notes (
    id VARCHAR(36) PRIMARY KEY,
    patient_id VARCHAR(36) NOT NULL REFERENCES patients(id),
    encounter_id VARCHAR(36) NOT NULL REFERENCES encounters(id),
    type VARCHAR(50) NOT NULL,
    text TEXT NOT NULL,
    author_id VARCHAR(100) NOT NULL,
    author_name VARCHAR(200),
    written_at TIMESTAMP NOT NULL,
    status VARCHAR(50),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version INTEGER DEFAULT 0
);

CREATE INDEX idx_notes_encounter ON clinical_notes(encounter_id);
CREATE INDEX idx_notes_patient ON clinical_notes(patient_id);

-- Clinical note attachments table
CREATE TABLE IF NOT EXISTS clinical_note_attachments (
    note_id VARCHAR(36) REFERENCES clinical_notes(id),
    file_name VARCHAR(255),
    file_type VARCHAR(100),
    file_size BIGINT,
    storage_path VARCHAR(500),
    uploaded_at TIMESTAMP,
    PRIMARY KEY (note_id, file_name)
);

