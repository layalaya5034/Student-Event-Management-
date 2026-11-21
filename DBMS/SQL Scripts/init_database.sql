-- ==============================================================================
-- SMART CAMPUS EVENT MANAGEMENT SYSTEM - SEED DATA SCRIPT
-- Scenario 6: Smart Campus Event Management and Student Participation Analysis
-- Register Numbers: 6, 36, 66, 96
-- ==============================================================================

-- ------------------------------------------------------------------------------
-- INSERT STUDENTS (Including Register Numbers 6, 36, 66, 96)
-- ------------------------------------------------------------------------------
INSERT OR IGNORE INTO students (student_id, register_number, student_name, department, year_of_study, email, phone) VALUES
('STU006', 6,  'Arun Kumar S',        'Information Technology',       3, 'arunkumar.it@vcet.edu',      '9876543210'),
('STU036', 36, 'Bhavana Ramesh',      'Computer Science',             2, 'bhavana.cse@vcet.edu',       '9876543211'),
('STU066', 66, 'Dhivya Prakash V',    'Electronics and Communication',3, 'dhivya.ece@vcet.edu',        '9876543212'),
('STU096', 96, 'Karthikeyan M',       'Information Technology',       4, 'karthikeyan.it@vcet.edu',    '9876543213'),
('STU101', 101,'Ravi Kumar K',        'Information Technology',       3, 'ravikumar.it@vcet.edu',      '9876543214'),
('STU102', 102,'Priya Dharshini',     'Computer Science',             3, 'priyadharshini.cse@vcet.edu','9876543215'),
('STU103', 103,'Karthik Sundaram',    'Electronics and Communication',2, 'karthik.ece@vcet.edu',       '9876543216'),
('STU104', 104,'Anitha Selvaraj',     'Electrical and Electronics',   3, 'anitha.eee@vcet.edu',        '9876543217'),
('STU105', 105,'Manoj Prabhakar',     'Mechanical',                   4, 'manoj.mech@vcet.edu',        '9876543218'),
('STU106', 106,'Suresh Rajan',        'Civil',                        2, 'suresh.civil@vcet.edu',      '9876543219'),
('STU107', 107,'Deepika Nair',        'Information Technology',       2, 'deepika.it@vcet.edu',        '9876543220'),
('STU108', 108,'Gowtham Chandran',    'Computer Science',             4, 'gowtham.cse@vcet.edu',       '9876543221'),
('STU109', 109,'Swetha Krishnan',     'Electronics and Communication',3, 'swetha.ece@vcet.edu',        '9876543222'),
('STU110', 110,'Vignesh Subramanian', 'Electrical and Electronics',   2, 'vignesh.eee@vcet.edu',       '9876543223'),
('STU111', 111,'Aravind Balaji',      'Mechanical',                   3, 'aravind.mech@vcet.edu',      '9876543224'),
('STU112', 112,'Harini Venkat',       'Civil',                        4, 'harini.civil@vcet.edu',      '9876543225'),
('STU113', 113,'Sanjay Mohan',        'Information Technology',       1, 'sanjay.it@vcet.edu',         '9876543226'),
('STU114', 114,'Keerthana Natarajan', 'Computer Science',             1, 'keerthana.cse@vcet.edu',     '9876543227'),
('STU115', 115,'Naveen Prasath',      'Electronics and Communication',4, 'naveen.ece@vcet.edu',        '9876543228'),
('STU116', 116,'Pavithra Devi',       'Electrical and Electronics',   1, 'pavithra.eee@vcet.edu',      '9876543229'),
('STU117', 117,'Dinesh Kumar',        'Mechanical',                   2, 'dinesh.mech@vcet.edu',       '9876543230'),
('STU118', 118,'Meenakshi Sundaram',  'Civil',                        3, 'meenakshi.civil@vcet.edu',   '9876543231'),
('STU119', 119,'Madhavan Parthiban',  'Information Technology',       4, 'madhavan.it@vcet.edu',       '9876543232'),
('STU120', 120,'Divya Bharathi',      'Computer Science',             3, 'divyab.cse@vcet.edu',        '9876543233'),
('STU121', 121,'Akash Vijay',         'Information Technology',       2, 'akash.it@vcet.edu',          '9876543234'),
('STU122', 122,'Janani Radhakrishnan','Computer Science',             2, 'janani.cse@vcet.edu',        '9876543235'),
('STU123', 123,'Logeshwaran M',       'Electronics and Communication',3, 'logesh.ece@vcet.edu',        '9876543236'),
('STU124', 124,'Nandhini Ganesan',    'Electrical and Electronics',   4, 'nandhini.eee@vcet.edu',      '9876543237'),
('STU125', 125,'Prasanna Venkatesh',  'Mechanical',                   1, 'prasanna.mech@vcet.edu',     '9876543238'),
('STU126', 126,'Revathi Saravanan',   'Civil',                        1, 'revathi.civil@vcet.edu',     '9876543239'),
('STU127', 127,'Saravanan Velu',      'Information Technology',       3, 'saravanan.it@vcet.edu',      '9876543240'),
('STU128', 128,'Tharani Murugan',     'Computer Science',             4, 'tharani.cse@vcet.edu',       '9876543241'),
('STU129', 129,'Varun Chakravarthy',  'Electronics and Communication',2, 'varun.ece@vcet.edu',         '9876543242'),
('STU130', 130,'Yamini Anand',        'Electrical and Electronics',   3, 'yamini.eee@vcet.edu',        '9876543243');

