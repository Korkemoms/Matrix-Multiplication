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

    private var multiplicationTable: ColoredMultiplicationTable? = null

    private val random = Random()
    private val prefs = Gdx.app.getPreferences("Matrix Multiplication");

    val SETTING_MIN_VALUE = "minValue"
    val SETTING_MAX_VALUE = "maxValue"
    val SETTING_MIN_ROWS_LEFT = "minRowsLeft"
    val SETTING_MAX_ROWS_LEFT = "maxRowsLeft"
    val SETTING_MIN_COLUMNS_LEFT = "minColumnsLeft"
    val SETTING_MAX_COLUMNS_LEFT = "maxColumnsLeft"
    val SETTING_MIN_COLUMNS_RIGHT = "minColumnsRight"
    val SETTING_MAX_COLUMNS_RIGHT = "maxColumnsRight"
    val SETTING_ANSWER_ALTERNATIVES = "answerAlternatives"
    val SETTING_ANSWER_MAX_ERROR = "answerMaxError"


    /** Smallest possible entry value. */
    var minValue = prefs.getInteger(SETTING_MIN_VALUE, -10)
        set(value) {
            if (value > maxValue) throw IllegalArgumentException()
            field = value;
            prefs.putInteger(SETTING_MIN_VALUE, value)
        }
    /** Largest possible entry value. */
    var maxValue = prefs.getInteger(SETTING_MAX_VALUE, 10)
        set(value) {
            if (value < minValue) throw IllegalArgumentException()
            field = value;
            prefs.putInteger(SETTING_MAX_VALUE, value)
        }

    /** Lowest possible number of rows in left matrix, A. */
    var minRowsLeft = prefs.getInteger(SETTING_MIN_ROWS_LEFT, 1)
        set(value) {
            if (value < 0 || value > maxRowsLeft) throw IllegalArgumentException()
            field = value;
            prefs.putInteger(SETTING_MIN_ROWS_LEFT, value)
        }
    /** Highest possible number of rows in left matrix, A. */
    var maxRowsLeft = prefs.getInteger(SETTING_MAX_ROWS_LEFT, 3)
        set(value) {
            if (value < 0 || value < minRowsLeft) throw IllegalArgumentException()
            field = value;
            prefs.putInteger(SETTING_MAX_ROWS_LEFT, value)
        }

    /** Lowest possible number of columns in left matrix, A. The number of rows in the right matrix B must be
     * the same. */
    var minColumnsLeft = prefs.getInteger(SETTING_MIN_COLUMNS_LEFT, 1)
        set(value) {
            if (value < 0 || value > maxColumnsLeft) throw IllegalArgumentException()
            field = value;
            prefs.putInteger(SETTING_MIN_COLUMNS_LEFT, value)
        }
    /** Highest possible number of columns in left matrix, A. The number of rows in the right matrix B must be
     * the same. */
    var maxColumnsLeft = prefs.getInteger(SETTING_MAX_COLUMNS_LEFT, 3)
        set(value) {
            if (value < 0 || value < minColumnsLeft) throw IllegalArgumentException()
            field = value;
            prefs.putInteger(SETTING_MAX_COLUMNS_LEFT, value)
        }

    /** Lowest possible number of rows in right matrix, B. */
    var minColumnsRight = prefs.getInteger(SETTING_MIN_COLUMNS_RIGHT, 1)
        set(value) {
            if (value < 0 || value > minColumnsRight) throw IllegalArgumentException()
            field = value;
            prefs.putInteger(SETTING_MIN_COLUMNS_RIGHT, value)
        }
    /** Highest possible number of rows in right matrix, B. */
    var maxColumnsRight = prefs.getInteger(SETTING_MAX_COLUMNS_RIGHT, 3)
        set(value) {
            if (value < 0 || value < minColumnsRight) throw IllegalArgumentException()
            field = value;
            prefs.putInteger(SETTING_MAX_COLUMNS_RIGHT, value)
        }

    /** The number of value to choose from when entering your answer. */
    var answerAlternatives = prefs.getInteger(SETTING_ANSWER_ALTERNATIVES, 3)
        set(value) {
            if (value < 0) throw IllegalArgumentException()
            field = value;
            prefs.putInteger(SETTING_ANSWER_ALTERNATIVES, value)
        }
    /** The maximum error of the answer alternatives. */
    var answerMaxError = prefs.getInteger(SETTING_ANSWER_MAX_ERROR, 10)
        set(value) {
            if (value < 0) throw IllegalArgumentException()
            field = value;
            prefs.putInteger(SETTING_ANSWER_MAX_ERROR, value)
        }

    /** The progress of the game, iterates over all the entries in the product matrix C. */
    var progress = 0
        set(value) {
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

    /** Must be called after updating settings to ensure they
     * persist after app is closed. */
    fun saveSettingsForever() {
        prefs.flush()
    }

    /**
     * [newGame] must be called before [connect].
     * */
    fun newGame() {
        rowsLeft = getRandomLeftRowCount()
        columnsLeft = getRandomLeftColumnCount()
        columnsRight = getRandomRightColumnCount()
    }

    private fun getRandomLeftRowCount() = random.nextInt(maxRowsLeft - minRowsLeft + 1) + minRowsLeft

    private fun getRandomLeftColumnCount() = random.nextInt(maxColumnsLeft - minColumnsLeft + 1) + minColumnsLeft

    private fun getRandomRightColumnCount() = random.nextInt(maxColumnsRight - minColumnsRight + 1) + minColumnsRight

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
            multiplicationTable.randomizeEntries(minValue, maxValue)
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
                                    progress = 0
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
                error = random.nextInt(answerMaxError * 2) - answerMaxError
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