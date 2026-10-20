package com.example.swpp.tododapp.tasks

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

/**
 * Unit tests for [dueDateLabel], one test per Tip in specs/DueDateUtils.spec.md.
 * The oracle (expected label) comes from the spec, not from the implementation.
 */
class DueDateUtilsTest {

    @Test
    fun dueDateLabel_completedWithDueDate_returnsClear() {
        // GIVEN - a completed task, due 2026-10-23, today is 2026-10-20
        val dueDate: LocalDate? = LocalDate.of(2026, 10, 23)
        val today = LocalDate.of(2026, 10, 20)

        // WHEN - the label is built
        val result = dueDateLabel(dueDate, today, isCompleted = true)

        // THEN - it is "CLEAR"
        assertEquals("CLEAR", result)
    }

    @Test
    fun dueDateLabel_completedOverdue_returnsClear() {
        // GIVEN - a completed task, due 2026-10-17, today is 2026-10-20
        val dueDate: LocalDate? = LocalDate.of(2026, 10, 17)
        val today = LocalDate.of(2026, 10, 20)

        // WHEN - the label is built
        val result = dueDateLabel(dueDate, today, isCompleted = true)

        // THEN - it is "CLEAR"
        assertEquals("CLEAR", result)
    }

    @Test
    fun dueDateLabel_completedNoDueDate_returnsClear() {
        // GIVEN - a completed task, due never, today is 2026-10-20
        val dueDate: LocalDate? = null
        val today = LocalDate.of(2026, 10, 20)

        // WHEN - the label is built
        val result = dueDateLabel(dueDate, today, isCompleted = true)

        // THEN - it is "CLEAR"
        assertEquals("CLEAR", result)
    }

    @Test
    fun dueDateLabel_activeNoDueDate_returnsEmpty() {
        // GIVEN - an active task, due never, today is 2026-10-20
        val dueDate: LocalDate? = null
        val today = LocalDate.of(2026, 10, 20)

        // WHEN - the label is built
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - it is ""
        assertEquals("", result)
    }

    @Test
    fun dueDateLabel_activeDueToday_returnsDDay() {
        // GIVEN - an active task, due 2026-10-20, today is 2026-10-20
        val dueDate: LocalDate? = LocalDate.of(2026, 10, 20)
        val today = LocalDate.of(2026, 10, 20)

        // WHEN - the label is built
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - it is "D-Day"
        assertEquals("D-Day", result)
    }

    @Test
    fun dueDateLabel_activeDueIn3Days_returnsDMinus3() {
        // GIVEN - an active task, due 2026-10-23, today is 2026-10-20
        val dueDate: LocalDate? = LocalDate.of(2026, 10, 23)
        val today = LocalDate.of(2026, 10, 20)

        // WHEN - the label is built
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - it is "D-3"
        assertEquals("D-3", result)
    }

    @Test
    fun dueDateLabel_activeOverdueBy3Days_returnsDPlus3() {
        // GIVEN - an active task, due 2026-10-17, today is 2026-10-20
        val dueDate: LocalDate? = LocalDate.of(2026, 10, 17)
        val today = LocalDate.of(2026, 10, 20)

        // WHEN - the label is built
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - it is "D+3"
        assertEquals("D+3", result)
    }

    @Test
    fun dueDateLabel_activeDueIn66Days_returnsDMinus66() {
        // GIVEN - an active task, due 2026-12-25, today is 2026-10-20
        val dueDate: LocalDate? = LocalDate.of(2026, 12, 25)
        val today = LocalDate.of(2026, 10, 20)

        // WHEN - the label is built
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - it is "D-66"
        assertEquals("D-66", result)
    }

    @Test
    fun dueDateLabel_activeDueInOneYear_returnsDMinus365() {
        // GIVEN - an active task, due 2027-10-20, today is 2026-10-20
        val dueDate: LocalDate? = LocalDate.of(2027, 10, 20)
        val today = LocalDate.of(2026, 10, 20)

        // WHEN - the label is built
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - it is "D-365"
        assertEquals("D-365", result)
    }

    @Test
    fun dueDateLabel_activeOverdueBy61Days_returnsDPlus61() {
        // GIVEN - an active task, due 2026-08-20, today is 2026-10-20
        val dueDate: LocalDate? = LocalDate.of(2026, 8, 20)
        val today = LocalDate.of(2026, 10, 20)

        // WHEN - the label is built
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - it is "D+61"
        assertEquals("D+61", result)
    }

