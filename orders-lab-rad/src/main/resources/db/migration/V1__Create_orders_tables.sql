-- Orders table
CREATE TABLE IF NOT EXISTS orders (
    id VARCHAR(36) PRIMARY KEY,
    order_number VARCHAR(50) UNIQUE NOT NULL,
    patient_id VARCHAR(36) NOT NULL,
    encounter_id VARCHAR(36) NOT NULL,
    type VARCHAR(20) NOT NULL,
    status VARCHAR(50) NOT NULL,
    priority VARCHAR(50),
    ordered_by VARCHAR(100) NOT NULL,
    ordered_by_name VARCHAR(200),
    order_date TIMESTAMP NOT NULL,
    started_date TIMESTAMP,
    resulted_date TIMESTAMP,
    verified_date TIMESTAMP,
    clinical_indication VARCHAR(500),
    comments VARCHAR(1000),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version INTEGER DEFAULT 0
);

CREATE INDEX idx_orders_patient ON orders(patient_id);
CREATE INDEX idx_orders_encounter ON orders(encounter_id);
CREATE INDEX idx_orders_status ON orders(status);

-- Order items table
CREATE TABLE IF NOT EXISTS order_items (
    order_id VARCHAR(36) REFERENCES orders(id),
    test_code VARCHAR(50),
    test_name VARCHAR(200),
    panel VARCHAR(100),
    priority VARCHAR(50),
    clinician_notes VARCHAR(500),
    expected_result_at TIMESTAMP,
    PRIMARY KEY (order_id, test_code)
);

-- Specimens table
CREATE TABLE IF NOT EXISTS specimens (
    id VARCHAR(36) PRIMARY KEY,
    specimen_number VARCHAR(50) UNIQUE NOT NULL,
    order_id VARCHAR(36) NOT NULL REFERENCES orders(id),
    order_item_id VARCHAR(36) NOT NULL,
    patient_id VARCHAR(36) NOT NULL,
    type VARCHAR(100) NOT NULL,
    container VARCHAR(100),
    collected_at TIMESTAMP NOT NULL,
    collector_id VARCHAR(100) NOT NULL,
    collector_name VARCHAR(200),
    received_at TIMESTAMP,
    received_by VARCHAR(100),
    status VARCHAR(50) NOT NULL,
    rejection_reason VARCHAR(500),
    quantity VARCHAR(50),
    storage_conditions VARCHAR(200),
    expiry_time TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version INTEGER DEFAULT 0
);

CREATE INDEX idx_specimens_order ON specimens(order_item_id);
CREATE INDEX idx_specimens_status ON specimens(status);

-- Results table
CREATE TABLE IF NOT EXISTS results (
    id VARCHAR(36) PRIMARY KEY,
    result_number VARCHAR(50) UNIQUE NOT NULL,
    order_id VARCHAR(36) NOT NULL REFERENCES orders(id),
    order_item_id VARCHAR(36) NOT NULL,
    patient_id VARCHAR(36) NOT NULL,
    test_code VARCHAR(50) NOT NULL,
    test_name VARCHAR(200) NOT NULL,
    value_numeric DECIMAL(10,2),
    value_text VARCHAR(1000),
    value_coded VARCHAR(100),
    value_type VARCHAR(20),
    units VARCHAR(50),
    reference_range VARCHAR(100),
    abnormal_flag BOOLEAN DEFAULT false,
    abnormal_flag_text VARCHAR(10),
    critical_flag BOOLEAN DEFAULT false,
    critical_value BOOLEAN DEFAULT false,
    verified BOOLEAN DEFAULT false,
    verified_by VARCHAR(100),
    verified_by_name VARCHAR(200),
    verified_at TIMESTAMP,
    performed_at TIMESTAMP NOT NULL,
    performed_by VARCHAR(100),
    device_id VARCHAR(100),
    comments VARCHAR(1000),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version INTEGER DEFAULT 0
);

CREATE INDEX idx_results_order_item ON results(order_item_id);
CREATE INDEX idx_results_patient ON results(patient_id);
CREATE INDEX idx_results_abnormal ON results(abnormal_flag);
CREATE INDEX idx_results_critical ON results(critical_flag);

-- Imaging studies table
CREATE TABLE IF NOT EXISTS imaging_studies (
    id VARCHAR(36) PRIMARY KEY,
    study_number VARCHAR(50) UNIQUE NOT NULL,
    order_id VARCHAR(36) NOT NULL REFERENCES orders(id),
    patient_id VARCHAR(36) NOT NULL,
    modality VARCHAR(50) NOT NULL,
    body_part VARCHAR(100) NOT NULL,
    accession_number VARCHAR(50),
    study_date TIMESTAMP NOT NULL,
    performed_by VARCHAR(100),
    performed_by_name VARCHAR(200),
    interpreted_by VARCHAR(100),
    interpreted_by_name VARCHAR(200),
    interpreted_at TIMESTAMP,
    status VARCHAR(50) NOT NULL,
    radiologist_findings TEXT,
    impression TEXT,
    recommendations VARCHAR(1000),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version INTEGER DEFAULT 0
);

CREATE INDEX idx_imaging_order ON imaging_studies(order_id);
CREATE INDEX idx_imaging_patient ON imaging_studies(patient_id);
CREATE INDEX idx_imaging_modality ON imaging_studies(modality);

-- Imaging report attachments table
CREATE TABLE IF NOT EXISTS imaging_report_attachments (
    study_id VARCHAR(36) REFERENCES imaging_studies(id),
    file_name VARCHAR(255),
    file_type VARCHAR(100),
    file_size BIGINT,
    storage_path VARCHAR(500),
    uploaded_at TIMESTAMP,
    uploaded_by VARCHAR(100),
    PRIMARY KEY (study_id, file_name)
);

