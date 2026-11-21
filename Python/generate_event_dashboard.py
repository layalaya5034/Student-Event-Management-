# ==============================================================================
# HTML VISUAL DASHBOARD GENERATOR FOR SMART CAMPUS EVENT MANAGEMENT
# Scenario 6: Smart Campus Event Management and Student Participation Analysis
# Register Numbers: 6, 36, 66, 96
# ==============================================================================

import os
import glob
import base64

BASE_DIR = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
CHARTS_DIR = os.path.join(BASE_DIR, "Python", "Data Visualization", "charts")
OUT_HTML = os.path.join(BASE_DIR, "visuals_dashboard.html")

CARDS_META = {
    "01_event_registration_distribution.png": ("Event Registration Distribution", "Registrations", "Bar chart displaying the number of active student enrollments across campus events."),
    "02_daily_registration_trends.png": ("Daily Registration Trends", "Trends", "Time-series line chart tracking daily student registration velocity and cancellations."),
    "03_monthly_event_trends.png": ("Monthly Event Trends", "Trends", "Monthly distribution of scheduled campus symposia, workshops, and competitions."),
    "04_department_participation.png": ("Department-wise Participation", "Departments", "Verified student participation totals categorized by academic engineering branch."),
    "05_yearwise_participation.png": ("Year-wise Student Participation", "Students", "Academic year breakdown of student activity engagement from Year 1 to Year 4."),
    "06_event_type_distribution.png": ("Event Type Distribution", "Events", "Proportional breakdown across Technical, Workshop, Cultural, Sports, and Symposium events."),
    "07_most_popular_events.png": ("Most Popular Events", "Registrations", "Top events ranked by total student demand and registration counts."),
    "08_least_popular_events.png": ("Events Requiring Promotion", "Registrations", "Events with lowest registrations identified for campus publicity and faculty coordination."),
    "09_top_participating_students.png": ("Top Participating Students", "Students", "Leaderboard of students with the highest number of distinct event participations."),
    "10_present_absent_distribution.png": ("Present vs Absent Distribution", "Attendance", "Overall ratio of students present versus absent across verified event sessions."),
    "11_event_attendance_percentage.png": ("Event Attendance Rates", "Attendance", "Turnout percentage calculated per event (Present Registrations / Active Registrations)."),
    "12_event_capacity_utilization.png": ("Event Capacity Utilization", "Capacity", "Utilization percentage showing events nearing maximum venue capacity or overbooking."),
    "13_available_seats_by_event.png": ("Available Seat Inventory", "Capacity", "Real-time remaining seat availability across open and upcoming events."),
    "14_event_status_distribution.png": ("Event Lifecycle Status", "Events", "Distribution of events across COMPLETED, ONGOING, OPEN, UPCOMING, and CANCELLED states."),
    "15_registration_vs_attendance.png": ("Registrations vs Actual Attendance", "Attendance", "Direct comparative analysis between registered students and verified present attendees."),
    "16_daily_attendance_trends.png": ("Daily Attendance Turnout", "Attendance", "Daily attendance volumes and presence ratios over the active semester timeline."),
    "17_monthly_participation_trends.png": ("Monthly Participation Volume", "Trends", "Monthly trajectory of student engagement in academic and co-curricular activities."),
    "18_students_maximum_participation.png": ("Students with Maximum Participations", "Students", "Detailed breakdown of the most active campus contributors and their departments."),
    "19_department_wise_attendance.png": ("Department Attendance Breakdown", "Departments", "Stacked comparison of present vs absent attendance records across each department."),
    "20_category_reg_vs_part_distribution.png": ("Category Reg vs Participation", "Events", "Comparison of initial interest vs final verified participation across event categories.")
}

