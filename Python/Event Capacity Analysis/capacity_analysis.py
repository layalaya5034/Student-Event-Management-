# ==============================================================================
# EVENT CAPACITY ANALYSIS MODULE
# Scenario 6: Smart Campus Event Management and Student Participation Analysis
# ==============================================================================

import os
import sys

base_dir = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
if base_dir not in sys.path:
    sys.path.insert(0, base_dir)

from importlib.machinery import SourceFileLoader
prep_module = SourceFileLoader("preprocess_data", os.path.join(base_dir, "Data Preprocessing", "preprocess_data.py")).load_module()

def analyze_capacity():
    data = prep_module.load_and_preprocess_data()
    events = data['events']
    regs = data['registrations'][data['registrations']['status'] == 'REGISTERED']
    reg_cnt = regs.groupby('event_id').size().reset_index(name='active_reg')
    merged = events.merge(reg_cnt, on='event_id', how='left').fillna({'active_reg': 0})
    merged['available_seats'] = (merged['max_participants'] - merged['active_reg']).clip(lower=0)
    merged['utilization_pct'] = (merged['active_reg'] * 100.0 / merged['max_participants']).round(1)

    print("\n--- EVENT CAPACITY & SEAT UTILIZATION ---")
    print(f"{'Event ID':<10} {'Event Name':<28} {'Capacity':<10} {'Registered':<12} {'Available':<11} {'Utilization %'}")
    print("-" * 80)
    for _, r in merged.iterrows():
        print(f"{r['event_id']:<10} {r['event_name'][:26]:<28} {int(r['max_participants']):<10} {int(r['active_reg']):<12} {int(r['available_seats']):<11} {r['utilization_pct']:.1f}%")
    print("-" * 80 + "\n")

if __name__ == "__main__":
    analyze_capacity()
