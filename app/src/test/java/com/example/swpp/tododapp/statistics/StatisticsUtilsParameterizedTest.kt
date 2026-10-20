package com.example.swpp.tododapp.statistics

import com.example.swpp.tododapp.data.Task
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/** Appendix Exercise 2: the three StatisticsUtilsTest cases as one parameterized test. */
@RunWith(Parameterized::class)
class StatisticsUtilsParameterizedTest(
    private val name: String,
    private val input: List<Task>,
    private val expectedActive: Float,
    private val expectedCompleted: Float
) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): List<Array<Any?>> = listOf(
            arrayOf(
                "half half",
                listOf(Task("t1", "desc", true, "Test_id1"), Task("t2", "desc", false, "Test_id2")),
                50f, 50f
            ),
            arrayOf(
                "empty",
                emptyList<Task>(),
                0f, 0f
            ),
            arrayOf(
                "all completed",
                listOf(Task("t1", "desc", true, "Test_id1"), Task("t2", "desc", true, "Test_id2")),
                0f, 100f
            )
        )
    }

    @Test
    fun getActiveAndCompletedStats_returnsExpectedValues() {
        val result = getActiveAndCompletedStats(input)
        assertEquals(expectedActive, result.activeTasksPercent)
        assertEquals(expectedCompleted, result.completedTasksPercent)
    }
}
