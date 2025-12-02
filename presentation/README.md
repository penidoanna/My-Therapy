# My-Therapy Presentation

This folder contains the PowerPoint presentation for the My-Therapy project.

## Contents

- **My-Therapy-presentation.pptx**: The main presentation file containing:
  - Slides 1-3: User-provided images (centered, scaled to fit while preserving aspect ratio)
  - Slide 4: Design - UI/UX design specifications
  - Slide 5: System Architecture - Three-tier architecture overview
  - Slide 6: Components Used - Technology stack and packages
  - Slide 7: Main Algorithms - Core algorithm descriptions
  - Slide 8: Roadmap - Development phases
  - Slide 9: Next Steps / Demo - Future tasks and demo preparation

## Generator Script

The presentation can be regenerated using the script at `tools/generate_presentation.py`.

### Dependencies

Install the required Python packages:

```bash
pip install python-pptx Pillow
```

### Required Images

Prepare three image files for the first three slides:
- `slide1.png` - First slide image
- `slide2.png` - Second slide image
- `slide3.png` - Third slide image

### Usage

```bash
python tools/generate_presentation.py --images slide1.png slide2.png slide3.png
```

With custom output path:

```bash
python tools/generate_presentation.py --images slide1.png slide2.png slide3.png --out output.pptx
```

### Script Features

- Uses python-pptx and Pillow for PPTX generation
- Accepts three image file paths as CLI arguments
- Optional `--out` parameter for custom output path
- Automatically handles missing or invalid images with placeholder slides
- Creates slides 4-9 programmatically with ultra-short bullets
- Layout: Title 34pt, bullets 18pt, rounded rectangle (#F5F6F7) on the right

## Visual Style

- Large whitespace, image-first design
- Calibri-like fonts (Calibri / system-sans)
- Title: 34pt, Body: 18pt
- Primary teal color, dark gray text, light panels
- WCAG-compliant contrast
