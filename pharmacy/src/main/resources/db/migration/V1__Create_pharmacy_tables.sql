-- Medications table
CREATE TABLE IF NOT EXISTS medications (
    id VARCHAR(36) PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(200) NOT NULL,
    brand_name VARCHAR(200),
    dosage_form VARCHAR(100),
    strength VARCHAR(100),
    route VARCHAR(100),
    therapeutic_class VARCHAR(200),
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version INTEGER DEFAULT 0
);

CREATE INDEX idx_medications_code ON medications(code);
CREATE INDEX idx_medications_name ON medications(name);

-- Formulary items table
CREATE TABLE IF NOT EXISTS formulary_items (
    id VARCHAR(36) PRIMARY KEY,
    medication_id VARCHAR(36) NOT NULL REFERENCES medications(id),
    ddd DECIMAL(10,2),
    ddd_unit VARCHAR(50),
    strength VARCHAR(100),
    route VARCHAR(100),
    unit_price_ngn DECIMAL(10,2),
    reimbursable BOOLEAN DEFAULT false,
    requires_prescription BOOLEAN DEFAULT true,
    schedule VARCHAR(50),
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version INTEGER DEFAULT 0
);

CREATE INDEX idx_formulary_medication ON formulary_items(medication_id);
CREATE INDEX idx_formulary_active ON formulary_items(active);

-- Prescriptions table
CREATE TABLE IF NOT EXISTS prescriptions (
    id VARCHAR(36) PRIMARY KEY,
    prescription_number VARCHAR(50) UNIQUE NOT NULL,
    patient_id VARCHAR(36) NOT NULL,
    encounter_id VARCHAR(36) NOT NULL,
    status VARCHAR(50) NOT NULL,
    prescribed_by VARCHAR(100) NOT NULL,
    prescribed_by_name VARCHAR(200),
    prescribed_date TIMESTAMP NOT NULL,
    valid_until TIMESTAMP,
    notes VARCHAR(1000),
    allergy_override BOOLEAN DEFAULT false,
    allergy_override_reason VARCHAR(500),
    allergy_override_by VARCHAR(100),
    interaction_override BOOLEAN DEFAULT false,
    interaction_override_reason VARCHAR(500),
    interaction_override_by VARCHAR(100),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version INTEGER DEFAULT 0
);

CREATE INDEX idx_prescriptions_patient ON prescriptions(patient_id);
CREATE INDEX idx_prescriptions_encounter ON prescriptions(encounter_id);
CREATE INDEX idx_prescriptions_status ON prescriptions(status);

-- Prescription items table
CREATE TABLE IF NOT EXISTS prescription_items (
    prescription_id VARCHAR(36) REFERENCES prescriptions(id),
    medication_code VARCHAR(50),
    medication_name VARCHAR(200),
    quantity INTEGER,
    sig VARCHAR(500),
    route VARCHAR(100),
    duration_days INTEGER,
    repeats INTEGER DEFAULT 0,
    additional_instructions VARCHAR(1000),
    PRIMARY KEY (prescription_id, medication_code)
);

-- Dispenses table
CREATE TABLE IF NOT EXISTS dispenses (
    id VARCHAR(36) PRIMARY KEY,
    dispense_number VARCHAR(50) UNIQUE NOT NULL,
    prescription_id VARCHAR(36) NOT NULL REFERENCES prescriptions(id),
    patient_id VARCHAR(36) NOT NULL,
    dispense_date TIMESTAMP NOT NULL,
    dispensed_by VARCHAR(100) NOT NULL,
    dispensed_by_name VARCHAR(200),
    total_amount_ngn DECIMAL(10,2),
    payment_status VARCHAR(50),
    payment_reference VARCHAR(100),
    label_text VARCHAR(2000),
    instruction_sheet TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version INTEGER DEFAULT 0
);

CREATE INDEX idx_dispenses_prescription ON dispenses(prescription_id);
CREATE INDEX idx_dispenses_patient ON dispenses(patient_id);
CREATE INDEX idx_dispenses_date ON dispenses(dispense_date);

-- Dispense lines table
CREATE TABLE IF NOT EXISTS dispense_lines (
    dispense_id VARCHAR(36) REFERENCES dispenses(id),
    medication_code VARCHAR(50),
    medication_name VARCHAR(200),
    quantity_dispensed INTEGER,
    batch_id VARCHAR(100),
    expiry_date DATE,
    unit_price DECIMAL(10,2),
    line_total DECIMAL(10,2),
    expiry_days INTEGER,
    PRIMARY KEY (dispense_id, medication_code, batch_id)
);

-- Stock ledger table
CREATE TABLE IF NOT EXISTS stock_ledger (
    id VARCHAR(36) PRIMARY KEY,
    medication_code VARCHAR(50) NOT NULL,
    batch_number VARCHAR(100) NOT NULL,
    expiry_date DATE NOT NULL,
    quantity INTEGER NOT NULL,
    unit_cost_ngn DECIMAL(10,2),
    movement_type VARCHAR(50),
    reference_id VARCHAR(36),
    status VARCHAR(50),
    location VARCHAR(100),
    supplier_id VARCHAR(36),
    last_count_date DATE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version INTEGER DEFAULT 0
);

CREATE INDEX idx_stock_medication ON stock_ledger(medication_code);
CREATE INDEX idx_stock_batch ON stock_ledger(batch_number);
CREATE INDEX idx_stock_expiry ON stock_ledger(expiry_date);
CREATE INDEX idx_stock_status ON stock_ledger(status);

-- Suppliers table
CREATE TABLE IF NOT EXISTS suppliers (
    id VARCHAR(36) PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(200) NOT NULL,
    contact_person VARCHAR(200),
    email VARCHAR(100),
    phone VARCHAR(20),
    address VARCHAR(500),
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version INTEGER DEFAULT 0
);

CREATE INDEX idx_suppliers_code ON suppliers(code);
CREATE INDEX idx_suppliers_active ON suppliers(active);

