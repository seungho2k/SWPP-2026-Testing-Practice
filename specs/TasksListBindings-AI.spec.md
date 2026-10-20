# Spec: `TasksListBindings.kt`

> Post-conditions are the test oracle: write the expected behavior from intent, not from the code.
> Tag each post-condition [Unit] if its test runs on the JVM; otherwise tag it [Integration].
> End each valid case with `Tested by <test>` if an existing test checks it, or `Tip:` describing the test to write.

## `@BindingAdapter("app:dueDateLabel") fun setDueDateLabel(textView: TextView, task: Task?)`

**Intent**: Binding adapter that sets the D-day label of a task row by calling `dueDateLabel(task.dueDate, LocalDate.now(), task.isCompleted)` and showing it in `textView`.

**Pre-conditions**
- `textView` is the `due_date_text` view of a task row.
- `task` may be null.

**Post-conditions**
- Success cases
  - Active task due in 3 days: `textView.text` is `"D-3"` and `textView` is `VISIBLE`. `[Unit]`
    - Tip: Given a TextView and Task(dueDate = today + 3, isCompleted = false) / When setDueDateLabel is called / Then text is "D-3" and visibility is VISIBLE
  - Completed task: `textView.text` is `"CLEAR"` and `textView` is `VISIBLE`. `[Unit]`
    - Tip: Given a TextView and Task(dueDate = today, isCompleted = true) / When setDueDateLabel is called / Then text is "CLEAR"
  - Active task with no due date: the label is empty, so `textView` is left unchanged. `[Unit]`
    - Tip: Given a TextView and Task(dueDate = null, isCompleted = false) / When setDueDateLabel is called / Then text is unchanged
- Error cases
  - None. `[Unit]`
    - Tip: Given task = null / When setDueDateLabel is called / Then it does not throw
- Edge cases
  - Active task due today: `textView.text` is `"D-Day"`. `[Unit]`
    - Tip: Given Task(dueDate = today, isCompleted = false) / When setDueDateLabel is called / Then text is "D-Day"
