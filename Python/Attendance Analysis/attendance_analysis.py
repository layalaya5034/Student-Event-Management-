# ==============================================================================
# ATTENDANCE ANALYSIS MODULE
# Scenario 6: Smart Campus Event Management and Student Participation Analysis
# ==============================================================================

import os
import sys

base_dir = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
if base_dir not in sys.path:
    sys.path.insert(0, base_dir)

from importlib.machinery import SourceFileLoader
prep_module = SourceFileLoader("preprocess_data", os.path.join(base_dir, "Data Preprocessing", "preprocess_data.py")).load_module()

def analyze_attendance():
    data = prep_module.load_and_preprocess_data()
    att = data['attendance']
    print("\n--- CAMPUS ATTENDANCE ANALYTICS ---")
    counts = att['attendance_status'].value_counts()
    total = len(att)
    present = counts.get('PRESENT', 0)
    absent = counts.get('ABSENT', 0)
    pct = (present * 100.0 / total) if total > 0 else 0.0
    print(f"Total Logged Attendance Records : {total}")
    print(f"Total Present                   : {present}")
    print(f"Total Absent                    : {absent}")
    print(f"Overall Attendance Percentage   : {pct:.2f}%")
    print("-----------------------------------\n")

if __name__ == "__main__":
    analyze_attendance()
