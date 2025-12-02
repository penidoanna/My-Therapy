#!/usr/bin/env python3
"""
My Therapy Presentation Generator
==================================

This script generates a PowerPoint presentation for the My Therapy project.
It creates a PPTX file with:
  - 3 full-bleed image slides (from user-provided images)
  - 6 content slides with visual-first design (title, bullets, placeholder area)

Usage:
    python generate_presentation.py <slide1_image> <slide2_image> <slide3_image>

Example:
    python tools/generate_presentation.py slide1.png slide2.png slide3.png

Requirements:
    - python-pptx
    - Pillow

Install requirements:
    pip install python-pptx Pillow

Output:
    presentation/My-Therapy-presentation.pptx
"""

import argparse
import os
import sys
from pathlib import Path

from PIL import Image
from pptx import Presentation
from pptx.dml.color import RGBColor
from pptx.enum.shapes import MSO_AUTO_SHAPE_TYPE
from pptx.enum.text import PP_ALIGN, MSO_ANCHOR
from pptx.util import Inches, Pt


# Slide dimensions (widescreen 16:9)
SLIDE_WIDTH = Inches(13.333)
SLIDE_HEIGHT = Inches(7.5)

# Colors
TITLE_COLOR = RGBColor(0x2E, 0x74, 0xB5)  # Professional blue
BULLET_COLOR = RGBColor(0x33, 0x33, 0x33)  # Dark gray
PLACEHOLDER_COLOR = RGBColor(0xE8, 0xE8, 0xE8)  # Light gray


def validate_image_paths(image_paths):
    """Validate that all image paths exist."""
    for img_path in image_paths:
        if not os.path.exists(img_path):
            print(f"Error: Image file not found: {img_path}")
            sys.exit(1)


def create_full_bleed_image_slide(prs, image_path):
    """Create a slide with a full-bleed image (covers entire slide)."""
    blank_slide_layout = prs.slide_layouts[6]  # Blank layout
    slide = prs.slides.add_slide(blank_slide_layout)

    # Open image to get dimensions for proper scaling
    with Image.open(image_path) as img:
        img_width, img_height = img.size

    # Calculate scaling to cover entire slide (may crop)
    slide_ratio = SLIDE_WIDTH / SLIDE_HEIGHT
    img_ratio = img_width / img_height

    if img_ratio > slide_ratio:
        # Image is wider - scale by height
        height = SLIDE_HEIGHT
        width = SLIDE_HEIGHT * img_ratio
        left = (SLIDE_WIDTH - width) / 2
        top = Inches(0)
    else:
        # Image is taller - scale by width
        width = SLIDE_WIDTH
        height = SLIDE_WIDTH / img_ratio
        left = Inches(0)
        top = (SLIDE_HEIGHT - height) / 2

    slide.shapes.add_picture(image_path, left, top, width=width, height=height)
    return slide


def create_content_slide(prs, title, bullets):
    """
    Create a visual-first content slide with:
    - Big title at top
    - 3-5 short bullets on the left
    - Large light-shaded rectangle on the right for icons/screenshots
    """
    blank_slide_layout = prs.slide_layouts[6]  # Blank layout
    slide = prs.slides.add_slide(blank_slide_layout)

    # Add title
    title_left = Inches(0.5)
    title_top = Inches(0.4)
    title_width = Inches(12)
    title_height = Inches(1)

    title_box = slide.shapes.add_textbox(title_left, title_top, title_width, title_height)
    title_frame = title_box.text_frame
    title_frame.word_wrap = True
    title_para = title_frame.paragraphs[0]
    title_para.text = title
    title_para.font.size = Pt(40)
    title_para.font.bold = True
    title_para.font.name = "Calibri"
    title_para.font.color.rgb = TITLE_COLOR

    # Add bullets on the left side
    bullets_left = Inches(0.5)
    bullets_top = Inches(1.6)
    bullets_width = Inches(6)
    bullets_height = Inches(5.5)

    bullet_box = slide.shapes.add_textbox(bullets_left, bullets_top, bullets_width, bullets_height)
    bullet_frame = bullet_box.text_frame
    bullet_frame.word_wrap = True

    for i, bullet_text in enumerate(bullets):
        if i == 0:
            para = bullet_frame.paragraphs[0]
        else:
            para = bullet_frame.add_paragraph()
        para.text = f"• {bullet_text}"
        para.font.size = Pt(20)
        para.font.name = "Calibri"
        para.font.color.rgb = BULLET_COLOR
        para.space_before = Pt(12)
        para.space_after = Pt(6)

    # Add placeholder rectangle on the right side
    rect_left = Inches(7)
    rect_top = Inches(1.6)
    rect_width = Inches(5.8)
    rect_height = Inches(5.4)

    shape = slide.shapes.add_shape(MSO_AUTO_SHAPE_TYPE.RECTANGLE, rect_left, rect_top, rect_width, rect_height)
    shape.fill.solid()
    shape.fill.fore_color.rgb = PLACEHOLDER_COLOR
    shape.line.fill.background()  # No border

    # Add placeholder text inside the rectangle
    if shape.has_text_frame:
        tf = shape.text_frame
        tf.word_wrap = True
        p = tf.paragraphs[0]
        p.text = "[Visual / Icon / Screenshot]"
        p.font.size = Pt(16)
        p.font.name = "Calibri"
        p.font.color.rgb = RGBColor(0x99, 0x99, 0x99)
        p.alignment = PP_ALIGN.CENTER

    return slide


