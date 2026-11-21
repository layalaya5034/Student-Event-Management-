# ==============================================================================
# STUDENT PARTICIPATION ANALYSIS MODULE
# Scenario 6: Smart Campus Event Management and Student Participation Analysis
# ==============================================================================

import os
import sys

base_dir = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
if base_dir not in sys.path:
    sys.path.insert(0, base_dir)

from importlib.machinery import SourceFileLoader
prep_module = SourceFileLoader("preprocess_data", os.path.join(base_dir, "Data Preprocessing", "preprocess_data.py")).load_module()

def analyze_participation():
    data = prep_module.load_and_preprocess_data()
    parts = data['participations'].merge(data['students'], on='student_id')
    print("\n--- STUDENT PARTICIPATION & ACHIEVEMENT ANALYSIS ---")
    status_summary = parts.groupby('participation_status').size()
    print("Participation Outcomes:")
    for status, count in status_summary.items():
        print(f"  - {status:<18}: {count}")
    
    cert_summary = parts.groupby('certificate_status').size()
    print("\nCertificate Distribution:")
    for status, count in cert_summary.items():
        print(f"  - {status:<18}: {count}")
    print("----------------------------------------------------\n")

if __name__ == "__main__":
    analyze_participation()
