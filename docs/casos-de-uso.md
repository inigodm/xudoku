# Catálogo de Casos de Uso — xudoku

> **Propósito:** ahorrar tokens en sesiones futuras. Antes de escribir tests o añadir funcionalidad, leer este archivo es suficiente para conocer qué acciones existen, sus precondiciones y efectos, sin releer el ViewModel ni las pantallas.
>
> **Fuentes:** `GameViewModel.kt`, `XudokuNavGraph.kt`, `GameScreen.kt`  
> **Última actualización:** 2026-07-25

---

## Acciones del jugador en partida (`GameViewModel`)

### CU-01 — Iniciar partida

| Campo        | Valor |
|--------------|-------|
| Actor        | Sistema / Jugador |
| Trigger      | El jugador selecciona una dificultad en `DifficultyScreen` → navegación a `GameScreen` → `LaunchedEffect(difficulty)` |
| Precondición | Ninguna (puede haber una partida previa en curso) |
| Método VM    | `GameViewModel.startGame(difficulty: Difficulty)` |
| Postcondición | `cells` contiene el puzzle nuevo, `mistakes=0`, `elapsedSeconds=0`, `isCompleted=false`, `isLoading=false` (tras generación), temporizador corriendo |
| Efecto UI    | Grid visible con celdas dadas; spinner/texto "Generando puzzle…" mientras `isLoading=true` |
| Tested       | ✅ |

---

### CU-02 — Seleccionar celda

| Campo        | Valor |
|--------------|-------|
| Actor        | Jugador |
| Trigger      | Tap en cualquier celda del grid |
| Precondición | Partida en curso (`isLoading=false`) |
| Método VM    | `GameViewModel.selectCell(row: Int, col: Int)` |
| Postcondición | `selectedCell = Pair(row, col)` |
| Efecto UI    | Celda seleccionada resaltada; celdas de misma fila/columna/caja resaltadas; tecla del teclado activa si la celda tiene valor |
| Tested       | ✅ |

---

### CU-03 — Introducir número (modo normal)

| Campo        | Valor |
|--------------|-------|
| Actor        | Jugador |
| Trigger      | Tap en tecla numérica del `NumberPad` con `isNotesMode=false` |
| Precondición | `selectedCell != null`, celda no es `isGiven` |
| Método VM    | `GameViewModel.enterNumber(number: Int)` |
| Postcondición | Si la celda ya contiene `number` sin error (`!isError`), no realiza ninguna acción (no re-valida ni duplica puntuación). Si cambia de número: `cells[row][col].value = number`; si incorrecto: `isError=true`, `mistakes += 1`; notas de esa celda eliminadas; si se alcanzan 9 instancias correctas del número, se elimina de todas las notas; movimiento guardado en historial |
| Efecto UI    | Número visible en celda; celda en rojo si error; contador de errores actualizado |
| Tested       | ✅ |

---

### CU-04 — Introducir nota en lápiz (modo notas)

| Campo        | Valor |
|--------------|-------|
| Actor        | Jugador |
| Trigger      | Tap en tecla numérica con `isNotesMode=true` |
| Precondición | `selectedCell != null`, celda no es `isGiven`, `isNotesMode=true` |
| Método VM    | `GameViewModel.enterNumber(number: Int)` (misma función, rama distinta) |
| Postcondición | `notes[Pair(row,col)]` añade o quita `number` (toggle); movimiento guardado |
| Efecto UI    | Dígito pequeño aparece/desaparece en la celda |
| Tested       | ✅ |

---

### CU-05 — Activar/desactivar modo notas

| Campo        | Valor |
|--------------|-------|
| Actor        | Jugador |
| Trigger      | Tap en botón "Notas" de la toolbar |
| Precondición | Partida en curso |
| Método VM    | `GameViewModel.toggleNotesMode()` |
| Postcondición | `isNotesMode` invierte su valor |
| Efecto UI    | Badge visible sobre el icono; comportamiento del teclado cambia |
| Tested       | ✅ |

