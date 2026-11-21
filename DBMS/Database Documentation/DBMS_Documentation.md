# DATABASE MANAGEMENT SYSTEMS (25ITT31) - LABORATORY & PROJECT DOCUMENTATION
## Scenario 6: Smart Campus Event Management and Student Participation Analysis System
### Student Register Numbers: 6, 36, 66, 96
### Department: Information Technology, Velammal College of Engineering and Technology

---

## 1. System Architecture & Schema Design (Unit 1)
The Smart Campus Event Management System database is modeled to capture event logistics, student admissions, attendance tracking, and competitive participation analytics.
- **Entity Sets**:
  - `STUDENTS`: Strong entity capturing student credentials, roll numbers, and academic branch.
  - `EVENTS`: Strong entity defining campus events, venue scheduling, categories, and capacities.
  - `REGISTRATIONS`: Associative entity tracking event enrollment status (`REGISTERED`, `CANCELLED`, `WAITLISTED`).
  - `ATTENDANCE`: Verifiable transactional entity for physical presence (`PRESENT`, `ABSENT`).
  - `PARTICIPATIONS`: Achievement and activity outcome entity (`WINNER`, `RUNNER_UP`, `PARTICIPATED`, `NOT_PARTICIPATED`).

---

## 2. Relational Query Language & Normalization (Units 2 & 3)
All tables satisfy **Boyce-Codd Normal Form (BCNF)**:
1. Every determinant in every non-trivial functional dependency is a superkey.
2. No partial dependencies ($1NF \to 2NF$).
3. No transitive dependencies ($2NF \to 3NF$).
4. Redundancy is minimized and multi-valued dependencies are isolated across associative entities.

---

## 3. Transaction Management & Concurrency Control (Unit 4)
The database enforces strict **ACID Properties**:
- **Atomicity**: Multi-step registration (capacity check $\to$ duplicate verification $\to$ insertion) runs in isolated transactions with atomic `COMMIT` or `ROLLBACK`.
- **Consistency**: Foreign keys with `PRAGMA foreign_keys = ON` guarantee referential consistency.
- **Isolation**: Serialized execution prevents lost updates when multiple students register simultaneously for limited capacity seats.
- **Durability**: Write-Ahead Logging (WAL) ensures committed records persist across power interruptions.

---

## 4. Query Optimization & Indexing Strategies (Unit 3 & 5)
B-Tree indexes are established on frequently filtered columns:
- `idx_reg_student`, `idx_reg_event`, `idx_reg_status`
- `idx_att_event`, `idx_part_student`
Views (`v_event_overview`, `v_student_participation_summary`, `v_attendance_overview`, `v_department_summary`) pre-aggregate analytical queries to provide instantaneous responses on CMD dashboards.
