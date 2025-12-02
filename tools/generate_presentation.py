#!/usr/bin/env python3
"""
Generate My-Therapy PowerPoint Presentation

This script generates a PowerPoint presentation for the My-Therapy project.
It creates 9 slides total:
  - 3 image slides (full-bleed, centered, preserving aspect ratio)
  - 6 content slides with project information

Usage:
    python generate_presentation.py <slide1_image> <slide2_image> <slide3_image> [--out <output_path>]

Example:
    python generate_presentation.py slide1.png slide2.png slide3.png --out presentation/My-Therapy-presentation.pptx

Dependencies:
    - python-pptx
    - Pillow

If an image path is missing or invalid, the script inserts a placeholder slide
with a light gray background and still produces the PPTX.
"""

import argparse
import os
import sys
from pathlib import Path

try:
    from pptx import Presentation
    from pptx.util import Inches, Pt
    from pptx.dml.color import RGBColor
    from pptx.enum.text import PP_ALIGN, MSO_ANCHOR
    from pptx.enum.shapes import MSO_SHAPE
except ImportError:
    print("Error: python-pptx is required. Install with: pip install python-pptx")
    sys.exit(1)

try:
    from PIL import Image
except ImportError:
    print("Error: Pillow is required. Install with: pip install Pillow")
    sys.exit(1)


# Slide dimensions (16:9 widescreen)
SLIDE_WIDTH = Inches(13.333)
SLIDE_HEIGHT = Inches(7.5)

# Colors
LIGHT_PANEL_COLOR = RGBColor(0xF5, 0xF6, 0xF7)  # #F5F6F7
TEAL_COLOR = RGBColor(0x00, 0x80, 0x80)  # Primary teal
DARK_GRAY_COLOR = RGBColor(0x33, 0x33, 0x33)  # Dark gray for text
PLACEHOLDER_COLOR = RGBColor(0xE0, 0xE0, 0xE0)  # Light gray for placeholder

# Font settings
TITLE_FONT_SIZE = Pt(34)
BODY_FONT_SIZE = Pt(18)
FONT_NAME = "Calibri"


def add_image_slide(prs, image_path, slide_num):
    """
    Add a slide with a full-bleed image centered and scaled to fit
    while preserving aspect ratio. If image is missing, add placeholder.
    """
    slide_layout = prs.slide_layouts[6]  # Blank layout
    slide = prs.slides.add_slide(slide_layout)

    if image_path and os.path.exists(image_path):
        try:
            # Get image dimensions to calculate scaling
            with Image.open(image_path) as img:
                img_width, img_height = img.size

            # Calculate scale to fit within slide while preserving aspect ratio
            slide_width_inches = 13.333
            slide_height_inches = 7.5

            scale_w = slide_width_inches / (img_width / 96)  # Assume 96 DPI
            scale_h = slide_height_inches / (img_height / 96)
            scale = min(scale_w, scale_h)

            # Calculate final dimensions
            final_width = (img_width / 96) * scale
            final_height = (img_height / 96) * scale

            # Calculate position to center
            left = (slide_width_inches - final_width) / 2
            top = (slide_height_inches - final_height) / 2

            # Add the image
            slide.shapes.add_picture(
                image_path,
                Inches(left),
                Inches(top),
                Inches(final_width),
                Inches(final_height)
            )
        except Exception as e:
            print(f"Warning: Could not load image {image_path}: {e}")
            _add_placeholder(slide, f"Image Slide {slide_num}")
    else:
        if image_path:
            print(f"Warning: Image not found: {image_path}")
        _add_placeholder(slide, f"Image Slide {slide_num}")


def _add_placeholder(slide, text):
    """Add a placeholder rectangle with text for missing images."""
    # Add a large gray rectangle as placeholder
    shape = slide.shapes.add_shape(
        MSO_SHAPE.ROUNDED_RECTANGLE,
        Inches(1),
        Inches(1),
        Inches(11.333),
        Inches(5.5)
    )
    shape.fill.solid()
    shape.fill.fore_color.rgb = PLACEHOLDER_COLOR
    shape.line.fill.background()

    # Add text to the shape
    text_frame = shape.text_frame
    text_frame.word_wrap = True
    p = text_frame.paragraphs[0]
    p.text = f"[{text} - Image Placeholder]"
    p.alignment = PP_ALIGN.CENTER
    p.font.size = Pt(24)
    p.font.name = FONT_NAME
    p.font.color.rgb = DARK_GRAY_COLOR