-- ------------------------------------------------------------------------------
-- INSERT EVENTS (Diverse statuses, categories, and capacities)
-- ------------------------------------------------------------------------------
INSERT OR IGNORE INTO events (event_id, event_name, event_type, event_date, venue, max_participants, event_status, description) VALUES
('EVT101', 'Technical Symposium',      'Symposium',   '2026-11-10', 'Auditorium',        50, 'COMPLETED', 'Annual state-level technical symposium with paper presentation and coding.'),
('EVT102', 'Python Workshop',          'Workshop',    '2026-11-12', 'Lab 2 (Computing)', 20, 'COMPLETED', 'Hands-on Python and Data Science workshop using NumPy and Pandas.'),
('EVT103', 'Cultural Fest - Choreo',   'Cultural',    '2026-11-15', 'Open Air Theatre',  40, 'COMPLETED', 'Inter-collegiate cultural celebration and dance events.'),
('EVT104', 'Coding Competition',       'Competition', '2026-11-18', 'Lab 1 (MCA)',       25, 'COMPLETED', '3-hour competitive programming challenge in C++, Java, and Python.'),
('EVT105', 'AI & Deep Learning Talk',  'Seminar',     '2026-11-20', 'Seminar Hall 1',    30, 'COMPLETED', 'Distinguished lecture on Generative AI and Transformer architectures.'),
('EVT106', 'Web Dev Hackathon',        'Technical',   '2026-11-21', 'Innovation Hub',    20, 'ONGOING',   '24-hour full-stack web application development hackathon.'),
('EVT107', 'RoboWars Challenge',       'Competition', '2026-11-21', 'Indoor Sports Hall',15, 'ONGOING',   'Combat robotics and autonomous maze traversal tournament.'),
('EVT108', 'Cloud Computing Bootcamp', 'Workshop',    '2026-11-22', 'Lab 3 (Networking)',12, 'OPEN',      'Deploying microservices on AWS and Google Cloud Platform.'),
('EVT109', 'Sports Meet - Badminton',  'Sports',      '2026-11-22', 'Badminton Court',   16, 'OPEN',      'Annual intra-college singles and doubles badminton championship.'),
('EVT110', 'IoT Innovation Expo',      'Technical',   '2026-11-22', 'ECE Project Lab',   10, 'OPEN',      'Showcase of smart sensor nodes, microcontrollers, and edge computing.'),
('EVT111', 'Cyber Security Conclave',  'Symposium',   '2026-11-25', 'Auditorium',        40, 'UPCOMING',  'National conference on zero-trust architectures and ethical hacking.'),
('EVT112', 'Data Analytics Masterclass','Workshop',   '2026-11-27', 'Lab 2 (Computing)', 15, 'UPCOMING',  'Applied exploratory data analysis and time-series forecasting.'),
('EVT113', 'Guest Lecture: VLSI Design','Seminar',    '2026-11-28', 'Seminar Hall 2',    25, 'UPCOMING',  'Silicon design methodologies and modern FPGA synthesis.'),
('EVT114', 'Green Energy Symposium',   'Symposium',   '2026-11-30', 'Auditorium',        35, 'UPCOMING',  'Innovations in renewable power distribution and battery tech.'),
('EVT115', 'AutoCAD 3D Modeling Lab',  'Workshop',    '2026-12-02', 'CAD Lab',           15, 'UPCOMING',  'Advanced generative mechanical modeling and thermal simulation.'),
('EVT116', 'Smart City Infrastructure','Seminar',     '2026-12-05', 'Civil Seminar Room',30, 'UPCOMING',  'BIM modeling and resilient urban civil design.'),
('EVT117', 'Gaming Tournament - E-Sports','Other',    '2026-11-14', 'Campus Center',     20, 'CANCELLED', 'Event cancelled due to network maintenance schedule.'),
('EVT118', 'Photography Showcase',     'Cultural',    '2026-12-10', 'Art Gallery',       25, 'UPCOMING',  'Campus life through the lens: exhibition and judging.'),
('EVT119', 'General Seminar on Ethics','Seminar',     '2026-12-12', 'Seminar Hall 1',    20, 'UPCOMING',  'Professional ethics and academic integrity seminar.'),
('EVT120', 'Drone Flying Competition', 'Competition', '2026-11-16', 'College Grounds',   15, 'CANCELLED', 'Cancelled due to adverse weather warnings.');

