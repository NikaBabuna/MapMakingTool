<!--
  File: docs/protocol/navigation/pointers.md
  Purpose: For each common question, the one page that answers it, and when to stop
  Audience: Agents
  Update when: A standing pointer path changes, or a common question gains a home
-->

# Pointers

Each question has one home. Open that page, read it by the ladder in [reading.md](reading.md), and stop when the last column is true. If your question is not here, take the shelf from [../environment/map.md](../environment/map.md), open that shelf's door, and follow one row.

## Conduct

| Question | Open | You are done when |
|----------|------|-------------------|
| How must I behave? | [../brief.md](../brief.md), then [../core-workflow.md](../core-workflow.md) | You know your role and the flow catalogue |
| What must I read at the start of a chat? | [bounds.md](bounds.md), Startup read set | You have the list |
| Which flow do I run now? | [../core-workflow.md](../core-workflow.md) Article 3, then that flow's row on [../flows/README.md](../flows/README.md) | You have one flow name |
| What exactly is a Goal, Step, Accept, Seal, safe point, or torn? | [../environment/core-definition.md](../environment/core-definition.md) | You have the definition row |
| How must I talk to the human? | [../environment/style.md](../environment/style.md) | You know the register and the confidence labels |
| When is this Step's work correct? | [../environment/correctness.md](../environment/correctness.md) Article 2, then the one Article it points to | You know the standard for this Step's kind |
| May I create, move, or rename this folder? | [../environment/phase.md](../environment/phase.md) | You have the phase and the depth row for the path |
| What must this file look like, and how do I edit it? | [../blueprints/README.md](../blueprints/README.md), then the group door, then that blueprint's key block and one operation | You have the operation's Edit steps |
| What is each docs folder for? | [../environment/map.md](../environment/map.md) | You have the folder's row |

## Paperwork

| Question | Open | You are done when |
|----------|------|-------------------|
| What is the Active Goal? | The `**Active Goal:**` line of `docs/paperwork/goals.md` (search, one line) | You have the id and link |
| Which Step is open? | A search for `\| in progress \|` in `docs/paperwork/steps.md` | You have zero or one row |
| What does the Active Goal promise, and what is next? | The Active Goal file: its Result, Planned Steps, and Progress sections (headings, then ranges) | You have the next `not started` row |
| What did Step F-0xx require? | `docs/paperwork/steps/F-0xx.md`: its Requirements and Test map sections | You have the rows |
| Why was X decided? | `docs/paperwork/decisions.md` (search the topic), then that one ADR | You have the **Why** paragraph |
| What moved recently, and when? | The top of `## Structure` in `docs/paperwork/changelog.md` (read the first 20 lines) | You have the dated lines you need |
| What comes after this Goal? | `docs/paperwork/roadmap.md` | You have the next row |
| What is the next free id? | **Naming** of the paperwork blueprint for that kind ([../blueprints/paperwork/](../blueprints/paperwork/README.md)) | You have the rule, and one search that applies it |

## Scope and product

| Question | Open | You are done when |
|----------|------|-------------------|
| Is X in scope? | `docs/product/concept.md`: **In scope** and **What it is not** | You found X in **In scope**, in **What it is not**, or in neither (then it is out) |
| What is the product, for a person? | `docs/product/concept.md`: title and first paragraph | You can say it in one sentence |
| What does a person do with it? | `docs/product/journeys.md`, one journey section | You have the steps |
| What does domain word X mean? | `docs/product/glossary.md` (search the word) | You have the row |
| What is the rule for X in the domain? | `docs/product/wiki/README.md`, then the one page it names | You have the rule |
| What is a control called, or how does the screen look? | `docs/product/style-guide.md`, the one area section | You have the row |

## Architecture and code

| Question | Open | You are done when |
|----------|------|-------------------|
| Which modules exist, and which way do they depend? | `docs/architecture/program.md` | You have the module table and the diagram |
| What command runs the tests? | The **Witness command** line of `docs/architecture/program.md` (search, one line) | You have the command |
| Where do tests live, and how are they named? | The **Tests** line of `docs/architecture/program.md` | You have the rule |
| How do I find where a unit is declared? | The **Declarations** line of `docs/architecture/program.md` | You have the pattern and the folders |
| Which areas does the paper have? | The level table of `docs/architecture/README.md` | You have the area whose question matches yours |
| What does one run of area X do, in order? | `docs/architecture/<area>/README.md`, the level page | You have the stage list |
| How is mechanism X described? | The level page for its area, then the one mechanism page it names | You have the section you need |
| Where is the code for X? | [code.md](code.md) | You have a path and a line range |
| What does implementation word X mean? | The glossary that `docs/architecture/README.md` links (search the word) | You have the row |
| What is still undecided in the implementation? | The open-questions page that `docs/architecture/README.md` links | You have the **Still open** rows |
| Which tests witness Step F-0xx? | That Step record's Test map | You have the test names |
| Which Step does test T witness? | A search for T's class name in `docs/paperwork/steps/` | You have the Step ids |

## Repository state

| Question | Open | You are done when |
|----------|------|-------------------|
| What is the safe point? | `git log --format="%h %s"`, first subject matching `^Accept F-` or `^Seal: ` | You have one commit |
| What is unsealed? | `git status --porcelain`, and `git log --format="%h %s" <safe point>..HEAD` | You have the path list and the commit list |
| What did one commit change? | `git show --stat <hash>` | You have the file list |
| Where is a document I know only by name? | [../../navigation.md](../../navigation.md) (search the name) | You have its row |
| What must I never open? | **Heavy places** in [../../navigation.md](../../navigation.md) | You have the list |
