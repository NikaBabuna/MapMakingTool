<!--
  File: docs/protocol/navigation/pointers.md
  Purpose: Where pointers live and which question each one answers
  Audience: Agents and humans
  Update when: A pointer home changes
-->

# Pointers

A pointer is a link that names the next document and says why you would open it. Follow one. Do not open the documents listed beside it “while you are here.”

| Question | Open this | You are done when |
|----------|-----------|-------------------|
| Which room of conduct applies? | [../README.md](../README.md) | You know whether you need environment, navigation, a flow, or a blueprint |
| Which flow may I run? | [../environment/dispatch.md](../environment/dispatch.md) | One row matches the facts, or no row matches and you must ask |
| What does this protocol word mean? | [../environment/dictionary.md](../environment/dictionary.md) | The row’s “do not confuse it with” does not match your use |
| What may I change, and what is blocked? | [../environment/conduct.md](../environment/conduct.md) and [../environment/correctness.md](../environment/correctness.md) | You can say which rule would break |
| How do I write this kind of file? | The flow step names the kind. Then [../blueprints/README.md](../blueprints/README.md) names the blueprint | You have the skeleton for that one kind |
| What is in this folder? | That folder’s `README.md` | You have a child to open, or you know the folder is the wrong place |
| Where does a document live? | [../../navigation.md](../../navigation.md) | You have a path. You do not read every row |
| Which Goal is active? | [../../paperwork/goals.md](../../paperwork/goals.md), the Active Goal line only | You have the link. You do not re-read every finished Goal |
| What Steps does that Goal contain, and which are done? | The active Goal’s Step table | You know the next `not started` row, or that one is `in progress` |
| Is a Step already underway? | The step registry, [../../paperwork/steps.md](../../paperwork/steps.md) | You see `in progress`, or you see that nothing is |
| What must that Step make true? | `docs/paperwork/steps/F-0xx.md` for that id, only if the row links one | You have the requirement table. You do not open neighboring `F-` files |

A page that is not reachable by following one of these pointers is not a required read for the turn. It may still be the right page if the human named it.
