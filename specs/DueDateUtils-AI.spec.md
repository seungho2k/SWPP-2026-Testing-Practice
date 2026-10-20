# Spec: `DueDateUtils.kt`

> Post-conditions are the test oracle: write the expected behavior from intent, not from the code.
> Tag each post-condition [Unit] if its test runs on the JVM; otherwise tag it [Integration].
> End each valid case with `Tested by <test>` if an existing test checks it, or `Tip:` describing the test to write.

## `internal fun dueDateLabel(dueDate: LocalDate?, today: LocalDate, isCompleted: Boolean): String`

**Intent**: Builds the D-day label shown next to a task in the list, based on the number of days between `today` and `dueDate` (`Period.between(today, dueDate).days`).

**Pre-conditions**
- `today` is not null.
- `dueDate` may be null (task has no due date).

**Post-conditions**
- Success cases
  - Completed task with a due date returns `"CLEAR"`. `[Unit]`
    - Tip: Given dueDate = 2026-10-23, today = 2026-10-20, isCompleted = true / When dueDateLabel is called / Then it returns "CLEAR"
  - Completed task with no due date returns `"CLEAR"`. `[Unit]`
    - Tip: Given dueDate = null, today = 2026-10-20, isCompleted = true / When dueDateLabel is called / Then it returns "CLEAR"
  - Active task with no due date returns `""`. `[Unit]`
    - Tip: Given dueDate = null, today = 2026-10-20, isCompleted = false / When dueDateLabel is called / Then it returns ""
  - Active task due today returns `"D-Day"`. `[Unit]`
    - Tip: Given dueDate = today = 2026-10-20, isCompleted = false / When dueDateLabel is called / Then it returns "D-Day"
  - Active task due in the future returns `"D-N"`. `[Unit]`
    - Tip: Given a due date 3 days after today / When dueDateLabel is called / Then it returns "D-3"
  - Active overdue task returns `"D+N"`. `[Unit]`
    - Tip: Given a due date 3 days before today / When dueDateLabel is called / Then it returns "D+3"
- Error cases
  - None. `[Unit]`
    - Tip: Given any combination of inputs / When dueDateLabel is called / Then it does not throw
- Edge cases
  - Due tomorrow returns `"D-1"`. `[Unit]`
    - Tip: Given dueDate = 2026-10-21, today = 2026-10-20, isCompleted = false / When dueDateLabel is called / Then it returns "D-1"
  - Due yesterday returns `"D+1"`. `[Unit]`
    - Tip: Given dueDate = 2026-10-19, today = 2026-10-20, isCompleted = false / When dueDateLabel is called / Then it returns "D+1"
  - Due date across a month end returns the correct count. `[Unit]`
    - Tip: Given dueDate = 2026-02-01, today = 2026-01-31, isCompleted = false / When dueDateLabel is called / Then it returns "D-1"
