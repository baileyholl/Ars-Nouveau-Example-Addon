# Agent Instructions — Ars Nouveau

## Knowledge Graph First

This project uses **graphify** as its persistent knowledge store. Before reading files at scale or storing large
tool-call results in context, check whether the graph already has the answer.

### When to use graphify

| Situation                                          | Action                            |
|----------------------------------------------------|-----------------------------------|
| Exploring architecture or cross-file relationships | `/graphify query "<question>"`    |
| Understanding a specific class or concept          | `/graphify explain "<ClassName>"` |
| Tracing how two systems connect                    | `/graphify path "<A>" "<B>"`      |
| New files added / code changed                     | `/graphify . --update`            |
| No graph exists yet                                | `/graphify .` (full build)        |

**Do NOT** cat or read source files en masse to answer structural questions — that burns context. Query the graph
instead.

### Checking for unindexed files

Before running a full `/graphify` rebuild, check whether any files are missing from the graph using Grep:

```
Grep pattern: class\s+\w+|interface\s+\w+
path: src/main/java/com/hollingsworth/arsnouveau/
glob: *.java
output_mode: files_with_matches
```

Compare the result against `graphify-out/graph.json` node `source_file` values. Files absent from the graph are
unindexed — run `/graphify . --update` to index only those.

To check a specific file is indexed:

```bash
grep -c '"source_file"' graphify-out/graph.json 2>/dev/null && \
python3 -c "
import json
from pathlib import Path
g = json.loads(Path('graphify-out/graph.json').read_text())
files = {n['source_file'] for n in g.get('nodes', [])}
print(f'{len(files)} files indexed')
" || echo "No graph found — run /graphify ."
```

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
- **Graph is the map**: After any non-trivial exploration session, run `/graphify . --update` to keep the graph current
  so future sessions start warm.