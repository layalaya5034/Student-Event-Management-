# ==============================================================================
# DATA VISUALIZATION GENERATOR
# Scenario 6: Smart Campus Event Management and Student Participation Analysis
# ==============================================================================

import os
import sys

# Add project root to sys.path so sibling imports work
current_dir = os.path.dirname(os.path.abspath(__file__))
parent_dir = os.path.abspath(os.path.join(current_dir, ".."))
if parent_dir not in sys.path:
    sys.path.insert(0, parent_dir)

from importlib.machinery import SourceFileLoader
prep_module_path = os.path.join(parent_dir, "Data Preprocessing", "preprocess_data.py")
preprocess_module = SourceFileLoader("preprocess_data", prep_module_path).load_module()
load_and_preprocess_data = preprocess_module.load_and_preprocess_data

import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
import seaborn as sns
import pandas as pd
import numpy as np

# Output directory for charts
CHARTS_DIR = os.path.join(current_dir, "charts")
os.makedirs(CHARTS_DIR, exist_ok=True)

# Styling palette
sns.set_theme(style="whitegrid", palette="muted")
plt.rcParams['font.sans-serif'] = 'DejaVu Sans'
plt.rcParams['font.size'] = 10

def generate_chart_01(data):
    """1. Bar chart – Number of registrations by event"""
    plt.figure(figsize=(10, 6))
    regs = data['registrations'][data['registrations']['status'] == 'REGISTERED']
    counts = regs.groupby('event_id').size().reset_index(name='count')
    merged = counts.merge(data['events'], on='event_id')
    merged.sort_values('count', ascending=False, inplace=True)
    
    ax = sns.barplot(x='event_name', y='count', data=merged, palette='Blues_r')
    plt.title('1. Active Registrations per Campus Event', fontsize=14, fontweight='bold', pad=15)
    plt.xlabel('Event Name', fontweight='bold')
    plt.ylabel('Number of Active Registrations', fontweight='bold')
    plt.xticks(rotation=45, ha='right')
    for p in ax.patches:
        ax.annotate(f"{int(p.get_height())}", (p.get_x() + p.get_width() / 2., p.get_height()),
                    ha='center', va='bottom', fontsize=9, xytext=(0, 3), textcoords='offset points')
    plt.tight_layout()
    out = os.path.join(CHARTS_DIR, "01_event_registration_distribution.png")
    plt.savefig(out, dpi=300)
    plt.close()
    return out

def generate_chart_02(data):
    """2. Line chart – Daily event registration trends"""
    plt.figure(figsize=(10, 5))
    regs = data['registrations'].copy()
    regs['reg_date_str'] = regs['registration_date'].dt.strftime('%Y-%m-%d')
    daily = regs.groupby(['reg_date_str', 'status']).size().unstack(fill_value=0)
    
    for col in daily.columns:
        plt.plot(daily.index, daily[col], marker='o', linewidth=2.5, label=f"Status: {col}")
    plt.title('2. Daily Registration Trends (Active vs Cancelled/Waitlisted)', fontsize=14, fontweight='bold', pad=15)
    plt.xlabel('Date (YYYY-MM-DD)', fontweight='bold')
    plt.ylabel('Daily Registrations', fontweight='bold')
    plt.xticks(rotation=45, ha='right')
    plt.legend(frameon=True)
    plt.tight_layout()
    out = os.path.join(CHARTS_DIR, "02_daily_registration_trends.png")
    plt.savefig(out, dpi=300)
    plt.close()
    return out

def generate_chart_03(data):
    """3. Line chart – Monthly event trends"""
    plt.figure(figsize=(9, 5))
    ev = data['events'].copy()
    ev['month'] = ev['event_date'].dt.strftime('%Y-%m')
    monthly = ev.groupby('month').size().reset_index(name='event_count')
    
    plt.plot(monthly['month'], monthly['event_count'], marker='s', color='#2b5c8f', linewidth=3, markersize=8)
    plt.title('3. Monthly Campus Event Scheduling Trends', fontsize=14, fontweight='bold', pad=15)
    plt.xlabel('Month', fontweight='bold')
    plt.ylabel('Number of Scheduled Events', fontweight='bold')
    for x, y in zip(monthly['month'], monthly['event_count']):
        plt.annotate(f"{y}", (x, y), textcoords="offset points", xytext=(0,10), ha='center', fontweight='bold')
    plt.tight_layout()
    out = os.path.join(CHARTS_DIR, "03_monthly_event_trends.png")
    plt.savefig(out, dpi=300)
    plt.close()
    return out

