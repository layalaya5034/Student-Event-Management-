# ==============================================================================
# EVENT POPULARITY ANALYSIS MODULE
# Scenario 6: Smart Campus Event Management and Student Participation Analysis
# ==============================================================================

import os
import sys

base_dir = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
if base_dir not in sys.path:
    sys.path.insert(0, base_dir)

from importlib.machinery import SourceFileLoader
prep_module = SourceFileLoader("preprocess_data", os.path.join(base_dir, "Data Preprocessing", "preprocess_data.py")).load_module()

def analyze_popularity():
    data = prep_module.load_and_preprocess_data()
    regs = data['registrations'][data['registrations']['status'] == 'REGISTERED']
    counts = regs.groupby('event_id').size().reset_index(name='registrations')
    merged = counts.merge(data['events'], on='event_id').sort_values('registrations', ascending=False)
    
    print("\n--- MOST & LEAST POPULAR CAMPUS EVENTS ---")
    print("Top 5 Most Popular Events:")
    for _, r in merged.head(5).iterrows():
        print(f"  - {r['event_name']} ({r['event_type']}): {r['registrations']} registrations / Max: {r['max_participants']}")
        
    print("\nTop 5 Least Registered Events:")
    for _, r in merged.tail(5).iterrows():
        print(f"  - {r['event_name']} ({r['event_type']}): {r['registrations']} registrations / Max: {r['max_participants']}")
    print("------------------------------------------\n")

if __name__ == "__main__":
    analyze_popularity()