-- ------------------------------------------------------------------------------
-- INSERT REGISTRATIONS
-- ------------------------------------------------------------------------------
INSERT OR IGNORE INTO registrations (registration_id, student_id, event_id, registration_date, status) VALUES
-- Event 101 (Technical Symposium)
('REG1001', 'STU006', 'EVT101', '2026-11-01', 'REGISTERED'),
('REG1002', 'STU036', 'EVT101', '2026-11-01', 'REGISTERED'),
('REG1003', 'STU066', 'EVT101', '2026-11-01', 'REGISTERED'),
('REG1004', 'STU096', 'EVT101', '2026-11-02', 'REGISTERED'),
('REG1005', 'STU101', 'EVT101', '2026-11-02', 'REGISTERED'),
('REG1006', 'STU102', 'EVT101', '2026-11-02', 'REGISTERED'),
('REG1007', 'STU103', 'EVT101', '2026-11-03', 'REGISTERED'),
('REG1008', 'STU104', 'EVT101', '2026-11-03', 'REGISTERED'),
('REG1009', 'STU105', 'EVT101', '2026-11-03', 'REGISTERED'),
('REG1010', 'STU107', 'EVT101', '2026-11-04', 'REGISTERED'),
('REG1011', 'STU108', 'EVT101', '2026-11-04', 'REGISTERED'),
('REG1012', 'STU109', 'EVT101', '2026-11-04', 'REGISTERED'),
('REG1013', 'STU110', 'EVT101', '2026-11-05', 'REGISTERED'),
('REG1014', 'STU111', 'EVT101', '2026-11-05', 'REGISTERED'),
('REG1015', 'STU113', 'EVT101', '2026-11-05', 'CANCELLED'),
('REG1016', 'STU114', 'EVT101', '2026-11-05', 'REGISTERED'),
('REG1017', 'STU115', 'EVT101', '2026-11-06', 'REGISTERED'),
('REG1018', 'STU117', 'EVT101', '2026-11-06', 'REGISTERED'),
('REG1019', 'STU119', 'EVT101', '2026-11-06', 'REGISTERED'),
('REG1020', 'STU120', 'EVT101', '2026-11-06', 'REGISTERED'),

-- Event 102 (Python Workshop - Max 20, Full!)
('REG1021', 'STU006', 'EVT102', '2026-11-02', 'REGISTERED'),
('REG1022', 'STU036', 'EVT102', '2026-11-02', 'REGISTERED'),
('REG1023', 'STU066', 'EVT102', '2026-11-02', 'REGISTERED'),
('REG1024', 'STU096', 'EVT102', '2026-11-03', 'REGISTERED'),
('REG1025', 'STU101', 'EVT102', '2026-11-03', 'REGISTERED'),
('REG1026', 'STU102', 'EVT102', '2026-11-03', 'REGISTERED'),
('REG1027', 'STU103', 'EVT102', '2026-11-04', 'REGISTERED'),
('REG1028', 'STU104', 'EVT102', '2026-11-04', 'REGISTERED'),
('REG1029', 'STU107', 'EVT102', '2026-11-04', 'REGISTERED'),
('REG1030', 'STU108', 'EVT102', '2026-11-05', 'REGISTERED'),
('REG1031', 'STU109', 'EVT102', '2026-11-05', 'REGISTERED'),
('REG1032', 'STU110', 'EVT102', '2026-11-05', 'REGISTERED'),
('REG1033', 'STU113', 'EVT102', '2026-11-06', 'REGISTERED'),
('REG1034', 'STU114', 'EVT102', '2026-11-06', 'REGISTERED'),
('REG1035', 'STU115', 'EVT102', '2026-11-06', 'REGISTERED'),
('REG1036', 'STU119', 'EVT102', '2026-11-07', 'REGISTERED'),
('REG1037', 'STU120', 'EVT102', '2026-11-07', 'REGISTERED'),
('REG1038', 'STU121', 'EVT102', '2026-11-07', 'REGISTERED'),
('REG1039', 'STU122', 'EVT102', '2026-11-08', 'REGISTERED'),
('REG1040', 'STU123', 'EVT102', '2026-11-08', 'REGISTERED'),
('REG1041', 'STU124', 'EVT102', '2026-11-08', 'WAITLISTED'),
('REG1042', 'STU125', 'EVT102', '2026-11-08', 'WAITLISTED'),

