# AGENTS.md — xudoku

## Proyecto

App Android nativa (Kotlin + Jetpack Compose) para generar y jugar Sudokus con solución única.

## Antes de trabajar

1. Leer `.agent/skills/INDEX.md`.
2. Cargar únicamente los skills aplicables.
3. Leer solo los archivos necesarios para la tarea.

## Reglas

- No hacer cambios fuera del alcance solicitado.
- Mantener separadas las capas `model`, `ui` y `ViewModel`.
- No añadir dependencias Android en `model/`.
- Si una petición de UI requiere modificar `model/`, pedir confirmación antes.
- Usar `val` salvo necesidad justificada de `var`.
- **OBLIGATORIO TDD PARA NUEVA LÓGICA:** Si añades o modificas lógica de negocio (incluyendo ViewModel, Model o mapeos de datos para UI), **DEBES APLICAR TDD ESTRICTO**. Empieza siempre escribiendo el Test (RED), implementa lo mínimo (GREEN) y refactoriza. **TOTALMENTE PROHIBIDO** saltarse este flujo bajo la excusa de "es un cambio menor". Consulta el skill `tdd-nueva-funcionalidad` en caso de duda.
- Ejecutar tests después de cambios relevantes.