def generate_chart_04(data):
    """4. Bar chart – Department-wise student participation"""
    plt.figure(figsize=(10, 6))
    parts = data['participations'].merge(data['students'], on='student_id')
    valid = parts[parts['participation_status'].isin(['PARTICIPATED', 'WINNER', 'RUNNER_UP', 'PARTICIPANT'])]
    dept_counts = valid.groupby('department').size().reset_index(name='participations')
    dept_counts.sort_values('participations', ascending=False, inplace=True)
    
    ax = sns.barplot(x='department', y='participations', data=dept_counts, palette='viridis')
    plt.title('4. Department-wise Student Event Participation', fontsize=14, fontweight='bold', pad=15)
    plt.xlabel('Department', fontweight='bold')
    plt.ylabel('Total Verified Participations', fontweight='bold')
    plt.xticks(rotation=30, ha='right')
    for p in ax.patches:
        ax.annotate(f"{int(p.get_height())}", (p.get_x() + p.get_width() / 2., p.get_height()),
                    ha='center', va='bottom', fontsize=9, xytext=(0, 3), textcoords='offset points')
    plt.tight_layout()
    out = os.path.join(CHARTS_DIR, "04_department_participation.png")
    plt.savefig(out, dpi=300)
    plt.close()
    return out

def generate_chart_05(data):
    """5. Bar chart – Year-wise student participation"""
    plt.figure(figsize=(8, 5))
    parts = data['participations'].merge(data['students'], on='student_id')
    valid = parts[parts['participation_status'].isin(['PARTICIPATED', 'WINNER', 'RUNNER_UP', 'PARTICIPANT'])]
    year_counts = valid.groupby('year_of_study').size().reset_index(name='count')
    year_counts['year_label'] = year_counts['year_of_study'].apply(lambda y: f"Year {y}")
    
    ax = sns.barplot(x='year_label', y='count', data=year_counts, palette='crest')
    plt.title('5. Year-wise Student Participation Distribution', fontsize=14, fontweight='bold', pad=15)
    plt.xlabel('Academic Year', fontweight='bold')
    plt.ylabel('Number of Participations', fontweight='bold')
    for p in ax.patches:
        ax.annotate(f"{int(p.get_height())}", (p.get_x() + p.get_width() / 2., p.get_height()),
                    ha='center', va='bottom', fontsize=10, xytext=(0, 4), textcoords='offset points')
    plt.tight_layout()
    out = os.path.join(CHARTS_DIR, "05_yearwise_participation.png")
    plt.savefig(out, dpi=300)
    plt.close()
    return out

def generate_chart_06(data):
    """6. Pie chart – Event type distribution"""
    plt.figure(figsize=(7, 7))
    types = data['events']['event_type'].value_counts()
    colors = sns.color_palette('pastel')[0:len(types)]
    plt.pie(types, labels=types.index, autopct='%1.1f%%', startangle=140, colors=colors,
            wedgeprops={'edgecolor': 'white', 'linewidth': 1.5})
    plt.title('6. Distribution of Campus Events by Category', fontsize=14, fontweight='bold', pad=15)
    plt.tight_layout()
    out = os.path.join(CHARTS_DIR, "06_event_type_distribution.png")
    plt.savefig(out, dpi=300)
    plt.close()
    return out

def generate_chart_07(data):
    """7. Bar chart – Most popular events"""
    plt.figure(figsize=(10, 5))
    regs = data['registrations'][data['registrations']['status'] == 'REGISTERED']
    top_events = regs.groupby('event_id').size().reset_index(name='count')
    top_events = top_events.merge(data['events'], on='event_id').nlargest(6, 'count')
    
    ax = sns.barplot(x='count', y='event_name', data=top_events, palette='mako')
    plt.title('7. Top Most Popular Events by Registration Volume', fontsize=14, fontweight='bold', pad=15)
    plt.xlabel('Active Registrations', fontweight='bold')
    plt.ylabel('Event Title', fontweight='bold')
    for p in ax.patches:
        ax.annotate(f"{int(p.get_width())}", (p.get_width(), p.get_y() + p.get_height() / 2.),
                    ha='left', va='center', fontsize=10, xytext=(5, 0), textcoords='offset points')
    plt.tight_layout()
    out = os.path.join(CHARTS_DIR, "07_most_popular_events.png")
    plt.savefig(out, dpi=300)
    plt.close()
    return out