-- Event 103 (Cultural Fest)
('REG1043', 'STU006', 'EVT103', '2026-11-05', 'REGISTERED'),
('REG1044', 'STU036', 'EVT103', '2026-11-05', 'REGISTERED'),
('REG1045', 'STU066', 'EVT103', '2026-11-06', 'REGISTERED'),
('REG1046', 'STU101', 'EVT103', '2026-11-06', 'REGISTERED'),
('REG1047', 'STU102', 'EVT103', '2026-11-06', 'REGISTERED'),
('REG1048', 'STU105', 'EVT103', '2026-11-07', 'REGISTERED'),
('REG1049', 'STU106', 'EVT103', '2026-11-07', 'REGISTERED'),
('REG1050', 'STU111', 'EVT103', '2026-11-07', 'REGISTERED'),
('REG1051', 'STU112', 'EVT103', '2026-11-08', 'REGISTERED'),
('REG1052', 'STU117', 'EVT103', '2026-11-08', 'CANCELLED'),

-- Event 104 (Coding Competition)
('REG1053', 'STU006', 'EVT104', '2026-11-08', 'REGISTERED'),
('REG1054', 'STU036', 'EVT104', '2026-11-08', 'REGISTERED'),
('REG1055', 'STU096', 'EVT104', '2026-11-09', 'REGISTERED'),
('REG1056', 'STU101', 'EVT104', '2026-11-09', 'REGISTERED'),
('REG1057', 'STU102', 'EVT104', '2026-11-09', 'REGISTERED'),
('REG1058', 'STU108', 'EVT104', '2026-11-10', 'REGISTERED'),
('REG1059', 'STU113', 'EVT104', '2026-11-10', 'REGISTERED'),
('REG1060', 'STU114', 'EVT104', '2026-11-10', 'REGISTERED'),
('REG1061', 'STU119', 'EVT104', '2026-11-11', 'REGISTERED'),
('REG1062', 'STU120', 'EVT104', '2026-11-11', 'REGISTERED'),
('REG1063', 'STU121', 'EVT104', '2026-11-11', 'REGISTERED'),
('REG1064', 'STU122', 'EVT104', '2026-11-11', 'REGISTERED'),

-- Event 105 (AI & Deep Learning Talk)
('REG1065', 'STU006', 'EVT105', '2026-11-10', 'REGISTERED'),
('REG1066', 'STU036', 'EVT105', '2026-11-10', 'REGISTERED'),
('REG1067', 'STU066', 'EVT105', '2026-11-11', 'REGISTERED'),
('REG1068', 'STU096', 'EVT105', '2026-11-11', 'REGISTERED'),
('REG1069', 'STU101', 'EVT105', '2026-11-11', 'REGISTERED'),
('REG1070', 'STU103', 'EVT105', '2026-11-12', 'REGISTERED'),
('REG1071', 'STU107', 'EVT105', '2026-11-12', 'REGISTERED'),
('REG1072', 'STU109', 'EVT105', '2026-11-12', 'REGISTERED'),
('REG1073', 'STU123', 'EVT105', '2026-11-13', 'REGISTERED'),

-- Ongoing Events (EVT106, EVT107)
('REG1074', 'STU006', 'EVT106', '2026-11-15', 'REGISTERED'),
('REG1075', 'STU036', 'EVT106', '2026-11-15', 'REGISTERED'),
('REG1076', 'STU096', 'EVT106', '2026-11-16', 'REGISTERED'),
('REG1077', 'STU101', 'EVT106', '2026-11-16', 'REGISTERED'),
('REG1078', 'STU102', 'EVT106', '2026-11-16', 'REGISTERED'),
('REG1079', 'STU066', 'EVT107', '2026-11-15', 'REGISTERED'),
('REG1080', 'STU103', 'EVT107', '2026-11-15', 'REGISTERED'),
('REG1081', 'STU105', 'EVT107', '2026-11-16', 'REGISTERED'),
('REG1082', 'STU111', 'EVT107', '2026-11-16', 'REGISTERED'),

-- Tomorrow's Events (EVT108, EVT109, EVT110 - Max reach / alerts)
('REG1083', 'STU006', 'EVT108', '2026-11-18', 'REGISTERED'),
('REG1084', 'STU036', 'EVT108', '2026-11-18', 'REGISTERED'),
('REG1085', 'STU096', 'EVT108', '2026-11-19', 'REGISTERED'),
('REG1086', 'STU101', 'EVT108', '2026-11-19', 'REGISTERED'),
('REG1087', 'STU102', 'EVT108', '2026-11-19', 'REGISTERED'),
('REG1088', 'STU107', 'EVT108', '2026-11-20', 'REGISTERED'),
('REG1089', 'STU108', 'EVT108', '2026-11-20', 'REGISTERED'),
('REG1090', 'STU119', 'EVT108', '2026-11-20', 'REGISTERED'),
('REG1091', 'STU120', 'EVT108', '2026-11-20', 'REGISTERED'),
('REG1092', 'STU121', 'EVT108', '2026-11-21', 'REGISTERED'),
('REG1093', 'STU122', 'EVT108', '2026-11-21', 'REGISTERED'),
('REG1094', 'STU127', 'EVT108', '2026-11-21', 'REGISTERED'), -- Reached 12/12 max capacity!