---

### CU-06 — Borrar celda seleccionada

| Campo        | Valor |
|--------------|-------|
| Actor        | Jugador |
| Trigger      | Tap en botón "Borrar" de la toolbar **o** tecla delete del `NumberPad` |
| Precondición | `selectedCell != null`, celda no es `isGiven` |
| Método VM    | `GameViewModel.clearSelectedCell()` |
| Postcondición | `cells[row][col] = CellState(0, false, false)`; notas de esa celda eliminadas; movimiento guardado |
| Efecto UI    | Celda queda vacía |
| Tested       | ✅ |

---

### CU-07 — Deshacer último movimiento

| Campo        | Valor |
|--------------|-------|
| Actor        | Jugador |
| Trigger      | Tap en botón "Deshacer" de la toolbar |
| Precondición | `moveHistory` no vacío |
| Método VM    | `GameViewModel.undoLastMove()` |
| Postcondición | El último `GameMove` se extrae del historial; `cells[row][col]` restaurado al valor y notas previos; si el movimiento revertido era un error, `mistakes` se decrementa (mínimo 0); `isCompleted=false` |
| Efecto UI    | Celda vuelve a estado anterior |
| Tested       | ✅ |

---

### CU-08 — Solicitar pista

| Campo        | Valor |
|--------------|-------|
| Actor        | Jugador |
| Trigger      | Tap en botón "Pista" de la toolbar |
| Precondición | Partida en curso; `hintsRemaining > 0`; existe al menos una celda vacía no dada |
| Método VM    | `GameViewModel.requestHint()` |
| Postcondición | Si `hintsRemaining > 0`: se decrementa `hintsRemaining`. Si hay celda seleccionada vacía: se revela su valor correcto allí. Si no: se busca la primera celda vacía no dada en orden fila-columna y se revela. Notas de esa celda eliminadas; si se alcanzan 9 instancias, se elimina de todas las notas. `selectedCell` apunta a la celda revelada. `checkCompletion()` ejecutado. Si `hintsRemaining == 0`: no se ejecuta ningún cambio. |
| Efecto UI    | Celda revelada muestra el número correcto. El badge en el botón de pista muestra `hintsRemaining` (3 en fácil/muy fácil, 2 en media, 1 en difícil, 0 en imposible). Si es 0, el botón de pista queda deshabilitado. |
| Tested       | ✅ |

---

### CU-09 — Completar puzzle (detección automática)

| Campo        | Valor |
|--------------|-------|
| Actor        | Sistema |
| Trigger      | Después de `enterNumber` o `requestHint`, `checkCompletion()` detecta que todas las celdas tienen valor ≠ 0 y ninguna es `isError=true` |
| Precondición | Partida en curso |
| Método VM    | `checkCompletion()` (interno) → `_isCompleted.value = true` + `timerJob?.cancel()` |
| Postcondición | `isCompleted=true`; temporizador detenido |
| Efecto UI    | `LaunchedEffect(isCompleted)` en `GameScreen` calcula el score y navega a `VictoryScreen` |
| Tested       | ✅ |

---

## Flujos de navegación

### CU-10 — Splash → Selección de dificultad

| Campo        | Valor |
|--------------|-------|
| Actor        | Sistema |
| Trigger      | `SplashScreen` llama a `onSplashComplete` al terminar la animación |
| Precondición | App arranca por primera vez en sesión |
| Método VM    | N/A |
| Postcondición | Back stack: `[Difficulty]` (Splash eliminado con `popUpTo inclusive`) |
| Efecto UI    | Usuario ve `DifficultyScreen` |
| Tested       | ✅ |

---

### CU-11 — Seleccionar dificultad → Ir a partida

