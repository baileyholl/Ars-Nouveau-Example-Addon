# Agent Instructions — Ars Nouveau

## Asking for Clarification and Validation

Use bash `read` prompts for interactive clarification or validation steps. Never silently assume — ask.

### Clarification template

```bash
read -rp "Which subsystem are you targeting? (api/client/common/setup): " SUBSYSTEM
echo "Targeting: $SUBSYSTEM"
```

### Validation template (y/n)

```bash
read -rp "Proceed with reindex of $SUBSYSTEM? [y/N]: " CONFIRM
case "$CONFIRM" in
  [yY]|[yY][eE][sS]) echo "Confirmed." ;;
  *) echo "Aborted."; exit 0 ;;
esac
```

### Multi-choice template

```bash
echo "Select action:"
echo "  1) Query graph"
echo "  2) Update graph (incremental)"
echo "  3) Full rebuild"
read -rp "Choice [1-3]: " CHOICE
```

Use these patterns before destructive operations (full rebuild, file deletion) and when the user's intent is ambiguous.

## General Rules

- **Read before edit**: Read the full file before making changes.
- **Grep over cat**: Use the Grep tool to locate symbols across the codebase; never cat an entire file to find a class.
- **No large dumps**: Do not print raw JSON from graphify output files into chat — summarize instead.

## Testing Exceptions

- For player-level bonus glyph slot changes, do not add automated tests unless the user explicitly requests them.