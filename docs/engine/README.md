# Engine documentation

Pool-System Framework — step-based simulation architecture. **Host-ready (G-002).**

| Doc | Purpose |
|-----|---------|
| [architecture.md](architecture.md) | Package layout, host ports, code structure |
| [glossary.md](glossary.md) | Engine terminology |
| [specs/](specs/) | Framework specification — [specs/README.md](specs/README.md) |

Product-independent. Used by Aethelgard and potentially other simulations. Product plugs in via `PoolCompute`, `FieldMergeType`, and `EventEmissionPolicy`.
