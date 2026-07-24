# Style Tokens — Vivid Logic / Deep Galactic
> Sistema de diseño extraído de `design/stitch-export/stitch_sudoku_logic_master/vivid_logic/DESIGN.md`
> y cruzado con todos los `code.html` exportados desde Google Stitch.
>
> **Fuente de verdad para Compose:** `ui/theme/Color.kt`, `ui/theme/Type.kt`, `ui/theme/Theme.kt`.
> **No repitas estos valores sueltos en componentes — referencia siempre los tokens.**

---

## 1. Paleta de Colores

### Fondo y Superficies
| Token Kotlin              | Hex       | Tailwind class             | Uso principal |
|---------------------------|-----------|----------------------------|---------------|
| `Background`              | `#0B1326` | `bg-background`            | Canvas de la app (idéntico a Surface) |
| `Surface`                 | `#0B1326` | `bg-surface`               | — |
| `SurfaceContainerLowest`  | `#060E20` | `bg-surface-container-lowest` | — |
| `SurfaceContainerLow`     | `#131B2E` | `bg-surface-container-low` | Bottom nav background |
| `SurfaceContainer`        | `#171F33` | `bg-surface-container`     | Glass panel base (con opacity 0.6) |
| `SurfaceContainerHigh`    | `#222A3D` | `bg-surface-container-high`| — |
| `SurfaceContainerHighest` | `#2D3449` | `bg-surface-container-highest` | Progress bar track, botón 3D shadow |
| `SurfaceBright`           | `#31394D` | `bg-surface-bright`        | — |

### Colores de Rol

| Token Kotlin            | Hex       | Descripción de uso |
|-------------------------|-----------|--------------------|
| `Primary`               | `#C0C1FF` | Brand principal, textos del header, íconos activos |
| `PrimaryContainer`      | `#8083FF` | Botón "Hard", fondo del botón Notas activo |
| `OnPrimary`             | `#1000A9` | Texto sobre Primary |
| `OnPrimaryContainer`    | `#0D0096` | Texto sobre PrimaryContainer |
| `InversePrimary`        | `#494BD6` | Sombra 3D del botón Hard |
| `Secondary`             | `#DDB7FF` | Color difícil-Medio, confetti, keypad delete |
| `SecondaryContainer`    | `#6F00BE` | Botón "Medium", fondo tab activo Bottom Nav |
| `OnSecondary`           | `#490080` | Texto sobre Secondary |
| `OnSecondaryContainer`  | `#D6A9FF` | Texto sobre SecondaryContainer |
| `Tertiary`              | `#4CD7F6` | "Correct" inputs, timer, celda resaltada, chip Easy |
| `TertiaryContainer`     | `#009EB9` | Botón "Easy", keypad número activo |
| `OnTertiary`            | `#003640` | Texto sobre Tertiary |
| `OnTertiaryContainer`   | `#002F38` | Texto sobre TertiaryContainer |
| `ErrorColor`            | `#FFB4AB` | Texto de error |
| `ErrorContainer`        | `#93000A` | Botón "Extreme" |
| `OnError`               | `#690005` | Sombra 3D botón Extreme |
| `OnErrorContainer`      | `#FFDAD6` | Texto sobre ErrorContainer |
| `OnSurface`             | `#DAE2FD` | Texto principal sobre fondo oscuro |
| `OnSurfaceVariant`      | `#C7C4D7` | Texto secundario |
| `Outline`               | `#908FA0` | Bordes sutiles, íconos inactivos, labels auxiliares |
| `OutlineVariant`        | `#464554` | Borde separador bottom nav |

### Colores especiales sin token Kotlin (solo HTML export)
| Nombre lógico             | Hex       | Uso |
|---------------------------|-----------|-----|
| `SurfaceTint`             | `#C0C1FF` | (igual a Primary) |
| `InverseSurface`          | `#DAE2FD` | (igual a OnSurface) |
| `InverseOnSurface`        | `#283044` | |
| `SecondaryFixed`          | `#F0DBFF` | |
| `SecondaryFixedDim`       | `#DDB7FF` | (igual a Secondary) |
| `PrimaryFixed`            | `#E1E0FF` | |
| `PrimaryFixedDim`         | `#C0C1FF` | (igual a Primary) |
| `TertiaryFixed`           | `#ACEDFF` | |
| `TertiaryFixedDim`        | `#4CD7F6` | (igual a Tertiary) |