def generate_presentation(image_paths, output_path):
    """Generate the complete presentation."""
    prs = Presentation()
    prs.slide_width = SLIDE_WIDTH
    prs.slide_height = SLIDE_HEIGHT

    # Validate and add 3 full-bleed image slides
    validate_image_paths(image_paths)
    for image_path in image_paths:
        create_full_bleed_image_slide(prs, image_path)

    # Define the 6 content slides
    content_slides = [
        (
            "Design",
            [
                "MVC: Controllers → Services → Repositories",
                "Role-based UIs: Patient / Psychologist / Admin",
                "Server-side rendering + REST endpoints",
                "Therapeutic Resources integrated",
            ],
        ),
        (
            "System Architecture",
            [
                "Three-tier: Presentation → Application → Data",
                "Spring Boot backend + PostgreSQL (RDS)",
                "External: Stripe (payments), SMTP (emails), AWS deploy",
                "Security: Spring Security + JWT",
            ],
        ),
        (
            "Components Used",
            [
                "Spring Boot, Spring Data JPA, Spring Security, JUnit",
                "PostgreSQL, Stripe API, Spring Mail, AWS (EC2/RDS/CloudWatch)",
                "Packages: model/, repository/, service/, controller/, security/",
                "Frontend: static HTML templates",
            ],
        ),
        (
            "Main Algorithms",
            [
                "Slot generation: S = { [b^s + m·d, b^s + (m+1)·d) }",
                "Overlap check: s_i < e_j ∧ s_j < e_i",
                "Atomic booking: transaction → check overlaps → insert → commit",
                "Reminders: query appointments at t+24h → send emails",
            ],
        ),
        (
            "Roadmap",
            [
                "Development → Testing → Deployment → Monitoring",
            ],
        ),
        (
            "Next Steps / Demo",
            [
                "Prepare demo data, test Stripe in sandbox, finalize UI polish",
            ],
        ),
    ]

    # Add content slides
    for title, bullets in content_slides:
        create_content_slide(prs, title, bullets)

    # Save presentation
    output_dir = os.path.dirname(output_path)
    if output_dir and not os.path.exists(output_dir):
        os.makedirs(output_dir)

    prs.save(output_path)
    print(f"Presentation saved to: {output_path}")


def main():
    parser = argparse.ArgumentParser(
        description="Generate My Therapy PowerPoint presentation",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog=__doc__,
    )
    parser.add_argument(
        "images",
        nargs=3,
        metavar="IMAGE",
        help="Path to image file (provide exactly 3 images for slides 1-3)",
    )
    parser.add_argument(
        "-o",
        "--output",
        default="presentation/My-Therapy-presentation.pptx",
        help="Output path for the PPTX file (default: presentation/My-Therapy-presentation.pptx)",
    )

    args = parser.parse_args()

    # Validate images exist and generate presentation
    validate_image_paths(args.images)
    generate_presentation(args.images, args.output)


if __name__ == "__main__":
    main()