| Campo        | Valor |
|--------------|-------|
| Actor        | Jugador |
| Trigger      | Tap en una tarjeta de dificultad en `DifficultyScreen` |
| Precondición | Usuario en `DifficultyScreen` |
| Método VM    | N/A (la navegación llama a `startGame` vía `LaunchedEffect`) |
| Postcondición | Back stack: `[Difficulty, Game]` |
| Efecto UI    | `GameScreen` con la dificultad elegida |
| Tested       | ✅ |

---

### CU-12 — Completar puzzle → Pantalla de victoria

| Campo        | Valor |
|--------------|-------|
| Actor        | Sistema |
| Trigger      | `isCompleted=true` en `GameScreen` |
| Precondición | Partida completada (CU-09) |
| Método VM    | `computeScore(elapsed, mistakes, difficulty)` (función privada en GameScreen) |
| Postcondición | Back stack: `[Difficulty, Victory]` (`popUpTo(DIFFICULTY)` elimina Game) |
| Efecto UI    | `VictoryScreen` con segundos, errores, dificultad y puntuación |
| Tested       | ✅ |

---

### CU-13 — Victoria → Nueva partida (misma dificultad)

| Campo        | Valor |
|--------------|-------|
| Actor        | Jugador |
| Trigger      | Tap en "Jugar de nuevo" en `VictoryScreen` |
| Precondición | Usuario en `VictoryScreen` |
| Método VM    | N/A |
| Postcondición | Back stack: `[Difficulty, Game]` (nueva instancia) |
| Efecto UI    | Nueva `GameScreen` con la misma dificultad |
| Tested       | ✅ |

---

### CU-14 — Victoria → Menú principal

| Campo        | Valor |
|--------------|-------|
| Actor        | Jugador |
| Trigger      | Tap en "Menú principal" en `VictoryScreen` |
| Precondición | Usuario en `VictoryScreen` |
| Método VM    | N/A |
| Postcondición | Back stack: `[Difficulty]` |
| Efecto UI    | `DifficultyScreen` |
| Tested       | ✅ |

---

### CU-15 — Abandonar partida (volver atrás)

| Campo        | Valor |
|--------------|-------|
| Actor        | Jugador |
| Trigger      | Tap en flecha ← en `GameScreen` |
| Precondición | Usuario en `GameScreen` |
| Método VM    | N/A |
| Postcondición | `popBackStack(DIFFICULTY, inclusive=false)`; ViewModel no se destruye (sigue en memoria si vuelve) |
| Efecto UI    | `DifficultyScreen` |
| Tested       | ✅ |

---

## Cálculo de puntuación

### CU-16 — Calcular score final

| Campo        | Valor |
|--------------|-------|
| Actor        | Sistema |
| Trigger      | Al completar el puzzle, antes de navegar a Victoria |
| Precondición | `isCompleted=true` |
| Método VM    | `computeScore(elapsedSeconds, mistakes, difficulty)` (privado en `GameScreen.kt`) |
| Postcondición | Score = `baseScore(difficulty) - (elapsedSeconds/10).coerceAtMost(baseScore/2) - mistakes*200`, mínimo 100 |
| Efecto UI    | Score visible en `VictoryScreen` |
| Tested       | ✅ |

---

## Fin de juego

### CU-17 — Agotar errores (pantalla de fin de juego)

| Campo        | Valor |
|--------------|-------|
| Actor        | Sistema |
| Trigger      | Tras `enterNumber`, `mistakes` alcanza 3 errores |
| Precondición | Partida en curso |
| Método VM    | `enterNumber` incrementa `mistakes`; al llegar a 3, `_isGameOver.value = true` y cancela el temporizador |
| Postcondición | `isGameOver=true`, temporizador detenido |
| Efecto UI    | `GameScreen` detecta `isGameOver=true` y navega a `GameOverScreen` (`game_over/{seconds}/{mistakes}`) con `popUpTo(DIFFICULTY)` |
| Tested       | ✅ |

---

## Sistema de Progresión y Experiencia

