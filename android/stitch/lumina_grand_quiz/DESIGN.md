---
name: Lumina Grand Quiz
colors:
  surface: '#121414'
  surface-dim: '#121414'
  surface-bright: '#37393a'
  surface-container-lowest: '#0c0f0f'
  surface-container-low: '#1a1c1c'
  surface-container: '#1e2020'
  surface-container-high: '#282a2b'
  surface-container-highest: '#333535'
  on-surface: '#e2e2e2'
  on-surface-variant: '#ccc3da'
  inverse-surface: '#e2e2e2'
  inverse-on-surface: '#2f3131'
  outline: '#958da3'
  outline-variant: '#4a4457'
  surface-tint: '#d1bcff'
  primary: '#d1bcff'
  on-primary: '#3c0090'
  primary-container: '#7000ff'
  on-primary-container: '#ddcdff'
  inverse-primary: '#7212ff'
  secondary: '#fff9ef'
  on-secondary: '#3a3000'
  secondary-container: '#ffdb3c'
  on-secondary-container: '#725f00'
  tertiary: '#cbbff6'
  on-tertiary: '#332957'
  tertiary-container: '#5f5585'
  on-tertiary-container: '#d9ceff'
  error: '#ffb4ab'
  on-error: '#690005'
  error-container: '#93000a'
  on-error-container: '#ffdad6'
  primary-fixed: '#e9ddff'
  primary-fixed-dim: '#d1bcff'
  on-primary-fixed: '#23005b'
  on-primary-fixed-variant: '#5700c9'
  secondary-fixed: '#ffe16d'
  secondary-fixed-dim: '#e9c400'
  on-secondary-fixed: '#221b00'
  on-secondary-fixed-variant: '#544600'
  tertiary-fixed: '#e7deff'
  tertiary-fixed-dim: '#cbbff6'
  on-tertiary-fixed: '#1d1440'
  on-tertiary-fixed-variant: '#49406f'
  background: '#121414'
  on-background: '#e2e2e2'
  surface-variant: '#333535'
typography:
  headline-xl:
    fontFamily: Sora
    fontSize: 48px
    fontWeight: '800'
    lineHeight: 56px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Sora
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
  headline-lg-mobile:
    fontFamily: Sora
    fontSize: 24px
    fontWeight: '700'
    lineHeight: 32px
  body-md:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  label-md:
    fontFamily: JetBrains Mono
    fontSize: 14px
    fontWeight: '500'
    lineHeight: 20px
    letterSpacing: 0.05em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  unit: 8px
  gutter: 16px
  margin-mobile: 20px
  margin-desktop: 64px
  container-max-width: 1200px
---

## Brand & Style

The design system is centered on the concept of "High-Stakes Radiance." It evokes the tension, prestige, and cinematic drama of a televised grand arena. The target audience is the ambitious, knowledge-seeking player who values status and intellectual mastery.

The visual style is a hybrid of **Glassmorphism** and **Futuristic 3D**. It leverages deep spatial depth, using translucent surfaces to reveal complex light-play in the background. Elements are not just flat layers but objects with physical presence, featuring realistic lighting, "hot" glowing edges, and 3D bevels that suggest a high-tech, premium console.

## Colors

The palette is anchored in **Deep Midnight Blue** (Tertiary) to provide infinite spatial depth. **Royal Purple** (Primary) acts as the energetic core, used for active states and primary surfaces. **Vibrant Gold** (Secondary) is reserved exclusively for "Success," "High-Value Rewards," and "Victory Highlights," creating a clear psychological link between the color and high-stakes achievement.

- **Primary Glow:** Use the primary purple with high-saturation glows for interactive elements.
- **Victory Gradient:** A transition from deep gold to a bright, near-white metallic highlight for winning states.
- **Glass Tint:** Translucent surfaces should use a 20% opacity white or primary tint with a heavy backdrop blur (20px+).

## Typography

The typography strategy balances authority with technical precision. **Sora** is the headline face, chosen for its geometric, futuristic proportions that feel both premium and modern. For body content, **Inter** provides high legibility within Material 3-inspired layouts, ensuring complex questions are easy to read under pressure.

**JetBrains Mono** is utilized for labels, timers, and currency values to reinforce the "systematic" and "technical" nature of the game engine. All typography should be rendered with high contrast against the dark backgrounds, often utilizing a slight outer glow on headings to simulate neon lighting.

## Layout & Spacing

This design system uses a **Fluid Grid** with fixed maximum containers to maintain a cinematic feel on larger screens. The spacing rhythm is based on an 8px base unit.

- **Mobile:** A 4-column layout with 20px side margins. Content is stacked vertically to prioritize the "Ladder" and "Question" hierarchy.
- **Desktop/Tablet:** A 12-column layout. The "Lifelines" and "Progress Ladder" are treated as persistent sidebars, pinning the central "Hexagonal Question Arena" in the middle of the viewport.
- **Safe Zones:** High-intensity UI elements (like the timer) require significant negative space (32px minimum) to avoid visual clutter during high-stakes moments.

## Elevation & Depth

Depth is the primary communicator of hierarchy. The design system uses a three-tier elevation model:

1.  **The Void (Base):** The deepest layer, a dark purple-to-black gradient with subtle moving particle effects.
2.  **Glass Containers (Mid):** Semi-transparent surfaces with a `1px` inner-stroke of light to define edges. These contain the primary information like question text and option lists.
3.  **Floating 3D Objects (Top):** Active buttons and icons that use realistic drop shadows (offset 8px, blur 16px, 40% black) and "top-down" lighting gradients to appear physically raised from the glass.

Glow effects are used as "elevation indicators"—an element with a wider, softer outer glow is considered "hot" or "active."

## Shapes

The shape language is dominated by the **Hexagon** and the **Beveled Rectangle**. 

- **Hexagonal Question Cards:** All primary question containers must feature pointed side-terminations (hexagonal logic) to reference classic quiz show iconography.
- **3D Bevels:** Buttons and chips use a `0.5rem` base roundedness but are augmented with a `2px` top highlight and a `4px` bottom shadow-stroke to create a physical "pressed" or "raised" look.
- **Interactive States:** On hover or focus, shapes should expand slightly (1.02x scale) and increase their glow intensity.

## Components

### 3D Answer Buttons
Buttons are not flat. They use a linear gradient (top-to-bottom) and a persistent 3D "ledge" at the bottom. The "Selected" state transforms the button into a vibrant Gold-to-Yellow gradient with a white inner glow.

### Hexagonal Question Cards
The central container for the quiz. It features a semi-transparent glass background and a "Futuristic Glowing Border"—a 2px stroke that cycles through primary purple and secondary gold during key moments.

### Glowing Progress Ladder
A vertical stack of glass containers. The "Current Level" is highlighted with a 3D gold extrusion effect. Completed levels are dimmed, while future levels are high-transparency glass.

### Animated Lifeline Icons
Circular glass orbs. When used, the icon should "shatter" or fade out with a particle effect. Available lifelines pulse slowly with a primary-colored aura.

### Input Fields & Checkboxes
Utilize the "Glassmorphism" style with a heavy backdrop blur. Focus states should trigger a 1px secondary gold border and a 4px outer glow.