### Gradientes recurrentes
| Nombre           | Definición | Uso |
|------------------|-----------|-----|
| `bg-radial`      | `radial-gradient(circle at top center, #171F33 0%, #0B1326 100%)` | Body background estático |
| `bg-radial-mouse`| `radial-gradient(circle at {x}% {y}%, #131B2E 0%, #0B1326 100%)` | Body background reactivo al cursor |
| `progress-bar`   | `linear-gradient(to right, #4CD7F6, #8083FF)` | Barras de progreso (`from-tertiary to-primary-container`) |
| `score-glow`     | `linear-gradient(to right, secondary/10, tertiary/10, primary/10)` | Overlay en tarjeta de puntuación |

### Colores confetti (array)
```kotlin
// #C0C1FF · #DDB7FF · #4CD7F6 · #8083FF
val ConfettiColors = listOf(Primary, Secondary, Tertiary, PrimaryContainer)
```

---

## 2. Tipografía

> **Fuentes:** Quicksand (headers, números) · Montserrat (labels, cuerpo)
> En Compose: `QuicksandFamily` y `MontserratFamily` definidos en `Type.kt`.

| Token Compose          | Familia     | Size   | Weight    | Line-h | Letter-sp | Uso |
|------------------------|-------------|--------|-----------|--------|-----------|-----|
| `displayLarge`         | Quicksand   | 48 sp  | Bold 700  | 56 sp  | -0.02 em  | Título victorias, score grande |
| `headlineLarge`        | Quicksand   | 32 sp  | Bold 700  | 40 sp  | —         | Headers de sección desktop |
| `headlineMedium`       | Quicksand   | 24 sp  | Bold 700  | 32 sp  | —         | Headers de sección mobile, nombres dificultad |
| `titleLarge` ⚠️        | Quicksand   | 28 sp  | Medium 500| 28 sp  | —         | **Números del Sudoku** (grid-number) |
| `bodyLarge`            | Montserrat  | 18 sp  | Medium 500| 28 sp  | —         | Texto cuerpo destacado |
| `bodyMedium`           | Montserrat  | 16 sp  | Normal 400| 24 sp  | —         | Texto cuerpo estándar |
| `labelLarge`           | Montserrat  | 14 sp  | Bold 700  | 20 sp  | +0.05 em  | Labels uppercase, chips, progress labels |
| `labelSmall`           | Montserrat  | 12 sp  | SemiBold 600 | 16 sp | —      | Sub-labels, contadores secundarios |

> ⚠️ `titleLarge` se reutiliza para los números del grid — en los componentes, úsalo como `MaterialTheme.typography.titleLarge`.

---

## 3. Espaciados

| Token           | Valor  | dp aprox. | Uso |
|-----------------|--------|-----------|-----|
| `grid-gap`      | 2 px   | 2 dp      | Gutter entre celdas dentro de un bloque 3×3 |
| `block-gap`     | 4 px   | 4 dp      | Gutter entre los 9 bloques del grid |
| `stack-sm`      | 0.5 rem | 8 dp     | Separación pequeña entre elementos |
| `stack-md`      | 1 rem  | 16 dp     | Separación media (padding interno cards) |
| `stack-lg`      | 2 rem  | 32 dp     | Separación grande entre secciones |
| `container-padding` | 1.5 rem | 24 dp | Margen horizontal del contenido (safe area) |

---

## 4. Bordes y Radios

| Token Tailwind  | Valor     | dp aprox. | Uso |
|-----------------|-----------|-----------|-----|
| `rounded-sm`    | 4 px      | 4 dp      | Celdas del Sudoku (tight but modern) |
| `rounded` (default) | 8 px  | 8 dp      | Radius base general |
| `rounded-md`    | 12 px     | 12 dp     | — |
| `rounded-lg`    | 16 px     | 16 dp     | Cards / modales |
| `rounded-xl`    | 24 px     | 24 dp     | Botones de dificultad, keypad, action buttons |
| `rounded-2xl`   | 24 px+    | 24 dp     | Score card en Victoria |
| `rounded-full`  | 9999 px   | pill      | Bottom nav activo, chips de dificultad, badges |

### Grosor de borde
| Contexto               | Valor |
|------------------------|-------|
| Glass panel stroke     | `1px solid rgba(255,255,255,0.10)` |
| Grid exterior border   | `4dp` — `SurfaceContainerHighest` |
| Bottom nav top border  | `1px solid OutlineVariant/20%` |
| Cell selected outline  | `2dp` — `Primary (#C0C1FF)` |

---

## 5. Efectos visuales

### Glass Morphism (`.glass-panel`)
```
background:       rgba(23, 31, 51, 0.60)   /* SurfaceContainer + alpha 60% */
backdrop-filter:  blur(12px)
border:           1px solid rgba(255, 255, 255, 0.10)
```
> En Compose: combina `Background.copy(alpha=0.6f)` + `BlurEffect` (API ≥31) o aproximar con color sólido.

