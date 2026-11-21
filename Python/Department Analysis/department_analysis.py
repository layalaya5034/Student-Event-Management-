# ==============================================================================
# DEPARTMENT ANALYSIS MODULE
# Scenario 6: Smart Campus Event Management and Student Participation Analysis
# ==============================================================================

import os
import sys

base_dir = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
if base_dir not in sys.path:
    sys.path.insert(0, base_dir)

from importlib.machinery import SourceFileLoader
prep_module = SourceFileLoader("preprocess_data", os.path.join(base_dir, "Data Preprocessing", "preprocess_data.py")).load_module()

def analyze_department():
    data = prep_module.load_and_preprocess_data()
    students = data['students']
    regs = data['registrations'].merge(students, on='student_id')
    parts = data['participations'].merge(students, on='student_id')

    print("\n--- DEPARTMENT-WISE PARTICIPATION & ENROLLMENT ---")
    print(f"{'Department':<35} {'Students':<10} {'Registrations':<15} {'Participations'}")
    print("-" * 75)
    for dept in sorted(students['department'].unique()):
        s_cnt = len(students[students['department'] == dept])
        r_cnt = len(regs[(regs['department'] == dept) & (regs['status'] == 'REGISTERED')])
        p_cnt = len(parts[parts['department'] == dept])
        print(f"{dept:<35} {s_cnt:<10} {r_cnt:<15} {p_cnt}")
    print("-" * 75 + "\n")

if __name__ == "__main__":
    analyze_department()
