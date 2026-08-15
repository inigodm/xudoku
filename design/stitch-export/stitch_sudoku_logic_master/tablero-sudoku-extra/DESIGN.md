---
name: Vivid Logic
colors:
  surface: '#0b1326'
  surface-dim: '#0b1326'
  surface-bright: '#31394d'
  surface-container-lowest: '#060e20'
  surface-container-low: '#131b2e'
  surface-container: '#171f33'
  surface-container-high: '#222a3d'
  surface-container-highest: '#2d3449'
  on-surface: '#dae2fd'
  on-surface-variant: '#c7c4d7'
  inverse-surface: '#dae2fd'
  inverse-on-surface: '#283044'
  outline: '#908fa0'
  outline-variant: '#464554'
  surface-tint: '#c0c1ff'
  primary: '#c0c1ff'
  on-primary: '#1000a9'
  primary-container: '#8083ff'
  on-primary-container: '#0d0096'
  inverse-primary: '#494bd6'
  secondary: '#ddb7ff'
  on-secondary: '#490080'
  secondary-container: '#6f00be'
  on-secondary-container: '#d6a9ff'
  tertiary: '#4cd7f6'
  on-tertiary: '#003640'
  tertiary-container: '#009eb9'
  on-tertiary-container: '#002f38'
  error: '#ffb4ab'
  on-error: '#690005'
  error-container: '#93000a'
  on-error-container: '#ffdad6'
  primary-fixed: '#e1e0ff'
  primary-fixed-dim: '#c0c1ff'
  on-primary-fixed: '#07006c'
  on-primary-fixed-variant: '#2f2ebe'
  secondary-fixed: '#f0dbff'
  secondary-fixed-dim: '#ddb7ff'
  on-secondary-fixed: '#2c0051'
  on-secondary-fixed-variant: '#6900b3'
  tertiary-fixed: '#acedff'
  tertiary-fixed-dim: '#4cd7f6'
  on-tertiary-fixed: '#001f26'
  on-tertiary-fixed-variant: '#004e5c'
  background: '#0b1326'
  on-background: '#dae2fd'
  surface-variant: '#2d3449'
typography:
  display-lg:
    fontFamily: Quicksand
    fontSize: 48px
    fontWeight: '700'
    lineHeight: 56px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Quicksand
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
  headline-lg-mobile:
    fontFamily: Quicksand
    fontSize: 24px
    fontWeight: '700'
    lineHeight: 32px
  grid-number:
    fontFamily: Quicksand
    fontSize: 28px
    fontWeight: '500'
    lineHeight: 28px
  body-lg:
    fontFamily: Montserrat
    fontSize: 18px
    fontWeight: '500'
    lineHeight: 28px
  body-md:
    fontFamily: Montserrat
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  label-lg:
    fontFamily: Montserrat
    fontSize: 14px
    fontWeight: '700'
    lineHeight: 20px
    letterSpacing: 0.05em
  label-sm:
    fontFamily: Montserrat
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  grid-gap: 2px
  block-gap: 4px
  container-padding: 1.5rem
  stack-sm: 0.5rem
  stack-md: 1rem
  stack-lg: 2rem
---

## Brand & Style
The design system focuses on transforming the traditionally academic nature of Sudoku into a high-energy, digital-first entertainment experience. The brand personality is **vibrant, playful, and kinetic**, targeting a modern audience that seeks mental stimulation within a visually stimulating environment. 

The aesthetic direction is a fusion of **Glassmorphism** and **Tactile Modernism**. It leverages deep, immersive backgrounds with translucent overlays to create depth, while interactive elements utilize 3D-style effects to provide satisfying haptic feedback. The goal is to evoke an emotional response of "flow"—a state of focused, effortless concentration wrapped in a "cool" and engaging interface.

## Colors
This design system utilizes a "Deep Galactic" palette to create a high-contrast environment where gameplay elements pop.

- **Primary (Indigo):** Used for the main UI structure, active grid highlights, and primary brand moments.
- **Secondary (Purple):** Used for auxiliary interactive elements and gradient transitions.
- **Tertiary (Cyan):** Reserved for "Correct" inputs, timer icons, and selection glows.
- **Accent (Lime):** High-visibility color used for achievements, "New Game" actions, and celebratory UI states.
- **Neutral (Slate/Navy):** The foundation for backgrounds, ensuring the electric colors remain legible and vibrant without causing eye strain during long sessions.

## Typography
The typography strategy balances playfulness with extreme legibility. **Quicksand** is used for headers and the Sudoku numbers themselves; its rounded terminals reinforce the friendly, approachable aesthetic. **Montserrat** provides a grounded, geometric contrast for functional labels and body text, ensuring instructions and settings are easy to parse. Numbers in the grid should be vertically centered and utilize the `grid-number` token to maximize tap-target clarity.

## Layout & Spacing
The layout is optimized for one-handed mobile play. It follows a **Fixed-Width Logic** for the game board to maintain perfect square proportions across all devices, while auxiliary UI (stats, menus) uses a **Fluid Grid**.

- **Game Board:** A 9x9 grid with 2px internal gutters and 4px external "block" borders to clearly define the 3x3 sub-grids.
- **Safe Areas:** 24px horizontal margins on mobile to prevent thumb-clashing with the screen edges.
- **Vertical Rhythm:** A bottom-heavy layout places the number pad in the most accessible "thumb zone," with the grid occupying the center-top.

## Elevation & Depth
This design system rejects flat design in favor of **multi-layered depth**:

1.  **The Canvas:** Deep Indigo solid background.
2.  **The Grid:** Low-opacity glass panels (Backdrop Blur: 12px) with a 1px inner stroke to catch "light."
3.  **Floating Elements:** Cards and modals use higher-transparency glass effects with subtle ambient shadows (Color: Indigo-900, Opacity: 40%, Blur: 20px).
4.  **Active Interactive Layer:** Buttons use a physical 3D-offset. Rather than a shadow, they use a solid color "bottom layer" (3px-4px) that shifts the element down on press, simulating a mechanical switch.

## Shapes
A **Rounded (0.5rem)** base is applied to almost all elements to maintain the friendly brand personality. 
- **Sudoku Cells:** Use a subtle `rounded-sm` (4px) to keep the grid feeling tight but modern.
- **Action Buttons:** Use a more aggressive `rounded-xl` or full pill-shape to invite interaction.
- **Container Modals:** Utilize `rounded-lg` (1rem) for a soft, premium feel.

## Components

### The Sudoku Grid
Cells are semi-transparent glass tiles.
- **Selected State:** Primary color border (2px) with a subtle inner glow.
- **Highlight State (Same Numbers):** Low-opacity Cyan background.
- **Error State:** Soft Red glow behind the number, no background change.

### 3D Buttons
Buttons consist of a "Top" face and a "Side" edge.
- **Primary Button:** Indigo top face with a darker Indigo base. On `active` state, the top face translates 3px down.
- **Number Pad:** Large, square-ish tiles with high-contrast White text. These should feel "squishy" and tactile.

### Glass Cards
Used for game summaries and settings.
- **Style:** 15% white opacity, 20px backdrop-blur, and a 1px border at 20% white.

### Chips & Progress
- **Difficulty Chips:** Use Secondary (Purple) for "Hard" and Tertiary (Cyan) for "Easy," utilizing a pill-shape with high-contrast text.
- **Progress Bars:** Dual-gradient (Cyan to Lime) to show level completion.

### Input Indicators
Small "pencil" marks for notes should be placed in the corners of the cell in a monospaced-style alignment, using a muted light-blue color to distinguish from final answers.