### Glass Cards (tarjetas de resumen)
```
background:       rgba(255, 255, 255, 0.05)  /* 5% white */
backdrop-filter:  blur(20px)
border:           1px solid rgba(255, 255, 255, 0.10)
```

### Sombras
| Elemento               | Sombra |
|------------------------|--------|
| Glow celda correcta    | `inset 0 0 15px rgba(76, 215, 246, 0.30)` (Cyan/Tertiary) |
| Celda seleccionada     | `0 0 0 2px #C0C1FF` (Primary ring) |
| Glow error             | `soft red glow` detrás del número (sin cambio de bg) |
| Glow Easy button       | `0 0 15px rgba(76, 215, 246, 0.30)` |
| Glow Medium button     | `0 0 15px rgba(214, 169, 255, 0.30)` |
| Glow Hard button       | `0 0 15px rgba(128, 131, 255, 0.30)` |
| Glow Extreme button    | `0 0 15px rgba(147, 0, 10, 0.30)` |
| Bottom nav             | `0 -4px 20px rgba(0, 0, 0, 0.40)` |
| Progress bar glow      | `0 0 10px rgba(76, 215, 246, 0.50)` |
| Score text glow        | `drop-shadow: 0 0 12px rgba(221, 183, 255, 0.40)` |
| Victory title glow     | `drop-shadow: 0 0 15px rgba(192, 193, 255, 0.40)` |

### Botón 3D (`.btn-3d`)
```
• Top face:   color normal del botón
• Side edge:  color más oscuro, desplazado +3–4px en Y con z-index -1
• On press:   translateY(+3px) — top face desciende hasta la sombra
• Transition: 0.1s ease-out
```
| Botón         | Top face            | Side edge            |
|---------------|---------------------|----------------------|
| Easy          | TertiaryContainer   | `#004E5C`            |
| Medium        | SecondaryContainer  | `#490080`            |
| Hard          | PrimaryContainer    | `#2F2EBE`            |
| Extreme       | ErrorContainer      | `#690005`            |
| Number pad    | glass-panel         | SurfaceContainerHighest |
| Num highlighted| Tertiary           | TertiaryContainer    |
| Delete key    | Secondary           | SecondaryContainer   |

---

## 6. Patrones visuales recurrentes

### Bottom Navigation
- Fondo: `SurfaceContainerLow` con 90% opacidad + `blur(12px)` + `rounded-t-xl`
- Borde superior: `1px OutlineVariant/20%`
- Tab activo: círculo `SecondaryContainer` + `OnSecondaryContainer` (pill w-12 h-12)
- Tab inactivo: ícono en `Outline`, hover → `Primary`

### Progress Bar
```
Track:  SurfaceContainerHighest, rounded-full, h-6dp (1.5 rem) o h-3dp según pantalla
Fill:   gradient from-Tertiary to-PrimaryContainer, glow Cyan/50%
```

### Chip de dificultad (inline badge)
```
background: SecondaryContainer · text: OnSecondaryContainer · pill-shape · label-sm
```

### Ícono con badge activo (botón Notas ON)
```
Container: PrimaryContainer, rounded-xl
Badge:     Tertiary · círculo 4dp · borde 2dp Background · posición -top-1 -right-1
```

### Pencil marks (notas en celda)
```
Posición: esquinas absolutas (top-left, top-right, bottom-left, bottom-right)
Fuente:   monospace · 10sp · color: Tertiary (muted)
```

---

## 7. Animaciones

| Nombre            | Definición |
|-------------------|-----------|
| `celebrate`       | `scale(0.9) opacity(0)` → `scale(1.05)` → `scale(1) opacity(1)` — 0.6s `cubic-bezier(0.34, 1.56, 0.64, 1)` |
| `btn-3d press`    | `translateY(0)` → `translateY(3px)` — 0.1s ease |
| `tab press`       | `scale(1)` → `scale(0.90)` — 150ms |
| `hover chevron`   | `translateX(4px) opacity(0)` → `translateX(0) opacity(1)` |
| `parallax logo`   | Mouse-follow subtle translate ÷50 |
| `bg radial mouse` | Radial gradient centrado en posición del cursor |
| `confetti`        | Partículas rectangulares cayendo — colors: ConfettiColors |
| `glow hover`      | Button glow → `0 0 25px rgba(255,255,255,0.40)` on hover |
| `progress fill`   | `transition: width 1s ease` |

---

## 8. Iconos (Material Symbols Outlined)

