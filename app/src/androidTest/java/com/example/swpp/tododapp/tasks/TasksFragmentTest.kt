package com.example.swpp.tododapp.tasks

import android.os.Bundle
import android.view.View
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.Visibility
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.hasSibling
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.swpp.tododapp.R
import com.example.swpp.tododapp.ServiceLocator
import com.example.swpp.tododapp.data.Task
import com.example.swpp.tododapp.data.source.FakeAndroidTestRepository
import com.example.swpp.tododapp.data.source.TasksRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.anyOf
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
@MediumTest
@ExperimentalCoroutinesApi
class TasksFragmentTest {

    private lateinit var repository: TasksRepository

    @Before
    fun initRepository() {
        repository = FakeAndroidTestRepository()
        ServiceLocator.tasksRepository = repository
    }

    @After
    fun cleanupDb() = runTest {
        ServiceLocator.resetRepository()
    }

    // ---------- In-class exercise: navigation with a mocked NavController ----------

    @Test
    fun clickTask_navigateToDetailFragmentOne() = runTest {
        repository.saveTask(Task("TITLE1", "DESCRIPTION1", false, "id1"))
        repository.saveTask(Task("TITLE2", "DESCRIPTION2", true, "id2"))

        // GIVEN - On the home screen with two tasks
        val scenario = launchFragmentInContainer<TasksFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)
        scenario.onFragment { Navigation.setViewNavController(it.view!!, navController) }

        // WHEN - Click on the first list item
        onView(withId(R.id.tasks_list))
            .perform(
                RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
                    hasDescendant(withText("TITLE1")), click()
                )
            )

        // THEN - Verify that we navigate to the first detail screen
        verify(navController).navigate(
            TasksFragmentDirections.actionTasksFragmentToTaskDetailFragment("id1")
        )
    }

    // ---------- Spec: TasksListBindings.spec.md, list check/uncheck cases ----------

    private fun checkboxOf(title: String) =
        onView(allOf(withId(R.id.complete_checkbox), hasSibling(withText(title))))

    private fun dueLabelOf(title: String) =
        onView(allOf(withId(R.id.due_date_text), hasSibling(withText(title))))

    /** "No label": empty text, or the view is not VISIBLE. */
    private fun showsNoLabel(): Matcher<View> =
        anyOf(withText(""), not(withEffectiveVisibility(Visibility.VISIBLE)))

    private fun showsLabel(label: String): Matcher<View> =
        allOf(withText(label), isDisplayed())

    @Test
    fun dueDateLabel_checkThenUncheckTaskWithNoDueDate_clearThenNoLabel() = runTest {
        // GIVEN - the task list shows one active task without a due date
        repository.saveTask(Task("NO_DUE", "D", false, "id_no_due", dueDate = null))
        launchFragmentInContainer<TasksFragment>(Bundle(), R.style.AppTheme)
        dueLabelOf("NO_DUE").check(matches(showsNoLabel()))

        // WHEN - the task is checked
        checkboxOf("NO_DUE").perform(click())
        // THEN - the label is "CLEAR"
        dueLabelOf("NO_DUE").check(matches(showsLabel("CLEAR")))

        // WHEN - the task is unchecked again
        checkboxOf("NO_DUE").perform(click())
        // THEN - the label is gone (no stale "CLEAR")
        dueLabelOf("NO_DUE").check(matches(showsNoLabel()))
    }

    @Test
    fun dueDateLabel_checkThenUncheckTaskDueIn3Days_dMinus3ThenClearThenDMinus3() = runTest {
        // GIVEN - the task list shows one active task due in 3 days
        repository.saveTask(Task("DUE3", "D", false, "id_due3", dueDate = LocalDate.now().plusDays(3)))
        launchFragmentInContainer<TasksFragment>(Bundle(), R.style.AppTheme)
        dueLabelOf("DUE3").check(matches(showsLabel("D-3")))

        // WHEN - checked  THEN - "CLEAR"
        checkboxOf("DUE3").perform(click())
        dueLabelOf("DUE3").check(matches(showsLabel("CLEAR")))

        // WHEN - unchecked  THEN - back to "D-3"
        checkboxOf("DUE3").perform(click())
        dueLabelOf("DUE3").check(matches(showsLabel("D-3")))
    }
}
