package org.ajm.laforkids.desktop.tests

import org.ajm.laforkids.Settings
import org.junit.Test
import org.junit.Assert.*
import java.util.*

/**
 * Tests for [Settings].
 */
class SettingsTest {

    val random = Random()


    @Test
    fun rejectImpossibleValues() {

        for (i in 0 until 100) {

            // test that illegal values cause exceptions
            // also ensure that illegal values can not be saved in the gdx preferences
            val k = random.nextInt(10) + 1

            var settings = Settings("Test Tag")
            assertEquals(0, settings.dataInvariant().size) // check that everything is ok
            val minValue = random.nextInt(k * 2) - k
            val maxValue = minValue - random.nextInt(k) - 1
            settings.minValue = minValue
            settings.maxValue = maxValue
            assertNotEquals(0, settings.dataInvariant().size) // check that the illegal values are detected
            assertFalse(settings.saveSettingsForever()) // check that the illegal values can not be saved

            settings = Settings("Test Tag")
            assertEquals(0, settings.dataInvariant().size) // check that the previous illegal values are gone
            val minRowsLeft = random.nextInt(k) + 1
            val maxRowsLeft = minRowsLeft - random.nextInt(k) - 1
            settings.minRowsLeft = minRowsLeft
            settings.maxRowsLeft = maxRowsLeft
            assertNotEquals(0, settings.dataInvariant().size) // check that the illegal values are detected
            assertFalse(settings.saveSettingsForever()) // check that the illegal values can not be saved

            settings = Settings("Test Tag")
            assertEquals(0, settings.dataInvariant().size) // check that the previous illegal values are gone
            val minColumnsLeft = random.nextInt(k) + 1
            val maxColumnsLeft = minColumnsLeft - random.nextInt(k) - 1
            settings.minColumnsLeft = minColumnsLeft
            settings.maxColumnsLeft = maxColumnsLeft
            assertNotEquals(0, settings.dataInvariant().size) // check that the illegal values are detected
            assertFalse(settings.saveSettingsForever()) // check that the illegal values can not be saved

            settings = Settings("Test Tag")
            assertEquals(0, settings.dataInvariant().size) // check that the previous illegal values are gone
            val minColumnsRight = random.nextInt(k) + 1
            val maxColumnsRight = minColumnsRight - random.nextInt(k) - 1
            settings.minColumnsRight = minColumnsRight
            settings.maxColumnsRight = maxColumnsRight
            assertNotEquals(0, settings.dataInvariant().size) // check that the illegal values are detected
            assertFalse(settings.saveSettingsForever()) // check that the illegal values can not be saved

            settings = Settings("Test Tag")
            assertEquals(0, settings.dataInvariant().size) // check that the previous illegal values are gone
            val answerAlternatives = -random.nextInt(k)
            settings.answerAlternatives = answerAlternatives
            assertNotEquals(0, settings.dataInvariant().size) // check that the illegal values are detected
            assertFalse(settings.saveSettingsForever()) // check that the illegal values can not be saved

            settings = Settings("Test Tag")
            assertEquals(0, settings.dataInvariant().size) // check that the previous illegal values are gone
            val answerMaxError = -random.nextInt(k) - 1
            settings.answerMaxError = answerMaxError
            assertNotEquals(0, settings.dataInvariant().size) // check that the illegal values are detected
            assertFalse(settings.saveSettingsForever()) // check that the illegal values can not be saved

        }
    }

    @Test
    fun setGetPersist() {

        for (i in 0 until 10) {

            // test set and get with values that should be legal
            var settings = Settings("Test Tag")
            val k = random.nextInt(500) + 1

            val minValue = random.nextInt(k * 2) - k
            val maxValue = minValue + random.nextInt(k)
            settings.minValue = minValue
            settings.maxValue = maxValue
            assertEquals(minValue, settings.minValue)
            assertEquals(maxValue, settings.maxValue)

            val minRowsLeft = random.nextInt(k) + 1
            val maxRowsLeft = minRowsLeft + random.nextInt(k)
            settings.minRowsLeft = minRowsLeft
            settings.maxRowsLeft = maxRowsLeft
            assertEquals(minRowsLeft, settings.minRowsLeft)
            assertEquals(maxRowsLeft, settings.maxRowsLeft)

            val minColumnsLeft = random.nextInt(k) + 1
            val maxColumnsLeft = minColumnsLeft + random.nextInt(k)
            settings.minColumnsLeft = minColumnsLeft
            settings.maxColumnsLeft = maxColumnsLeft
            assertEquals(minColumnsLeft, settings.minColumnsLeft)
            assertEquals(maxColumnsLeft, settings.maxColumnsLeft)

            val minColumnsRight = random.nextInt(k) + 1
            val maxColumnsRight = minColumnsRight + random.nextInt(k)
            settings.minColumnsRight = minColumnsRight
            settings.maxColumnsRight = maxColumnsRight
            assertEquals(minColumnsRight, settings.minColumnsRight)
            assertEquals(maxColumnsRight, settings.maxColumnsRight)

            val answerAlternatives = random.nextInt(k) + 1
            settings.answerAlternatives = answerAlternatives
            assertEquals(answerAlternatives, settings.answerAlternatives)

            val answerMaxError = random.nextInt(k)
            settings.answerMaxError = answerMaxError
            assertEquals(answerMaxError, settings.answerMaxError)

            settings.dataInvariant()
            settings.saveSettingsForever()


            // test that the values persist in the gdx preferences
            settings = Settings("Test Tag")

            assertEquals(minValue, settings.minValue)
            assertEquals(maxValue, settings.maxValue)

            assertEquals(minRowsLeft, settings.minRowsLeft)
            assertEquals(maxRowsLeft, settings.maxRowsLeft)

            assertEquals(minColumnsLeft, settings.minColumnsLeft)
            assertEquals(maxColumnsLeft, settings.maxColumnsLeft)

            assertEquals(minColumnsRight, settings.minColumnsRight)
            assertEquals(maxColumnsRight, settings.maxColumnsRight)

            assertEquals(answerAlternatives, settings.answerAlternatives)

            assertEquals(answerMaxError, settings.answerMaxError)

        }
    }
}