### CU-18 — Ganar Experiencia (XP) al finalizar partida

| Campo        | Valor |
|--------------|-------|
| Actor        | Sistema |
| Trigger      | Al completar el puzzle (CU-09) antes de mostrar la pantalla de Victoria |
| Precondición | `isCompleted=true` |
| Método VM    | `GameViewModel` invoca a `ProgressionManager.calculateXP` y llama a `updateXP` en `ProgressionRepository` |
| Postcondición | El XP total del jugador se incrementa aplicando bonus de tiempo, racha y ausencias de errores. |
| Efecto UI    | La barra de XP se llena progresivamente en `VictoryScreen` |
| Tested       | ✅ |

---

### CU-19 — Subir de Nivel y Rango

| Campo        | Valor |
|--------------|-------|
| Actor        | Sistema |
| Trigger      | Cuando el XP total supera el `xpRequiredForNextLevel` calculado por la curva |
| Precondición | `ProgressionState` actualizado con nueva XP tras una victoria |
| Método VM    | `ProgressionManager.getLevelForXP` y `ProgressionManager.getRankForLevel` proveen el nuevo estado vía `ProgressionViewModel` |
| Postcondición | Nivel incrementado. El Rango puede haber cambiado (ej. de Novato a Aprendiz). La bandera `hasLeveledUp` se emite. |
| Efecto UI    | `VictoryScreen` muestra un `AlertDialog` de "¡Nivel Aumentado!". Además, los badges en `ProfileScreen` y `VictoryScreen` reflejan el nuevo Nivel. |
| Tested       | ✅ |

---

### CU-20 — Actualizar y Perder Rachas (Streaks)

| Campo        | Valor |
|--------------|-------|
| Actor        | Sistema |
| Trigger      | Victoria (aumenta racha de victorias y diaria) o Abandono de partida / Game Over (pierde racha de victorias) |
| Precondición | Partida finalizada o interrumpida |
| Método VM    | `ProgressionRepository.updateStreaks()` en victoria, o `ProgressionViewModel.onGameAbandoned()` en abandono |
| Postcondición | `winStreak` se incrementa o resetea a 0. `dailyStreak` se incrementa si se jugó en días consecutivos. |
| Efecto UI    | `ProfileScreen` actualiza la UI de "Daily Streak". La fórmula de XP se afecta en la próxima partida. |
| Tested       | ✅ |

---

### CU-21 — Desbloquear / Bloquear Dificultades

| Campo        | Valor |
|--------------|-------|
| Actor        | Sistema |
| Trigger      | Al renderizar `DifficultyScreen` se chequea el nivel actual del usuario frente a constantes `minLevel` |
| Precondición | El estado del jugador se ha cargado en `ProgressionViewModel` |
| Método VM    | Comparación `level >= difficultyRequiredLevel` |
| Postcondición | La dificultad se renderiza normal y clickeable o en estado atenuado con candado. |
| Efecto UI    | Icono de candado aparece sobre dificultades superiores (Hard, Extreme) si nivel insuficiente; no se puede hacer tap (click deshabilitado). |
| Tested       | ✅ |

---

### CU-22 — Consultar Estadísticas Básicas en el Perfil

| Campo        | Valor |
|--------------|-------|
| Actor        | Jugador |
| Trigger      | Tap en el icono de perfil en la barra inferior (Bottom Bar) |
| Precondición | El jugador ha completado o no partidas (estadísticas globales) |
| Método VM    | `StatsViewModel.loadStats("Global")` |
| Postcondición | El estado UI de `StatsViewModel` se carga con total de victorias, win rate, partidas jugadas, etc. |
| Efecto UI    | `ProfileScreen` muestra el número de partidas jugadas, win rate, victorias y racha diaria. |
| Tested       | ✅ |

---

| Total CUs | Tested ✅ | Pendientes ❌ |
|-----------|----------|--------------|
| 22        | 22       | 0            |
