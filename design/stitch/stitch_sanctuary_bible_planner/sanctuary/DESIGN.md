---
name: Sanctuary
colors:
  surface: '#111318'
  surface-dim: '#111318'
  surface-bright: '#37393f'
  surface-container-lowest: '#0c0e13'
  surface-container-low: '#1a1b21'
  surface-container: '#1e1f25'
  surface-container-high: '#282a2f'
  surface-container-highest: '#33353a'
  on-surface: '#e2e2e9'
  on-surface-variant: '#c9c4d5'
  inverse-surface: '#e2e2e9'
  inverse-on-surface: '#2e3036'
  outline: '#928f9e'
  outline-variant: '#474553'
  surface-tint: '#c7bfff'
  primary: '#c7bfff'
  on-primary: '#2b138f'
  primary-container: '#8f82f7'
  on-primary-container: '#25068a'
  inverse-primary: '#5b4dbf'
  secondary: '#45dfa4'
  on-secondary: '#003825'
  secondary-container: '#00bd85'
  on-secondary-container: '#00452e'
  tertiary: '#f9bd22'
  on-tertiary: '#402d00'
  tertiary-container: '#b98a00'
  on-tertiary-container: '#382800'
  error: '#ffb4ab'
  on-error: '#690005'
  error-container: '#93000a'
  on-error-container: '#ffdad6'
  primary-fixed: '#e4dfff'
  primary-fixed-dim: '#c7bfff'
  on-primary-fixed: '#170065'
  on-primary-fixed-variant: '#4332a6'
  secondary-fixed: '#68fcbf'
  secondary-fixed-dim: '#45dfa4'
  on-secondary-fixed: '#002114'
  on-secondary-fixed-variant: '#005137'
  tertiary-fixed: '#ffdf9f'
  tertiary-fixed-dim: '#f9bd22'
  on-tertiary-fixed: '#261a00'
  on-tertiary-fixed-variant: '#5c4300'
  background: '#111318'
  on-background: '#e2e2e9'
  surface-variant: '#33353a'
typography:
  display-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 40px
    fontWeight: '600'
    lineHeight: 48px
    letterSpacing: -0.02em
  display-lg-mobile:
    fontFamily: Plus Jakarta Sans
    fontSize: 30px
    fontWeight: '600'
    lineHeight: 38px
    letterSpacing: -0.015em
  headline-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 26px
    fontWeight: '600'
    lineHeight: 34px
    letterSpacing: -0.01em
  headline-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
  scripture-hero:
    fontFamily: Newsreader
    fontSize: 26px
    fontWeight: '400'
    lineHeight: 38px
  scripture-body:
    fontFamily: Newsreader
    fontSize: 19px
    fontWeight: '400'
    lineHeight: 32px
    letterSpacing: 0.01em
  scripture-verse-num:
    fontFamily: Plus Jakarta Sans
    fontSize: 11px
    fontWeight: '600'
    lineHeight: 16px
    letterSpacing: 0.04em
  body-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 15px
    fontWeight: '400'
    lineHeight: 22px
  body-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 13px
    fontWeight: '400'
    lineHeight: 18px
  label-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 20px
    letterSpacing: 0.01em
  label-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
    letterSpacing: 0.02em
  label-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 10px
    fontWeight: '600'
    lineHeight: 14px
    letterSpacing: 0.05em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  gutter: 1rem
  margin: 1rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 1rem
  space-lg: 1.5rem
  space-xl: 2rem
---

## Brand & Style

This design system delivers a contemplative, distraction-free environment for daily Scripture engagement and spiritual formation. It merges Material 3 ergonomics with an editorial, sacred atmosphere that emphasizes stillness over stimulation.

### Design Principles
- **Reverent Restraint:** Every visual element must justify its presence. Uncluttered layouts, generous reading whitespace, and deliberate pacing replace flashy micro-interactions.
- **Dignified Depth:** Depth is established through tonal darkness and precise surface tiers, never aggressive dropshadows or noisy skeuomorphism.
- **Editorial Legibility:** UI chrome recedes into deep charcoal, allowing sacred texts to command visual priority with optimal typographic cadence.
- **Intentional Signals:** Chromatic cues are sparse and purposeful. Success is serene rather than jubilant; overdue states prompt reflection rather than anxiety.

