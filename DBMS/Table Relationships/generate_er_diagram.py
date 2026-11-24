# ==============================================================================
# SMART CAMPUS EVENT MANAGEMENT SYSTEM - E-R DIAGRAM GENERATOR
# Course Code: 25ITT31 - Database Management Systems (Scenario 6)
# Standard Cheney Notation (Cheney E-R Model)
# ==============================================================================

import os
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
import matplotlib.patches as patches

def generate_er_diagram(output_path):
    fig, ax = plt.subplots(figsize=(20, 14), dpi=300)
    ax.set_xlim(0, 100)
    ax.set_ylim(0, 100)
    ax.axis('off')

    # Color Palette (Standard Academic Database Textbook Theme)
    entity_color = '#E3F2FD'     # Soft blue
    entity_edge = '#1565C0'      # Deep blue
    rel_color = '#FFF3E0'        # Soft amber/orange
    rel_edge = '#E65100'         # Deep orange
    attr_color = '#F3E5F5'       # Soft violet
    attr_edge = '#7B1FA2'        # Deep purple
    pk_color = '#E8F5E9'         # Soft green
    pk_edge = '#2E7D32'          # Deep green

    # Header & Title
    ax.text(50, 97.5, "SMART CAMPUS EVENT MANAGEMENT & STUDENT PARTICIPATION SYSTEM", 
            ha='center', va='center', fontsize=18, fontweight='bold', color='#0D47A1')
    ax.text(50, 95, "ENTITY-RELATIONSHIP (E-R) DIAGRAM | SCENARIO 6 (CHENEY NOTATION)", 
            ha='center', va='center', fontsize=13, fontweight='bold', color='#1565C0')
    ax.text(50, 92.5, "Course Code: 25ITT31 - DBMS | Register Numbers: 6, 36, 66, 96 | VCET B.Tech IT", 
            ha='center', va='center', fontsize=10, style='italic', color='#546E7A')

    # Helper: Entity Box (Rectangle)
    def draw_entity(x, y, w, h, text, is_associative=False):
        rect = patches.FancyBboxPatch((x - w/2, y - h/2), w, h, boxstyle="square,pad=0",
                                      facecolor=entity_color, edgecolor=entity_edge, linewidth=2.2, zorder=3)
        ax.add_patch(rect)
        if is_associative:
            # Diamond inside rectangle for associative entity
            d_w = w * 0.9
            d_h = h * 0.85
            diamond = patches.Polygon([[x, y + d_h/2], [x + d_w/2, y], [x, y - d_h/2], [x - d_w/2, y]],
                                      closed=True, facecolor='none', edgecolor=rel_edge, linewidth=1.5, zorder=3)
            ax.add_patch(diamond)
        ax.text(x, y, text, ha='center', va='center', fontsize=11, fontweight='bold', color='#0D47A1', zorder=4)

    # Helper: Relationship (Diamond)
    def draw_relationship(x, y, size_w, size_h, text):
        hw = size_w / 2
        hh = size_h / 2
        diamond = patches.Polygon([[x, y + hh], [x + hw, y], [x, y - hh], [x - hw, y]],
                                  closed=True, facecolor=rel_color, edgecolor=rel_edge, linewidth=2, zorder=3)
        ax.add_patch(diamond)
        ax.text(x, y, text, ha='center', va='center', fontsize=9.5, fontweight='bold', color='#BF360C', zorder=4)

    # Helper: Attribute (Ellipse)
    def draw_attribute(x, y, rx, ry, text, is_pk=False, is_fk=False):
        ec = pk_edge if is_pk else (rel_edge if is_fk else attr_edge)
        fc = pk_color if is_pk else ('#FFF8E1' if is_fk else attr_color)
        lw = 2.0 if (is_pk or is_fk) else 1.2
        ellipse = patches.Ellipse((x, y), rx * 2, ry * 2, facecolor=fc, edgecolor=ec, linewidth=lw, zorder=3)
        ax.add_patch(ellipse)
        
        txt = f"{text} (PK)" if is_pk else (f"{text} (FK)" if is_fk else text)
        fontw = 'bold' if (is_pk or is_fk) else 'normal'
        ax.text(x, y, txt, ha='center', va='center', fontsize=8, fontweight=fontw, color='#212121', zorder=4)

    # Helper: Line with optional cardinality label
    def draw_line(p1, p2, label=None, lbl_pos=0.5, offset=(0, 2)):
        ax.plot([p1[0], p2[0]], [p1[1], p2[1]], color='#37474F', linewidth=1.6, zorder=2)
        if label:
            lx = p1[0] + (p2[0] - p1[0]) * lbl_pos + offset[0]
            ly = p1[1] + (p2[1] - p1[1]) * lbl_pos + offset[1]
            ax.text(lx, ly, label, ha='center', va='center', fontsize=10, fontweight='bold', 
                    color='#D84315', bbox=dict(boxstyle="round,pad=0.25", facecolor='#FFFDE7', edgecolor='#FFF59D', alpha=0.95), zorder=5)

    # --------------------------------------------------------------------------
    # 1. CORE ENTITY POSITIONS
    # --------------------------------------------------------------------------
    stu_pos = (18, 55)
    reg_pos = (50, 55)
    evt_pos = (82, 55)
    att_pos = (35, 20)
    part_pos = (65, 20)

    # Draw Entities
    draw_entity(stu_pos[0], stu_pos[1], 16, 7.5, "STUDENTS")
    draw_entity(evt_pos[0], evt_pos[1], 16, 7.5, "EVENTS")
    draw_entity(reg_pos[0], reg_pos[1], 18, 8, "REGISTRATIONS\n(Associative)", is_associative=True)
    draw_entity(att_pos[0], att_pos[1], 16, 7.5, "ATTENDANCE")
    draw_entity(part_pos[0], part_pos[1], 16, 7.5, "PARTICIPATIONS")

    # --------------------------------------------------------------------------
    # 2. RELATIONSHIPS (Diamonds & Cardinality)
    # --------------------------------------------------------------------------
    # Student -> Enrolls -> Registration (1 : N)
    draw_relationship(34, 55, 9, 6, "ENROLLS")
    draw_line((stu_pos[0] + 8, stu_pos[1]), (34 - 4.5, 55), "1", 0.5, (0, 2))
    draw_line((34 + 4.5, 55), (reg_pos[0] - 9, 55), "N", 0.5, (0, 2))

    # Event -> Offers -> Registration (1 : N)
    draw_relationship(66, 55, 9, 6, "OFFERS")
    draw_line((evt_pos[0] - 8, evt_pos[1]), (66 + 4.5, 55), "1", 0.5, (0, 2))
    draw_line((66 - 4.5, 55), (reg_pos[0] + 9, 55), "N", 0.5, (0, 2))

    # Registration -> Verifies -> Attendance (1 : 1)
    draw_relationship(41, 37.5, 9, 6, "VERIFIES")
    draw_line((reg_pos[0] - 5, reg_pos[1] - 4), (41, 37.5 + 3), "1", 0.5, (-2, 0))
    draw_line((41, 37.5 - 3), (att_pos[0] + 3, att_pos[1] + 3.75), "1", 0.5, (-2, 0))

    # Registration -> Produces -> Participation (1 : 1)
    draw_relationship(59, 37.5, 9, 6, "PRODUCES")
    draw_line((reg_pos[0] + 5, reg_pos[1] - 4), (59, 37.5 + 3), "1", 0.5, (2, 0))
    draw_line((59, 37.5 - 3), (part_pos[0] - 3, part_pos[1] + 3.75), "1", 0.5, (2, 0))

    # --------------------------------------------------------------------------
    # 3. ATTRIBUTES (Ellipses)
    # --------------------------------------------------------------------------
    # STUDENTS Attributes
    stu_attrs = [
        (8, 73, "student_id", True, False),
        (8, 67, "register_number", False, False),
        (8, 61, "student_name", False, False),
        (8, 55, "department", False, False),
        (8, 49, "year_of_study", False, False),
        (8, 43, "email", False, False),
        (8, 37, "phone", False, False)
    ]
    for ax_coord, ay_coord, name, is_pk, is_fk in stu_attrs:
        draw_attribute(ax_coord, ay_coord, 5.5, 2, name, is_pk, is_fk)
        draw_line((ax_coord + 5.5, ay_coord), (stu_pos[0] - 8, stu_pos[1]))

    # EVENTS Attributes
    evt_attrs = [
        (92, 75, "event_id", True, False),
        (92, 69, "event_name", False, False),
        (92, 63, "event_type", False, False),
        (92, 57, "event_date", False, False),
        (92, 51, "venue", False, False),
        (92, 45, "max_participants", False, False),
        (92, 39, "event_status", False, False),
        (92, 33, "description", False, False)
    ]
    for ax_coord, ay_coord, name, is_pk, is_fk in evt_attrs:
        draw_attribute(ax_coord, ay_coord, 5.8, 2, name, is_pk, is_fk)
        draw_line((ax_coord - 5.8, ay_coord), (evt_pos[0] + 8, evt_pos[1]))

    # REGISTRATIONS Attributes
    reg_attrs = [
        (42, 75, "registration_id", True, False),
        (47, 81, "student_id", False, True),
        (53, 81, "event_id", False, True),
        (58, 75, "registration_date", False, False),
        (50, 70, "status", False, False)
    ]
    for ax_coord, ay_coord, name, is_pk, is_fk in reg_attrs:
        draw_attribute(ax_coord, ay_coord, 5.5, 2, name, is_pk, is_fk)
        draw_line((ax_coord, ay_coord - 2), (reg_pos[0], reg_pos[1] + 4))

    # ATTENDANCE Attributes
    att_attrs = [
        (20, 22, "attendance_id", True, False),
        (20, 16, "registration_id", False, True),
        (22, 10, "attendance_date", False, False),
        (35, 9, "attendance_status", False, False)
    ]
    for ax_coord, ay_coord, name, is_pk, is_fk in att_attrs:
        draw_attribute(ax_coord, ay_coord, 5.5, 2, name, is_pk, is_fk)
        draw_line((ax_coord, ay_coord), (att_pos[0] - 4, att_pos[1]))

    # PARTICIPATIONS Attributes
    part_attrs = [
        (80, 22, "participation_id", True, False),
        (80, 16, "registration_id", False, True),
        (78, 10, "participation_status", False, False),
        (65, 9, "certificate_status", False, False),
        (52, 9, "remarks", False, False)
    ]
    for ax_coord, ay_coord, name, is_pk, is_fk in part_attrs:
        draw_attribute(ax_coord, ay_coord, 5.5, 2, name, is_pk, is_fk)
        draw_line((ax_coord, ay_coord), (part_pos[0] + 4, part_pos[1]))

    # --------------------------------------------------------------------------
    # 4. LEGEND & ANNOTATION BOX
    # --------------------------------------------------------------------------
    leg_x = 5
    leg_y = 12
    leg_box = patches.FancyBboxPatch((leg_x, leg_y - 8), 24, 9, boxstyle="round,pad=0.3",
                                     facecolor='#FAFAFA', edgecolor='#B0BEC5', linewidth=1.2, zorder=6)
    ax.add_patch(leg_box)
    ax.text(leg_x + 1, leg_y, "E-R NOTATION LEGEND (Cheney):", fontsize=9, fontweight='bold', color='#37474F', zorder=7)
    
    # Legend items
    ax.add_patch(patches.Rectangle((leg_x + 1, leg_y - 2.5), 2.5, 1.5, facecolor=entity_color, edgecolor=entity_edge, lw=1.5, zorder=7))
    ax.text(leg_x + 4.5, leg_y - 1.8, "Entity Set", fontsize=8, color='#212121', zorder=7)
    
    ax.add_patch(patches.Polygon([[leg_x + 12, leg_y - 1.5], [leg_x + 13.5, leg_y - 2.5], [leg_x + 12, leg_y - 3.5], [leg_x + 10.5, leg_y - 2.5]], facecolor=rel_color, edgecolor=rel_edge, lw=1.5, zorder=7))
    ax.text(leg_x + 14.5, leg_y - 2.5, "Relationship", fontsize=8, color='#212121', zorder=7)

    ax.add_patch(patches.Ellipse((leg_x + 2.25, leg_y - 5.5), 2.5, 1.5, facecolor=pk_color, edgecolor=pk_edge, lw=1.5, zorder=7))
    ax.text(leg_x + 4.5, leg_y - 5.5, "Primary Key", fontsize=8, color='#212121', zorder=7)

    ax.add_patch(patches.Ellipse((leg_x + 12, leg_y - 5.5), 2.5, 1.5, facecolor=attr_color, edgecolor=attr_edge, lw=1.2, zorder=7))
    ax.text(leg_x + 14.5, leg_y - 5.5, "Attribute", fontsize=8, color='#212121', zorder=7)

    plt.tight_layout()
    plt.savefig(output_path, dpi=300, bbox_inches='tight')
    plt.close()
    print(f"[SUCCESS] E-R Diagram generated successfully: {output_path}")

if __name__ == "__main__":
    base = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", ".."))
    out1 = os.path.join(os.path.dirname(__file__), "E_R_Diagram.png")
    out2 = os.path.join(base, "E_R_Diagram.png")
    generate_er_diagram(out1)
    generate_er_diagram(out2)
