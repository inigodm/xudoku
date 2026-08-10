# Informe de Optimizaciones de Rendimiento y Batería (Xudoku)

A continuación se detalla una serie de mejoras y buenas prácticas arquitectónicas enfocadas en reducir el consumo de batería, evitar sobrecalentamiento del dispositivo y asegurar que la interfaz corra a unos fluidos 60/120 FPS.

## 1. Gestión del Ciclo de Vida y Batería (Timer)

Actualmente, el temporizador de la partida (`timerJob` en `GameViewModel`) se ejecuta en una corrutina con un `delay(1000L)`. 

**Problema:** Si el usuario minimiza la aplicación (pasa a segundo plano) y no se gestiona correctamente el ciclo de vida, la corrutina seguirá despertando a la CPU cada segundo, lo que causará un drenaje de batería innecesario.
**Soluciones:**
- Utilizar `collectAsStateWithLifecycle()` en las pantallas de Compose en lugar de `collectAsState()`. Esto asegura que las recolecciones de flujos de estado se detengan automáticamente cuando la app no está visible.
- Implementar un `LifecycleEventObserver` (o usar las herramientas de ViewModel) para pausar automáticamente el temporizador (`_elapsedSeconds`) cuando la app entra en estado `ON_PAUSE` o `ON_STOP`, y reanudarla (calculando el offset de tiempo) en `ON_RESUME`.

## 2. Reducción de Recomposiciones en el Tablero de Sudoku

El tablero de Sudoku contiene 81 celdas. En Jetpack Compose, un cambio en el estado global del tablero puede provocar que las 81 celdas se redibujen si no se gestionan bien los estados.

**Soluciones:**
- **Inmutabilidad:** Asegurarse de que las clases del modelo (como `CellState`) estén anotadas con `@Immutable` o `@Stable`. Si se modifican a través de copias profundas, Compose será capaz de omitir la recomposición de aquellas celdas cuyo valor no ha cambiado.
- **Estados derivados (`derivedStateOf`):** Si un componente de la UI (por ejemplo, el botón de "Deshacer") depende de si hay un historial de movimientos, envuélvelo en `derivedStateOf` para evitar que la UI principal se recomponga cada vez que el reloj avanza.
- Limitar el alcance del estado del Timer: Crear un subcomponente `@Composable fun TimerDisplay(viewModel: GameViewModel)` que sea el único que observe el flujo del reloj. Así, cuando el reloj avanza cada segundo, **sólo** se recompone el pequeño texto del temporizador y no todo el `Scaffold` o el `GameScreen`.

## 3. Optimización de Renderizado y Gráficos (Canvas)

En pantallas como `DifficultyScreen` y `StatsScreen` se hace un uso intensivo de gradientes, efectos de brillo (`drawGlowCircle`) y trazados `Canvas`.

**Soluciones:**
- Los efectos complejos de dibujo como el brillo (`drawGlowCircle`) que utilizan múltiples pasadas de transparencia (`alpha = (1f - i / 8f)`) son costosos computacionalmente (Overdraw). En su lugar, es más barato pre-renderizar el brillo en una imagen (PNG/WebP) o utilizar el modificador `Modifier.graphicsLayer { shadowElevation = ... }`.
- Si se mantiene el uso de `drawBehind`, aplicar `Modifier.graphicsLayer` a la caja contenedora para aislarla en un *RenderNode* independiente, de forma que la GPU pueda almacenar el resultado en caché si el contenido no cambia.

## 4. Persistencia y Bases de Datos

- **Room:** Comprobar que las operaciones masivas o de agregado (como calcular los puntos o max streaks en el historial) se hagan mediante una query `SQL` optimizada (ej: `SELECT SUM(puntuacion) FROM games`) en lugar de extraer todas las entidades a memoria con `getAllResults()` y procesarlas con `.filter { }` o `.map { }` en el código Kotlin, ahorrando memoria y uso de CPU.
- **SharedPreferences:** Confirmar que los guardados de progreso de nivel (`ProgressionRepository`) utilicen `apply()` (asíncrono) en vez de `commit()` (síncrono y bloqueante del hilo).

## 5. Carga de Imágenes y Vectores

- Hemos detectado el uso de iconos vectoriales por defecto. Si la app creciera incluyendo imágenes o avatares (pantalla de Perfil), se recomienda encarecidamente utilizar librerías de carga asíncrona cacheadas como **Coil** y priorizar el uso de formatos modernos como **WebP** para mantener bajo el uso de memoria (Heap).
