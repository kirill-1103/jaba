DROP TABLE IF EXISTS roles CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS user_roles CASCADE;
DROP TABLE IF EXISTS applicant_request CASCADE;
DROP TABLE IF EXISTS activation_token CASCADE;

DROP SEQUENCE IF EXISTS user_seq;
DROP SEQUENCE IF EXISTS applicant_request_seq;
DROP SEQUENCE IF EXISTS token_seq;

CREATE SEQUENCE IF NOT EXISTS user_seq START 1;
CREATE SEQUENCE IF NOT EXISTS applicant_request_seq START 1;
CREATE SEQUENCE IF NOT EXISTS token_seq START 1;

-- DROP TABLE IF EXISTS roles CASCADE ;
-- DROP TABLE IF EXISTS users CASCADE ;
-- DROP TABLE IF EXISTS user_roles CASCADE;
-- DROP TABLE IF EXISTS applicant_request CASCADE ;
-- DROP TABLE IF EXISTS activation_token CASCADE ;


CREATE TABLE IF NOT EXISTS roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE
);

CREATE TABLE IF NOT EXISTS users (
    id BIGINT DEFAULT nextval('user_seq') NOT NULL PRIMARY KEY,
    login VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR NOT NULL,
    name VARCHAR(255) NOT NULL,
    surname VARCHAR(255) NOT NULL,
    patronymic VARCHAR(255),
    photo_path VARCHAR,
    group_number VARCHAR(255),
    activation_status VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT,
    role_id BIGINT,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (role_id) REFERENCES roles (id)
);

CREATE TABLE IF NOT EXISTS applicant_request (
    id BIGINT DEFAULT nextval('applicant_request_seq') NOT NULL PRIMARY KEY,
    leader_name VARCHAR NOT NULL,
    subunit_name VARCHAR NOT NULL,
    current_position VARCHAR NOT NULL,
    work_experience VARCHAR NOT NULL,
    personal_achievements VARCHAR NOT NULL,
    motivation_message VARCHAR NOT NULL,
    request_status VARCHAR,
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS activation_token (
    id BIGINT DEFAULT nextval('token_seq') NOT NULL PRIMARY KEY,
    token VARCHAR,
    expiration_date TIMESTAMP,
    user_id BIGINT,
    token_type VARCHAR,
    FOREIGN KEY (user_id) REFERENCES users (id)
);

