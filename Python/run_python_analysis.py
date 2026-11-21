# ==============================================================================
# PYTHON DATA ANALYSIS MODULE RUNNER
# Scenario 6: Smart Campus Event Management and Student Participation Analysis
# Register Numbers: 6, 36, 66, 96
# ==============================================================================

import os
import sys

# Ensure parent directory is in sys.path
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
if BASE_DIR not in sys.path:
    sys.path.insert(0, BASE_DIR)

from importlib.machinery import SourceFileLoader
prep_module = SourceFileLoader("preprocess_data", os.path.join(BASE_DIR, "Data Preprocessing", "preprocess_data.py")).load_module()
load_data = prep_module.load_and_preprocess_data

vis_module = SourceFileLoader("generate_visualizations", os.path.join(BASE_DIR, "Data Visualization", "generate_visualizations.py")).load_module()

def display_menu():
    print("============================================================")
    print("                 PYTHON DATA ANALYSIS")
    print("============================================================")
    print("1.  Event Registration Distribution")
    print("2.  Daily Registration Trends")
    print("3.  Monthly Event Trends")
    print("4.  Student Participation Analysis")
    print("5.  Department-wise Participation Analysis")
    print("6.  Year-wise Student Participation")
    print("7.  Event Popularity Analysis")
    print("8.  Most Registered Events")
    print("9.  Least Registered Events")
    print("10. Attendance Distribution")
    print("11. Event-wise Attendance Percentage")
    print("12. Top Participating Students")
    print("13. Event Capacity Utilization")
    print("14. Event Type Distribution")
    print("15. Registration vs Attendance Analysis")
    print("16. Certificate Eligibility Analysis")
    print("17. Complete Data Analysis")
    print("18. Return to Main Menu")
    print("============================================================")

def run_option(opt):
    data = load_data()
    print(f"\n[INFO] Executing Python Analysis Option {opt} using real-time database records...\n")
    
    if opt == 1:
        path = vis_module.generate_chart_01(data)
        print(f"[SUCCESS] Event Registration Distribution chart generated:\n  -> {path}")
    elif opt == 2:
        path = vis_module.generate_chart_02(data)
        print(f"[SUCCESS] Daily Registration Trends chart generated:\n  -> {path}")
    elif opt == 3:
        path = vis_module.generate_chart_03(data)
        print(f"[SUCCESS] Monthly Event Trends chart generated:\n  -> {path}")
    elif opt == 4:
        path1 = vis_module.generate_chart_04(data)
        path2 = vis_module.generate_chart_09(data)
        print(f"[SUCCESS] Student Participation Analysis charts generated:\n  -> {path1}\n  -> {path2}")
    elif opt == 5:
        path = vis_module.generate_chart_04(data)
        print(f"[SUCCESS] Department-wise Participation chart generated:\n  -> {path}")
    elif opt == 6:
        path = vis_module.generate_chart_05(data)
        print(f"[SUCCESS] Year-wise Student Participation chart generated:\n  -> {path}")
    elif opt == 7:
        path1 = vis_module.generate_chart_07(data)
        path2 = vis_module.generate_chart_08(data)
        print(f"[SUCCESS] Event Popularity Analysis charts generated:\n  -> {path1}\n  -> {path2}")
    elif opt == 8:
        path = vis_module.generate_chart_07(data)
        print(f"[SUCCESS] Most Registered Events chart generated:\n  -> {path}")
    elif opt == 9:
        path = vis_module.generate_chart_08(data)
        print(f"[SUCCESS] Least Registered Events chart generated:\n  -> {path}")
    elif opt == 10:
        path = vis_module.generate_chart_10(data)
        print(f"[SUCCESS] Attendance Distribution chart generated:\n  -> {path}")
    elif opt == 11:
        path = vis_module.generate_chart_11(data)
        print(f"[SUCCESS] Event-wise Attendance Percentage chart generated:\n  -> {path}")
    elif opt == 12:
        path = vis_module.generate_chart_09(data)
        print(f"[SUCCESS] Top Participating Students chart generated:\n  -> {path}")
    elif opt == 13:
        path1 = vis_module.generate_chart_12(data)
        path2 = vis_module.generate_chart_13(data)
        print(f"[SUCCESS] Event Capacity Utilization charts generated:\n  -> {path1}\n  -> {path2}")
    elif opt == 14:
        path = vis_module.generate_chart_06(data)
        print(f"[SUCCESS] Event Type Distribution chart generated:\n  -> {path}")
    elif opt == 15:
        path = vis_module.generate_chart_15(data)
        print(f"[SUCCESS] Registration vs Attendance chart generated:\n  -> {path}")
    elif opt == 16:
        # Certificate eligibility analysis
        parts = data['participations'].merge(data['students'], on='student_id').merge(data['events'], on='event_id')
        eligible = parts[(parts['participation_status'].isin(['PARTICIPATED', 'WINNER', 'RUNNER_UP', 'PARTICIPANT'])) & (parts['certificate_status'] == 'ISSUED')]
        print("--------------------------------------------------------------------------------")
        print("                        CERTIFICATE ELIGIBILITY ANALYSIS")
        print("--------------------------------------------------------------------------------")
        print(f"{'Student ID':<10} {'Student Name':<20} {'Event Name':<25} {'Outcome':<15} {'Certificate'}")
        print("-" * 80)
        for _, row in eligible.head(10).iterrows():
            print(f"{row['student_id']:<10} {row['student_name']:<20} {row['event_name'][:24]:<25} {row['participation_status']:<15} {row['certificate_status']}")
        print("-" * 80)
        print(f"Total Issued Certificates: {len(eligible)} across {len(eligible['student_id'].unique())} distinct students.\n")
    elif opt == 17:
        print("[INFO] Running Complete Data Analysis Pipeline...")
        charts = vis_module.generate_all_visualizations()
        print("\n================================================================================")
        print("                    COMPLETE PYTHON DATA ANALYSIS REPORT")
        print("================================================================================")
        print(f"Total Students Processed       : {len(data['students'])}")
        print(f"Total Events Processed         : {len(data['events'])}")
        print(f"Total Registrations Analyzed   : {len(data['registrations'])}")
        print(f"Total Attendance Records       : {len(data['attendance'])}")
        print(f"Total Participations Analyzed  : {len(data['participations'])}")
        print(f"Total Visualizations Generated : {len(charts)}")
        print("All high-resolution figures have been saved to:")
        print(f"  {vis_module.CHARTS_DIR}")
        print("================================================================================\n")
    else:
        print(f"[ERROR] Invalid option {opt}.")

def interactive_loop():
    while True:
        display_menu()
        choice = input("Enter your choice (1-18): ").strip()
        if not choice:
            continue
        if not choice.isdigit():
            print("[ERROR] Please enter a valid number (1-18).\n")
            continue
        opt = int(choice)
        if opt == 18:
            print("[INFO] Returning to Main Menu...")
            break
        elif 1 <= opt <= 17:
            try:
                run_option(opt)
            except Exception as e:
                print(f"[ERROR] An exception occurred while executing option {opt}: {e}")
        else:
            print("[ERROR] Choice out of range. Please choose between 1 and 18.\n")
        input("\nPress Enter to continue...")

if __name__ == "__main__":
    if len(sys.argv) > 1:
        arg = sys.argv[1].strip()
        if arg == "--all":
            run_option(17)
        elif arg.startswith("--option="):
            opt_num = int(arg.split("=")[1])
            run_option(opt_num)
        elif arg.isdigit():
            run_option(int(arg))
        else:
            interactive_loop()
    else:
        interactive_loop()
