# My Therapy Presentation

This folder contains the PowerPoint presentation for the My Therapy project.

## Contents

- `My-Therapy-presentation.pptx` - The main presentation file

## Presentation Structure

The presentation consists of 9 slides:

1. **Slides 1-3**: Full-bleed image slides (cover images)
2. **Slide 4 - Design**: MVC architecture, role-based UIs, REST endpoints
3. **Slide 5 - System Architecture**: Three-tier architecture, tech stack overview
4. **Slide 6 - Components Used**: Spring ecosystem, databases, external services
5. **Slide 7 - Main Algorithms**: Slot generation, overlap checking, atomic booking
6. **Slide 8 - Roadmap**: Development lifecycle overview
7. **Slide 9 - Next Steps / Demo**: Demo preparation and UI finalization

## Regenerating the Presentation

The presentation can be regenerated using the generator script in `tools/generate_presentation.py`.

### Requirements

```bash
pip install python-pptx Pillow
```

### Required Images

Prepare three image files to use as cover slides:
- `slide1.png` (or .jpg/.jpeg)
- `slide2.png` (or .jpg/.jpeg)
- `slide3.png` (or .jpg/.jpeg)

### Usage

From the repository root directory:

```bash
python tools/generate_presentation.py slide1.png slide2.png slide3.png
```

Or with custom output path:

```bash
python tools/generate_presentation.py slide1.png slide2.png slide3.png -o custom/path/output.pptx
```

### Example

```bash
# Using images from a specific directory
python tools/generate_presentation.py \
    /path/to/images/slide1.png \
    /path/to/images/slide2.png \
    /path/to/images/slide3.png
```

The script will generate the presentation at `presentation/My-Therapy-presentation.pptx` by default.

## Visual Style

- Large whitespace and clean layout
- Image-first design on cover slides
- Large titles with Calibri font
- Concise bullet points
- Visual placeholders for icons/screenshots on content slides
