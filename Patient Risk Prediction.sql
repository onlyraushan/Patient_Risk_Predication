--1. Dimension Tables

--dim_patient
CREATE TABLE dim_patient (
    patient_id SERIAL PRIMARY KEY,
    name VARCHAR(100),
    age INT,
    gender VARCHAR(10),
    address TEXT
);

--dim_hospital
CREATE TABLE dim_hospital (
    hospital_id SERIAL PRIMARY KEY,
    name VARCHAR(100),
    location VARCHAR(100),
    type VARCHAR(50)
);

--dim_diagnosis
CREATE TABLE dim_diagnosis (
    diagnosis_id SERIAL PRIMARY KEY,
    code VARCHAR(20),
    description TEXT,
    category VARCHAR(50)
);

--2. Fact Table

--fact_patient_risk
CREATE TABLE fact_patient_risk (
    risk_id SERIAL PRIMARY KEY,
    patient_id INT REFERENCES dim_patient(patient_id),
    hospital_id INT REFERENCES dim_hospital(hospital_id),
    diagnosis_id INT REFERENCES dim_diagnosis(diagnosis_id),
    risk_score DECIMAL(5,2),
    prediction_date DATE
);


CREATE INDEX idx_patient ON fact_patient_risk(patient_id);
CREATE INDEX idx_hospital ON fact_patient_risk(hospital_id);
CREATE INDEX idx_diagnosis ON fact_patient_risk(diagnosis_id);


INSERT INTO dim_patient (name, age, gender, address)
VALUES 
('Amit Sharma', 65, 'Male', 'Delhi'),
('Priya Verma', 58, 'Female', 'Bangalore'),
('Ravi Kumar', 72, 'Male', 'Hyderabad');

INSERT INTO dim_hospital (name, location, type)
VALUES 
('Apollo Hospital', 'Delhi', 'Private'),
('Fortis Healthcare', 'Bangalore', 'Private'),
('AIIMS', 'Delhi', 'Government');


INSERT INTO dim_diagnosis (code, description, category)
VALUES 
('D001', 'Type 2 Diabetes', 'Chronic'),
('D002', 'Hypertension', 'Chronic'),
('D003', 'Coronary Artery Disease', 'Chronic');

INSERT INTO fact_patient_risk (patient_id, hospital_id, diagnosis_id, risk_score, prediction_date)
VALUES 
(1, 1, 1, 85.50, '2025-08-01'),
(2, 2, 2, 72.30, '2025-08-02'),
(3, 3, 3, 90.10, '2025-08-03');


