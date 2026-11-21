-- ==============================================================================
-- SMART CAMPUS EVENT MANAGEMENT SYSTEM - ANALYTICAL & REPORTING SQL QUERIES
-- Scenario 6: Smart Campus Event Management and Student Participation Analysis
-- Register Numbers: 6, 36, 66, 96
-- ==============================================================================

-- ------------------------------------------------------------------------------
-- 1. OVERALL DASHBOARD METRICS
-- ------------------------------------------------------------------------------
SELECT 
    (SELECT COUNT(*) FROM students) AS total_students,
    (SELECT COUNT(*) FROM events) AS total_events,
    (SELECT COUNT(*) FROM registrations) AS total_registrations,
    (SELECT COUNT(*) FROM registrations WHERE status = 'REGISTERED') AS active_registrations,
    (SELECT COUNT(*) FROM registrations WHERE status = 'CANCELLED') AS cancelled_registrations,
    (SELECT COUNT(*) FROM registrations WHERE status = 'WAITLISTED') AS waitlisted_registrations,
    (SELECT COUNT(*) FROM attendance WHERE attendance_status = 'PRESENT') AS total_present,
    (SELECT COUNT(*) FROM attendance WHERE attendance_status = 'ABSENT') AS total_absent,
    (SELECT COUNT(*) FROM participations) AS total_participations,
    (SELECT COUNT(*) FROM events WHERE event_status = 'COMPLETED') AS completed_events,
    (SELECT COUNT(*) FROM events WHERE event_status IN ('OPEN', 'UPCOMING')) AS upcoming_events,
    (SELECT COUNT(*) FROM events WHERE event_status = 'ONGOING') AS ongoing_events,
    (SELECT COUNT(*) FROM events WHERE event_status = 'CANCELLED') AS cancelled_events;

-- ------------------------------------------------------------------------------
-- 2. STUDENT PARTICIPATION OVERVIEW
-- ------------------------------------------------------------------------------
SELECT 
    COUNT(s.student_id) AS total_registered_students,
    COUNT(DISTINCT r.student_id) AS students_with_registrations,
    (COUNT(s.student_id) - COUNT(DISTINCT r.student_id)) AS students_with_zero_registrations,
    COUNT(DISTINCT p.student_id) AS students_participated,
    COUNT(DISTINCT CASE WHEN a.attendance_status = 'ABSENT' THEN a.student_id END) AS students_absent,
    (
        SELECT COUNT(*) FROM (
            SELECT student_id FROM participations GROUP BY student_id HAVING COUNT(*) > 1
        )
    ) AS students_with_multiple_participations
FROM students s
LEFT JOIN registrations r ON s.student_id = r.student_id AND r.status = 'REGISTERED'
LEFT JOIN attendance a ON r.registration_id = a.registration_id
LEFT JOIN participations p ON r.registration_id = p.registration_id;

-- ------------------------------------------------------------------------------
-- 3. DEPARTMENT-WISE SUMMARY
-- ------------------------------------------------------------------------------
SELECT 
    s.department,
    COUNT(DISTINCT s.student_id) AS student_count,
    COUNT(DISTINCT CASE WHEN r.status = 'REGISTERED' THEN r.student_id END) AS registered_student_count,
    COUNT(CASE WHEN r.status = 'REGISTERED' THEN 1 END) AS total_registrations,
    COUNT(CASE WHEN a.attendance_status = 'PRESENT' THEN 1 END) AS present_count,
    COUNT(CASE WHEN p.participation_status IN ('PARTICIPATED', 'WINNER', 'RUNNER_UP') THEN 1 END) AS participation_count
FROM students s
LEFT JOIN registrations r ON s.student_id = r.student_id
LEFT JOIN attendance a ON r.registration_id = a.registration_id
LEFT JOIN participations p ON r.registration_id = p.registration_id
GROUP BY s.department
ORDER BY participation_count DESC, total_registrations DESC;

-- ------------------------------------------------------------------------------
-- 4. MOST POPULAR & LEAST POPULAR EVENTS (Active Registrations)
-- ------------------------------------------------------------------------------
-- Most Popular:
SELECT 
    e.event_id,
    e.event_name,
    e.event_type,
    e.max_participants,
    COUNT(CASE WHEN r.status = 'REGISTERED' THEN 1 END) AS active_registrations,
    ROUND((COUNT(CASE WHEN r.status = 'REGISTERED' THEN 1 END) * 100.0) / e.max_participants, 2) AS capacity_pct
FROM events e
LEFT JOIN registrations r ON e.event_id = r.event_id
GROUP BY e.event_id
ORDER BY active_registrations DESC, e.event_name ASC
LIMIT 5;

-- Least Popular:
SELECT 
    e.event_id,
    e.event_name,
    e.event_type,
    e.max_participants,
    COUNT(CASE WHEN r.status = 'REGISTERED' THEN 1 END) AS active_registrations
FROM events e
LEFT JOIN registrations r ON e.event_id = r.event_id
WHERE e.event_status != 'CANCELLED'
GROUP BY e.event_id
ORDER BY active_registrations ASC, e.event_name ASC
LIMIT 5;

