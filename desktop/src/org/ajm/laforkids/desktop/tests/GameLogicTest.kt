package org.ajm.laforkids.desktop.tests

import org.ajm.laforkids.actors.ColoredMultiplicationTable
import org.ajm.laforkids.GameLogic
import org.ajm.laforkids.desktop.DesktopLauncher
import org.junit.Assert
import org.junit.Test
import java.util.*

class GameLogicTest {

    private val random = Random()

    @Test
    fun run() {
        for (i in 0 until 10) {


            val settings = SettingsFactory("Test Tag 53").generateTestSettings()

            val gameLogic = GameLogic(settings)


            gameLogic.newGame()

            val multiplicationTable = ColoredMultiplicationTable(DesktopLauncher.skin!!,
                    gameLogic.rowsLeft, gameLogic.columnsLeft, gameLogic.columnsRight, gameLogic.answerAlternatives)

            multiplicationTable.init(settings.minValue, settings.maxValue)

            gameLogic.init(multiplicationTable)


            for (j in 0 until gameLogic.maxProgress()) {
                Assert.assertFalse(gameLogic.isComplete())
                gameLogic.updateAnswerAlternatives()
                gameLogic.progress(gameLogic.getCorrectAnswer())
            }

            assertExceptionThrown {
                gameLogic.updateAnswerAlternatives()
            }

            Assert.assertTrue(gameLogic.isComplete())
        }

    }
}