Todos los íconos usan **Material Symbols Outlined** con `font-variation-settings: FILL 0, wght 400`.
Excepciones donde se usa `FILL 1`: `local_fire_department` (Extreme), `emoji_events` (Victory badge, Bottom Nav activo en Victory).

| Contexto                  | Ícono Material Symbol    |
|---------------------------|--------------------------|
| Back button               | `arrow_back`             |
| Settings                  | `settings`               |
| Easy difficulty           | `sentiment_satisfied`    |
| Medium difficulty         | `sentiment_neutral`      |
| Hard difficulty           | `psychology`             |
| Extreme difficulty        | `local_fire_department`  |
| Undo action               | `undo`                   |
| Erase/Clear action        | `backspace`              |
| Notes toggle              | `edit_note`              |
| Hint                      | `lightbulb`              |
| Delete (keypad)           | `close`                  |
| Play (bottom nav)         | `grid_view`              |
| Stats (bottom nav)        | `leaderboard`            |
| Badges (bottom nav)       | `emoji_events`           |
| Profile (bottom nav)      | `person`                 |
| Timer (Victory)           | `timer`                  |
| Mistakes (Victory)        | `cancel`                 |
| Difficulty (Victory)      | `psychology`             |
| Score star (Victory)      | `stars`                  |
| New Record badge          | `emoji_events`           |
| Home / Main menu          | `home`                   |
| Next arrow                | `chevron_right`          |

---

## 9. Mapeo Compose ↔ Stitch

> Cómo traducir los tokens HTML/Tailwind a Material3 en Compose.

| Tailwind / DESIGN.md token | Compose equivalente |
|----------------------------|---------------------|
| `background` / `surface`   | `MaterialTheme.colorScheme.background` |
| `primary`                  | `MaterialTheme.colorScheme.primary` |
| `primary-container`        | `MaterialTheme.colorScheme.primaryContainer` |
| `on-primary-container`     | `MaterialTheme.colorScheme.onPrimaryContainer` |
| `secondary`                | `MaterialTheme.colorScheme.secondary` |
| `secondary-container`      | `MaterialTheme.colorScheme.secondaryContainer` |
| `on-secondary-container`   | `MaterialTheme.colorScheme.onSecondaryContainer` |
| `tertiary`                 | `MaterialTheme.colorScheme.tertiary` |
| `tertiary-container`       | `MaterialTheme.colorScheme.tertiaryContainer` |
| `error`                    | `MaterialTheme.colorScheme.error` |
| `error-container`          | `MaterialTheme.colorScheme.errorContainer` |
| `on-error-container`       | `MaterialTheme.colorScheme.onErrorContainer` |
| `on-surface`               | `MaterialTheme.colorScheme.onSurface` |
| `on-surface-variant`       | `MaterialTheme.colorScheme.onSurfaceVariant` |
| `outline`                  | `MaterialTheme.colorScheme.outline` |
| `outline-variant`          | `MaterialTheme.colorScheme.outlineVariant` |
| `surface-container-low`    | `MaterialTheme.colorScheme.surfaceContainerLow` |
| `surface-container-highest`| `MaterialTheme.colorScheme.surfaceContainerHighest` |
| `display-lg` (48px/700)    | `MaterialTheme.typography.displayLarge` |
| `headline-lg-mobile` (24px)| `MaterialTheme.typography.headlineMedium` |
| `grid-number` (28px/500)   | `MaterialTheme.typography.titleLarge` |
| `label-lg` (14px/700/+0.05)| `MaterialTheme.typography.labelLarge` |
| `label-sm` (12px/600)      | `MaterialTheme.typography.labelSmall` |
| `body-lg` (18px/500)       | `MaterialTheme.typography.bodyLarge` |
| `body-md` (16px/400)       | `MaterialTheme.typography.bodyMedium` |

---

## 10. Checklist para nuevos componentes

- [ ] Colores → sólo de `MaterialTheme.colorScheme.*` o constantes de `Color.kt`
- [ ] Tipografía → sólo de `MaterialTheme.typography.*`
- [ ] Espaciado → usar múltiplos de los tokens (`8.dp`, `16.dp`, `24.dp`, `32.dp`)
- [ ] Radios → `4.dp` celdas · `8.dp` default · `16.dp` cards · `24.dp` botones · pill para chips
- [ ] Glass effect → `SurfaceContainer.copy(alpha=0.6f)` como approximación en Compose (sin blur real en API<31)
- [ ] Botones 3D → `offset(y = 3.dp)` en shadow layer + `animateAsState` para press
- [ ] Progress bar → gradient `Brush.horizontalGradient(Tertiary, PrimaryContainer)`
- [ ] No hardcodear hex en composables — si necesitas un color sin token, agrégalo a `Color.kt`
