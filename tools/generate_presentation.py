#!/usr/bin/env python3
"""
My-Therapy Presentation Generator

This script generates a PowerPoint presentation for the My-Therapy project.
It creates a 9-slide PPTX file:
  - Slides 1-3: Full-bleed user-provided images (centered, aspect ratio preserved)
  - Slides 4-9: Content slides (Design, System Architecture, Components Used,
                Main Algorithms, Roadmap, Next Steps / Demo)

Usage:
    python generate_presentation.py slide1.png slide2.png slide3.png [--out output.pptx]

Arguments:
    slide1      Path to image for slide 1 (required, use placeholder if missing)
    slide2      Path to image for slide 2 (required, use placeholder if missing)
    slide3      Path to image for slide 3 (required, use placeholder if missing)
    --out       Output path for the PPTX file (default: presentation/My-Therapy-presentation.pptx)

Dependencies:
    - python-pptx
    - Pillow

Example:
    python tools/generate_presentation.py images/slide1.png images/slide2.png images/slide3.png
    python tools/generate_presentation.py slide1.png slide2.png slide3.png --out my-presentation.pptx
"""

import argparse
import os

from pptx import Presentation
from pptx.util import Inches, Pt
from pptx.dml.color import RGBColor
from pptx.enum.text import PP_ALIGN, MSO_ANCHOR
from pptx.enum.shapes import MSO_SHAPE

try:
    from PIL import Image
except ImportError:
    Image = None


# Slide dimensions (widescreen 16:9)
SLIDE_WIDTH = Inches(13.333)
SLIDE_HEIGHT = Inches(7.5)

# Colors
PRIMARY_TEAL = RGBColor(0, 128, 128)
DARK_GRAY = RGBColor(51, 51, 51)
LIGHT_PANEL = RGBColor(245, 246, 247)  # #F5F6F7
WHITE = RGBColor(255, 255, 255)

# Font settings
TITLE_FONT_SIZE = Pt(34)
BODY_FONT_SIZE = Pt(18)
FONT_NAME = "Calibri"


def create_placeholder_slide(prs, slide_number):
    """Create a placeholder slide when image is missing."""
    slide_layout = prs.slide_layouts[6]  # Blank layout
    slide = prs.slides.add_slide(slide_layout)

    # Add centered placeholder text
    left = Inches(4)
    top = Inches(3)
    width = Inches(5.333)
    height = Inches(1.5)

    shape = slide.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, left, top, width, height)
    shape.fill.solid()
    shape.fill.fore_color.rgb = LIGHT_PANEL
    shape.line.fill.background()

    tf = shape.text_frame
    tf.word_wrap = True
    p = tf.paragraphs[0]
    p.text = f"Image Placeholder (Slide {slide_number})"
    p.font.size = Pt(24)
    p.font.name = FONT_NAME
    p.font.color.rgb = DARK_GRAY
    p.alignment = PP_ALIGN.CENTER

    return slide


def add_image_slide(prs, image_path, slide_number):
    """Add a full-bleed image slide with centered image preserving aspect ratio."""
    if not image_path or not os.path.exists(image_path):
        print(f"Warning: Image not found at '{image_path}', inserting placeholder for slide {slide_number}")
        return create_placeholder_slide(prs, slide_number)

    slide_layout = prs.slide_layouts[6]  # Blank layout
    slide = prs.slides.add_slide(slide_layout)

    # Get image dimensions
    if Image:
        with Image.open(image_path) as img:
            img_width, img_height = img.size
    else:
        # Fallback: assume 16:9 ratio if PIL not available
        img_width, img_height = 1920, 1080

    # Calculate scaling to fit slide while preserving aspect ratio
    slide_w = SLIDE_WIDTH.emu
    slide_h = SLIDE_HEIGHT.emu

    img_ratio = img_width / img_height
    slide_ratio = slide_w / slide_h

    if img_ratio > slide_ratio:
        # Image is wider than slide, fit by width
        new_width = slide_w
        new_height = int(slide_w / img_ratio)
    else:
        # Image is taller than slide, fit by height
        new_height = slide_h
        new_width = int(slide_h * img_ratio)

    # Center the image
    left = (slide_w - new_width) // 2
    top = (slide_h - new_height) // 2

    slide.shapes.add_picture(image_path, left, top, new_width, new_height)

    return slide