-- ------------------------------------------------------------------------------
-- 5. TOP PARTICIPATING STUDENTS (Ranked)
-- ------------------------------------------------------------------------------
SELECT 
    DENSE_RANK() OVER (ORDER BY COUNT(DISTINCT p.event_id) DESC) AS rank,
    s.student_id,
    s.student_name,
    s.department,
    s.year_of_study,
    COUNT(DISTINCT p.event_id) AS total_events_participated,
    COUNT(CASE WHEN p.participation_status = 'WINNER' THEN 1 END) AS total_wins,
    COUNT(CASE WHEN p.participation_status = 'RUNNER_UP' THEN 1 END) AS total_runner_ups
FROM students s
JOIN participations p ON s.student_id = p.student_id
WHERE p.participation_status IN ('PARTICIPATED', 'WINNER', 'RUNNER_UP', 'PARTICIPANT')
GROUP BY s.student_id
ORDER BY total_events_participated DESC, total_wins DESC, s.student_name ASC
LIMIT 10;

-- ------------------------------------------------------------------------------
-- 6. STUDENTS WITH ZERO PARTICIPATION
-- ------------------------------------------------------------------------------
SELECT 
    s.student_id,
    s.register_number,
    s.student_name,
    s.department,
    s.year_of_study,
    COUNT(r.registration_id) AS total_registrations
FROM students s
LEFT JOIN registrations r ON s.student_id = r.student_id
LEFT JOIN participations p ON r.registration_id = p.registration_id
WHERE p.participation_id IS NULL
GROUP BY s.student_id
ORDER BY s.student_id ASC;

-- ------------------------------------------------------------------------------
-- 7. EVENT ATTENDANCE PERCENTAGE ANALYSIS
-- ------------------------------------------------------------------------------
SELECT 
    e.event_id,
    e.event_name,
    e.event_date,
    COUNT(CASE WHEN r.status = 'REGISTERED' THEN 1 END) AS registered_count,
    COUNT(CASE WHEN a.attendance_status = 'PRESENT' THEN 1 END) AS present_count,
    COUNT(CASE WHEN a.attendance_status = 'ABSENT' THEN 1 END) AS absent_count,
    CASE 
        WHEN COUNT(a.attendance_id) = 0 THEN 0.0
        ELSE ROUND((COUNT(CASE WHEN a.attendance_status = 'PRESENT' THEN 1 END) * 100.0) / COUNT(a.attendance_id), 2)
    END AS attendance_pct
FROM events e
LEFT JOIN registrations r ON e.event_id = r.event_id AND r.status = 'REGISTERED'
LEFT JOIN attendance a ON r.registration_id = a.registration_id
WHERE e.event_status = 'COMPLETED'
GROUP BY e.event_id
ORDER BY attendance_pct DESC;

-- ------------------------------------------------------------------------------
-- 8. EVENT CAPACITY UTILIZATION & AVAILABLE SEATS
-- ------------------------------------------------------------------------------
SELECT 
    e.event_id,
    e.event_name,
    e.max_participants AS capacity,
    COUNT(CASE WHEN r.status = 'REGISTERED' THEN 1 END) AS registered,
    MAX(0, e.max_participants - COUNT(CASE WHEN r.status = 'REGISTERED' THEN 1 END)) AS available_seats,
    ROUND((COUNT(CASE WHEN r.status = 'REGISTERED' THEN 1 END) * 100.0) / e.max_participants, 2) AS utilization_pct,
    CASE 
        WHEN COUNT(CASE WHEN r.status = 'REGISTERED' THEN 1 END) >= e.max_participants THEN 'FULL'
        WHEN COUNT(CASE WHEN r.status = 'REGISTERED' THEN 1 END) >= (e.max_participants * 0.8) THEN 'NEARLY FULL'
        ELSE 'AVAILABLE'
    END AS capacity_status
FROM events e
LEFT JOIN registrations r ON e.event_id = r.event_id
WHERE e.event_status IN ('OPEN', 'UPCOMING', 'ONGOING')
GROUP BY e.event_id
ORDER BY utilization_pct DESC;

-- ------------------------------------------------------------------------------
-- 9. CERTIFICATE ELIGIBILITY QUERY
-- Rule: Present in Attendance AND Participated/Winner/RunnerUp in Participation
-- ------------------------------------------------------------------------------
SELECT 
    s.student_id,
    s.student_name,
    s.department,
    e.event_id,
    e.event_name,
    p.participation_status,
    p.certificate_status,
    a.attendance_status
FROM registrations r
JOIN students s ON r.student_id = s.student_id
JOIN events e ON r.event_id = e.event_id
JOIN attendance a ON r.registration_id = a.registration_id
JOIN participations p ON r.registration_id = p.registration_id
WHERE a.attendance_status = 'PRESENT' 
  AND p.participation_status IN ('PARTICIPATED', 'WINNER', 'RUNNER_UP', 'PARTICIPANT')
ORDER BY s.student_name ASC, e.event_date ASC;

-- ------------------------------------------------------------------------------
-- 10. DAILY / WEEKLY / MONTHLY REGISTRATION TRENDS
-- ------------------------------------------------------------------------------
SELECT 
    registration_date,
    COUNT(*) AS total_registrations,
    COUNT(CASE WHEN status = 'REGISTERED' THEN 1 END) AS active_registrations,
    COUNT(CASE WHEN status = 'CANCELLED' THEN 1 END) AS cancelled_registrations
FROM registrations
GROUP BY registration_date
ORDER BY registration_date ASC;
