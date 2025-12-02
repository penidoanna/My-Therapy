# My-Therapy Presentation

This folder contains the PowerPoint presentation for the My-Therapy project.

## Contents

- **My-Therapy-presentation.pptx** — The main presentation file with 9 slides:
  - Slides 1-3: Image slides (full-bleed, centered, preserving aspect ratio)
  - Slide 4: Design — Visual style and UI guidelines
  - Slide 5: System Architecture — Three-tier architecture overview
  - Slide 6: Components Used — Technologies and packages
  - Slide 7: Main Algorithms — Slot generation, overlap detection, booking
  - Slide 8: Roadmap — Development timeline
  - Slide 9: Next Steps / Demo — Action items

## Required Image Filenames

The generator script expects three image files:

- `slide1.png` — First image slide
- `slide2.png` — Second image slide
- `slide3.png` — Third image slide

If images are not provided, placeholder slides will be generated.

## Dependencies

To regenerate the presentation, install the following Python packages:

```bash
pip install python-pptx Pillow
```

## Regenerating the Presentation

Use the generator script located in `tools/`:

```bash
# From the repository root
python tools/generate_presentation.py slide1.png slide2.png slide3.png --out presentation/My-Therapy-presentation.pptx
```

### Arguments

| Argument | Description |
|----------|-------------|
| `slide1` | Path to the first image file |
| `slide2` | Path to the second image file |
| `slide3` | Path to the third image file |
| `--out`  | (Optional) Output path for the PPTX file. Default: `presentation/My-Therapy-presentation.pptx` |

### Example with Custom Output Path

```bash
python tools/generate_presentation.py images/intro.png images/overview.png images/conclusion.png --out output/custom.pptx
```

## Visual Style

The presentation follows these design guidelines:

- **Fonts**: Calibri / system sans-serif
- **Title size**: 34pt
- **Body text size**: 18pt
- **Colors**:
  - Primary: Teal (#008080)
  - Text: Dark gray (#333333)
  - Right panel: Light gray (#F5F6F7)
- **Layout**: Large whitespace, image-first design, left-aligned bullets, right-side panel for icons/screenshots
