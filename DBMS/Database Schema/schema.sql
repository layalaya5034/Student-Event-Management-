-- ==============================================================================
-- SMART CAMPUS EVENT MANAGEMENT SYSTEM - DATABASE SCHEMA (DDL)
-- Scenario 6: Smart Campus Event Management and Student Participation Analysis
-- Register Numbers: 6, 36, 66, 96
-- ==============================================================================

PRAGMA foreign_keys = ON;

-- ------------------------------------------------------------------------------
-- 1. TABLE: students
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS students (
    student_id          TEXT PRIMARY KEY,
    register_number     INTEGER UNIQUE,
    student_name        TEXT NOT NULL,
    department          TEXT NOT NULL,
    year_of_study       INTEGER NOT NULL CHECK(year_of_study BETWEEN 1 AND 4),
    email               TEXT UNIQUE,
    phone               TEXT,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ------------------------------------------------------------------------------
-- 2. TABLE: events
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS events (
    event_id            TEXT PRIMARY KEY,
    event_name          TEXT NOT NULL,
    event_type          TEXT NOT NULL CHECK(event_type IN (
                            'Technical', 'Workshop', 'Seminar', 'Cultural', 
                            'Sports', 'Competition', 'Symposium', 'Other'
                        )),
    event_date          TEXT NOT NULL, -- Format: YYYY-MM-DD
    venue               TEXT NOT NULL,
    max_participants    INTEGER NOT NULL CHECK(max_participants > 0),
    event_status        TEXT NOT NULL DEFAULT 'OPEN' CHECK(event_status IN (
                            'UPCOMING', 'OPEN', 'ONGOING', 'COMPLETED', 'CANCELLED'
                        )),
    description         TEXT,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ------------------------------------------------------------------------------
-- 3. TABLE: registrations
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS registrations (
    registration_id     TEXT PRIMARY KEY,
    student_id          TEXT NOT NULL,
    event_id            TEXT NOT NULL,
    registration_date   TEXT NOT NULL, -- Format: YYYY-MM-DD
    status              TEXT NOT NULL DEFAULT 'REGISTERED' CHECK(status IN (
                            'REGISTERED', 'CANCELLED', 'WAITLISTED'
                        )),
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON UPDATE CASCADE ON DELETE RESTRICT,
    FOREIGN KEY (event_id) REFERENCES events(event_id) ON UPDATE CASCADE ON DELETE RESTRICT,
    UNIQUE(student_id, event_id)
);

-- ------------------------------------------------------------------------------
-- 4. TABLE: attendance
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS attendance (
    attendance_id       TEXT PRIMARY KEY,
    registration_id     TEXT NOT NULL UNIQUE,
    student_id          TEXT NOT NULL,
    event_id            TEXT NOT NULL,
    attendance_date     TEXT NOT NULL, -- Format: YYYY-MM-DD
    attendance_status   TEXT NOT NULL CHECK(attendance_status IN ('PRESENT', 'ABSENT')),
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (registration_id) REFERENCES registrations(registration_id) ON UPDATE CASCADE ON DELETE RESTRICT,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON UPDATE CASCADE ON DELETE RESTRICT,
    FOREIGN KEY (event_id) REFERENCES events(event_id) ON UPDATE CASCADE ON DELETE RESTRICT
);

-- ------------------------------------------------------------------------------
-- 5. TABLE: participations
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS participations (
    participation_id     TEXT PRIMARY KEY,
    registration_id      TEXT NOT NULL UNIQUE,
    student_id           TEXT NOT NULL,
    event_id             TEXT NOT NULL,
    participation_status TEXT NOT NULL DEFAULT 'PARTICIPATED' CHECK(participation_status IN (
                             'PARTICIPATED', 'WINNER', 'RUNNER_UP', 'PARTICIPANT', 'NOT_PARTICIPATED'
                         )),
    remarks              TEXT,
    certificate_status   TEXT NOT NULL DEFAULT 'PENDING' CHECK(certificate_status IN (
                             'ISSUED', 'PENDING', 'NOT_ELIGIBLE'
                         )),
    created_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (registration_id) REFERENCES registrations(registration_id) ON UPDATE CASCADE ON DELETE RESTRICT,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON UPDATE CASCADE ON DELETE RESTRICT,
    FOREIGN KEY (event_id) REFERENCES events(event_id) ON UPDATE CASCADE ON DELETE RESTRICT
);

-- ------------------------------------------------------------------------------
-- INDEXES FOR QUERY OPTIMIZATION
-- ------------------------------------------------------------------------------
CREATE INDEX IF NOT EXISTS idx_students_dept ON students(department);
CREATE INDEX IF NOT EXISTS idx_students_year ON students(year_of_study);
CREATE INDEX IF NOT EXISTS idx_events_date ON events(event_date);
CREATE INDEX IF NOT EXISTS idx_events_status ON events(event_status);
CREATE INDEX IF NOT EXISTS idx_events_type ON events(event_type);
CREATE INDEX IF NOT EXISTS idx_reg_student ON registrations(student_id);
CREATE INDEX IF NOT EXISTS idx_reg_event ON registrations(event_id);
CREATE INDEX IF NOT EXISTS idx_reg_status ON registrations(status);
CREATE INDEX IF NOT EXISTS idx_att_event ON attendance(event_id);
CREATE INDEX IF NOT EXISTS idx_att_status ON attendance(attendance_status);
CREATE INDEX IF NOT EXISTS idx_part_student ON participations(student_id);
CREATE INDEX IF NOT EXISTS idx_part_status ON participations(participation_status);

-- ------------------------------------------------------------------------------
-- VIEWS
-- ------------------------------------------------------------------------------

-- 1. View: Event Summary & Capacity
CREATE VIEW IF NOT EXISTS v_event_overview AS
SELECT 
    e.event_id,
    e.event_name,
    e.event_type,
    e.event_date,
    e.venue,
    e.max_participants,
    COUNT(CASE WHEN r.status = 'REGISTERED' THEN 1 END) AS active_registrations,
    COUNT(CASE WHEN r.status = 'WAITLISTED' THEN 1 END) AS waitlisted_count,
    COUNT(CASE WHEN r.status = 'CANCELLED' THEN 1 END) AS cancelled_count,
    MAX(0, e.max_participants - COUNT(CASE WHEN r.status = 'REGISTERED' THEN 1 END)) AS available_seats,
    ROUND((COUNT(CASE WHEN r.status = 'REGISTERED' THEN 1 END) * 100.0) / e.max_participants, 2) AS capacity_utilization_pct,
    e.event_status
FROM events e
LEFT JOIN registrations r ON e.event_id = r.event_id
GROUP BY e.event_id;

-- 2. View: Student Participation Summary
CREATE VIEW IF NOT EXISTS v_student_participation_summary AS
SELECT 
    s.student_id,
    s.register_number,
    s.student_name,
    s.department,
    s.year_of_study,
    COUNT(DISTINCT CASE WHEN r.status = 'REGISTERED' THEN r.event_id END) AS total_registered_events,
    COUNT(DISTINCT CASE WHEN a.attendance_status = 'PRESENT' THEN a.event_id END) AS total_attended_events,
    COUNT(DISTINCT CASE WHEN p.participation_status IN ('PARTICIPATED', 'WINNER', 'RUNNER_UP', 'PARTICIPANT') THEN p.event_id END) AS total_participated_events,
    COUNT(CASE WHEN p.participation_status = 'WINNER' THEN 1 END) AS wins_count,
    COUNT(CASE WHEN p.participation_status = 'RUNNER_UP' THEN 1 END) AS runner_up_count,
    COUNT(CASE WHEN p.certificate_status = 'ISSUED' THEN 1 END) AS certificates_issued
FROM students s
LEFT JOIN registrations r ON s.student_id = r.student_id
LEFT JOIN attendance a ON r.registration_id = a.registration_id
LEFT JOIN participations p ON r.registration_id = p.registration_id
GROUP BY s.student_id;

-- 3. View: Event Attendance Analysis
CREATE VIEW IF NOT EXISTS v_attendance_overview AS
SELECT 
    e.event_id,
    e.event_name,
    e.event_date,
    COUNT(CASE WHEN r.status = 'REGISTERED' THEN 1 END) AS total_registered,
    COUNT(CASE WHEN a.attendance_status = 'PRESENT' THEN 1 END) AS present_count,
    COUNT(CASE WHEN a.attendance_status = 'ABSENT' THEN 1 END) AS absent_count,
    CASE 
        WHEN COUNT(a.attendance_id) = 0 THEN 0.0
        ELSE ROUND((COUNT(CASE WHEN a.attendance_status = 'PRESENT' THEN 1 END) * 100.0) / COUNT(a.attendance_id), 2)
    END AS attendance_percentage
FROM events e
LEFT JOIN registrations r ON e.event_id = r.event_id AND r.status = 'REGISTERED'
LEFT JOIN attendance a ON r.registration_id = a.registration_id
GROUP BY e.event_id;

-- 4. View: Department Participation Summary
CREATE VIEW IF NOT EXISTS v_department_summary AS
SELECT 
    s.department,
    COUNT(DISTINCT s.student_id) AS total_students,
    COUNT(DISTINCT CASE WHEN r.status = 'REGISTERED' THEN r.student_id END) AS students_with_registrations,
    COUNT(CASE WHEN r.status = 'REGISTERED' THEN 1 END) AS total_registrations,
    COUNT(CASE WHEN a.attendance_status = 'PRESENT' THEN 1 END) AS total_attendances,
    COUNT(CASE WHEN p.participation_status IN ('PARTICIPATED', 'WINNER', 'RUNNER_UP', 'PARTICIPANT') THEN 1 END) AS total_participations
FROM students s
LEFT JOIN registrations r ON s.student_id = r.student_id
LEFT JOIN attendance a ON r.registration_id = a.registration_id
LEFT JOIN participations p ON r.registration_id = p.registration_id
GROUP BY s.department;
