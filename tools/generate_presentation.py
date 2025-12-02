#!/usr/bin/env python3
"""
My-Therapy Presentation Generator

This script generates a PowerPoint presentation for the My-Therapy project.
It creates a PPTX file with three image slides (from user-provided images)
followed by six additional content slides with visual-first design.

Usage:
    python generate_presentation.py <slide1.png> <slide2.png> <slide3.png> [output.pptx]

Arguments:
    slide1.png  - Path to the first slide image
    slide2.png  - Path to the second slide image
    slide3.png  - Path to the third slide image
    output.pptx - (Optional) Output file path. Default: presentation/My-Therapy-presentation.pptx

Requirements:
    pip install python-pptx Pillow

Example:
    python tools/generate_presentation.py images/slide1.png images/slide2.png images/slide3.png
"""

import sys
import os
from pathlib import Path

try:
    from pptx import Presentation
    from pptx.util import Inches, Pt
    from pptx.dml.color import RGBColor
    from pptx.enum.text import PP_ALIGN
    from pptx.enum.shapes import MSO_SHAPE
    from PIL import Image
except ImportError as e:
    print(f"Error: Missing required library. Please install dependencies:")
    print("  pip install python-pptx Pillow")
    sys.exit(1)


# Slide content configuration
ADDITIONAL_SLIDES = [
    {
        "title": "Design",
        "bullets": [
            "MVC: Controllers → Services → Repositories",
            "Role-based UIs: Patient / Psychologist / Admin",
            "Server-side rendering + REST endpoints",
            "Therapeutic Resources integrated",
        ],
    },
    {
        "title": "System Architecture",
        "bullets": [
            "Three-tier: Presentation → Application → Data",
            "Spring Boot backend + PostgreSQL (RDS)",
            "External: Stripe (payments), SMTP (emails), AWS deploy",
            "Security: Spring Security + JWT",
        ],
    },
    {
        "title": "Components Used",
        "bullets": [
            "Spring Boot, Spring Data JPA, Spring Security, JUnit",
            "PostgreSQL, Stripe API, Spring Mail, AWS (EC2/RDS/CloudWatch)",
            "Packages: model/, repository/, service/, controller/, security/",
            "Frontend: static HTML templates",
        ],
    },
    {
        "title": "Main Algorithms",
        "bullets": [
            "Slot generation: S = { [b^s + m·d, b^s + (m+1)·d) }",
            "Overlap check: s_i < e_j ∧ s_j < e_i",
            "Atomic booking: transaction → check overlaps → insert → commit",
            "Reminders: query appointments at t+24h → send emails",
        ],
    },
    {
        "title": "Roadmap",
        "bullets": [
            "Development → Testing → Deployment → Monitoring",
        ],
    },
    {
        "title": "Next Steps / Demo",
        "bullets": [
            "Prepare demo data",
            "Test Stripe in sandbox",
            "Finalize UI polish",
        ],
    },
]


def create_image_slide(prs, image_path):
    """Create a slide with a full-page image."""
    # Use blank layout
    blank_layout = prs.slide_layouts[6]
    slide = prs.slides.add_slide(blank_layout)

    # Get slide dimensions
    slide_width = prs.slide_width
    slide_height = prs.slide_height

    # Open image to get dimensions
    with Image.open(image_path) as img:
        img_width, img_height = img.size

    # Calculate scaling to fit slide while maintaining aspect ratio
    width_ratio = slide_width / img_width
    height_ratio = slide_height / img_height
    scale = min(width_ratio, height_ratio)

    new_width = int(img_width * scale)
    new_height = int(img_height * scale)

    # Center the image
    left = (slide_width - new_width) // 2
    top = (slide_height - new_height) // 2

    slide.shapes.add_picture(image_path, left, top, new_width, new_height)
    return slide