def add_content_slide(prs, title, bullets):
    """
    Add a content slide with:
    - Title top-left (34pt)
    - Bullets left (18pt)
    - Right-side light panel for icons/screenshots
    """
    slide_layout = prs.slide_layouts[6]  # Blank layout
    slide = prs.slides.add_slide(slide_layout)

    # Add title (top-left)
    title_box = slide.shapes.add_textbox(Inches(0.5), Inches(0.5), Inches(6), Inches(1))
    title_frame = title_box.text_frame
    title_frame.word_wrap = True
    p = title_frame.paragraphs[0]
    p.text = title
    p.font.size = TITLE_FONT_SIZE
    p.font.name = FONT_NAME
    p.font.bold = True
    p.font.color.rgb = TEAL_COLOR

    # Add bullets (left side)
    bullets_box = slide.shapes.add_textbox(Inches(0.5), Inches(1.7), Inches(6), Inches(5))
    bullets_frame = bullets_box.text_frame
    bullets_frame.word_wrap = True

    for i, bullet in enumerate(bullets):
        if i == 0:
            p = bullets_frame.paragraphs[0]
        else:
            p = bullets_frame.add_paragraph()
        p.text = f"• {bullet}"
        p.font.size = BODY_FONT_SIZE
        p.font.name = FONT_NAME
        p.font.color.rgb = DARK_GRAY_COLOR
        p.space_after = Pt(12)

    # Add right-side panel (light rounded rectangle for icons/screenshots)
    panel = slide.shapes.add_shape(
        MSO_SHAPE.ROUNDED_RECTANGLE,
        Inches(7),
        Inches(0.5),
        Inches(5.8),
        Inches(6.5)
    )
    panel.fill.solid()
    panel.fill.fore_color.rgb = LIGHT_PANEL_COLOR
    panel.line.fill.background()

    # Add placeholder text in the panel
    panel_text = slide.shapes.add_textbox(Inches(7.3), Inches(3.2), Inches(5.2), Inches(1))
    panel_frame = panel_text.text_frame
    p = panel_frame.paragraphs[0]
    p.text = "[Icon / Screenshot Area]"
    p.alignment = PP_ALIGN.CENTER
    p.font.size = Pt(14)
    p.font.name = FONT_NAME
    p.font.color.rgb = RGBColor(0x99, 0x99, 0x99)


def create_presentation(image_paths, output_path):
    """Create the complete presentation with 9 slides."""
    prs = Presentation()

    # Set slide dimensions (16:9)
    prs.slide_width = SLIDE_WIDTH
    prs.slide_height = SLIDE_HEIGHT

    # Slide 1-3: Image slides
    for i, img_path in enumerate(image_paths, 1):
        add_image_slide(prs, img_path, i)

    # Slide 4: Design
    add_content_slide(prs, "Design", [
        "Calibri / system-sans — Title 34 / Body 18",
        "Primary teal, dark gray, light panels",
        "Rounded buttons (primary / ghost)",
        "24px spacing system; responsive grid",
        "Line icons + large screenshots",
        "WCAG contrast & keyboard focus"
    ])

    # Slide 5: System Architecture
    add_content_slide(prs, "System Architecture", [
        "Three-tier: Presentation → Application → Data",
        "Spring Boot + PostgreSQL (RDS)",
        "Stripe, SMTP, AWS",
        "Spring Security + JWT"
    ])

    # Slide 6: Components Used
    add_content_slide(prs, "Components Used", [
        "Spring Boot, Spring Data JPA, Spring Security, JUnit",
        "PostgreSQL, Stripe API, Spring Mail, AWS",
        "Packages: model/, repository/, service/, controller/, security/",
        "Frontend: static HTML templates"
    ])

    # Slide 7: Main Algorithms
    add_content_slide(prs, "Main Algorithms", [
        "Slot gen: S = { [b^s + m·d, b^s + (m+1)·d) }",
        "Overlap: s_i < e_j ∧ s_j < e_i",
        "Atomic booking: tx → check overlaps → insert → commit",
        "Reminders: query t+24h → send emails"
    ])

    # Slide 8: Roadmap
    add_content_slide(prs, "Roadmap", [
        "Development → Testing → Deployment → Monitoring"
    ])

    # Slide 9: Next Steps / Demo
    add_content_slide(prs, "Next Steps / Demo", [
        "Prepare demo data",
        "Test Stripe sandbox",
        "UI polish"
    ])

    # Ensure output directory exists
    output_dir = os.path.dirname(output_path)
    if output_dir:
        os.makedirs(output_dir, exist_ok=True)

    # Save the presentation
    prs.save(output_path)
    print(f"Presentation saved to: {output_path}")


def main():
    parser = argparse.ArgumentParser(
        description="Generate My-Therapy PowerPoint Presentation",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog=__doc__
    )
    parser.add_argument(
        "images",
        nargs=3,
        metavar="IMAGE",
        help="Paths to the three image files for slides 1-3 (slide1 slide2 slide3)"
    )
    parser.add_argument(
        "--out",
        default="presentation/My-Therapy-presentation.pptx",
        help="Output path for the PPTX file (default: presentation/My-Therapy-presentation.pptx)"
    )

    args = parser.parse_args()

    # Validate that at least one image exists or warn the user
    images_found = sum(1 for img in args.images if os.path.exists(img))
    if images_found == 0:
        print("Warning: No image files found. All image slides will use placeholders.")

    create_presentation(args.images, args.out)


if __name__ == "__main__":
    main()
