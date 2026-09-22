<!--
  File: docs/architecture/host/glossary.md
  Purpose: Framework words
  Audience: Agents and humans
  Update when: A host type's meaning changes
-->

# Host glossary

Words for the Pool-System host. World words live in [the product glossary](../../product/glossary.md).

| Term | Meaning | Do not confuse it with |
|------|---------|------------------------|
| Pool | Shared state. `update` once per step, delegated to `PoolCompute`. Holds the heartbeat and a typed field map. | A world grid. Grids are field values the product stores in the Pool. |
| PoolCompute | Pluggable update. Default `SkeletonPoolCompute`. Wired on `EngineSetup`. | `EventEmissionPolicy`, which only chooses events. |
| EventEmissionPolicy | Pluggable choice of which events fire during compute. Default emits the config path list. | The category tree. The policy picks paths. The tree defines ancestry. |
| EngineSetup | The wiring of one run: tree, claimers, systems, schema, user ports, compute, emission, diagnostics. | `EngineConfig`, which is the step-0 seed. |
| EngineConfig | Initial heartbeat, scripted emission paths, and field seeds. The only input to step 0. | A later step's snapshot. |
| Step | One `runStep` transaction: stage input, compute, claim, systems, barrier, merge, clear, user view. | A product generation. A generation is a product system running inside a step. |
| Event | A buffer notification carrying a category. | A field write. Writes travel in the output buffer, not the event buffer. |
| Category tree | Hierarchy of categories. Each system is assigned one. | The product's world tree. The product builds that tree. The engine does not ship one. |
| EventClaimer | Claims by ancestry: own category or a descendant. | A system. A system has a claimer. Stub claimers exist without a system. |
| System | `EngineSystem`. Runs when it claimed at least one event. Returns `OUT_SYS`. | A sub-system. The system is the claimer and the aggregate. |
| System config | Id, category, sub-system list, optional conflict resolver. | The field schema. |
| Sub-system | One block with declared write-ranges and `execute` against snapshot plus staging. | A system. Sub-systems chain. Systems do not. |
| Out_spec | One sub-system's staging writes. | `OUT_SYS`, the system's field map after every sub-system. |
| OUT_SYS | `Map<String, Object>` returned by `EngineSystem.run`. | The Pool. `OUT_SYS` is merged afterwards. |
| ProvenancedWrite | `(systemId, value)` in the step output buffer. | A bare value. Resolvers see the id. |
| FieldMergeType | `merge(standing, writers)` for one field. | `FieldType`, which is only the four built-ins. |
| Typed merge | `TypedMerge.merge` applying each field's type, then `Pool.applyFields`. | Sub-system conflict resolution, which orders execution inside one system. |
| EngineDiagnostics | Port for step start, settle, claim, and unmatched. Default bridges to SLF4J. | The product `DiagnosticsHub`. |
| User view | `UserView.onSettled` after merge. Read-only. | The input view, which is sampled before compute. |
| User input | Named-action register: press, release, consume. | The input view, which is one step's frozen set. |
| Input view | Staged active actions. All of them are simultaneous for compute. | A queue of keystrokes. |
| Claim / finish | Barrier counts of systems that claimed and systems that finished. Merge waits until the counts are equal. | A sub-system counter. The barrier counts systems. |

Parent: [one engine step](README.md).
