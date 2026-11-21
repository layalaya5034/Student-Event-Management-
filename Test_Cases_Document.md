# TEST CASES SPECIFICATION & VERIFICATION REPORT
## Smart Campus Event Management and Student Participation Analysis System
### Scenario 6 | Course Code: 25ITT33 (Software Engineering - Unit 4)
### Academic Year: 2026 | Register Numbers: 6, 36, 66, 96
### Department of Information Technology, Velammal College of Engineering and Technology (Autonomous)

---

## 1. TESTING STRATEGY & METHODOLOGY

In accordance with **Unit 4 (Software Testing)** of the 25ITT33 syllabus, this document outlines the verification and validation (V&V) procedures applied to Scenario 6.

### 1.1 Levels of Testing
1. **Unit Testing**: Validation of individual domain models (`Student`, `Event`, `Registration`, `Attendance`, `Participation`) and utility classes (`InputReader`, `DualLogger`).
2. **Integration Testing**: Verification of JDBC transactional contracts between Java DAO services and the SQLite relational engine (`smart_campus_events.db`).
3. **Validation & System Testing**: End-to-end black-box verification through the centralized Windows CMD menus and batch launchers (`SmartCampusEventManagement.bat`, `run_all.bat`).

### 1.2 Test Design Techniques
- **Black-Box Testing**:
  - *Equivalence Partitioning (EP)*: Testing valid vs. invalid academic years ($1 \le \text{Year} \le 4$), event statuses, and numeric choices.
  - *Boundary Value Analysis (BVA)*: Testing event capacity limits at minimum ($1$), boundary ($\text{Max} - 1$), full ($\text{Max}$), and overflow ($\text{Max} + 1$).
- **White-Box Testing**:
  - *Control Flow Graph (CFG) Analysis*: Mapping predicate nodes in the registration decision logic.
  - *Cyclomatic Complexity ($V(G)$)*: Deriving linearly independent execution paths.

---

## 2. WHITE-BOX TESTING: BASIS PATH ANALYSIS

### 2.1 Registration Validation Algorithm (`RegistrationMenu.java`)
The core registration workflow executes the following predicate checks:
1. **Node 1 (Check Student)**: `SELECT student_name FROM students WHERE student_id = ?`
   - *Condition 1*: If student does not exist $\to$ **Abort (Error)**.
2. **Node 2 (Check Event Status)**: `SELECT max_participants, event_status FROM events WHERE event_id = ?`
   - *Condition 2*: If status is `CANCELLED` or `COMPLETED` $\to$ **Abort (Error)**.
3. **Node 3 (Check Duplicate Registration)**: `SELECT registration_id FROM registrations WHERE student_id = ? AND event_id = ?`
   - *Condition 3*: If active registration exists $\to$ **Abort (Conflict Alert)**.
4. **Node 4 (Check Venue Capacity)**: `SELECT COUNT(*) FROM registrations WHERE event_id = ? AND status = 'REGISTERED'`
   - *Condition 4*: If active registrations $\ge$ `max_participants` $\to$ **Offer Waitlist**.
5. **Node 5 (Atomic Transaction)**: `INSERT INTO registrations ...` followed by `conn.commit()`.

### 2.2 Cyclomatic Complexity Calculation:
$$V(G) = P + 1 = 4 \text{ predicate nodes} + 1 = 5 \text{ linearly independent basis paths}$$

- **Path 1**: Node 1 [Student Not Found] $\to$ Exit with Error.
- **Path 2**: Node 1 [Valid] $\to$ Node 2 [Event Closed/Cancelled] $\to$ Exit with Error.
- **Path 3**: Node 1 [Valid] $\to$ Node 2 [Open] $\to$ Node 3 [Duplicate Detected] $\to$ Exit with Warning.
- **Path 4**: Node 1 [Valid] $\to$ Node 2 [Open] $\to$ Node 3 [Unique] $\to$ Node 4 [Capacity Full] $\to$ Divert to Waitlist.
- **Path 5**: Node 1 [Valid] $\to$ Node 2 [Open] $\to$ Node 3 [Unique] $\to$ Node 4 [Available Seats $> 0$] $\to$ Node 5 [Commit Transaction].