('REG1095', 'STU105', 'EVT109', '2026-11-18', 'REGISTERED'),
('REG1096', 'STU111', 'EVT109', '2026-11-19', 'REGISTERED'),
('REG1097', 'STU117', 'EVT109', '2026-11-20', 'REGISTERED'),

-- EVT110 (Low registration alert: only 1 registered out of 10)
('REG1098', 'STU066', 'EVT110', '2026-11-20', 'REGISTERED'),

-- Upcoming (EVT111)
('REG1099', 'STU006', 'EVT111', '2026-11-20', 'REGISTERED'),
('REG1100', 'STU036', 'EVT111', '2026-11-20', 'REGISTERED'),
('REG1101', 'STU096', 'EVT111', '2026-11-21', 'REGISTERED'),
('REG1102', 'STU101', 'EVT111', '2026-11-21', 'REGISTERED');

-- ------------------------------------------------------------------------------
-- INSERT ATTENDANCE (Completed events: EVT101, EVT102, EVT103, EVT104, EVT105)
-- ------------------------------------------------------------------------------
INSERT OR IGNORE INTO attendance (attendance_id, registration_id, student_id, event_id, attendance_date, attendance_status) VALUES
-- EVT101 Attendance (19 active registered)
('ATT1001', 'REG1001', 'STU006', 'EVT101', '2026-11-10', 'PRESENT'),
('ATT1002', 'REG1002', 'STU036', 'EVT101', '2026-11-10', 'PRESENT'),
('ATT1003', 'REG1003', 'STU066', 'EVT101', '2026-11-10', 'PRESENT'),
('ATT1004', 'REG1004', 'STU096', 'EVT101', '2026-11-10', 'PRESENT'),
('ATT1005', 'REG1005', 'STU101', 'EVT101', '2026-11-10', 'PRESENT'),
('ATT1006', 'REG1006', 'STU102', 'EVT101', '2026-11-10', 'PRESENT'),
('ATT1007', 'REG1007', 'STU103', 'EVT101', '2026-11-10', 'PRESENT'),
('ATT1008', 'REG1008', 'STU104', 'EVT101', '2026-11-10', 'PRESENT'),
('ATT1009', 'REG1009', 'STU105', 'EVT101', '2026-11-10', 'PRESENT'),
('ATT1010', 'REG1010', 'STU107', 'EVT101', '2026-11-10', 'PRESENT'),
('ATT1011', 'REG1011', 'STU108', 'EVT101', '2026-11-10', 'PRESENT'),
('ATT1012', 'REG1012', 'STU109', 'EVT101', '2026-11-10', 'PRESENT'),
('ATT1013', 'REG1013', 'STU110', 'EVT101', '2026-11-10', 'ABSENT'),
('ATT1014', 'REG1014', 'STU111', 'EVT101', '2026-11-10', 'PRESENT'),
('ATT1015', 'REG1016', 'STU114', 'EVT101', '2026-11-10', 'PRESENT'),
('ATT1016', 'REG1017', 'STU115', 'EVT101', '2026-11-10', 'ABSENT'),
('ATT1017', 'REG1018', 'STU117', 'EVT101', '2026-11-10', 'PRESENT'),
('ATT1018', 'REG1019', 'STU119', 'EVT101', '2026-11-10', 'PRESENT'),
('ATT1019', 'REG1020', 'STU120', 'EVT101', '2026-11-10', 'PRESENT'),

