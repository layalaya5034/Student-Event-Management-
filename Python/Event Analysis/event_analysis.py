# ==============================================================================
# EVENT ANALYSIS MODULE
# Scenario 6: Smart Campus Event Management and Student Participation Analysis
# ==============================================================================

import os
import sys

base_dir = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
if base_dir not in sys.path:
    sys.path.insert(0, base_dir)

from importlib.machinery import SourceFileLoader
prep_module = SourceFileLoader("preprocess_data", os.path.join(base_dir, "Data Preprocessing", "preprocess_data.py")).load_module()

def analyze_events():
    data = prep_module.load_and_preprocess_data()
    events = data['events']
    print("\n--- EVENT LIFECYCLE & CATEGORY ANALYSIS ---")
    status_summary = events.groupby('event_status').size()
    print("Events by Status:")
    for status, count in status_summary.items():
        print(f"  - {status:<12}: {count}")
    
    print("\nEvents by Category:")
    type_summary = events.groupby('event_type').size()
    for etype, count in type_summary.items():
        print(f"  - {etype:<15}: {count}")
    print("-------------------------------------------\n")

if __name__ == "__main__":
    analyze_events()