def create_content_slide(prs, title, bullets):
    """Create a visual-first content slide with title, bullets on left, and placeholder on right."""
    # Use blank layout for custom positioning
    blank_layout = prs.slide_layouts[6]
    slide = prs.slides.add_slide(blank_layout)

    slide_width = prs.slide_width
    slide_height = prs.slide_height

    # Title - large, at top
    title_left = Inches(0.5)
    title_top = Inches(0.4)
    title_width = Inches(9)
    title_height = Inches(0.8)

    title_box = slide.shapes.add_textbox(title_left, title_top, title_width, title_height)
    title_frame = title_box.text_frame
    title_para = title_frame.paragraphs[0]
    title_para.text = title
    title_para.font.size = Pt(36)
    title_para.font.bold = True
    title_para.font.name = "Calibri"
    title_para.font.color.rgb = RGBColor(0x2E, 0x74, 0xB5)  # Blue color

    # Bullets on left side (about 45% of slide width)
    bullets_left = Inches(0.5)
    bullets_top = Inches(1.5)
    bullets_width = Inches(4.5)
    bullets_height = Inches(4.5)

    bullets_box = slide.shapes.add_textbox(bullets_left, bullets_top, bullets_width, bullets_height)
    bullets_frame = bullets_box.text_frame
    bullets_frame.word_wrap = True

    for i, bullet in enumerate(bullets):
        if i == 0:
            para = bullets_frame.paragraphs[0]
        else:
            para = bullets_frame.add_paragraph()
        para.text = f"• {bullet}"
        para.font.size = Pt(18)
        para.font.name = "Calibri"
        para.font.color.rgb = RGBColor(0x33, 0x33, 0x33)
        para.space_after = Pt(12)

    # Light-shaded rectangle on right (placeholder for icons/screenshots)
    rect_left = Inches(5.3)
    rect_top = Inches(1.5)
    rect_width = Inches(4.2)
    rect_height = Inches(4.5)

    shape = slide.shapes.add_shape(
        MSO_SHAPE.RECTANGLE,
        rect_left,
        rect_top,
        rect_width,
        rect_height,
    )
    shape.fill.solid()
    shape.fill.fore_color.rgb = RGBColor(0xE8, 0xF0, 0xF8)  # Light blue/gray
    shape.line.color.rgb = RGBColor(0xC0, 0xD0, 0xE0)  # Slightly darker border

    # Add placeholder text in rectangle
    text_frame = shape.text_frame
    text_frame.word_wrap = True
    para = text_frame.paragraphs[0]
    para.text = "[Icon/Screenshot Area]"
    para.font.size = Pt(14)
    para.font.name = "Calibri"
    para.font.color.rgb = RGBColor(0x88, 0x88, 0x88)
    para.alignment = PP_ALIGN.CENTER

    return slide


def generate_presentation(image_paths, output_path):
    """Generate the complete presentation."""
    # Validate image paths
    for img_path in image_paths:
        if not os.path.exists(img_path):
            print(f"Error: Image file not found: {img_path}")
            sys.exit(1)

    # Create presentation with widescreen dimensions (16:9)
    prs = Presentation()
    prs.slide_width = Inches(10)
    prs.slide_height = Inches(5.625)  # 16:9 aspect ratio

    print("Creating presentation...")

    # Add image slides (first three slides)
    for i, img_path in enumerate(image_paths):
        print(f"  Adding image slide {i + 1}: {img_path}")
        create_image_slide(prs, img_path)

    # Add content slides
    for slide_data in ADDITIONAL_SLIDES:
        print(f"  Adding content slide: {slide_data['title']}")
        create_content_slide(prs, slide_data["title"], slide_data["bullets"])

    # Save presentation
    output_dir = os.path.dirname(output_path)
    if output_dir and not os.path.exists(output_dir):
        os.makedirs(output_dir)

    prs.save(output_path)
    print(f"\nPresentation saved to: {output_path}")
    print(f"Total slides: {len(prs.slides)}")


def main():
    if len(sys.argv) < 4:
        print(__doc__)
        sys.exit(1)

    image_paths = sys.argv[1:4]
    output_path = (
        sys.argv[4]
        if len(sys.argv) > 4
        else "presentation/My-Therapy-presentation.pptx"
    )

    generate_presentation(image_paths, output_path)


if __name__ == "__main__":
    main()