def generate_chart_08(data):
    """8. Bar chart – Least popular events"""
    plt.figure(figsize=(10, 5))
    regs = data['registrations'][data['registrations']['status'] == 'REGISTERED']
    event_counts = data['events'][data['events']['event_status'] != 'CANCELLED'][['event_id', 'event_name']].copy()
    counts = regs.groupby('event_id').size().reset_index(name='count')
    merged = event_counts.merge(counts, on='event_id', how='left').fillna(0)
    least = merged.nsmallest(5, 'count')
    
    ax = sns.barplot(x='count', y='event_name', data=least, palette='flare')
    plt.title('8. Events with Lowest Registrations (Need Promotion)', fontsize=14, fontweight='bold', pad=15)
    plt.xlabel('Active Registrations', fontweight='bold')
    plt.ylabel('Event Title', fontweight='bold')
    for p in ax.patches:
        ax.annotate(f"{int(p.get_width())}", (p.get_width(), p.get_y() + p.get_height() / 2.),
                    ha='left', va='center', fontsize=10, xytext=(5, 0), textcoords='offset points')
    plt.tight_layout()
    out = os.path.join(CHARTS_DIR, "08_least_popular_events.png")
    plt.savefig(out, dpi=300)
    plt.close()
    return out

def generate_chart_09(data):
    """9. Bar chart – Top participating students"""
    plt.figure(figsize=(10, 6))
    parts = data['participations'].merge(data['students'], on='student_id')
    valid = parts[parts['participation_status'].isin(['PARTICIPATED', 'WINNER', 'RUNNER_UP', 'PARTICIPANT'])]
    top_students = valid.groupby(['student_id', 'student_name']).size().reset_index(name='event_count')
    top_students.sort_values('event_count', ascending=False, inplace=True)
    top_students = top_students.head(7)
    
    ax = sns.barplot(x='student_name', y='event_count', data=top_students, palette='magma')
    plt.title('9. Top Participating Students across Campus Activities', fontsize=14, fontweight='bold', pad=15)
    plt.xlabel('Student Name', fontweight='bold')
    plt.ylabel('Distinct Events Participated', fontweight='bold')
    plt.xticks(rotation=25, ha='right')
    for p in ax.patches:
        ax.annotate(f"{int(p.get_height())}", (p.get_x() + p.get_width() / 2., p.get_height()),
                    ha='center', va='bottom', fontsize=10, xytext=(0, 3), textcoords='offset points')
    plt.tight_layout()
    out = os.path.join(CHARTS_DIR, "09_top_participating_students.png")
    plt.savefig(out, dpi=300)
    plt.close()
    return out

def generate_chart_10(data):
    """10. Pie chart – Present and absent student distribution"""
    plt.figure(figsize=(7, 7))
    att_counts = data['attendance']['attendance_status'].value_counts()
    colors = ['#4CAF50', '#F44336'] if 'PRESENT' in att_counts.index else sns.color_palette('pastel')
    plt.pie(att_counts, labels=att_counts.index, autopct='%1.1f%%', startangle=90, colors=colors,
            explode=[0.05, 0] if len(att_counts) == 2 else None, shadow=True,
            wedgeprops={'edgecolor': 'white', 'linewidth': 1.5})
    plt.title('10. Overall Campus Event Attendance (Present vs Absent)', fontsize=14, fontweight='bold', pad=15)
    plt.tight_layout()
    out = os.path.join(CHARTS_DIR, "10_present_absent_distribution.png")
    plt.savefig(out, dpi=300)
    plt.close()
    return out

def generate_chart_11(data):
    """11. Bar chart – Event-wise attendance percentage"""
    plt.figure(figsize=(10, 6))
    att = data['attendance'].merge(data['events'], on='event_id')
    grouped = att.groupby(['event_name', 'attendance_status']).size().unstack(fill_value=0)
    if 'PRESENT' in grouped.columns:
        grouped['total'] = grouped.sum(axis=1)
        grouped['pct'] = (grouped['PRESENT'] * 100.0 / grouped['total']).round(2)
        grouped.sort_values('pct', ascending=False, inplace=True)
        
        ax = sns.barplot(x=grouped.index, y=grouped['pct'], palette='Greens_r')
        plt.title('11. Event-wise Attendance Percentage (Present / Total Logged)', fontsize=14, fontweight='bold', pad=15)
        plt.xlabel('Event Name', fontweight='bold')
        plt.ylabel('Attendance %', fontweight='bold')
        plt.xticks(rotation=35, ha='right')
        for p in ax.patches:
            ax.annotate(f"{p.get_height():.1f}%", (p.get_x() + p.get_width() / 2., p.get_height()),
                        ha='center', va='bottom', fontsize=9, xytext=(0, 3), textcoords='offset points')
    plt.tight_layout()
    out = os.path.join(CHARTS_DIR, "11_event_attendance_percentage.png")
    plt.savefig(out, dpi=300)
    plt.close()
    return out

