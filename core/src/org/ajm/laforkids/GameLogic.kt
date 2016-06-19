package org.ajm.laforkids


import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Array
import java.util.*

/**
 * The rules of the game is defined and mostly implemented here.
 */
class GameLogic {

    constructor(settings: Settings) {
        this.settings = settings
    }

    val settings: Settings


    private var multiplicationTable: ColoredMultiplicationTable? = null

    private val random = Random()


    /** The progress of the game, iterates over all the entries in the product matrix C. */
    var progress = 0
        private set(value) {
            if (value < 0) throw IllegalArgumentException()
            field = value
        }

    var rowsLeft = 0
        private set(value) {
            field = value
        }
    var columnsLeft = 0
        private set(value) {
            field = value
        }
    var columnsRight = 0
        private set(value) {
            field = value
        }


    /**
     * [newGame] must be called before [connect].
     * */
    fun newGame(rowsLeft: Int = getRandomLeftRowCount(),
                columnsLeft: Int = getRandomLeftColumnCount(),
                columnsRight: Int = getRandomRightColumnCount()) {
        this.rowsLeft = rowsLeft
        this.columnsLeft = columnsLeft
        this.columnsRight = columnsRight
        progress = 0
        multiplicationTable = null
    }

    private fun getRandomLeftRowCount() = random.nextInt(settings.maxRowsLeft - settings.minRowsLeft + 1) + settings.minRowsLeft

    private fun getRandomLeftColumnCount() = random.nextInt(settings.maxColumnsLeft - settings.minColumnsLeft + 1) + settings.minColumnsLeft

    private fun getRandomRightColumnCount() = random.nextInt(settings.maxColumnsRight - settings.minColumnsRight + 1) + settings.minColumnsRight

    /**
     * Adds functionality to certain buttons. Prepares the entries of the matrices
     * and prepares the answers. The size of the matrices must match the sizes
     * determined by a previous [newGame] call.
     */
    fun connect(multiplicationTable: ColoredMultiplicationTable, main: Main, newGame: Boolean) {
        if (multiplicationTable.rowsLeft != rowsLeft || rowsLeft <= 0) throw IllegalArgumentException()
        if (multiplicationTable.columnsLeft != columnsLeft || columnsLeft <= 0) throw IllegalArgumentException()
        if (multiplicationTable.columnsRight != columnsRight || columnsRight <= 0) throw IllegalArgumentException()


        if (this.multiplicationTable == multiplicationTable) return
        val old = this.multiplicationTable
        this.multiplicationTable = multiplicationTable


        // set the entries of left and right matrices
        if (old != null && !newGame) {
            multiplicationTable.copyEntries(old)
        } else {
            multiplicationTable.randomizeEntries(settings.minValue, settings.maxValue)
            updateAnswerAlternatives()
        }


        multiplicationTable.highlightCol = getHighlightCol()
        multiplicationTable.highlightRow = getHighlightRow()


        // add functionality to the answer buttons
        for (cell in multiplicationTable.matrixAnswers.cells) {
            val actor = cell.actor
            if (actor is Label) {
                actor.addListener(object : ClickListener() {
                    override fun clicked(event: InputEvent?, x: Float, y: Float) {
                        val answer = actor.text.toString().toInt()

                        val correctAnswer = getCorrectAnswer()

                        if (answer.equals(correctAnswer)) {
                            // set entry of product matrix to the correct answer
                            multiplicationTable.matrixProduct.set(getHighlightRow(), getHighlightCol(), correctAnswer)

                            progress++
                            val completed = progress >= multiplicationTable.rowsLeft * multiplicationTable.columnsRight

                            Gdx.graphics.requestRendering()
                            Gdx.app.postRunnable({
                                if (completed) {
                                    // new game
                                    main.init(true, false)
                                }

                                // ask for answer to the next entry
                                multiplicationTable.highlightCol = getHighlightCol()
                                multiplicationTable.highlightRow = getHighlightRow()
                                updateAnswerAlternatives()
                                multiplicationTable.beginAnimation()
                            })
                        } else {
                            // wrong answer, hide label
                            actor.isVisible = false
                        }
                    }
                })
            }
        }

        // add functionality to help button
        main.menu!!.helpLabel.addListener (object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                var help = ""

                val i = getHighlightRow()
                val j = getHighlightCol()
                // convolve i'th left row with j'th right col
                for (k in 0 until multiplicationTable.columnsLeft) {
                    val a = multiplicationTable.matrixLeft.get(i, k).toInt()
                    val b = multiplicationTable.matrixRight.get(k, j).toInt()

                    help += parentheses(a) + "*" + parentheses(b) + "+"
                }
                help = help.dropLast(1)

                main.showMessage(help)
                main.menu!!.hideMenu()
            }
        })

        // add functionality to next button
        main.menu!!.nextLabel.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                progress = 0
                main.init(true, false)
            }
        })
    }

    private fun parentheses(i: Int): String {
        if (i >= 0) return i.toString()
        return "($i)"
    }

    private fun getHighlightRow() = progress % multiplicationTable!!.rowsLeft
    private fun getHighlightCol() = progress / multiplicationTable!!.rowsLeft


    /**
     * Update the answer alternatives found on the bottom
     * of the screen.
     */
    private fun updateAnswerAlternatives() {
        val correctAnswer = getCorrectAnswer()

        // make up some alternatives
        val errors = Array<Int>()
        for (i in 0 until multiplicationTable!!.answerAlternatives) {
            var error = 0;
            var j = 0
            while ((error == 0 || errors.contains(error, false)) && j++ < 10) {
                error = random.nextInt(Math.max(settings.answerMaxError * 2, 1)) - settings.answerMaxError
            }
            errors.add(error)
            multiplicationTable!!.matrixAnswers.set(0, i, correctAnswer + error)
        }

        // let one entry be the correct answer
        multiplicationTable!!.matrixAnswers.set(0, random.nextInt(multiplicationTable!!.answerAlternatives), correctAnswer)

    }

    /**
     * Compute the correct answer for currently highlighted entry.
     */
    private fun getCorrectAnswer(): Int {
        val i = getHighlightRow()
        val j = getHighlightCol()
        // convolve i'th left row with j'th right col
        var correctAnswer = 0
        for (k in 0 until multiplicationTable!!.columnsLeft) {
            val a = multiplicationTable!!.matrixLeft.get(i, k).toInt()
            val b = multiplicationTable!!.matrixRight.get(k, j).toInt()
            correctAnswer += a * b
        }
        return correctAnswer
    }
}