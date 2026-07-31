-- MedColleague Full-Stack PostgreSQL / Supabase Database Schema

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Enum Types
CREATE TYPE user_role AS ENUM ('STUDENT', 'RESIDENT', 'ATTENDING', 'ADMIN');
CREATE TYPE subscription_plan AS ENUM ('FREE', 'PRO_MONTHLY', 'PRO_ANNUAL', 'INSTITUTIONAL');
CREATE TYPE exam_type AS ENUM ('USMLE_STEP_1', 'USMLE_STEP_2_CK', 'USMLE_STEP_3', 'NCLEX_RN', 'MRCP_UK', 'GENERAL_CLINICAL');

-- Users Table
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    role user_role DEFAULT 'STUDENT',
    plan subscription_plan DEFAULT 'FREE',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Questions Bank Table
CREATE TABLE questions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    exam_type exam_type NOT NULL,
    organ_system VARCHAR(100) NOT NULL,
    vignette TEXT NOT NULL,
    options JSONB NOT NULL, -- ["Option A", "Option B", ...]
    correct_index INT NOT NULL,
    explanation TEXT NOT NULL,
    high_yield_pearl TEXT NOT NULL,
    difficulty VARCHAR(50) DEFAULT 'Medium',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Question Attempts & Analytics
CREATE TABLE question_attempts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    question_id UUID REFERENCES questions(id) ON DELETE CASCADE,
    selected_index INT NOT NULL,
    is_correct BOOLEAN NOT NULL,
    time_taken_sec INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Flashcards (SM-2 Spaced Repetition)
CREATE TABLE flashcards (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    organ_system VARCHAR(100) NOT NULL,
    front TEXT NOT NULL,
    back TEXT NOT NULL,
    pearl TEXT NOT NULL
);

-- User Flashcard SM-2 State
CREATE TABLE flashcard_reviews (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    flashcard_id UUID REFERENCES flashcards(id) ON DELETE CASCADE,
    ease_factor FLOAT DEFAULT 2.5,
    interval_days INT DEFAULT 1,
    repetitions INT DEFAULT 0,
    next_review_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- AI Tutor Sessions Log
CREATE TABLE tutor_sessions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    document_text TEXT NOT NULL,
    dialogue JSONB NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create Indexes for Performance
CREATE INDEX idx_questions_organ_system ON questions(organ_system);
CREATE INDEX idx_question_attempts_user ON question_attempts(user_id);
CREATE INDEX idx_flashcard_reviews_due ON flashcard_reviews(user_id, next_review_at);