def generate_chart_12(data):
    """12. Bar chart – Event capacity utilization"""
    plt.figure(figsize=(10, 6))
    regs = data['registrations'][data['registrations']['status'] == 'REGISTERED']
    reg_counts = regs.groupby('event_id').size().reset_index(name='active_reg')
    merged = data['events'].merge(reg_counts, on='event_id', how='left').fillna({'active_reg': 0})
    merged['utilization_pct'] = (merged['active_reg'] * 100.0 / merged['max_participants']).round(1)
    merged.sort_values('utilization_pct', ascending=False, inplace=True)
    
    colors = ['#d9534f' if u >= 100 else '#f0ad4e' if u >= 80 else '#5cb85c' for u in merged['utilization_pct']]
    ax = sns.barplot(x='event_name', y='utilization_pct', data=merged, palette=colors)
    plt.axhline(100, color='red', linestyle='--', label='100% Full Capacity')
    plt.title('12. Event Capacity Utilization Percentage', fontsize=14, fontweight='bold', pad=15)
    plt.xlabel('Event Title', fontweight='bold')
    plt.ylabel('Utilization % (Registered / Max Capacity)', fontweight='bold')
    plt.xticks(rotation=45, ha='right')
    plt.legend()
    for p in ax.patches:
        ax.annotate(f"{p.get_height():.0f}%", (p.get_x() + p.get_width() / 2., p.get_height()),
                    ha='center', va='bottom', fontsize=8, xytext=(0, 2), textcoords='offset points')
    plt.tight_layout()
    out = os.path.join(CHARTS_DIR, "12_event_capacity_utilization.png")
    plt.savefig(out, dpi=300)
    plt.close()
    return out

def generate_chart_13(data):
    """13. Bar chart – Available seats by event"""
    plt.figure(figsize=(10, 6))
    regs = data['registrations'][data['registrations']['status'] == 'REGISTERED']
    reg_counts = regs.groupby('event_id').size().reset_index(name='active_reg')
    merged = data['events'].merge(reg_counts, on='event_id', how='left').fillna({'active_reg': 0})
    merged['available_seats'] = (merged['max_participants'] - merged['active_reg']).clip(lower=0)
    upcoming_open = merged[merged['event_status'].isin(['OPEN', 'UPCOMING', 'ONGOING'])].sort_values('available_seats', ascending=False)
    
    ax = sns.barplot(x='event_name', y='available_seats', data=upcoming_open, palette='copper')
    plt.title('13. Available Seats in Upcoming and Open Campus Events', fontsize=14, fontweight='bold', pad=15)
    plt.xlabel('Event Name', fontweight='bold')
    plt.ylabel('Available Seat Count', fontweight='bold')
    plt.xticks(rotation=40, ha='right')
    for p in ax.patches:
        ax.annotate(f"{int(p.get_height())}", (p.get_x() + p.get_width() / 2., p.get_height()),
                    ha='center', va='bottom', fontsize=9, xytext=(0, 3), textcoords='offset points')
    plt.tight_layout()
    out = os.path.join(CHARTS_DIR, "13_available_seats_by_event.png")
    plt.savefig(out, dpi=300)
    plt.close()
    return out

def generate_chart_14(data):
    """14. Pie chart – Completed, upcoming, ongoing, and cancelled events"""
    plt.figure(figsize=(7, 7))
    statuses = data['events']['event_status'].value_counts()
    colors = sns.color_palette('Set2')[0:len(statuses)]
    plt.pie(statuses, labels=statuses.index, autopct='%1.1f%%', startangle=120, colors=colors,
            wedgeprops={'edgecolor': 'white', 'linewidth': 1.5})
    plt.title('14. Distribution of Events by Lifecycle Status', fontsize=14, fontweight='bold', pad=15)
    plt.tight_layout()
    out = os.path.join(CHARTS_DIR, "14_event_status_distribution.png")
    plt.savefig(out, dpi=300)
    plt.close()
    return out

