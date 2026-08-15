# Skill: anadir-skills

## Uso

Activar **siempre** que se deba crear, añadir o registrar un nuevo skill para el agente, o cuando se solicite crear un skill nuevo en el proyecto.

## Pasos para añadir un Skill

1. **Crear el recurso del skill en `.agent/skills/resources/`**:
   - Crear un nuevo archivo markdown en `.agent/skills/resources/<nombre-del-skill>.md`.
   - Definir la estructura clara: `# Skill: <nombre>`, `## Uso`, `## Archivos relevantes`, `## Proceso / Reglas`.

2. **Registrar el skill en `.agent/skills/INDEX.md`**:
   - Editar `.agent/skills/INDEX.md`.
   - Añadir la entrada correspondiente a la tabla con el siguiente formato:
     `| <nombre-del-skill> | <Cuándo usar / Criterio de activación> | resources/<nombre-del-skill>.md |`

## Reglas y Convenciones

- Usar `kebab-case` para el nombre del skill y su archivo (ej: `anadir-skills.md`).
- Mantener las descripciones en `INDEX.md` concisas pero específicas para evitar cargas innecesarias de contexto.
- Verificar que las rutas en `INDEX.md` siempre apunten a `resources/<nombre-del-skill>.md`.
