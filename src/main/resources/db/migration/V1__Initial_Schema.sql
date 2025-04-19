-- Create users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    image_url VARCHAR(255),
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    password VARCHAR(255),
    provider VARCHAR(20),
    provider_id VARCHAR(255),
    dark_mode BOOLEAN DEFAULT FALSE,
    primary_color VARCHAR(20) DEFAULT '#4f46e5',
    email_notifications BOOLEAN DEFAULT TRUE,
    reminder_time VARCHAR(10) DEFAULT '1h',
    default_view VARCHAR(20) DEFAULT 'list',
    compact_mode BOOLEAN DEFAULT FALSE
);

-- Create interviews table
CREATE TABLE interviews (
    id BIGSERIAL PRIMARY KEY,
    company_name VARCHAR(255) NOT NULL,
    position VARCHAR(255) NOT NULL,
    date DATE NOT NULL,
    time TIME NOT NULL,
    type VARCHAR(20),
    status VARCHAR(20),
    location VARCHAR(255),
    notes TEXT,
    contact_name VARCHAR(255),
    contact_position VARCHAR(255),
    contact_email VARCHAR(255),
    contact_phone VARCHAR(50),
    rating INTEGER,
    feedback TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE
);

-- Create questions table
CREATE TABLE questions (
    id BIGSERIAL PRIMARY KEY,
    question VARCHAR(1000) NOT NULL,
    answer VARCHAR(2000),
    category VARCHAR(20),
    is_important BOOLEAN DEFAULT FALSE,
    interview_id BIGINT NOT NULL REFERENCES interviews(id) ON DELETE CASCADE
);

-- Create documents table
CREATE TABLE documents (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(20),
    url VARCHAR(1000) NOT NULL,
    content_type VARCHAR(100),
    size BIGINT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE
);

-- Create interview_documents join table
CREATE TABLE interview_documents (
    interview_id BIGINT REFERENCES interviews(id) ON DELETE CASCADE,
    document_id BIGINT REFERENCES documents(id) ON DELETE CASCADE,
    PRIMARY KEY (interview_id, document_id)
);

-- Create reminders table
CREATE TABLE reminders (
    id BIGSERIAL PRIMARY KEY,
    time TIMESTAMP WITH TIME ZONE NOT NULL,
    message VARCHAR(500) NOT NULL,
    is_completed BOOLEAN DEFAULT FALSE,
    interview_id BIGINT NOT NULL REFERENCES interviews(id) ON DELETE CASCADE
);