-- EVT102 Attendance (20 registered)
('ATT1020', 'REG1021', 'STU006', 'EVT102', '2026-11-12', 'PRESENT'),
('ATT1021', 'REG1022', 'STU036', 'EVT102', '2026-11-12', 'PRESENT'),
('ATT1022', 'REG1023', 'STU066', 'EVT102', '2026-11-12', 'PRESENT'),
('ATT1023', 'REG1024', 'STU096', 'EVT102', '2026-11-12', 'PRESENT'),
('ATT1024', 'REG1025', 'STU101', 'EVT102', '2026-11-12', 'PRESENT'),
('ATT1025', 'REG1026', 'STU102', 'EVT102', '2026-11-12', 'PRESENT'),
('ATT1026', 'REG1027', 'STU103', 'EVT102', '2026-11-12', 'PRESENT'),
('ATT1027', 'REG1028', 'STU104', 'EVT102', '2026-11-12', 'ABSENT'),
('ATT1028', 'REG1029', 'STU107', 'EVT102', '2026-11-12', 'PRESENT'),
('ATT1029', 'REG1030', 'STU108', 'EVT102', '2026-11-12', 'PRESENT'),
('ATT1030', 'REG1031', 'STU109', 'EVT102', '2026-11-12', 'PRESENT'),
('ATT1031', 'REG1032', 'STU110', 'EVT102', '2026-11-12', 'PRESENT'),
('ATT1032', 'REG1033', 'STU113', 'EVT102', '2026-11-12', 'ABSENT'),
('ATT1033', 'REG1034', 'STU114', 'EVT102', '2026-11-12', 'PRESENT'),
('ATT1034', 'REG1035', 'STU115', 'EVT102', '2026-11-12', 'PRESENT'),
('ATT1035', 'REG1036', 'STU119', 'EVT102', '2026-11-12', 'PRESENT'),
('ATT1036', 'REG1037', 'STU120', 'EVT102', '2026-11-12', 'PRESENT'),
('ATT1037', 'REG1038', 'STU121', 'EVT102', '2026-11-12', 'PRESENT'),
('ATT1038', 'REG1039', 'STU122', 'EVT102', '2026-11-12', 'PRESENT'),
('ATT1039', 'REG1040', 'STU123', 'EVT102', '2026-11-12', 'PRESENT'),

-- EVT103 Attendance (9 active registered)
('ATT1040', 'REG1043', 'STU006', 'EVT103', '2026-11-15', 'PRESENT'),
('ATT1041', 'REG1044', 'STU036', 'EVT103', '2026-11-15', 'PRESENT'),
('ATT1042', 'REG1045', 'STU066', 'EVT103', '2026-11-15', 'PRESENT'),
('ATT1043', 'REG1046', 'STU101', 'EVT103', '2026-11-15', 'PRESENT'),
('ATT1044', 'REG1047', 'STU102', 'EVT103', '2026-11-15', 'PRESENT'),
('ATT1045', 'REG1048', 'STU105', 'EVT103', '2026-11-15', 'PRESENT'),
('ATT1046', 'REG1049', 'STU106', 'EVT103', '2026-11-15', 'PRESENT'),
('ATT1047', 'REG1050', 'STU111', 'EVT103', '2026-11-15', 'ABSENT'),
('ATT1048', 'REG1051', 'STU112', 'EVT103', '2026-11-15', 'PRESENT'),

-- EVT104 Attendance (12 registered)
('ATT1049', 'REG1053', 'STU006', 'EVT104', '2026-11-18', 'PRESENT'),
('ATT1050', 'REG1054', 'STU036', 'EVT104', '2026-11-18', 'PRESENT'),
('ATT1051', 'REG1055', 'STU096', 'EVT104', '2026-11-18', 'PRESENT'),
('ATT1052', 'REG1056', 'STU101', 'EVT104', '2026-11-18', 'PRESENT'),
('ATT1053', 'REG1057', 'STU102', 'EVT104', '2026-11-18', 'PRESENT'),
('ATT1054', 'REG1058', 'STU108', 'EVT104', '2026-11-18', 'PRESENT'),
('ATT1055', 'REG1059', 'STU113', 'EVT104', '2026-11-18', 'PRESENT'),
('ATT1056', 'REG1060', 'STU114', 'EVT104', '2026-11-18', 'PRESENT'),
('ATT1057', 'REG1061', 'STU119', 'EVT104', '2026-11-18', 'ABSENT'),
('ATT1058', 'REG1062', 'STU120', 'EVT104', '2026-11-18', 'PRESENT'),
('ATT1059', 'REG1063', 'STU121', 'EVT104', '2026-11-18', 'PRESENT'),
('ATT1060', 'REG1064', 'STU122', 'EVT104', '2026-11-18', 'PRESENT'),

-- EVT105 Attendance (9 registered)
('ATT1061', 'REG1065', 'STU006', 'EVT105', '2026-11-20', 'PRESENT'),
('ATT1062', 'REG1066', 'STU036', 'EVT105', '2026-11-20', 'PRESENT'),
('ATT1063', 'REG1067', 'STU066', 'EVT105', '2026-11-20', 'PRESENT'),
('ATT1064', 'REG1068', 'STU096', 'EVT105', '2026-11-20', 'PRESENT'),
('ATT1065', 'REG1069', 'STU101', 'EVT105', '2026-11-20', 'PRESENT'),
('ATT1066', 'REG1070', 'STU103', 'EVT105', '2026-11-20', 'PRESENT'),
('ATT1067', 'REG1071', 'STU107', 'EVT105', '2026-11-20', 'PRESENT'),
('ATT1068', 'REG1072', 'STU109', 'EVT105', '2026-11-20', 'PRESENT'),
('ATT1069', 'REG1073', 'STU123', 'EVT105', '2026-11-20', 'ABSENT');

