# Skill: actualizar-documentacion

## Cuándo usar esta skill

Actívala **solo cuando el usuario lo pida explícitamente**, por ejemplo:
- "actualiza la documentación"
- "actualiza el index"
- "el skill de X está desactualizado"
- "acabo de cambiar Y, actualiza los docs"

**No la actives automáticamente** después de cada cambio de código. Es un proceso manual y deliberado para no generar ruido en tokens.

---

## Protocolo de actualización (ejecutar en orden)

### Paso 1 — Preguntar al usuario qué cambió

Antes de leer ningún archivo, pregunta:

> "¿Qué archivos modificaste o creaste? Lista las rutas relativas desde la raíz del proyecto y dime brevemente qué cambió en cada uno. Si no recuerdas la ruta exacta, describe el área (ej: 'añadí una pantalla de juego', 'cambié el generador')."

**Espera la respuesta antes de continuar.** No escanees todo el proyecto.

---

### Paso 2 — Leer solo los archivos afectados

Con la información del usuario, lee únicamente:
- Los archivos mencionados como modificados
- Los tests relacionados (si los hay)
- El bloque relevante de `PROJECT_INDEX.md` para comparar con el estado actual

No leas archivos que el usuario no haya mencionado ni hagas un `find` global.

---

### Paso 3 — Actualizar `PROJECT_INDEX.md`

Edita la tabla de archivos y/o la sección "Flujo de datos" **solo donde algo haya cambiado**:

- **Archivo nuevo**: añade una fila con ruta exacta, descripción de una línea de qué hace, y cuándo tocarlo.
- **Archivo modificado**: actualiza la descripción si la funcionalidad cambió; actualiza "Cuándo tocarlo" si cambiaron las dependencias.
- **Archivo eliminado**: elimina la fila.
- **Flujo de datos cambiado**: si se añadió un ViewModel, una pantalla, o cambió cómo se produce/consume `SudokuGame`, actualiza el diagrama.

Reglas de estilo para la tabla:
- Rutas siempre relativas a la raíz del proyecto (sin `/home/...`)
- Descripciones de una línea, concretas (menciona métodos/clases reales, no "contiene lógica")
- La columna "Cuándo tocarlo" debe indicar el skill a leer si aplica

---

### Paso 4 — Determinar qué skills actualizar

Para cada archivo modificado, determina si pertenece al área de algún skill existente:

| Si el cambio es en... | Skill a revisar |
|---|---|
| `model/SudokuBoard.kt` | `.agent/skills/tablero-core.md` |
| `model/SudokuGenerator.kt`, `SudokuGame` | `.agent/skills/generador.md` |
| `model/Difficulty.kt` | `.agent/skills/dificultad.md` |
| `ui/theme/`, `MainActivity.kt`, nuevos Composables, ViewModel | `.agent/skills/ui-tema.md` |
| Archivo nuevo sin skill existente | Crear un nuevo skill (ver Paso 5) |

Para cada skill afectado, actualiza **solo las secciones que cambiaron**:
- `## Archivos relevantes` — añade/elimina filas
- `## Modelo de datos` / `## API pública` — si cambiaron firmas o estructuras
- `## Invariantes` — si se añadió o eliminó una restricción
- `## Tests a correr` — si cambió el nombre o ubicación de los tests

---

### Paso 5 — Crear un nuevo skill (si aplica)

Si el cambio introduce un área nueva (ej: se añadió navegación, un repositorio de datos, una pantalla nueva), crea `.agent/skills/<nombre-kebab-case>.md` con esta estructura mínima:

```markdown
# Skill: <nombre>

## Cuándo usar esta skill
[qué peticiones del usuario la activan]

## Archivos relevantes
| Archivo | Rol |
|---|---|
| `ruta/exacta/Archivo.kt` | descripción |

## Modelo de datos / API pública
[tipos, firmas, estructuras clave]

## Invariantes que no se pueden romper
[lista numerada]

## Tests a correr después de cambios
[comandos exactos de ./gradlew]
```

Y añade una referencia al nuevo skill en `AGENTS.md` si es un área sensible.

---

### Paso 6 — Confirmar con el usuario

Al terminar, muestra un resumen compacto:

```
✅ Actualizado PROJECT_INDEX.md:
   - Fila modificada: ui/screen/GameScreen.kt
   - Fila añadida: ui/viewmodel/GameViewModel.kt
   - Flujo de datos actualizado (añadido ViewModel)

✅ Actualizado .agent/skills/ui-tema.md:
   - Sección "Estado actual de la UI" actualizada
   - Añadidos tests de ViewModel en "Tests a correr"

⚠️ No se actualizó AGENTS.md (ningún archivo sensible cambió)
```

---

## Lo que esta skill NO hace

- No escanea todo el proyecto automáticamente
- No actualiza `AGENTS.md` a menos que cambie algo en `model/` o se añada un área nueva sensible
- No reescribe skills que no hayan sido afectados por los cambios
- No añade información genérica o de relleno — solo lo que cambió de verdad