def generate_dashboard():
    os.makedirs(CHARTS_DIR, exist_ok=True)
    images = sorted(glob.glob(os.path.join(CHARTS_DIR, "*.png")))
    
    html = f"""<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Smart Campus Event Management - Visual Analytics Dashboard</title>
    <style>
        :root {{
            --bg-primary: #0f172a;
            --bg-secondary: #1e293b;
            --card-bg: #1e293b;
            --border-color: #334155;
            --text-primary: #f8fafc;
            --text-secondary: #94a3b8;
            --accent-blue: #38bdf8;
            --accent-green: #4ade80;
            --accent-purple: #c084fc;
            --accent-amber: #fbbf24;
        }}
        * {{ box-sizing: border-box; margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }}
        body {{ background: var(--bg-primary); color: var(--text-primary); padding: 20px; }}
        
        /* Header Banner */
        .header {{
            background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%);
            border: 1px solid var(--border-color);
            border-radius: 12px;
            padding: 24px;
            margin-bottom: 24px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            box-shadow: 0 4px 20px rgba(0,0,0,0.3);
        }}
        .header h1 {{ font-size: 24px; font-weight: 700; color: #fff; margin-bottom: 6px; }}
        .header p {{ color: var(--text-secondary); font-size: 14px; }}
        .badge-grp {{ display: flex; gap: 8px; margin-top: 8px; }}
        .badge {{ background: #334155; color: var(--accent-blue); padding: 4px 10px; border-radius: 6px; font-size: 12px; font-weight: 600; }}

        /* KPI Section */
        .kpi-grid {{
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 16px;
            margin-bottom: 24px;
        }}
        .kpi-card {{
            background: var(--card-bg);
            border: 1px solid var(--border-color);
            border-radius: 10px;
            padding: 18px;
            border-left: 4px solid var(--accent-blue);
        }}
        .kpi-card:nth-child(2) {{ border-left-color: var(--accent-green); }}
        .kpi-card:nth-child(3) {{ border-left-color: var(--accent-purple); }}
        .kpi-card:nth-child(4) {{ border-left-color: var(--accent-amber); }}
        .kpi-card .title {{ font-size: 12px; text-transform: uppercase; color: var(--text-secondary); font-weight: 600; margin-bottom: 6px; }}
        .kpi-card .value {{ font-size: 26px; font-weight: 800; color: #fff; }}

        /* Filter Controls */
        .filter-bar {{
            display: flex;
            gap: 10px;
            margin-bottom: 24px;
            flex-wrap: wrap;
        }}
        .filter-btn {{
            background: var(--card-bg);
            border: 1px solid var(--border-color);
            color: var(--text-secondary);
            padding: 8px 16px;
            border-radius: 8px;
            cursor: pointer;
            font-size: 13px;
            font-weight: 600;
            transition: all 0.2s;
        }}
        .filter-btn:hover, .filter-btn.active {{
            background: var(--accent-blue);
            color: #0f172a;
            border-color: var(--accent-blue);
        }}

        /* Image Cards Grid */
        .charts-grid {{
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
            gap: 20px;
        }}
        .chart-card {{
            background: var(--card-bg);
            border: 1px solid var(--border-color);
            border-radius: 10px;
            overflow: hidden;
            display: flex;
            flex-direction: column;
            transition: transform 0.2s, box-shadow 0.2s;
        }}
        .chart-card:hover {{
            transform: translateY(-4px);
            box-shadow: 0 8px 24px rgba(0,0,0,0.4);
            border-color: #475569;
        }}
        .chart-card .card-head {{
            padding: 14px 16px;
            border-bottom: 1px solid var(--border-color);
            display: flex;
            justify-content: space-between;
            align-items: center;
        }}
        .chart-card .card-title {{ font-size: 14px; font-weight: 700; color: #fff; }}
        .chart-card .card-tag {{
            background: #334155;
            color: var(--accent-blue);
            font-size: 11px;
            font-weight: 600;
            padding: 2px 8px;
            border-radius: 4px;
        }}
        .chart-img-container {{
            background: #ffffff;
            padding: 8px;
            display: flex;
            justify-content: center;
            align-items: center;
            cursor: pointer;
        }}
        .chart-img-container img {{
            width: 100%;
            height: auto;
            border-radius: 4px;
            display: block;
        }}
        .chart-card .card-foot {{
            padding: 12px 16px;
            font-size: 12px;
            color: var(--text-secondary);
            border-top: 1px solid var(--border-color);
            background: rgba(15, 23, 42, 0.4);
        }}

        /* Modal Overlay */
        .modal {{
            display: none;
            position: fixed;
            top: 0; left: 0; width: 100%; height: 100%;
            background: rgba(0,0,0,0.85);
            z-index: 1000;
            justify-content: center;
            align-items: center;
            padding: 20px;
        }}
        .modal img {{
            max-width: 90vw;
            max-height: 90vh;
            border-radius: 8px;
            box-shadow: 0 8px 32px rgba(0,0,0,0.8);
        }}
    </style>
</head>
<body>

    <!-- Header Banner -->
    <div class="header">
        <div>
            <h1>Smart Campus Event Management & Participation Analytics</h1>
            <p>Academic Project Suite | Scenario 6: Event Scheduling, Attendance Verification & Student Performance Analytics</p>
            <div class="badge-grp">
                <span class="badge">Register Numbers: 6, 36, 66, 96</span>
                <span class="badge">Java SE 17</span>
                <span class="badge">SQLite DBMS</span>
                <span class="badge">Python 3.13 EDA</span>
            </div>
        </div>
        <div>
            <span style="font-size: 13px; color: var(--accent-green); font-weight: 600;">● System Live & Synced</span>
        </div>
    </div>

    <!-- KPI Summary Section -->
    <div class="kpi-grid">
        <div class="kpi-card">
            <div class="title">Total Enrolled Students</div>
            <div class="value">34</div>
        </div>
        <div class="kpi-card">
            <div class="title">Campus Events Scheduled</div>
            <div class="value">20</div>
        </div>
        <div class="kpi-card">
            <div class="title">Active Registrations</div>
            <div class="value">98 / 102</div>
        </div>
        <div class="kpi-card">
            <div class="title">Overall Attendance Rate</div>
            <div class="value">89.86%</div>
        </div>
        <div class="kpi-card">
            <div class="title">Verified Participations</div>
            <div class="value">61</div>
        </div>
    </div>

    <!-- Filter Buttons -->
    <div class="filter-bar">
        <button class="filter-btn active" onclick="filterCategory('All')">All Visuals (20)</button>
        <button class="filter-btn" onclick="filterCategory('Registrations')">Registrations & Popularity</button>
        <button class="filter-btn" onclick="filterCategory('Attendance')">Attendance Analysis</button>
        <button class="filter-btn" onclick="filterCategory('Students')">Student Rankings</button>
        <button class="filter-btn" onclick="filterCategory('Departments')">Department Analytics</button>
        <button class="filter-btn" onclick="filterCategory('Capacity')">Capacity & Available Seats</button>
        <button class="filter-btn" onclick="filterCategory('Trends')">Time-Series Trends</button>
        <button class="filter-btn" onclick="filterCategory('Events')">Event Categories & Lifecycle</button>
    </div>

    <!-- Charts Grid -->
    <div class="charts-grid">
"""

    for img_path in images:
        fname = os.path.basename(img_path)
        meta = CARDS_META.get(fname, (fname.replace(".png", "").replace("_", " ").title(), "General", "Visual generated by Python data analysis."))
        title, tag, desc = meta
        rel_img = f"Python/Data Visualization/charts/{fname}"

        html += f"""
        <div class="chart-card" data-category="{tag}">
            <div class="card-head">
                <span class="card-title">{title}</span>
                <span class="card-tag">{tag}</span>
            </div>
            <div class="chart-img-container" onclick="openModal('{rel_img}')">
                <img src="{rel_img}" alt="{title}" loading="lazy">
            </div>
            <div class="card-foot">
                {desc}
            </div>
        </div>
"""

    html += """
    </div>

    <!-- Modal for Zoom -->
    <div id="imageModal" class="modal" onclick="closeModal()">
        <img id="modalImg" src="" alt="Zoomed Visual">
    </div>

    <script>
        function filterCategory(cat) {
            document.querySelectorAll('.filter-btn').forEach(b => b.classList.remove('active'));
            event.target.classList.add('active');
            
            const cards = document.querySelectorAll('.chart-card');
            cards.forEach(card => {
                if (cat === 'All' || card.getAttribute('data-category') === cat) {
                    card.style.display = 'flex';
                } else {
                    card.style.display = 'none';
                }
            });
        }

        function openModal(src) {
            document.getElementById('modalImg').src = src;
            document.getElementById('imageModal').style.display = 'flex';
        }

        function closeModal() {
            document.getElementById('imageModal').style.display = 'none';
        }
    </script>
</body>
</html>
"""

    with open(OUT_HTML, "w", encoding="utf-8") as f:
        f.write(html)
    print(f"[SUCCESS] Standalone HTML visuals dashboard generated at: {OUT_HTML}")

if __name__ == "__main__":
    generate_dashboard()
