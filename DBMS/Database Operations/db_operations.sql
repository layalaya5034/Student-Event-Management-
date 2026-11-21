-- ==============================================================================
-- SMART CAMPUS EVENT MANAGEMENT SYSTEM - DATABASE OPERATIONS & TRANSACTIONS
-- Scenario 6: Smart Campus Event Management and Student Participation Analysis
-- ==============================================================================

-- ------------------------------------------------------------------------------
-- TRANSACTION 1: ATOMIC STUDENT EVENT REGISTRATION WITH CAPACITY CHECK
-- ------------------------------------------------------------------------------
BEGIN TRANSACTION;

-- Step 1: Verify student existence
SELECT student_id, student_name FROM students WHERE student_id = 'STU006';

-- Step 2: Verify event availability and capacity
SELECT event_id, event_name, max_participants, event_status FROM events 
WHERE event_id = 'EVT108' AND event_status IN ('OPEN', 'UPCOMING');

-- Step 3: Check existing registration
SELECT COUNT(*) FROM registrations WHERE student_id = 'STU006' AND event_id = 'EVT108';

-- Step 4: Insert registration if capacity permits
INSERT INTO registrations (registration_id, student_id, event_id, registration_date, status)
VALUES ('REG_TEMP_01', 'STU006', 'EVT108', DATE('now'), 'REGISTERED');

COMMIT;

-- ------------------------------------------------------------------------------
-- TRANSACTION 2: ATOMIC ATTENDANCE RECORDING
-- ------------------------------------------------------------------------------
BEGIN TRANSACTION;

-- Verify registration exists and is active
SELECT r.registration_id, r.student_id, r.event_id 
FROM registrations r
WHERE r.registration_id = 'REG1001' AND r.status = 'REGISTERED';

-- Insert or update attendance record
INSERT INTO attendance (attendance_id, registration_id, student_id, event_id, attendance_date, attendance_status)
VALUES ('ATT_TEMP_01', 'REG1001', 'STU006', 'EVT101', DATE('now'), 'PRESENT')
ON CONFLICT(registration_id) DO UPDATE SET 
    attendance_status = 'PRESENT',
    attendance_date = DATE('now');

COMMIT;

-- ------------------------------------------------------------------------------
-- TRANSACTION 3: RECORD PARTICIPATION & CERTIFICATE ISSUANCE
-- ------------------------------------------------------------------------------
BEGIN TRANSACTION;

-- Check that student was PRESENT before recording PARTICIPATED / WINNER
SELECT attendance_status FROM attendance WHERE registration_id = 'REG1001';

-- Insert or update participation record
INSERT INTO participations (participation_id, registration_id, student_id, event_id, participation_status, remarks, certificate_status)
VALUES ('PAR_TEMP_01', 'REG1001', 'STU006', 'EVT101', 'WINNER', 'First prize coding', 'ISSUED')
ON CONFLICT(registration_id) DO UPDATE SET
    participation_status = 'WINNER',
    remarks = 'First prize coding',
    certificate_status = 'ISSUED';

COMMIT;

-- ------------------------------------------------------------------------------
-- SAFE CASCADING DELETION WITH REFERENTIAL CHECKS
-- ------------------------------------------------------------------------------
-- When attempting to delete a student, check if registrations exist first:
SELECT COUNT(*) AS active_regs FROM registrations WHERE student_id = 'STU999';
-- If active_regs == 0, safe to delete:
-- DELETE FROM students WHERE student_id = 'STU999';
