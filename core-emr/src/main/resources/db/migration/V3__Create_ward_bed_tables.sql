-- Wards table
CREATE TABLE IF NOT EXISTS wards (
    id VARCHAR(36) PRIMARY KEY,
    code VARCHAR(20) UNIQUE,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50),
    capacity INTEGER,
    occupied INTEGER DEFAULT 0,
    floor INTEGER,
    department VARCHAR(100),
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version INTEGER DEFAULT 0
);

-- Beds table
CREATE TABLE IF NOT EXISTS beds (
    id VARCHAR(36) PRIMARY KEY,
    ward_id VARCHAR(36) NOT NULL REFERENCES wards(id),
    code VARCHAR(50) UNIQUE,
    status VARCHAR(50) NOT NULL,
    room_number VARCHAR(50),
    bed_number VARCHAR(20),
    isolation BOOLEAN DEFAULT false,
    special_equipment VARCHAR(500),
    current_patient_id VARCHAR(36),
    notes VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version INTEGER DEFAULT 0
);

CREATE INDEX idx_beds_ward ON beds(ward_id);
CREATE INDEX idx_beds_status ON beds(status);

-- Bed isolation flags
CREATE TABLE IF NOT EXISTS bed_isolation_flags (
    bed_id VARCHAR(36) REFERENCES beds(id),
    flags VARCHAR(100),
    PRIMARY KEY (bed_id, flags)
);

-- Admissions table
CREATE TABLE IF NOT EXISTS admissions (
    id VARCHAR(36) PRIMARY KEY,
    admission_number VARCHAR(50) UNIQUE NOT NULL,
    patient_id VARCHAR(36) NOT NULL REFERENCES patients(id),
    bed_id VARCHAR(36) NOT NULL REFERENCES beds(id),
    encounter_id VARCHAR(36) REFERENCES encounters(id),
    admission_date TIMESTAMP NOT NULL,
    discharge_date TIMESTAMP,
    admission_type VARCHAR(50),
    admitting_physician_id VARCHAR(100),
    admitting_physician_name VARCHAR(200),
    discharging_physician_id VARCHAR(100),
    discharging_physician_name VARCHAR(200),
    discharge_summary TEXT,
    status VARCHAR(50),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version INTEGER DEFAULT 0
);

CREATE INDEX idx_admissions_patient ON admissions(patient_id);
CREATE INDEX idx_admissions_bed ON admissions(bed_id);
CREATE INDEX idx_admissions_number ON admissions(admission_number);

