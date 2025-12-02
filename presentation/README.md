# My-Therapy Presentation

This folder contains the PowerPoint presentation for the My-Therapy project.

## Contents

- `My-Therapy-presentation.pptx` - The main presentation file

## Presentation Structure

The presentation consists of 9 slides:

### Slides 1-3: Image Slides
These slides use user-provided images:
- `slide1.png` - First slide image
- `slide2.png` - Second slide image
- `slide3.png` - Third slide image

### Slides 4-9: Content Slides
Additional content slides with visual-first design:

1. **Design** - MVC architecture and role-based UIs
2. **System Architecture** - Three-tier architecture with Spring Boot and PostgreSQL
3. **Components Used** - Technologies and package structure
4. **Main Algorithms** - Slot generation, overlap checking, atomic booking
5. **Roadmap** - Development to monitoring pipeline
6. **Next Steps / Demo** - Demo preparation tasks

## Generating the Presentation

Use the generator script in the `tools/` folder to regenerate or customize the presentation.

### Requirements

```bash
pip install python-pptx Pillow
```

### Usage

```bash
python tools/generate_presentation.py <slide1.png> <slide2.png> <slide3.png> [output.pptx]
```

### Arguments

| Argument | Description |
|----------|-------------|
| `slide1.png` | Path to the first slide image |
| `slide2.png` | Path to the second slide image |
| `slide3.png` | Path to the third slide image |
| `output.pptx` | (Optional) Output file path. Default: `presentation/My-Therapy-presentation.pptx` |

### Example

```bash
# Generate presentation with custom images
python tools/generate_presentation.py images/slide1.png images/slide2.png images/slide3.png

# Generate to a specific output path
python tools/generate_presentation.py img/s1.png img/s2.png img/s3.png output/custom.pptx
```

## Image Requirements

The three input images should be:
- PNG or JPG format
- Preferably 16:9 aspect ratio for best display
- Following the project's visual style (large whitespace, Calibri-like fonts)

## Notes

- The generated PPTX embeds the images directly in the file
- The script does not modify or store copies of the original images
- Content slides include placeholder areas for icons/screenshots that can be customized later