---

## 3. MASTER TEST CASES SUITE (TC-01 TO TC-25)

| Test ID | Module | Test Scenario & Objective | Input Data | Expected Result | Actual Result | Status |
|---|---|---|---|---|---|---|
| **TC-01** | Dashboard | Live Dynamic Aggregation | Select Option 1 (Dashboard) | Query real-time counts for students, events, registrations, and attendance | Real-time counts (34 students, 20 events, 102 registrations) displayed dynamically | **PASSED** |
| **TC-02** | Dashboard | Capacity & Tomorrow Alert Generation | Select Option 1 (Dashboard) | Identify full events, unattened students, and events scheduled for next calendar day | Alerts emitted: 2 full events, 7 unattened students, 3 events tomorrow | **PASSED** |
| **TC-03** | Student | New Student Profile Registration | ID: `STU150`, Reg No: `150`, Name: `Senthil Nathan`, Dept: `IT`, Year: `3` | Student inserted into `students` table | `[SUCCESS] Student registered successfully with ID: STU150` | **PASSED** |
| **TC-04** | Student | Duplicate Student ID Prevention | ID: `STU006` (Existing student) | System rejects insertion | `[ERROR] Student ID already exists! Duplicate IDs are not permitted.` | **PASSED** |
| **TC-05** | Student | Focus Register Number Search | Search: `6` or `STU006` | Retrieve profile of student with Register Number 6 | Profile of Arun Kumar S (Reg No: 6, Dept: IT, Year: 3) displayed | **PASSED** |
| **TC-06** | Student | Referential Integrity on Delete | Delete `STU006` (Has dependent registrations) | Block deletion to protect audit logs | `[ERROR] Cannot delete student 'STU006': Dependent event registration/attendance records exist!` | **PASSED** |
| **TC-07** | Event | New Event Creation | ID: `EVT125`, Name: `AI Hackathon`, Type: `Technical`, Date: `2026-12-05`, Cap: `50` | Event inserted with status `OPEN` | `[SUCCESS] Event created successfully with ID: EVT125` | **PASSED** |
| **TC-08** | Event | Duplicate Event ID Prevention | ID: `EVT101` (Existing event) | Rejection of duplicate primary key | `[ERROR] Event ID 'EVT101' already exists! Event IDs must be unique.` | **PASSED** |
| **TC-09** | Event | Capacity Lowering Guard | Event: `EVT101`, New Cap: `10` (Active registrations = 19) | Reject setting capacity lower than active attendees | `[ERROR] Capacity cannot be set lower than active registrations (19)!` | **PASSED** |
| **TC-10** | Event | Category Filtering | Filter by: `Workshop` | Display only Workshop events (`EVT102`, `EVT108`, `EVT112`, `EVT115`) | Filtered list displayed with count: 4 | **PASSED** |
| **TC-11** | Registration| Standard Event Registration | Student: `STU103`, Event: `EVT111` (Seats available) | Registration recorded with status `REGISTERED` | `[SUCCESS] Student registered for the event successfully! [Reg ID: REG1103]` | **PASSED** |
| **TC-12** | Registration| Duplicate Registration Prevention | Student: `STU006`, Event: `EVT101` (Already registered) | Block repeat registration | `[WARNING] Student 'STU006' is ALREADY actively registered for this event!` | **PASSED** |
| **TC-13** | Registration| Capacity Saturation & Waitlist | Student: `STU124`, Event: `EVT102` (Capacity 20/20 full) | Offer waitlist registration | Prompted for waitlist; registered with status `WAITLISTED` | **PASSED** |
| **TC-14** | Registration| Registration Cancellation | Cancel `REG1015` | Status set to `CANCELLED`; record preserved in DB | `[SUCCESS] Registration REG1015 cancelled successfully. Historical audit preserved.` | **PASSED** |
| **TC-15** | Attendance | Attendance for Unregistered Student | Student: `STU106`, Event: `EVT101` (Not registered) | Rejection of attendance logging | `[ERROR] Student 'STU106' is NOT registered for event 'EVT101'! Attendance cannot be marked.` | **PASSED** |
| **TC-16** | Attendance | Valid Attendance Marking | Reg: `REG1001`, Student: `STU006`, Event: `EVT101`, Status: `PRESENT` | Attendance record inserted in `attendance` table | `[SUCCESS] Attendance recorded successfully! [Attendance ID: ATT1001]` | **PASSED** |
| **TC-17** | Attendance | Duplicate Attendance Prevention | Mark attendance again for `REG1001` | Detect existing attendance | `[WARNING] Attendance already recorded for this registration (Status: PRESENT)!` | **PASSED** |
| **TC-18** | Attendance | Bulk Event Attendance Entry | Event: `EVT106` | Prompt attendance (P/A/S) for all registered attendees sequentially | Bulk attendance recorded in an atomic transaction; success logged | **PASSED** |
| **TC-19** | Attendance | Attendance Rate Calculation | Event: `EVT101` (19 Active, 17 Present, 2 Absent) | Calculate $\frac{17}{19} \times 100 = 89.47\%$ | Output: `Attendance Percentage: 89.47%` | **PASSED** |
| **TC-20** | Participation| Record Activity Achievement | Student: `STU006`, Event: `EVT101`, Outcome: `WINNER`, Remarks: `1st prize` | Participation logged with `certificate_status = ISSUED` | `[SUCCESS] Participation record saved successfully! [Outcome: WINNER, Certificate: ISSUED]` | **PASSED** |
| **TC-21** | Participation| Duplicate Participation Prevention | Record participation again for `REG1001` | Rejection of duplicate outcome record | `[WARNING] Participation record already exists for this registration!` | **PASSED** |
| **TC-22** | Participation| Certificate Qualification Rule | Query students eligible for certificates | Must have `attendance = PRESENT` AND valid participation outcome | Verified list returned containing only physically present attendees | **PASSED** |
| **TC-23** | Reports | Daily & Monthly Reporting | Date: `2026-11-10` | Aggregate events, registrations, attendance, and participations for that day | Aligned summary report generated and saved to `EventAnalysis_Output.txt` | **PASSED** |
| **TC-24** | Python | Subprocess Chart Generation | Python Menu Option 17 (Complete Analysis) | Python 3.13 executes `generate_visualizations.py` and creates 20 PNG charts | 20 charts saved in `Python/Data Visualization/charts/` | **PASSED** |
| **TC-25** | System/UX | Dual Logging & Input Safety | Provide non-numeric input e.g. `ABC` at menu prompt | Catch exception, show warning, re-prompt without terminating | `[ERROR] Invalid numeric input. Please enter a whole number.` Console persists | **PASSED** |

