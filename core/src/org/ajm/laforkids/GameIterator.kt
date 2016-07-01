package org.ajm.laforkids


import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import java.util.*

/**
 * The progress of the game is implemented here.
 */
class GameIterator {

    private var multiplicationTable: IColoredMultiplicationTable? = null
    private val settings: Settings
    var gameLogic: GameLogic

    constructor(settings: Settings, gameLogic: GameLogic = GameLogic(settings)) {
        this.settings = settings
        this.gameLogic = gameLogic
    }

    /**
     * [newGame] must be called before [init].
     *
     * 1. [newGame]
     * 2. [init]
     * 3. [progress]
     * 4. [progress]
     * 5. ...
     * 6. [newGame]
     * 7. [init]
     * 8. ...
     * */
    fun newGame(rowsLeft: Int = gameLogic.getRandomLeftRowCount(),
                columnsLeft: Int = gameLogic.getRandomLeftColumnCount(),
                columnsRight: Int = gameLogic.getRandomRightColumnCount()) {
        gameLogic.newGame(rowsLeft, columnsLeft, columnsRight)
    }

    /**
     * @return whether the game is over, ie. the product matrix is completely filled in.
     *
     * 1. [newGame]
     * 2. [init]
     * 3. [progress]
     * 4. [progress]
     * 5. ...
     * 6. [newGame]
     * 7. [init]
     * 8. ...
     */
    fun progress(): Boolean {
        val completed = gameLogic.progress()

        if (!completed) {
            gameLogic.updateAnswerAlternatives()
        }
        return completed
    }

    /**
     * Adds functionality to certain buttons. Prepares the entries of the matrices
     * and prepares the answers. The size of the matrices must match the sizes
     * determined by a previous [newGame] call.
     *
     * 1. [newGame]
     * 2. [init]
     * 3. [progress]
     * 4. [progress]
     * 5. ...
     * 6. [newGame]
     * 7. [init]
     * 8. ...
     */
    fun init(multiplicationTable: IColoredMultiplicationTable, main: Main, newGame: Boolean) {

        val old = this.multiplicationTable
        this.multiplicationTable = multiplicationTable

        gameLogic.init(multiplicationTable)

        // set the entries of left and right matrices
        if (newGame) {
            multiplicationTable.init(settings.minValue, settings.maxValue)
            gameLogic.updateAnswerAlternatives()
        } else {
            multiplicationTable.init(old!!)
        }

        if (gameLogic.isComplete())
            newGameMessage(main)

        // add functionality to the answer buttons
        for (cell in multiplicationTable.matrixAnswers.cells) {
            val actor = cell.actor
            if (actor is Label) {
                actor.clearListeners()
                actor.addListener(object : ClickListener() {
                    override fun clicked(event: InputEvent?, x: Float, y: Float) {
                        val answer = actor.text.toString().toInt()

                        val correctAnswer = gameLogic.getCorrectAnswer()

                        if (answer.equals(correctAnswer)) {
                            val completed = progress()
                            if (completed) {
                                newGameMessage(main)
                            }
                        } else {
                            // wrong answer, hide label
                            actor.isVisible = false
                        }
                    }
                })
            }
        }

        // add functionality to help button
        main.menu!!.helpLabel.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                var help = ""

                val i = gameLogic.getHighlightRow()
                val j = gameLogic.getHighlightCol()
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
                main.init(true, false)
            }
        })

        // let user change values in left and right matrix
        val entries = Array<Cell<*>>()
        entries.addAll(multiplicationTable.matrixLeft.cells)
        entries.addAll(multiplicationTable.matrixRight.cells)

        for (cell in entries) {
            cell as Cell<Label>

            cell.actor.clearListeners()
            cell.actor.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {

                    // add TextField on top of cell
                    val textField = TextField(cell.actor.text.toString(), main.skin)
                    main.stage.addActor(textField)
                    val pos = cell.actor.localToStageCoordinates(Vector2())
                    textField.setPosition(pos.x, pos.y)
                    textField.setSize(cell.actorWidth, cell.actorHeight)
                    textField.setAlignment(Align.center)
                    textField.style.focusedFontColor = Color.WHITE
                    textField.style.font = cell.actor.style.font
                    textField.style = textField.style
                    main.stage.keyboardFocus = textField
                    textField.selectAll()

                    // filter for text input
                    val digitInput = TextField.TextFieldFilter { textField, c -> c.isDigit() || c.equals('-') }
                    textField.textFieldFilter = digitInput

                    // change value in matrix
                    textField.addListener(object : ChangeListener() {
                        override fun changed(event: ChangeEvent?, actor: Actor?) {
                            if (textField.text.length > 0)
                                cell.actor.setText(textField.text)
                        }
                    })

                    // remove text field when click elsewhere
                    main.stage.addListener(object : ClickListener() {
                        override fun clicked(event: InputEvent?, x: Float, y: Float) {
                            if (main.stage.hit(x, y, true) != textField) {
                                textField.remove()
                                gameLogic.updateAnswerAlternatives()
                                main.stage.removeListener(this)
                            }
                        }
                    })
                }
            })
        }
    }

    private fun newGameMessage(main: Main) {
        Gdx.app.postRunnable {
            main.showMessage("Touch anywhere for new game", "", {
                main.init(true, false)
            })
        }
    }

    private fun parentheses(i: Int): String {
        if (i >= 0) return i.toString()
        return "($i)"
    }

}