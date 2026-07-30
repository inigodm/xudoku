# Skill: actualizar-documentacion

## Uso

Activar solo cuando el usuario solicite actualizar documentación.

No ejecutar automáticamente después de cambios de código.

## Proceso

1. Preguntar qué archivos se modificaron/crearon y qué cambió.
2. Esperar respuesta antes de leer archivos.
3. Leer únicamente:
    - archivos afectados;
    - tests relacionados;
    - documentación relevante.

No escanear el proyecto completo.

## Actualizaciones

Modificar solo lo necesario:

### PROJECT_INDEX.md
Actualizar únicamente si cambia:
- archivos existentes;
- archivos añadidos/eliminados;
- flujo de datos o arquitectura.

Mantener:
- rutas relativas;
- descripciones concretas;
- referencias al skill correspondiente.

### Skills afectados
Actualizar solo secciones modificadas:
- archivos relevantes;
- modelo/API pública;
- invariantes;
- tests.

No reescribir skills completos.

## Nuevas áreas

Si aparece una nueva área funcional:

Crear `.agent/skills/<nombre>.md` con:

- Uso
- Archivos relevantes
- Modelo/API
- Invariantes
- Tests

Añadir referencia en `AGENTS.md` solo si es un área importante.

## Final

Mostrar resumen de:
- documentos actualizados;
- skills modificados;
- nuevos archivos creados.

## No hacer

- No actualizar documentación no afectada.
- No inventar información.
- No añadir texto genérico.
- No modificar código de producción.