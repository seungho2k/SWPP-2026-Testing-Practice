package com.example.swpp.tododapp.tasks

import android.view.View
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import com.example.swpp.tododapp.data.Task
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

/**
 * Direct-call tests for [setDueDateLabel], one test per Tip in specs/TasksListBindings.spec.md.
 * They need a real TextView, so they run as instrumented tests.
 */
@SmallTest
@RunWith(AndroidJUnit4::class)
class TasksListBindingsTest {

    private fun today(): LocalDate = LocalDate.now()

    /** Runs [block] on the main thread with a fresh TextView and returns that view. */
    private fun withFreshTextView(block: (TextView) -> Unit): TextView {
        lateinit var textView: TextView
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            textView = TextView(ApplicationProvider.getApplicationContext())
            block(textView)
        }
        return textView
    }

    private fun assertShowsLabel(expected: String, textView: TextView) {
        assertEquals(expected, textView.text.toString())
        assertEquals(View.VISIBLE, textView.visibility)
    }

    /** "No label": empty text, or the view is not VISIBLE. */
    private fun assertShowsNoLabel(textView: TextView) {
        assertTrue(
            "Expected no label but view shows \"${textView.text}\" (visibility=${textView.visibility})",
            textView.text.isEmpty() || textView.visibility != View.VISIBLE
        )
    }

    // ---------- Success cases ----------

    @Test
    fun setDueDateLabel_activeDueIn3Days_showsDMinus3() {
        // GIVEN - an active task due in 3 days
        val task = Task("T", "D", false, dueDate = today().plusDays(3))
        // WHEN - the label is bound to a fresh view
        val view = withFreshTextView { setDueDateLabel(it, task) }
        // THEN - "D-3" is shown
        assertShowsLabel("D-3", view)
    }

    @Test
    fun setDueDateLabel_activeOverdueBy3Days_showsDPlus3() {
        val task = Task("T", "D", false, dueDate = today().minusDays(3))
        val view = withFreshTextView { setDueDateLabel(it, task) }
        assertShowsLabel("D+3", view)
    }

    @Test
    fun setDueDateLabel_completedWithDueDate_showsClear() {
        val task = Task("T", "D", true, dueDate = today().plusDays(3))
        val view = withFreshTextView { setDueDateLabel(it, task) }
        assertShowsLabel("CLEAR", view)
    }

    @Test
    fun setDueDateLabel_completedNoDueDate_showsClear() {
        val task = Task("T", "D", true, dueDate = null)
        val view = withFreshTextView { setDueDateLabel(it, task) }
        assertShowsLabel("CLEAR", view)
    }

    @Test
    fun setDueDateLabel_activeNoDueDate_showsNoLabel() {
        val task = Task("T", "D", false, dueDate = null)
        val view = withFreshTextView { setDueDateLabel(it, task) }
        assertShowsNoLabel(view)
    }

    @Test
    fun setDueDateLabel_nullTask_showsNoLabel() {
        // GIVEN / WHEN - a null task is bound (must not throw)
        val view = withFreshTextView { setDueDateLabel(it, null) }
        // THEN - no label
        assertShowsNoLabel(view)
    }

    // ---------- Edge cases: D-Day boundary ----------

    @Test
    fun setDueDateLabel_activeDueToday_showsDDay() {
        val view = withFreshTextView { setDueDateLabel(it, Task("T", "D", false, dueDate = today())) }
        assertShowsLabel("D-Day", view)
    }

    @Test
    fun setDueDateLabel_activeDueTomorrow_showsDMinus1() {
        val view = withFreshTextView { setDueDateLabel(it, Task("T", "D", false, dueDate = today().plusDays(1))) }
        assertShowsLabel("D-1", view)
    }

    @Test
    fun setDueDateLabel_activeDueYesterday_showsDPlus1() {
        val view = withFreshTextView { setDueDateLabel(it, Task("T", "D", false, dueDate = today().minusDays(1))) }
        assertShowsLabel("D+1", view)
    }

    // ---------- Edge cases: rebinding a reused view ----------

    @Test
    fun setDueDateLabel_rebindClearToActiveNoDueDate_showsNoLabel() {
        // GIVEN - a view already showing "CLEAR" for a completed task with no due date
        val view = withFreshTextView {
            setDueDateLabel(it, Task("T", "D", true, "id1", dueDate = null))
            // WHEN - the same task is unchecked and rebound
            setDueDateLabel(it, Task("T", "D", false, "id1", dueDate = null))
        }
        // THEN - the stale "CLEAR" is gone
        assertShowsNoLabel(view)
    }

    @Test
    fun setDueDateLabel_rebindDMinus3ToNoDueDate_showsNoLabel() {
        val view = withFreshTextView {
            setDueDateLabel(it, Task("T", "D", false, "id1", dueDate = today().plusDays(3)))
            setDueDateLabel(it, Task("T", "D", false, "id1", dueDate = null))
        }
        assertShowsNoLabel(view)
    }

    @Test
    fun setDueDateLabel_rebindClearToActiveDueIn3Days_showsDMinus3() {
        val due = today().plusDays(3)
        val view = withFreshTextView {
            setDueDateLabel(it, Task("T", "D", true, "id1", dueDate = due))
            setDueDateLabel(it, Task("T", "D", false, "id1", dueDate = due))
        }
        assertShowsLabel("D-3", view)
    }

    @Test
    fun setDueDateLabel_rebindNoLabelToCompleted_showsClear() {
        val view = withFreshTextView {
            setDueDateLabel(it, Task("T", "D", false, "id1", dueDate = null))
            setDueDateLabel(it, Task("T", "D", true, "id1", dueDate = null))
        }
        assertShowsLabel("CLEAR", view)
    }
}