### Aesthetic Direction
A tailored blend of **Minimalism** and **Modern Material 3**, grounded in a dark-first canvas with subtle surface steps, micro-borders, and pristine typography.

## Colors

The palette is tuned for night and low-light devotionals, minimizing eye strain while maintaining structural hierarchy through discrete value steps.

### Palette Architecture
- **Base Canvas (`#0B0D12`):** Ground level for system backdrops, status bars, and fullscreen reading mode.
- **Surface Elevation Steps:**
  - `surface-container-low` (`#10131A`): Cards, bottom navigation bars, and grouped list backgrounds.
  - `surface-container` (`#151923`): Active card states, modal sheets, and dialogue containers.
  - `surface-container-high` (`#1B202C`): Dropdowns, floating controls, and elevated chips.
- **Primary Accent (`#8F82F7`):** Muted twilight indigo/violet. Applied to primary active states, selection pills, reading progress bars, and focal interactive elements.
- **Secondary / Success (`#34D399`):** Soft sage green denoting finished chapters, completed streaks, and prayer fulfillment.
- **Tertiary / Notice (`#FBBF24`):** Warm amber reserved for missed readings, pending reflections, or schedule revisions.
- **Error / Destructive (`#F87171`):** Subdued coral red, strictly isolated to irreversible actions (e.g., reset plan, clear journal).
- **Text & Content Tiers:**
  - `text-primary` (`#F3F4F6`): 95% opacity for scripture text and titles.
  - `text-secondary` (`#9CA3AF`): 65% opacity for verse citations, chapter labels, and metadata.
  - `text-tertiary` (`#6B7280`): 45% opacity for footnotes, timestamp hints, and unselected state labels.
  - `border-subtle` (`#222735`): Low-contrast dividing lines and card outlines.

## Typography

The dual typographic identity balances architectural precision with literary grace:
- **UI System (`Plus Jakarta Sans`):** Clean, geometric grotesque with gentle humanist curves. Used across app navigation, dashboards, metrics, and functional inputs.
- **Reading System (`Newsreader`):** A modern transitional serif engineered specifically for continuous, immersive reading. Calibrated optical metrics guarantee comfort during prolonged reflection.

### Rules of Application
- Verse numbers utilize `scripture-verse-num` positioned as superscripts or subtle margin indicators in `text-tertiary` to prevent disruption of prose flow.
- Scripture passages must observe a fixed line-height minimum of 1.6x font size to preserve visual breathability.
- Avoid uppercase styling except for compact `label-sm` metadata tags and chapter references (e.g., `PSALM 23:1`).

## Layout & Spacing

The layout operates on a strict **8dp grid** system (with 4dp sub-units for fine alignments and icon-to-label spacing).

### Form Factors & Adaptation
- **Mobile (<600dp):** Single-column layout. 16dp outer canvas margins (`space-md`), 16dp gutters. Navigated via a fixed 80dp tall Material 3 Bottom Navigation bar with 5 primary destinations.
- **Foldable & Tablet (600dp–1024dp):** Two-column split layout. Left pane handles chapter indexes or plan milestones (320dp fixed); right pane hosts the reading surface. Outer margins expand to 24dp (`space-lg`), and bottom navigation transitions into an adaptive Navigation Rail (72dp wide) or Navigation Drawer.
- **Desktop / Wide Landscape (>1024dp):** Content-centered reading canvas constrained to a maximum width of `680px` for Scripture text, avoiding over-extended measure lines.

### Vertical Rhythm
- Standard spacing between consecutive paragraphs in Scripture: `space-md` (16dp).
- Section grouping rhythm on dashboards: `space-xl` (32dp) between separate modules.

## Elevation & Depth

To avoid stark contrast and eye fatigue, this design system avoids heavy drop shadows and instead uses a **tonal layering architecture** combined with hairline containment boundaries.

