# AGENTS.md — Guía para agentes de IA en xudoku

## Qué es este proyecto

**xudoku** es una app Android nativa (Kotlin + Jetpack Compose) que genera y presenta puzzles de Sudoku con solución única garantizada. El núcleo de lógica (generación, validación, resolución) está completamente implementado y testeado; lo que falta es la capa UI del juego. `minSdk 24`, `targetSdk 36`.

---

## Comandos exactos

```bash
# Correr todos los unit tests (JVM, no necesita emulador)
./gradlew test

# Correr solo los tests del módulo app
./gradlew :app:test

# Compilar APK debug
./gradlew assembleDebug

# Compilar APK release
./gradlew assembleRelease

# Lint
./gradlew lint

# Tests instrumentados (requiere emulador o dispositivo conectado)
./gradlew connectedAndroidTest
```

> **Nota:** No hay `npm`, no hay scripts de Node. El build system es **Gradle** exclusivamente. Usa `./gradlew` (wrapper incluido), nunca instales Gradle globalmente.

---

## Convenciones de código

### Paquetes y estructura
```
com.inigo.xudoku
├── model/          → Lógica pura: SudokuBoard, SudokuGenerator, Difficulty
│   └── (sin dependencias Android aquí — solo stdlib Kotlin + java.util.Random)
├── ui/
│   └── theme/      → Color.kt, Theme.kt, Type.kt (Material3 / Compose)
└── MainActivity.kt → Entry point de Compose
```

### Naming
- Clases: `PascalCase` (`SudokuBoard`, `SudokuGenerator`)
- Funciones/variables: `camelCase` (`generateGame`, `visibleCells`)
- Constantes companion: `UPPER_SNAKE_CASE` (`EMPTY`, `SIZE`, `BOX_SIZE`)
- Tests: backtick strings descriptivas `` `generated puzzle has exactly one solution`() ``
- El tablero siempre se indexa como `board[row, col]` (fila primero)

### Formato del tablero
- Tipo: `Array<IntArray>` — matriz 9×9 de `Int`
- Vacío: `SudokuBoard.EMPTY = 0`
- Valores válidos: `1..9`
- Acceso: operador `board[row, col]` definido en `SudokuBoard`
- El resultado de generación es siempre un `SudokuGame(puzzle, solution)` donde ambos son `SudokuBoard` independientes (deep copies)

### Estilo general
- KDoc en funciones públicas de `model/`
- Sin uso de `var` cuando `val` es posible
- Backtracking recursivo en `solveFrom(index)` e `fillRandomised` — no iterar el tablero completo en cada paso, usar `index` como punto de entrada

---

## Protocolo obligatorio antes de cada tarea

Antes de escribir una sola línea de código, el agente DEBE completar estos pasos en orden:

1. **Identificar el scope** — ¿Qué capa toca la petición? (`model/` / `ui/` / ambas)
2. **Leer `.agent/skills/INDEX.md`** — Identificar los skills aplicables y abrirlos.
3. **Validar scope con el usuario** — Si la petición es de UI pero parece requerir lógica nueva en `model/`, PARAR y preguntar al usuario antes de implementar nada.
4. **Nunca hacer más de lo pedido** — Si el usuario pide pantallas, entregar pantallas. No añadir casos de uso, servicios ni lógica no solicitados.

---

## Reglas para agentes

1. **Antes de tocar cualquier archivo en `model/`**, lee el skill correspondiente en `.agent/skills/`.
2. **Corre `./gradlew test` después de cualquier cambio** en `model/` y verifica que los 19 tests existentes siguen en verde.
3. **No cambies la firma pública de `SudokuBoard`** (`get`, `set`, `clear`, `isEmpty`, `isValid`, `solve`, `countSolutions`, `copy`, `generateComplete`) sin actualizar también todos los tests que la usan.
4. **No añadas dependencias Android** (imports de `android.*` o `androidx.*`) en el paquete `model/` — debe seguir siendo lógica pura testeable en JVM.
5. **`SudokuGame` es un `data class`** — no lo conviertas a clase mutable; si necesitas estado de juego en la UI, crea un `ViewModel` separado.
6. Los archivos de tema (`ui/theme/`) son boilerplate de Material3; puedes editarlos libremente para cambiar colores o tipografía sin afectar la lógica.
7. **Regla de scope (UI → model/)**: Si la petición es de UI (`ui/`), no escribas ni modifiques nada en `model/` sin aprobación explícita del usuario en esa misma sesión. Pedir permiso no es opcional.
8. **Funcionalidad nueva en `model/` → TDD obligatorio**: Cualquier código nuevo en `model/` que no exista aún requiere: (a) leer `tdd-nueva-funcionalidad.md`, (b) proponer el diseño al usuario y esperar aprobación, (c) escribir los tests primero, código después. Sin excepción.

---

## Dónde NO tocar sin preguntar primero

| Archivo | Por qué |
|---|---|
| `model/SudokuBoard.kt` | Contiene solver + validador + generador base. Tiene 19 tests que lo cubren. Cambios aquí pueden romper la garantía de solución única. |
| `model/SudokuGenerator.kt` | El algoritmo de eliminación de celdas con `countSolutions(2)` es el mecanismo que garantiza unicidad. No cambies la lógica de `removeCells` sin entender las implicaciones. |
| `model/Difficulty.kt` | Los valores de `visibleCells` están calibrados. Cambiar `HARDEST(17)` puede hacer la generación imposible (17 es el mínimo teórico probado). |

---

## Estado actual del desarrollo

| Capa | Estado |
|---|---|
| Lógica core (`model/`) | ✅ Completa y testeada |
| Tema visual (`ui/theme/`) | ⚠️ Boilerplate de Android Studio, sin personalizar |
| UI del juego (`ui/screen/`) | ❌ No existe — solo placeholder "Hello Android" |
| ViewModel / estado | ❌ No existe |
| Navegación | ❌ No existe |

## Compose/Material3
- Versión de Material3 usada: [pon la tuya, ej: 1.2.1]
- Antes de usar un ícono de Icons.Outlined/Filled o un objeto tipo XxxDefaults,
  verifica que existe en esta versión — no asumas nombres "lógicos".

## Fuentes (Google Fonts downloadable)
- Requiere app/src/main/res/values/font_certs.xml (certificados AOSP estándar) — YA EXISTE, no lo borres ni regeneres.
- Si se usa GoogleFont.Provider, siempre referenciar R.array.com_google_android_gms_fonts_certs