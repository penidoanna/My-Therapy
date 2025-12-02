# My-Therapy Presentation

This folder contains the PowerPoint presentation for the My-Therapy project.

## Contents

- **My-Therapy-presentation.pptx** — The main presentation file with 9 slides:
  - Slides 1–3: User-provided images (full-bleed, centered, aspect ratio preserved)
  - Slide 4: Design (typography, colors, components, accessibility)
  - Slide 5: System Architecture (three-tier, Spring Boot, AWS)
  - Slide 6: Components Used (frameworks, packages, frontend)
  - Slide 7: Main Algorithms (slot generation, overlap detection, booking, reminders)
  - Slide 8: Roadmap (development phases)
  - Slide 9: Next Steps / Demo (preparation tasks)

## Required Images

Place these images in your working directory before running the generator:

| Filename       | Description              |
|----------------|--------------------------|
| `slide1.png`   | Image for slide 1        |
| `slide2.png`   | Image for slide 2        |
| `slide3.png`   | Image for slide 3        |

Images should be high resolution (recommended 1920×1080 or similar 16:9 aspect ratio).
If an image is missing, the script will insert a placeholder slide.

## Dependencies

Install the required Python packages:

```bash
pip install python-pptx Pillow
```

## Generating the Presentation

Run the generator script from the repository root:

```bash
python tools/generate_presentation.py slide1.png slide2.png slide3.png
```

### Options

- `--out <path>` — Specify output path (default: `presentation/My-Therapy-presentation.pptx`)

### Examples

```bash
# Generate with default output path
python tools/generate_presentation.py images/slide1.png images/slide2.png images/slide3.png

# Generate with custom output path
python tools/generate_presentation.py slide1.png slide2.png slide3.png --out output/my-presentation.pptx
```

## Visual Style

The presentation follows these design guidelines:

- **Font**: Calibri (system sans-serif fallback)
- **Title size**: 34pt, bold, dark gray
- **Body size**: 18pt, dark gray
- **Colors**: Primary teal, dark gray text, light panels (#F5F6F7)
- **Layout**: Large whitespace, image-first, large titles, short readable bullets
- **Content slides**: Title top-left, bullets left, light rounded rectangle on right for icons/screenshots
