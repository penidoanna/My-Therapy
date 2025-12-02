#!/usr/bin/env python3
"""
Generate My-Therapy PowerPoint Presentation

This script generates a PPTX presentation for the My-Therapy project.
It creates slides with user-provided images and programmatically generated
content slides with ultra-short bullets.

Usage:
    python tools/generate_presentation.py --images slide1.png slide2.png slide3.png [--out output.pptx]

Arguments:
    --images: Three image files for the first three slides (required)
    --out: Optional output path (default: presentation/My-Therapy-presentation.pptx)

Dependencies:
    - python-pptx
    - Pillow (PIL)

Install dependencies:
    pip install python-pptx Pillow
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


def create_image_slide(prs, image_path, slide_number):
    """
    Create a slide with a full-bleed image centered and scaled to fit.
    If the image is missing or invalid, create a placeholder slide.
    """
    slide_layout = prs.slide_layouts[6]  # Blank layout
    slide = prs.slides.add_slide(slide_layout)
    
    slide_width = prs.slide_width
    slide_height = prs.slide_height
    
    if image_path and os.path.exists(image_path):
        try:
            # Get image dimensions to calculate scaling
            if Image:
                with Image.open(image_path) as img:
                    img_width, img_height = img.size
            else:
                # Fallback: assume reasonable dimensions
                img_width, img_height = 1920, 1080
            
            # Calculate scaling to fit while preserving aspect ratio
            width_ratio = slide_width / img_width
            height_ratio = slide_height / img_height
            scale = min(width_ratio, height_ratio)
            
            new_width = int(img_width * scale)
            new_height = int(img_height * scale)
            
            # Center the image
            left = (slide_width - new_width) // 2
            top = (slide_height - new_height) // 2
            
            slide.shapes.add_picture(
                image_path,
                left,
                top,
                width=new_width,
                height=new_height
            )
        except Exception as e:
            print(f"Warning: Could not load image {image_path}: {e}")
            _add_placeholder_text(slide, slide_width, slide_height, slide_number)
    else:
        if image_path:
            print(f"Warning: Image not found: {image_path}")
        _add_placeholder_text(slide, slide_width, slide_height, slide_number)
    
    return slide


def _add_placeholder_text(slide, slide_width, slide_height, slide_number):
    """Add placeholder text for missing images."""
    left = Inches(1)
    top = Inches(3)
    width = slide_width - Inches(2)
    height = Inches(1)
    
    textbox = slide.shapes.add_textbox(left, top, width, height)
    tf = textbox.text_frame
    tf.paragraphs[0].text = f"[Image Placeholder - Slide {slide_number}]"
    tf.paragraphs[0].font.size = Pt(34)
    tf.paragraphs[0].font.name = "Calibri"
    tf.paragraphs[0].alignment = PP_ALIGN.CENTER


def create_content_slide(prs, title, bullets):
    """
    Create a content slide with title (34pt) top-left, bullets (18pt) on the left,
    and a large rounded rectangle on the right with fill color #F5F6F7.
    """
    slide_layout = prs.slide_layouts[6]  # Blank layout
    slide = prs.slides.add_slide(slide_layout)
    
    slide_width = prs.slide_width
    slide_height = prs.slide_height
    
    # Add title (34pt, top-left)
    title_left = Inches(0.5)
    title_top = Inches(0.5)
    title_width = Inches(5.5)
    title_height = Inches(0.8)
    
    title_box = slide.shapes.add_textbox(title_left, title_top, title_width, title_height)
    title_tf = title_box.text_frame
    title_tf.paragraphs[0].text = title
    title_tf.paragraphs[0].font.size = Pt(34)
    title_tf.paragraphs[0].font.name = "Calibri"
    title_tf.paragraphs[0].font.bold = True
    title_tf.paragraphs[0].font.color.rgb = RGBColor(0x33, 0x33, 0x33)  # Dark gray
    
    # Add bullets (18pt, left-aligned)
    bullets_left = Inches(0.5)
    bullets_top = Inches(1.5)
    bullets_width = Inches(5)
    bullets_height = Inches(4.5)
    
    bullets_box = slide.shapes.add_textbox(bullets_left, bullets_top, bullets_width, bullets_height)
    bullets_tf = bullets_box.text_frame
    bullets_tf.word_wrap = True
    
    for i, bullet in enumerate(bullets):
        if i == 0:
            p = bullets_tf.paragraphs[0]
        else:
            p = bullets_tf.add_paragraph()
        p.text = f"• {bullet}"
        p.font.size = Pt(18)
        p.font.name = "Calibri"
        p.font.color.rgb = RGBColor(0x33, 0x33, 0x33)
        p.space_after = Pt(12)
        p.alignment = PP_ALIGN.LEFT
    
    # Add rounded rectangle on the right (#F5F6F7)
    rect_left = Inches(6)
    rect_top = Inches(1.2)
    rect_width = Inches(3.5)
    rect_height = Inches(4.5)
    
    shape = slide.shapes.add_shape(
        MSO_SHAPE.ROUNDED_RECTANGLE,
        rect_left,
        rect_top,
        rect_width,
        rect_height
    )
    shape.fill.solid()
    shape.fill.fore_color.rgb = RGBColor(0xF5, 0xF6, 0xF7)
    shape.line.fill.background()  # No border
    
    # Add placeholder text in the rectangle
    shape.text_frame.paragraphs[0].text = "[Icon/Screenshot]"
    shape.text_frame.paragraphs[0].font.size = Pt(14)
    shape.text_frame.paragraphs[0].font.name = "Calibri"
    shape.text_frame.paragraphs[0].font.color.rgb = RGBColor(0x99, 0x99, 0x99)
    shape.text_frame.paragraphs[0].alignment = PP_ALIGN.CENTER
    shape.text_frame.anchor = MSO_ANCHOR.MIDDLE
    
    return slide


def generate_presentation(image_paths, output_path):
    """Generate the complete presentation."""
    prs = Presentation()
    
    # Set slide dimensions (16:9 widescreen)
    prs.slide_width = Inches(10)
    prs.slide_height = Inches(7.5)
    
    # Slides 1-3: User-provided images
    for i, image_path in enumerate(image_paths, 1):
        create_image_slide(prs, image_path, i)
    
    # Slide 4: Design
    create_content_slide(prs, "Design", [
        "Calibri / system-sans — Title 34 / Body 18",
        "Primary teal, dark gray, light panels",
        "Rounded buttons (primary / ghost)",
        "24px spacing system; responsive grid",
        "Line icons + large screenshots",
        "WCAG contrast & keyboard focus"
    ])
    
    # Slide 5: System Architecture
    create_content_slide(prs, "System Architecture", [
        "Three-tier: Presentation → Application → Data",
        "Spring Boot + PostgreSQL (RDS)",
        "Stripe, SMTP, AWS",
        "Spring Security + JWT"
    ])
    
    # Slide 6: Components Used
    create_content_slide(prs, "Components Used", [
        "Spring Boot, Spring Data JPA, Spring Security, JUnit",
        "PostgreSQL, Stripe API, Spring Mail, AWS",
        "Packages: model/, repository/, service/, controller/, security/",
        "Frontend: static HTML templates"
    ])
    
    # Slide 7: Main Algorithms
    create_content_slide(prs, "Main Algorithms", [
        "Slot gen: S = { [b^s + m·d, b^s + (m+1)·d) }",
        "Overlap: s_i < e_j ∧ s_j < e_i",
        "Atomic booking: tx → check overlaps → insert → commit",
        "Reminders: query t+24h → send emails"
    ])
    
    # Slide 8: Roadmap
    create_content_slide(prs, "Roadmap", [
        "Development → Testing → Deployment → Monitoring"
    ])
    
    # Slide 9: Next Steps / Demo
    create_content_slide(prs, "Next Steps / Demo", [
        "Prepare demo data; test Stripe sandbox; UI polish"
    ])
    
    # Ensure output directory exists
    output_dir = os.path.dirname(output_path)
    if output_dir and not os.path.exists(output_dir):
        os.makedirs(output_dir)
    
    # Save the presentation
    prs.save(output_path)
    print(f"Presentation saved to: {output_path}")


def main():
    parser = argparse.ArgumentParser(
        description="Generate My-Therapy PowerPoint Presentation",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
Examples:
    python tools/generate_presentation.py --images slide1.png slide2.png slide3.png
    python tools/generate_presentation.py --images img1.jpg img2.jpg img3.jpg --out my-presentation.pptx
        """
    )
    parser.add_argument(
        "--images",
        nargs=3,
        metavar="IMAGE",
        required=True,
        help="Three image files for slides 1-3 (slide1.png slide2.png slide3.png)"
    )
    parser.add_argument(
        "--out",
        default="presentation/My-Therapy-presentation.pptx",
        help="Output PPTX file path (default: presentation/My-Therapy-presentation.pptx)"
    )
    
    args = parser.parse_args()
    
    generate_presentation(args.images, args.out)


if __name__ == "__main__":
    main()