    @Test
    fun dueDateLabel_activeDueTomorrow_returnsDMinus1() {
        // GIVEN - an active task, due 2026-10-21, today is 2026-10-20
        val dueDate: LocalDate? = LocalDate.of(2026, 10, 21)
        val today = LocalDate.of(2026, 10, 20)

        // WHEN - the label is built
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - it is "D-1"
        assertEquals("D-1", result)
    }

    @Test
    fun dueDateLabel_activeDueYesterday_returnsDPlus1() {
        // GIVEN - an active task, due 2026-10-19, today is 2026-10-20
        val dueDate: LocalDate? = LocalDate.of(2026, 10, 19)
        val today = LocalDate.of(2026, 10, 20)

        // WHEN - the label is built
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - it is "D+1"
        assertEquals("D+1", result)
    }

    @Test
    fun dueDateLabel_activeDueIn30Days_returnsDMinus30() {
        // GIVEN - an active task, due 2026-11-19, today is 2026-10-20
        val dueDate: LocalDate? = LocalDate.of(2026, 11, 19)
        val today = LocalDate.of(2026, 10, 20)

        // WHEN - the label is built
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - it is "D-30"
        assertEquals("D-30", result)
    }

    @Test
    fun dueDateLabel_activeDueInOneMonth_returnsDMinus31() {
        // GIVEN - an active task, due 2026-11-20, today is 2026-10-20
        val dueDate: LocalDate? = LocalDate.of(2026, 11, 20)
        val today = LocalDate.of(2026, 10, 20)

        // WHEN - the label is built
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - it is "D-31"
        assertEquals("D-31", result)
    }

    @Test
    fun dueDateLabel_activeDueIn32Days_returnsDMinus32() {
        // GIVEN - an active task, due 2026-11-21, today is 2026-10-20
        val dueDate: LocalDate? = LocalDate.of(2026, 11, 21)
        val today = LocalDate.of(2026, 10, 20)

        // WHEN - the label is built
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - it is "D-32"
        assertEquals("D-32", result)
    }

    @Test
    fun dueDateLabel_activeOverdueBy29Days_returnsDPlus29() {
        // GIVEN - an active task, due 2026-09-21, today is 2026-10-20
        val dueDate: LocalDate? = LocalDate.of(2026, 9, 21)
        val today = LocalDate.of(2026, 10, 20)

        // WHEN - the label is built
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - it is "D+29"
        assertEquals("D+29", result)
    }

    @Test
    fun dueDateLabel_activeOverdueByOneMonth_returnsDPlus30() {
        // GIVEN - an active task, due 2026-09-20, today is 2026-10-20
        val dueDate: LocalDate? = LocalDate.of(2026, 9, 20)
        val today = LocalDate.of(2026, 10, 20)

        // WHEN - the label is built
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - it is "D+30"
        assertEquals("D+30", result)
    }

    @Test
    fun dueDateLabel_activeOverdueBy31Days_returnsDPlus31() {
        // GIVEN - an active task, due 2026-09-19, today is 2026-10-20
        val dueDate: LocalDate? = LocalDate.of(2026, 9, 19)
        val today = LocalDate.of(2026, 10, 20)

        // WHEN - the label is built
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - it is "D+31"
        assertEquals("D+31", result)
    }

    @Test
    fun dueDateLabel_activeDueAcrossMonthEnd_returnsDMinus1() {
        // GIVEN - an active task, due 2026-02-01, today is 2026-01-31
        val dueDate: LocalDate? = LocalDate.of(2026, 2, 1)
        val today = LocalDate.of(2026, 1, 31)

        // WHEN - the label is built
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - it is "D-1"
        assertEquals("D-1", result)
    }

    @Test
    fun dueDateLabel_activeDueAcrossYearEnd_returnsDMinus1() {
        // GIVEN - an active task, due 2027-01-01, today is 2026-12-31
        val dueDate: LocalDate? = LocalDate.of(2027, 1, 1)
        val today = LocalDate.of(2026, 12, 31)

        // WHEN - the label is built
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - it is "D-1"
        assertEquals("D-1", result)
    }

    @Test
    fun dueDateLabel_activeDueAcrossLeapDay_returnsDMinus2() {
        // GIVEN - an active task, due 2028-03-01, today is 2028-02-28
        val dueDate: LocalDate? = LocalDate.of(2028, 3, 1)
        val today = LocalDate.of(2028, 2, 28)

        // WHEN - the label is built
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - it is "D-2"
        assertEquals("D-2", result)
    }
}
