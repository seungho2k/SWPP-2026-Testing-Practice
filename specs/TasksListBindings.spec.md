# Spec: `TasksListBindings.kt`

> Post-conditions are the test oracle: write the expected behavior from intent, not from the code.
> Tag each post-condition [Unit] if its test runs on the JVM; otherwise tag it [Integration].
> End each valid case with `Tested by <test>` if an existing test checks it, or `Tip:` describing the test to write.

## `@BindingAdapter("app:dueDateLabel") fun setDueDateLabel(textView: TextView, task: Task?)`

**Intent**: Makes the `due_date_text` view of a task row show exactly the D-day label of the task it is **currently** bound to (computed with today's date): `"CLEAR"` for a completed task, `"D-Day"` / `"D-N"` / `"D+N"` for an active task with a due date, and **no label at all** for an active task with no due date or a null task. Because RecyclerView reuses rows and the list rebinds a row when a task is checked or unchecked, every call must fully overwrite whatever the view showed before.

"No label" means: the view shows no text, i.e. `textView.text` is `""` **or** `textView.visibility` is not `VISIBLE`.
"Shows label L" means: `textView.text == L` **and** `textView.visibility == VISIBLE`.

**Pre-conditions**
- `textView` is a `TextView` (in the app, `due_date_text` in `task_item.xml`); it may be fresh or may still show a label from an earlier bind.
- `task` may be null (data binding passes null before the variable is set).
- "today" is `LocalDate.now()` at the moment of the call; tests build due dates relative to it.

**Post-conditions**
- Success cases
  - Active task due in 3 days: shows `"D-3"`. `[Integration]`
    - Tip: Given a fresh TextView and Task("T", "D", false, dueDate = LocalDate.now().plusDays(3)) / When setDueDateLabel is called / Then text is "D-3" and visibility is VISIBLE
  - Active overdue task: shows `"D+3"`. `[Integration]`
    - Tip: Given a fresh TextView and Task("T", "D", false, dueDate = LocalDate.now().minusDays(3)) / When setDueDateLabel is called / Then text is "D+3" and visibility is VISIBLE
  - Completed task with a due date: shows `"CLEAR"`. `[Integration]`
    - Tip: Given a fresh TextView and Task("T", "D", true, dueDate = LocalDate.now().plusDays(3)) / When setDueDateLabel is called / Then text is "CLEAR" and visibility is VISIBLE
  - Completed task with no due date: shows `"CLEAR"`. `[Integration]`
    - Tip: Given a fresh TextView and Task("T", "D", true, dueDate = null) / When setDueDateLabel is called / Then text is "CLEAR" and visibility is VISIBLE
  - Active task with no due date on a fresh view: no label. `[Integration]`
    - Tip: Given a fresh TextView and Task("T", "D", false, dueDate = null) / When setDueDateLabel is called / Then text is "" or visibility is not VISIBLE
  - Null task: no label. `[Integration]`
    - Tip: Given a fresh TextView and task = null / When setDueDateLabel is called / Then text is "" or visibility is not VISIBLE
- Error cases
  - None. A null task is valid input (see Success cases) and must not throw. `[Integration]`
    - Tested by `TasksListBindingsTest.setDueDateLabel_nullTask_showsNoLabel`
- Edge cases
  - Boundary D-Day / D-N / D+N: due today shows `"D-Day"`, due tomorrow `"D-1"`, due yesterday `"D+1"`. `[Integration]`
    - Tip: Given a fresh TextView and Task("T", "D", false, dueDate = LocalDate.now()) / When setDueDateLabel is called / Then text is "D-Day" and visible; repeat with LocalDate.now().plusDays(1) → "D-1" and LocalDate.now().minusDays(1) → "D+1"
  - Rebind label → no label (completed, no due date → unchecked): a view showing `"CLEAR"` rebound to an active task with no due date shows no label. `[Integration]`
    - Tip: Given a TextView already bound to Task("T", "D", true, dueDate = null) (text "CLEAR") / When setDueDateLabel is called with Task("T", "D", false, dueDate = null) / Then text is "" or visibility is not VISIBLE
  - Rebind label → no label (due date cleared): a view showing `"D-3"` rebound to an active task with no due date shows no label. `[Integration]`
    - Tip: Given a TextView already bound to Task("T", "D", false, dueDate = LocalDate.now().plusDays(3)) (text "D-3") / When setDueDateLabel is called with Task("T", "D", false, dueDate = null) / Then text is "" or visibility is not VISIBLE
  - Rebind label → label: a view showing `"CLEAR"` rebound to the same task unchecked with a due date in 3 days shows `"D-3"`. `[Integration]`
    - Tip: Given a TextView already bound to Task("T", "D", true, dueDate = LocalDate.now().plusDays(3)) / When setDueDateLabel is called with Task("T", "D", false, dueDate = LocalDate.now().plusDays(3)) / Then text is "D-3" and visibility is VISIBLE
  - Rebind no label → label: a view that shows no label rebound to a completed task shows `"CLEAR"` (and becomes VISIBLE again, whatever way "no label" was implemented). `[Integration]`
    - Tip: Given a TextView already bound to Task("T", "D", false, dueDate = null) / When setDueDateLabel is called with Task("T", "D", true, dueDate = null) / Then text is "CLEAR" and visibility is VISIBLE
  - In the task list, checking then unchecking a task with no due date: label goes "" → `"CLEAR"` → no label. `[Integration]`
    - Tip: Given TasksFragment showing Task("NO_DUE", "D", false, dueDate = null) / When its checkbox is clicked, then clicked again / Then its due_date_text shows "CLEAR" after the first click and shows no label after the second
  - In the task list, checking then unchecking a task due in 3 days: label goes `"D-3"` → `"CLEAR"` → `"D-3"`. `[Integration]`
    - Tip: Given TasksFragment showing Task("DUE3", "D", false, dueDate = LocalDate.now().plusDays(3)) / When its checkbox is clicked, then clicked again / Then its due_date_text shows "D-3", then "CLEAR", then "D-3"

**Undefined behavior** (optional)
- A test that runs exactly across local midnight may see "today" change between building the task and binding it; tests do not guard against this.
- Whether "no label" is implemented as empty text or as `GONE`/`INVISIBLE` is left open; tests accept either.
