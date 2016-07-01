package org.ajm.laforkids.desktop

import org.ajm.laforkids.ColoredMultiplicationTable
import org.ajm.laforkids.GameLogic
import org.ajm.laforkids.IColoredMultiplicationTable
import org.ajm.laforkids.Settings
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


            for (i in 0 until gameLogic.maxProgress()) {
                Assert.assertFalse(gameLogic.isComplete())
                gameLogic.updateAnswerAlternatives()
                gameLogic.progress()
            }

            assertExceptionThrown {
                gameLogic.updateAnswerAlternatives()
            }

            Assert.assertTrue(gameLogic.isComplete())
        }

    }
}