def generate_chart_15(data):
    """15. Comparison chart – Event registrations versus attendance"""
    plt.figure(figsize=(10, 6))
    completed = data['events'][data['events']['event_status'] == 'COMPLETED']['event_id']
    regs = data['registrations'][(data['registrations']['event_id'].isin(completed)) & (data['registrations']['status'] == 'REGISTERED')]
    reg_cnt = regs.groupby('event_id').size().reset_index(name='Registered')
    
    att = data['attendance'][(data['attendance']['event_id'].isin(completed)) & (data['attendance']['attendance_status'] == 'PRESENT')]
    att_cnt = att.groupby('event_id').size().reset_index(name='Present Attendance')
    
    comp = reg_cnt.merge(att_cnt, on='event_id').merge(data['events'][['event_id', 'event_name']], on='event_id')
    melted = comp.melt(id_vars=['event_name'], value_vars=['Registered', 'Present Attendance'], var_name='Metric', value_name='Count')
    
    ax = sns.barplot(x='event_name', y='Count', hue='Metric', data=melted, palette=['#337ab7', '#5cb85c'])
    plt.title('15. Registered Students vs Actual Present Students (Completed Events)', fontsize=14, fontweight='bold', pad=15)
    plt.xlabel('Event Name', fontweight='bold')
    plt.ylabel('Student Count', fontweight='bold')
    plt.xticks(rotation=35, ha='right')
    plt.legend(frameon=True)
    plt.tight_layout()
    out = os.path.join(CHARTS_DIR, "15_registration_vs_attendance.png")
    plt.savefig(out, dpi=300)
    plt.close()
    return out

def generate_chart_16(data):
    """16. Line chart – Daily attendance trends"""
    plt.figure(figsize=(9, 5))
    att = data['attendance'].copy()
    att['att_date_str'] = att['attendance_date'].dt.strftime('%Y-%m-%d')
    daily = att.groupby(['att_date_str', 'attendance_status']).size().unstack(fill_value=0)
    
    for col in daily.columns:
        plt.plot(daily.index, daily[col], marker='o', linewidth=2.5, label=f"Attendance: {col}")
    plt.title('16. Daily Student Attendance Turnout Trends', fontsize=14, fontweight='bold', pad=15)
    plt.xlabel('Date (YYYY-MM-DD)', fontweight='bold')
    plt.ylabel('Student Count', fontweight='bold')
    plt.xticks(rotation=35, ha='right')
    plt.legend(frameon=True)
    plt.tight_layout()
    out = os.path.join(CHARTS_DIR, "16_daily_attendance_trends.png")
    plt.savefig(out, dpi=300)
    plt.close()
    return out

def generate_chart_17(data):
    """17. Line chart – Monthly participation trends"""
    plt.figure(figsize=(9, 5))
    parts = data['participations'].merge(data['events'][['event_id', 'event_date']], on='event_id')
    parts['month'] = parts['event_date'].dt.strftime('%Y-%m')
    monthly = parts.groupby('month').size().reset_index(name='count')
    
    plt.plot(monthly['month'], monthly['count'], marker='^', color='#9b59b6', linewidth=3, markersize=8)
    plt.title('17. Monthly Verified Student Participation Volume', fontsize=14, fontweight='bold', pad=15)
    plt.xlabel('Month', fontweight='bold')
    plt.ylabel('Total Participations Recorded', fontweight='bold')
    for x, y in zip(monthly['month'], monthly['count']):
        plt.annotate(f"{y}", (x, y), textcoords="offset points", xytext=(0,10), ha='center', fontweight='bold')
    plt.tight_layout()
    out = os.path.join(CHARTS_DIR, "17_monthly_participation_trends.png")
    plt.savefig(out, dpi=300)
    plt.close()
    return out

def generate_chart_18(data):
    """18. Bar chart – Students with maximum participation"""
    plt.figure(figsize=(10, 5))
    parts = data['participations'].merge(data['students'], on='student_id')
    counts = parts.groupby(['student_id', 'student_name', 'department']).size().reset_index(name='participations')
    counts.sort_values('participations', ascending=False, inplace=True)
    top = counts.head(8)
    
    ax = sns.barplot(x='participations', y='student_name', data=top, palette='Purples_r')
    plt.title('18. Students with Maximum Campus Event Participation Records', fontsize=14, fontweight='bold', pad=15)
    plt.xlabel('Number of Verified Events Participated', fontweight='bold')
    plt.ylabel('Student Name', fontweight='bold')
    for p in ax.patches:
        ax.annotate(f"{int(p.get_width())}", (p.get_width(), p.get_y() + p.get_height() / 2.),
                    ha='left', va='center', fontsize=10, xytext=(5, 0), textcoords='offset points')
    plt.tight_layout()
    out = os.path.join(CHARTS_DIR, "18_students_maximum_participation.png")
    plt.savefig(out, dpi=300)
    plt.close()
    return out

