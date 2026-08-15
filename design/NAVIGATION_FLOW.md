# Flujo de Navegación — xudoku

> Generado a partir del análisis de los diseños exportados desde Google Stitch
> (`design/stitch-export/stitch_sudoku_logic_master/`).
>
> **Nota:** `sudoku_game_flow/code.html` y `untitled_prototype/code.html` son
> duplicados del HTML de `selecci_n_de_dificultad/` — no contienen un diagrama
> de flujo propio; se usan aquí como referencia de punto de entrada.

---

## Diagrama de flujo

```
┌─────────────────────────────────────────────────────────────────┐
│                        SPLASH SCREEN                            │
│              Logo animado "S" neón + nombre app                 │
│              (pantalla de entrada, ~2 seg, sin input)           │
└────────────────────────────┬────────────────────────────────────┘
                             │ auto-navegación
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                  SELECCIÓN DE DIFICULTAD                        │
│  Logo · "Selecciona Dificultad"                                 │
│  [Fácil]  [Medio]  [Difícil]  [Extremo]                        │
│  Barra de Progreso Global                                       │
│  Bottom nav: [▣ Play*] [📊 Stats] [🏆 Badges] [👤 Profile]     │
└──┬─────────────────────────────────────────────────────────────-┘
   │ tap en cualquier dificultad
   ▼
┌─────────────────────────────────────────────────────────────────┐
│                    TABLERO DE JUEGO                             │
│  ← Atrás · "SUDOKU" · [Expert chip] · 04:22 · ⚙               │
│  MISTAKES 0/3 ·  Level 42 ████████░░                           │
│  ┌─────── Grid 9×9 ────────┐                                   │
│  │ celdas con glass effect  │                                   │
│  │ selección + notas (pencil)│                                  │
│  └──────────────────────────┘                                   │
│  [↩ Deshacer] [✕ Borrar] [📝 Notas] [💡 Pista]                 │
│  [1][2][3][4][5][6][7][8][9][✕]                                │
│  Bottom nav: [▣ Play*] [📊] [🏆] [👤]                          │
└──┬──────────────────────────────────────────────────────────────┘
   │ puzzle completado (todas las celdas correctas)
   ▼
┌─────────────────────────────────────────────────────────────────┐
│                    PANTALLA DE VICTORIA                         │
│  Confeti · "🏆 NUEVA MARCA"                                     │
│  "¡VICTORIA!" · "Nivel Completado con éxito"                   │
│  ┌──────────────────────────────────────────────┐              │
│  │  ⭐  PUNTUACIÓN FINAL  24,580                │              │
│  └──────────────────────────────────────────────┘              │
│  [⏱ TIEMPO 08:45]  [✕ ERRORES 0/3]                            │
│  [⚙ DIFICULTAD: Difícil]                                       │
│  NIVEL 14 ·  850 / 1000 XP  ████████░░                         │
│  [  Siguiente Nivel →  ]  (botón primario)                     │
│  [  🏠 MENÚ PRINCIPAL  ]  (botón secundario)                   │
│  Bottom nav: [▣] [📊] [🏆*] [👤]                               │
└──┬───────────────────┬────────────────────────────────────────-┘
   │ "Siguiente Nivel" │ "Menú Principal"
   │                   │
   ▼                   ▼
[TABLERO DE JUEGO   [SELECCIÓN DE
 (nueva partida,     DIFICULTAD]
 misma dificultad)]

```

---

## Bottom Navigation (global)

Presente en todas las pantallas **excepto Splash**.

| Tab | Icono | Destino |
|---|---|---|
| **Play** | `grid_view` | Selección de Dificultad |
| **Profile** | `person` | Perfil de Usuario + Estadísticas |

---

## Pantallas inventariadas

| ID | Nombre Stitch | Pantalla | Diseño definitivo |
|---|---|---|---|
| S1 | `sudoku_master_logo` | Asset del icono de la app | — |
| S2 | `selecci_n_de_dificultad` | Selección de Dificultad | ✅ |
| S3 | `tablero_de_sudoku` | Tablero de Juego | ✅ |
| S4 | `victoria_actualizada` | Pantalla de Victoria | ✅ (versión con puntuación) |
| S5 | `estad_sticas_completas_con_gr_fica_y_filtros` | Estadísticas | ✅ (versión completa) |
| S6 | `perfil_de_usuario` | Perfil de Usuario | ✅ |
| — | `victoria` | Victoria sin puntuación | ❌ descartada |
| — | `estad_sticas` | Estadísticas básica | ❌ descartada |
| — | `estad_sticas_con_gr_fica_y_filtros` | Estadísticas media | ❌ descartada |
| — | `sudoku_game_flow` | Duplicado de S2 | ❌ ignorar |
| — | `untitled_prototype` | Duplicado de S2 | ❌ ignorar |
| — | *(sin diseño)* | Splash Screen | 🆕 a crear |
| — | *(sin diseño)* | Badges | ⏳ omitida por ahora |

---

## Transiciones entre pantallas

| Desde | Acción | Hacia | Animación sugerida |
|---|---|---|---|
| Splash | automática (~2s) | Selección de Dificultad | Fade in |
| Selección | tap en dificultad | Tablero de Juego | Slide up |
| Tablero | puzzle completado | Victoria | Scale + fade in |
| Victoria | "Siguiente Nivel" | Tablero de Juego | Slide up |
| Victoria | "Menú Principal" | Selección de Dificultad | Slide down |
| Tablero | botón atrás (←) | Selección de Dificultad | Slide down |
| Cualquiera | tab "Stats" | Estadísticas | Crossfade |
| Cualquiera | tab "Profile" | Perfil | Crossfade |

---

## Datos que cada pantalla necesita del modelo

| Pantalla | Datos del modelo (`model/`) |
|---|---|
| Splash | ninguno |
| Selección de Dificultad | `Difficulty` (enum con `visibleCells`) |
| Tablero de Juego | `SudokuGame` (puzzle + solution), estado mutable de celda seleccionada, notas, historial de movimientos, temporizador, contador de errores |
| Victoria | tiempo total, errores cometidos, dificultad jugada |
| Estadísticas | historial de partidas (no existe aún en modelo) |
| Perfil | datos de usuario (no existe aún en modelo) |