-- ------------------------------------------------------------------------------
-- INSERT PARTICIPATIONS (Distinguished from attendance!)
-- ------------------------------------------------------------------------------
INSERT OR IGNORE INTO participations (participation_id, registration_id, student_id, event_id, participation_status, remarks, certificate_status) VALUES
-- EVT101 Participations
('PAR1001', 'REG1001', 'STU006', 'EVT101', 'WINNER',          'First place in Paper Presentation', 'ISSUED'),
('PAR1002', 'REG1002', 'STU036', 'EVT101', 'RUNNER_UP',       'Second place in Code Debugging',     'ISSUED'),
('PAR1003', 'REG1003', 'STU066', 'EVT101', 'PARTICIPATED',    'Active presenter in IoT track',     'ISSUED'),
('PAR1004', 'REG1004', 'STU096', 'EVT101', 'PARTICIPATED',    'Contributed to project exhibition', 'ISSUED'),
('PAR1005', 'REG1005', 'STU101', 'EVT101', 'PARTICIPATED',    'Attended and competed in tracks',   'ISSUED'),
('PAR1006', 'REG1006', 'STU102', 'EVT101', 'PARTICIPATED',    'Presented technical poster',        'ISSUED'),
('PAR1007', 'REG1007', 'STU103', 'EVT101', 'PARTICIPATED',    'Participant in quiz prelims',       'ISSUED'),
('PAR1008', 'REG1008', 'STU104', 'EVT101', 'PARTICIPATED',    'Participated in symposium seminars', 'ISSUED'),
('PAR1009', 'REG1009', 'STU105', 'EVT101', 'PARTICIPATED',    'Participated in CAD exhibition',    'ISSUED'),
('PAR1010', 'REG1010', 'STU107', 'EVT101', 'PARTICIPATED',    'Attended sessions',                 'ISSUED'),
('PAR1011', 'REG1011', 'STU108', 'EVT101', 'PARTICIPATED',    'Participated in technical quiz',    'ISSUED'),
('PAR1012', 'REG1012', 'STU109', 'EVT101', 'PARTICIPATED',    'Poster participant',                'ISSUED'),
('PAR1013', 'REG1014', 'STU111', 'EVT101', 'PARTICIPATED',    'Symposium delegate',                'ISSUED'),
('PAR1014', 'REG1016', 'STU114', 'EVT101', 'PARTICIPATED',    'Delegate',                          'ISSUED'),
('PAR1015', 'REG1018', 'STU117', 'EVT101', 'PARTICIPATED',    'Technical delegate',                'ISSUED'),
('PAR1016', 'REG1019', 'STU119', 'EVT101', 'PARTICIPATED',    'Paper participant',                 'ISSUED'),
('PAR1017', 'REG1020', 'STU120', 'EVT101', 'PARTICIPATED',    'Poster delegate',                   'ISSUED'),

-- EVT102 Participations (Python Workshop)
('PAR1018', 'REG1021', 'STU006', 'EVT102', 'PARTICIPATED',    'Completed all Jupyter notebooks',   'ISSUED'),
('PAR1019', 'REG1022', 'STU036', 'EVT102', 'WINNER',          'Best Data Analysis mini-project',   'ISSUED'),
('PAR1020', 'REG1023', 'STU066', 'EVT102', 'PARTICIPATED',    'Completed data visualization tasks','ISSUED'),
('PAR1021', 'REG1024', 'STU096', 'EVT102', 'PARTICIPATED',    'Submitted analysis script',         'ISSUED'),
('PAR1022', 'REG1025', 'STU101', 'EVT102', 'RUNNER_UP',       'Second best EDA submission',        'ISSUED'),
('PAR1023', 'REG1026', 'STU102', 'EVT102', 'PARTICIPATED',    'Completed lab sessions',            'ISSUED'),
('PAR1024', 'REG1027', 'STU103', 'EVT102', 'PARTICIPATED',    'Completed exercises',               'ISSUED'),
('PAR1025', 'REG1029', 'STU107', 'EVT102', 'PARTICIPATED',    'Completed lab work',                'ISSUED'),
('PAR1026', 'REG1030', 'STU108', 'EVT102', 'PARTICIPATED',    'Completed lab work',                'ISSUED'),
('PAR1027', 'REG1031', 'STU109', 'EVT102', 'PARTICIPATED',    'Workshop participant',              'ISSUED'),
('PAR1028', 'REG1034', 'STU114', 'EVT102', 'PARTICIPATED',    'Workshop participant',              'ISSUED'),
('PAR1029', 'REG1035', 'STU115', 'EVT102', 'PARTICIPATED',    'Hands-on attendee',                 'ISSUED'),
('PAR1030', 'REG1036', 'STU119', 'EVT102', 'PARTICIPATED',    'Hands-on attendee',                 'ISSUED'),
('PAR1031', 'REG1037', 'STU120', 'EVT102', 'PARTICIPATED',    'Hands-on attendee',                 'ISSUED'),
('PAR1032', 'REG1038', 'STU121', 'EVT102', 'PARTICIPATED',    'Completed exercises',               'ISSUED'),
('PAR1033', 'REG1039', 'STU122', 'EVT102', 'PARTICIPATED',    'Completed exercises',               'ISSUED'),
('PAR1034', 'REG1040', 'STU123', 'EVT102', 'PARTICIPATED',    'Completed exercises',               'ISSUED'),