def generate_chart_19(data):
    """19. Bar chart – Department-wise attendance"""
    plt.figure(figsize=(10, 6))
    att = data['attendance'].merge(data['students'], on='student_id')
    dept_att = att.groupby(['department', 'attendance_status']).size().unstack(fill_value=0)
    
    ax = dept_att.plot(kind='bar', stacked=True, color=['#e74c3c', '#2ecc71'], figsize=(10, 6))
    plt.title('19. Department-wise Attendance Breakdown (Present vs Absent)', fontsize=14, fontweight='bold', pad=15)
    plt.xlabel('Department', fontweight='bold')
    plt.ylabel('Student Attendance Count', fontweight='bold')
    plt.xticks(rotation=30, ha='right')
    plt.legend(title='Status', frameon=True)
    plt.tight_layout()
    out = os.path.join(CHARTS_DIR, "19_department_wise_attendance.png")
    plt.savefig(out, dpi=300)
    plt.close()
    return out

def generate_chart_20(data):
    """20. Visualization – Registration and participation distribution by event category"""
    plt.figure(figsize=(10, 6))
    reg_cat = data['registrations'][data['registrations']['status'] == 'REGISTERED'].merge(data['events'], on='event_id')
    reg_cnt = reg_cat.groupby('event_type').size().reset_index(name='Registrations')
    
    part_cat = data['participations'].merge(data['events'], on='event_id')
    part_cnt = part_cat.groupby('event_type').size().reset_index(name='Participations')
    
    merged = reg_cnt.merge(part_cnt, on='event_type', how='outer').fillna(0)
    melted = merged.melt(id_vars=['event_type'], value_vars=['Registrations', 'Participations'], var_name='Metric', value_name='Count')
    
    ax = sns.barplot(x='event_type', y='Count', hue='Metric', data=melted, palette='coolwarm')
    plt.title('20. Registration vs Participation by Event Category', fontsize=14, fontweight='bold', pad=15)
    plt.xlabel('Event Category / Type', fontweight='bold')
    plt.ylabel('Student Count', fontweight='bold')
    plt.xticks(rotation=30, ha='right')
    plt.legend(frameon=True)
    plt.tight_layout()
    out = os.path.join(CHARTS_DIR, "20_category_reg_vs_part_distribution.png")
    plt.savefig(out, dpi=300)
    plt.close()
    return out

def generate_all_visualizations():
    """Execute all 20 charts and return their saved paths."""
    print("[INFO] Fetching and preprocessing real-time database records...")
    data = load_and_preprocess_data()
    generators = [
        ("Chart 01: Event Registration Distribution", generate_chart_01),
        ("Chart 02: Daily Registration Trends", generate_chart_02),
        ("Chart 03: Monthly Event Trends", generate_chart_03),
        ("Chart 04: Department Participation", generate_chart_04),
        ("Chart 05: Year-wise Participation", generate_chart_05),
        ("Chart 06: Event Type Distribution", generate_chart_06),
        ("Chart 07: Most Popular Events", generate_chart_07),
        ("Chart 08: Least Popular Events", generate_chart_08),
        ("Chart 09: Top Participating Students", generate_chart_09),
        ("Chart 10: Attendance Distribution", generate_chart_10),
        ("Chart 11: Event Attendance Percentage", generate_chart_11),
        ("Chart 12: Event Capacity Utilization", generate_chart_12),
        ("Chart 13: Available Seats by Event", generate_chart_13),
        ("Chart 14: Event Status Distribution", generate_chart_14),
        ("Chart 15: Registration vs Attendance", generate_chart_15),
        ("Chart 16: Daily Attendance Trends", generate_chart_16),
        ("Chart 17: Monthly Participation Trends", generate_chart_17),
        ("Chart 18: Maximum Participation Students", generate_chart_18),
        ("Chart 19: Department-wise Attendance", generate_chart_19),
        ("Chart 20: Category Distribution Comparison", generate_chart_20)
    ]
    results = []
    for title, gen in generators:
        out_path = gen(data)
        print(f"[SUCCESS] Generated: {title} -> {out_path}")
        results.append(out_path)
    return results

if __name__ == "__main__":
    generate_all_visualizations()
