# Spec: `DueDateUtils.kt`

> Post-conditions are the test oracle: write the expected behavior from intent, not from the code.
> Tag each post-condition [Unit] if its test runs on the JVM; otherwise tag it [Integration].
> End each valid case with `Tested by <test>` if an existing test checks it, or `Tip:` describing the test to write.

## `internal fun dueDateLabel(dueDate: LocalDate?, today: LocalDate, isCompleted: Boolean): String`

**Intent**: Returns the D-day label for one task in the list. A completed task always shows `"CLEAR"`; an active task with no due date shows nothing (`""`); an active task with a due date shows `"D-Day"` if it is due today, `"D-N"` if it is due in N days, and `"D+N"` if it is overdue by N days, where N is the **total number of calendar days** between `today` and `dueDate` (not only the day-of-month part of a period).

**Pre-conditions**
- `today` is a valid `LocalDate` (the caller passes the current date).
- `dueDate` is either `null` (the task has no due date) or a valid `LocalDate`, which may be before, on, or after `today`.

**Post-conditions**
- Success cases
  - Completed task with a due date returns `"CLEAR"` (the due date is ignored, even if overdue). `[Unit]`
    - Tip: Given dueDate = 2026-10-23, today = 2026-10-20, isCompleted = true / When dueDateLabel is called / Then it returns "CLEAR"
  - Completed task with an overdue due date returns `"CLEAR"`. `[Unit]`
    - Tip: Given dueDate = 2026-10-17, today = 2026-10-20, isCompleted = true / When dueDateLabel is called / Then it returns "CLEAR"
  - Completed task with no due date returns `"CLEAR"`. `[Unit]`
    - Tip: Given dueDate = null, today = 2026-10-20, isCompleted = true / When dueDateLabel is called / Then it returns "CLEAR"
  - Active task with no due date returns `""` (no label). `[Unit]`
    - Tip: Given dueDate = null, today = 2026-10-20, isCompleted = false / When dueDateLabel is called / Then it returns ""
  - Active task due today returns `"D-Day"`. `[Unit]`
    - Tip: Given dueDate = 2026-10-20, today = 2026-10-20, isCompleted = false / When dueDateLabel is called / Then it returns "D-Day"
  - Active task due in N days (N < 1 month) returns `"D-N"`. `[Unit]`
    - Tip: Given dueDate = 2026-10-23, today = 2026-10-20, isCompleted = false / When dueDateLabel is called / Then it returns "D-3"
  - Active task overdue by N days (N < 1 month) returns `"D+N"`. `[Unit]`
    - Tip: Given dueDate = 2026-10-17, today = 2026-10-20, isCompleted = false / When dueDateLabel is called / Then it returns "D+3"
  - Active task due in more than one month returns the total day count. `[Unit]`
    - Tip: Given dueDate = 2026-12-25, today = 2026-10-20, isCompleted = false / When dueDateLabel is called / Then it returns "D-66"
  - Active task due in exactly one year returns the total day count. `[Unit]`
    - Tip: Given dueDate = 2027-10-20, today = 2026-10-20, isCompleted = false / When dueDateLabel is called / Then it returns "D-365"
  - Active task overdue by more than one month returns the total day count. `[Unit]`
    - Tip: Given dueDate = 2026-08-20, today = 2026-10-20, isCompleted = false / When dueDateLabel is called / Then it returns "D+61"
- Error cases
  - None. Every combination of a valid `today`, a nullable `dueDate` and a boolean is valid input and returns one of the labels above. `[Unit]`
    - Tested by `DueDateUtilsTest` (every Success/Edge case is a direct call that must not throw)
- Edge cases
  - Boundary D-Day / D-N: due tomorrow returns `"D-1"`. `[Unit]`
    - Tip: Given dueDate = 2026-10-21, today = 2026-10-20, isCompleted = false / When dueDateLabel is called / Then it returns "D-1"
  - Boundary D-Day / D+N: due yesterday returns `"D+1"`. `[Unit]`
    - Tip: Given dueDate = 2026-10-19, today = 2026-10-20, isCompleted = false / When dueDateLabel is called / Then it returns "D+1"
  - Boundary "one month ahead" (just below): due in 30 days returns `"D-30"`. `[Unit]`
    - Tip: Given dueDate = 2026-11-19, today = 2026-10-20, isCompleted = false / When dueDateLabel is called / Then it returns "D-30"
  - Boundary "one month ahead" (exactly): due one calendar month later returns `"D-31"`. `[Unit]`
    - Tip: Given dueDate = 2026-11-20, today = 2026-10-20, isCompleted = false / When dueDateLabel is called / Then it returns "D-31"
  - Boundary "one month ahead" (just above): returns `"D-32"`. `[Unit]`
    - Tip: Given dueDate = 2026-11-21, today = 2026-10-20, isCompleted = false / When dueDateLabel is called / Then it returns "D-32"
  - Boundary "one month overdue" (just below): overdue by 29 days returns `"D+29"`. `[Unit]`
    - Tip: Given dueDate = 2026-09-21, today = 2026-10-20, isCompleted = false / When dueDateLabel is called / Then it returns "D+29"
  - Boundary "one month overdue" (exactly): returns `"D+30"`. `[Unit]`
    - Tip: Given dueDate = 2026-09-20, today = 2026-10-20, isCompleted = false / When dueDateLabel is called / Then it returns "D+30"
  - Boundary "one month overdue" (just above): returns `"D+31"`. `[Unit]`
    - Tip: Given dueDate = 2026-09-19, today = 2026-10-20, isCompleted = false / When dueDateLabel is called / Then it returns "D+31"
  - Month end: due on the 1st of the next month returns `"D-1"`. `[Unit]`
    - Tip: Given dueDate = 2026-02-01, today = 2026-01-31, isCompleted = false / When dueDateLabel is called / Then it returns "D-1"
  - Year end: due on 1 January of the next year returns `"D-1"`. `[Unit]`
    - Tip: Given dueDate = 2027-01-01, today = 2026-12-31, isCompleted = false / When dueDateLabel is called / Then it returns "D-1"
  - Leap day: counting across 29 February in a leap year returns `"D-2"`. `[Unit]`
    - Tip: Given dueDate = 2028-03-01, today = 2028-02-28, isCompleted = false / When dueDateLabel is called / Then it returns "D-2"

**Undefined behavior** (optional)
- None. `today` is always supplied by the caller, so the function is pure and fully specified for every input.