### Layer Hierarchy
- **Level 0 (Base `#0B0D12`):** Primary background behind lists and reading screens.
- **Level 1 (Card & Bar `#10131A`):** Bottom navigation, non-interactive tiles, and inactive plan cards. Surrounded by an ultra-fine border: `1px solid #222735`.
- **Level 2 (Active Container `#151923`):** Active prayer journal cards, day-target modules, bottom sheet modals, and alert dialogues. Outlined by `1px solid rgba(143, 130, 247, 0.15)`.
- **Level 3 (Overlay & Floating `#1B202C`):** Floating action bars, audio playback pill, and scripture contextual menus (Highlight, Note, Share). Paired with an ambient shadow: `0 8px 24px -4px rgba(0, 0, 0, 0.65)`.

### Ambient Glows
For critical progress milestones (e.g., completing a 30-day plan phase), cards may exhibit a soft, muted radial aura using `rgba(52, 211, 153, 0.08)` spanning 32dp blur behind the container.

## Shapes

Shapes reflect modern Material 3 conventions, utilizing 12dp to 20dp corners to impart a gentle, safe, and welcoming touch across touchpoints.

### Scale Application
- **12dp (`rounded`):** Text input fields, secondary action buttons, audio control pills, and segmented filters.
- **16dp (`rounded-lg`):** Standard dashboard cards, Scripture quote modules, and devotion plan tiles.
- **20dp (`rounded-xl`):** Bottom sheets, modal dialogues, floating action surfaces, and primary feature hero cards.
- **Pill (Full Circular):** Chip filters, bottom navigation active indicator capsules, and play/pause controls.

## Components

### Buttons
- **Primary:** Background `#8F82F7`, foreground `#0B0D12` (high-contrast deep charcoal text for immediate clarity), 12dp rounded corner, 48dp minimum touch height. No drop shadows.
- **Tonal / Secondary:** Background `#1B202C`, foreground `#8F82F7`, border `1px solid #222735`.
- **Ghost:** Transparent background, foreground `#9CA3AF`, hover/press state `#151923`.

### Bottom Navigation (5 Destinations)
- **Destinations:** Home, Read, Plan, Progress, More.
- **Bar Height:** 80dp container in `#10131A` with a `1px` border at the top (`#222735`).
- **Active State:** Indicated by an elongated pill container (`#8F82F7` with 15% alpha) enclosing the icon, with icon and label tinted in pure `#8F82F7`.
- **Inactive State:** Tinted in `#9CA3AF`.

### Scripture Reader Interface
- **Contextual Action Bar:** Floating pill anchored 24dp above the bottom nav when verses are selected. Contains: *Highlight*, *Note*, *Bookmark*, *Share*.
- **Highlight System:** Translucent verse underlays:
  - Sage: `rgba(52, 211, 153, 0.25)`
  - Violet: `rgba(143, 130, 247, 0.25)`
  - Amber: `rgba(251, 191, 36, 0.25)`
- Verse selection triggers an unweighted hairline bracket along the left margin rather than abrupt full-row color shifts.

### Cards & Progress Indicators
- **Plan Day Card:** `#10131A` surface with 16dp radius. Features a linear progress track (4dp height, track `#1B202C`, indicator `#34D399` for on-track, `#FBBF24` for overdue).
- **Streak Tracker:** Minimalist ring or linear dots. Unfinished days render as `#1B202C` hollow circles; completed days fill with `#34D399`.

### Chips & Inputs
- **Filter Chips:** 32dp height, fully rounded pill. Unselected in `#10131A` with `#222735` border; selected in `#151923` with `#8F82F7` border and label.
- **Text Fields (Journal & Search):** Background `#10131A`, 12dp radius, border `1px solid #222735`. Focus state deepens border to `1px solid #8F82F7` with zero outer glow. Placeholder text in `#6B7280`.

### Selection Controls
- **Checkboxes & Radios:** 20dp geometric footprint with 6dp corner radius for checkboxes. Inactive border `#6B7280`; active fill `#8F82F7` with `#0B0D12` glyph check.