---

## 4. TEST EXECUTION SUMMARY

```
================================================================================
                       MASTER TEST SUITE SUMMARY RESULTS
================================================================================
Total Test Cases Executed : 25
Test Cases Passed         : 25
Test Cases Failed         : 0
Test Cases Blocked        : 0
Overall Pass Rate         : 100.0%

Software Verification    : OpenJDK 17.0.21 (Temurin-17.0.21+9)
Database Verification    : SQLite 3.46.1 with BCNF Foreign Key Integrity
Data Science Suite        : Python 3.13.5 (Pandas, Matplotlib, Seaborn)
Console UX & Logging      : DualLogger (Screen + 9 Output TXT Files)
================================================================================
```

---

## 5. TEST ENVIRONMENT SPECIFICATION

- **Operating System**: Windows 11 (64-bit)
- **Java Runtime Environment**: OpenJDK 17.0.21 (`Temurin-17.0.21+9`)
- **Database Engine**: SQLite 3.46.1 embedded via `sqlite-jdbc-3.46.1.0.jar`
- **Python Environment**: Python 3.13.5 (`Pandas 2.2+`, `Matplotlib 3.8+`, `Seaborn 0.13+`)
- **Execution Shell**: Windows Command Prompt (CMD) via `.bat` launchers
- **Logging Destination**: Local directory `C:\Student event management\Output\`
