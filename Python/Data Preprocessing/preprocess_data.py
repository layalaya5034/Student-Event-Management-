# ==============================================================================
# DATA PREPROCESSING MODULE
# Scenario 6: Smart Campus Event Management and Student Participation Analysis
# ==============================================================================

import os
import sqlite3
import pandas as pd

def get_db_connection():
    """Locate and connect to the SQLite database."""
    base_dir = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", ".."))
    candidate_paths = [
        os.path.join(base_dir, "DBMS", "smart_campus_events.db"),
        os.path.join(base_dir, "smart_campus_events.db"),
        r"C:\Student event management\DBMS\smart_campus_events.db"
    ]
    for path in candidate_paths:
        if os.path.exists(path):
            return sqlite3.connect(path)
    raise FileNotFoundError(f"Database not found. Checked: {candidate_paths}")

def load_and_preprocess_data():
    """Load and clean datasets from SQLite."""
    conn = get_db_connection()
    try:
        # Load tables
        students_df = pd.read_sql_query("SELECT * FROM students", conn)
        events_df = pd.read_sql_query("SELECT * FROM events", conn)
        registrations_df = pd.read_sql_query("SELECT * FROM registrations", conn)
        attendance_df = pd.read_sql_query("SELECT * FROM attendance", conn)
        participations_df = pd.read_sql_query("SELECT * FROM participations", conn)

        # Preprocessing: Dates & types
        events_df['event_date'] = pd.to_datetime(events_df['event_date'], errors='coerce')
        registrations_df['registration_date'] = pd.to_datetime(registrations_df['registration_date'], errors='coerce')
        attendance_df['attendance_date'] = pd.to_datetime(attendance_df['attendance_date'], errors='coerce')

        # Cleaning: Trim whitespace
        students_df['student_name'] = students_df['student_name'].astype(str).str.strip()
        students_df['department'] = students_df['department'].astype(str).str.strip()
        events_df['event_name'] = events_df['event_name'].astype(str).str.strip()
        events_df['event_type'] = events_df['event_type'].astype(str).str.strip()

        # Handle duplicates if any
        students_df.drop_duplicates(subset=['student_id'], inplace=True)
        events_df.drop_duplicates(subset=['event_id'], inplace=True)
        registrations_df.drop_duplicates(subset=['registration_id'], inplace=True)
        attendance_df.drop_duplicates(subset=['attendance_id'], inplace=True)
        participations_df.drop_duplicates(subset=['participation_id'], inplace=True)

        return {
            'students': students_df,
            'events': events_df,
            'registrations': registrations_df,
            'attendance': attendance_df,
            'participations': participations_df
        }
    finally:
        conn.close()

if __name__ == "__main__":
    data = load_and_preprocess_data()
    print("[SUCCESS] Data preprocessed successfully:")
    for k, df in data.items():
        print(f"  - {k.capitalize()}: {len(df)} records, columns: {list(df.columns)}")