-- EVT103 Participations (Cultural Fest)
('PAR1035', 'REG1043', 'STU006', 'EVT103', 'RUNNER_UP',       'Classical solo dance runner-up',    'ISSUED'),
('PAR1036', 'REG1044', 'STU036', 'EVT103', 'WINNER',          'Western group dance winner',        'ISSUED'),
('PAR1037', 'REG1045', 'STU066', 'EVT103', 'PARTICIPATED',    'Choreo crew participant',           'ISSUED'),
('PAR1038', 'REG1046', 'STU101', 'EVT103', 'PARTICIPATED',    'Stage performance participant',     'ISSUED'),
('PAR1039', 'REG1047', 'STU102', 'EVT103', 'PARTICIPATED',    'Vocal music participant',           'ISSUED'),
('PAR1040', 'REG1048', 'STU105', 'EVT103', 'PARTICIPATED',    'Mime team participant',             'ISSUED'),
('PAR1041', 'REG1049', 'STU106', 'EVT103', 'PARTICIPATED',    'Folk dance participant',            'ISSUED'),
('PAR1042', 'REG1051', 'STU112', 'EVT103', 'PARTICIPATED',    'Cultural performer',                'ISSUED'),

-- EVT104 Participations (Coding Competition)
('PAR1043', 'REG1053', 'STU006', 'EVT104', 'WINNER',          '1st Place - Solved 5/5 problems',   'ISSUED'),
('PAR1044', 'REG1054', 'STU036', 'EVT104', 'RUNNER_UP',       '2nd Place - Solved 4/5 problems',   'ISSUED'),
('PAR1045', 'REG1055', 'STU096', 'EVT104', 'PARTICIPATED',    'Solved 3/5 problems',               'ISSUED'),
('PAR1046', 'REG1056', 'STU101', 'EVT104', 'PARTICIPATED',    'Solved 3/5 problems',               'ISSUED'),
('PAR1047', 'REG1057', 'STU102', 'EVT104', 'PARTICIPATED',    'Solved 2/5 problems',               'ISSUED'),
('PAR1048', 'REG1058', 'STU108', 'EVT104', 'PARTICIPATED',    'Solved 2/5 problems',               'ISSUED'),
('PAR1049', 'REG1059', 'STU113', 'EVT104', 'PARTICIPATED',    'Solved 1/5 problems',               'ISSUED'),
('PAR1050', 'REG1060', 'STU114', 'EVT104', 'PARTICIPATED',    'Solved 1/5 problems',               'ISSUED'),
('PAR1051', 'REG1062', 'STU120', 'EVT104', 'PARTICIPATED',    'Coding challenge entrant',          'ISSUED'),
('PAR1052', 'REG1063', 'STU121', 'EVT104', 'PARTICIPATED',    'Coding challenge entrant',          'ISSUED'),
('PAR1053', 'REG1064', 'STU122', 'EVT104', 'PARTICIPATED',    'Coding challenge entrant',          'ISSUED'),

-- EVT105 Participations (AI Seminar)
('PAR1054', 'REG1065', 'STU006', 'EVT105', 'PARTICIPATED',    'Attended seminar & Q&A session',    'ISSUED'),
('PAR1055', 'REG1066', 'STU036', 'EVT105', 'PARTICIPATED',    'Attended seminar & Q&A session',    'ISSUED'),
('PAR1056', 'REG1067', 'STU066', 'EVT105', 'PARTICIPATED',    'Interactive participant',           'ISSUED'),
('PAR1057', 'REG1068', 'STU096', 'EVT105', 'PARTICIPATED',    'Active questioner in LLM topics',   'ISSUED'),
('PAR1058', 'REG1069', 'STU101', 'EVT105', 'PARTICIPATED',    'Seminar attendee',                  'ISSUED'),
('PAR1059', 'REG1070', 'STU103', 'EVT105', 'PARTICIPATED',    'Seminar attendee',                  'ISSUED'),
('PAR1060', 'REG1071', 'STU107', 'EVT105', 'PARTICIPATED',    'Seminar attendee',                  'ISSUED'),
('PAR1061', 'REG1072', 'STU109', 'EVT105', 'PARTICIPATED',    'Seminar attendee',                  'ISSUED');