def add_content_slide(prs, title, bullets):
    """
    Add a content slide with title at top-left, bullets on left side,
    and a light rounded rectangle on the right for icons/screenshots.
    """
    slide_layout = prs.slide_layouts[6]  # Blank layout
    slide = prs.slides.add_slide(slide_layout)

    # Title at top-left
    title_left = Inches(0.5)
    title_top = Inches(0.4)
    title_width = Inches(12)
    title_height = Inches(0.8)

    title_shape = slide.shapes.add_textbox(title_left, title_top, title_width, title_height)
    tf = title_shape.text_frame
    tf.word_wrap = True
    p = tf.paragraphs[0]
    p.text = title
    p.font.size = TITLE_FONT_SIZE
    p.font.name = FONT_NAME
    p.font.bold = True
    p.font.color.rgb = DARK_GRAY

    # Bullets on left side
    bullets_left = Inches(0.5)
    bullets_top = Inches(1.4)
    bullets_width = Inches(6.5)
    bullets_height = Inches(5.5)

    bullets_shape = slide.shapes.add_textbox(bullets_left, bullets_top, bullets_width, bullets_height)
    tf = bullets_shape.text_frame
    tf.word_wrap = True

    for i, bullet in enumerate(bullets):
        if i == 0:
            p = tf.paragraphs[0]
        else:
            p = tf.add_paragraph()
        p.text = f"• {bullet}"
        p.font.size = BODY_FONT_SIZE
        p.font.name = FONT_NAME
        p.font.color.rgb = DARK_GRAY
        p.space_after = Pt(12)
        p.alignment = PP_ALIGN.LEFT

    # Light rounded rectangle on the right for icons/screenshots
    rect_left = Inches(7.5)
    rect_top = Inches(1.2)
    rect_width = Inches(5.3)
    rect_height = Inches(5.8)

    rect = slide.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, rect_left, rect_top, rect_width, rect_height)
    rect.fill.solid()
    rect.fill.fore_color.rgb = LIGHT_PANEL
    rect.line.fill.background()

    # Add placeholder text inside rectangle
    rect_tf = rect.text_frame
    rect_tf.word_wrap = True
    rect_p = rect_tf.paragraphs[0]
    rect_p.text = "[Icons / Screenshots]"
    rect_p.font.size = Pt(14)
    rect_p.font.name = FONT_NAME
    rect_p.font.color.rgb = RGBColor(180, 180, 180)
    rect_p.alignment = PP_ALIGN.CENTER

    return slide


def generate_presentation(image1, image2, image3, output_path):
    """Generate the complete My-Therapy presentation."""
    prs = Presentation()
    prs.slide_width = SLIDE_WIDTH
    prs.slide_height = SLIDE_HEIGHT

    # Slide 1-3: User-provided images (full-bleed, centered)
    add_image_slide(prs, image1, 1)
    add_image_slide(prs, image2, 2)
    add_image_slide(prs, image3, 3)

    # Slide 4: Design
    design_bullets = [
        "Calibri / system-sans — Title 34 / Body 18",
        "Primary teal, dark gray, light panels",
        "Rounded buttons (primary / ghost)",
        "24px spacing system; responsive grid",
        "Line icons + large screenshots",
        "WCAG contrast & keyboard focus",
    ]
    add_content_slide(prs, "Design", design_bullets)

    # Slide 5: System Architecture
    arch_bullets = [
        "Three-tier: Presentation → Application → Data",
        "Spring Boot + PostgreSQL (RDS)",
        "Stripe, SMTP, AWS",
        "Spring Security + JWT",
    ]
    add_content_slide(prs, "System Architecture", arch_bullets)

    # Slide 6: Components Used
    components_bullets = [
        "Spring Boot, Spring Data JPA, Spring Security, JUnit",
        "PostgreSQL, Stripe API, Spring Mail, AWS",
        "Packages: model/, repository/, service/, controller/, security/",
        "Frontend: static HTML templates",
    ]
    add_content_slide(prs, "Components Used", components_bullets)

    # Slide 7: Main Algorithms
    algorithms_bullets = [
        "Slot gen: S = { [bˢ + m·d, bˢ + (m+1)·d) }",
        "Overlap: sᵢ < eⱼ ∧ sⱼ < eᵢ",
        "Atomic booking: tx → check overlaps → insert → commit",
        "Reminders: query t+24h → send emails",
    ]
    add_content_slide(prs, "Main Algorithms", algorithms_bullets)

    # Slide 8: Roadmap
    roadmap_bullets = [
        "Development → Testing → Deployment → Monitoring",
    ]
    add_content_slide(prs, "Roadmap", roadmap_bullets)

    # Slide 9: Next Steps / Demo
    next_steps_bullets = [
        "Prepare demo data",
        "Test Stripe sandbox",
        "UI polish",
    ]
    add_content_slide(prs, "Next Steps / Demo", next_steps_bullets)

    # Save the presentation
    output_dir = os.path.dirname(output_path)
    if output_dir and not os.path.exists(output_dir):
        os.makedirs(output_dir)

    prs.save(output_path)
    print(f"Presentation saved to: {output_path}")


def main():
    parser = argparse.ArgumentParser(
        description="Generate My-Therapy presentation PPTX",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog=__doc__,
    )
    parser.add_argument(
        "images",
        nargs=3,
        metavar="IMAGE",
        help="Paths to images for slides 1, 2, and 3 (in order: slide1 slide2 slide3)",
    )
    parser.add_argument(
        "--out",
        default="presentation/My-Therapy-presentation.pptx",
        help="Output path for the PPTX file (default: presentation/My-Therapy-presentation.pptx)",
    )

    args = parser.parse_args()

    image1, image2, image3 = args.images
    output_path = args.out

    # Validate image paths (warnings only, still generate with placeholders)
    for i, img_path in enumerate([image1, image2, image3], 1):
        if not os.path.exists(img_path):
            print(f"Warning: Image file '{img_path}' not found. Slide {i} will use a placeholder.")

    generate_presentation(image1, image2, image3, output_path)


if __name__ == "__main__":
    main()
