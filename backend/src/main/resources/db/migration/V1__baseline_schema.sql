CREATE TABLE citizens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    father_name VARCHAR(255),
    mother_name VARCHAR(255),
    date_of_birth DATE,
    sex VARCHAR(10)
);

CREATE TABLE national_identities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    national_id_Number VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255),
    father_name VARCHAR(255),
    mother_name VARCHAR(255),
    last_name VARCHAR(255),
    nationality VARCHAR(255),
    card_serial VARCHAR(255),
    gender VARCHAR(255),
    date_of_birth DATE,
    place_of_birth VARCHAR(255),
    place_of_issue VARCHAR(255),
    date_of_issue DATE,
    date_of_expiry DATE,
    blood_group VARCHAR(255),
    profession VARCHAR(255),
    address VARCHAR(255)
);

CREATE TABLE passports (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    last_name VARCHAR(255),
    mother_name VARCHAR(255),
    nationality VARCHAR(255),
    date_of_birth DATE,
    place_of_birth VARCHAR(255),
    date_of_issue DATE,
    date_of_expiry DATE,
    national_id_number VARCHAR(255) UNIQUE,
    sex VARCHAR(255),
    job VARCHAR(255),
    place_of_issue VARCHAR(255),
    issuing_authority VARCHAR(255),
    passport_number VARCHAR(255) UNIQUE
);

CREATE TABLE birth_certificate (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    national_id VARCHAR(255),
    certificate_number VARCHAR(255),
    full_name VARCHAR(255),
    gender VARCHAR(255),
    birth_date DATE,
    birth_place VARCHAR(255),
    father_name VARCHAR(255),
    father_birth_date DATE,
    father_birth_place VARCHAR(255),
    father_profession VARCHAR(255),
    mother_name VARCHAR(255),
    mother_birth_date DATE,
    mother_birth_place VARCHAR(255),
    mother_profession VARCHAR(255),
    declaration_date DATE,
    address VARCHAR(255),
    created_at TIMESTAMP
);

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    national_id_number VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255),
    email VARCHAR(255) UNIQUE
);
