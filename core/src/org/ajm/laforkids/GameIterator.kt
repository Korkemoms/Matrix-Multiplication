package org.ajm.laforkids


import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import org.ajm.laforkids.actors.IColoredMultiplicationTable
import org.ajm.laforkids.actors.Keypad
import org.ajm.laforkids.actors.VisualizedMultiplicationTable

/**
 * The progress of the game is implemented here: listeners are added to the different
 * buttons and labels so that the game can progress.
 */
class GameIterator {

    private var multiplicationTable: IColoredMultiplicationTable? = null
    private val settings: Settings
    var gameLogic: GameLogic
    private var main: Main? = null


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
     * @return whether the given answer was correct
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
    fun progress(answer: Int): Boolean {
        val correctAnswer = gameLogic.progress(answer)
        main!!.scoreLabel!!.score = gameLogic.score

        return correctAnswer
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
    fun init(multiplicationTable: VisualizedMultiplicationTable, main: Main, newGame: Boolean) {
        this.main = main
        val old = this.multiplicationTable
        this.multiplicationTable = multiplicationTable

        gameLogic.init(multiplicationTable)
        main.scoreLabel!!.score = gameLogic.score

        // set the entries of left and right matrices
        if (newGame) {
            multiplicationTable.init(settings.minValue, settings.maxValue)
            gameLogic.updateAnswerAlternatives()
        } else {
            multiplicationTable.init(old!!)
        }

        if (gameLogic.isComplete())
            newGameMessage()

        // add functionality to the answer buttons
        for (cell in multiplicationTable.matrixAnswers.cells) {
            val actor = cell.actor
            if (actor !is Label) continue

            actor.clearListeners()
            actor.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    val answer = actor.text.toString().toInt()
                    try {
                        val correctAnswer = progress(answer)
                        if (correctAnswer) {
                            val completed = gameLogic.isComplete()
                            if (completed)
                                newGameMessage()
                            else
                                gameLogic.updateAnswerAlternatives()

                            multiplicationTable.notifyChangeListeners()
                            main.scoreLabel!!.score = gameLogic.score
                        } else {
                            // wrong answer, hide label
                            actor.isVisible = false
                        }
                    } catch (e: Exception) {
                        // an entry may be just "-" (minus) if its currently being edited
                        Main.log("Trouble when user clicked on answer button.")
                        Main.log(e)
                    }
                }
            })

        }

        // add functionality to help button
        main.menu!!.helpLabel.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                try {
                    if (gameLogic.isComplete()) return

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

                } catch (e: Exception) {
                    Main.log(e)
                }
            }
        })

        // add functionality to next button
        main.menu!!.nextLabel.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                main.init(true, false)
            }
        })

        // let user change values in left and right matrix
        val entries = Array<Actor>()
        entries.addAll(multiplicationTable.matrixLeft.children)
        entries.addAll(multiplicationTable.matrixRight.children)

        var notIntegerLock = false


        for (actor in entries) {
            if (actor !is Label) continue

            actor.clearListeners()
            actor.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    if (notIntegerLock) return
                    if (gameLogic.isComplete()) return

                    // add TextField on top of cell
                    val textField = object : TextField(actor.text.toString(), main.skin) {
                        override fun draw(batch: Batch?, parentAlpha: Float) {
                            updateTextFieldPos()
                            super.draw(batch, parentAlpha)
                        }

                        fun updateTextFieldPos() {
                            val pos = actor.localToStageCoordinates(Vector2())
                            setPosition(pos.x, pos.y)
                            setSize(actor.width, actor.height)
                        }
                    }
                    textField.updateTextFieldPos()
                    main.stage.addActor(textField)

                    textField.setAlignment(Align.center)
                    textField.style.focusedFontColor = Color.WHITE
                    textField.style.font = actor.style.font
                    textField.style = textField.style
                    textField.maxLength = 6


                    main.stage.keyboardFocus = textField
                    textField.selectAll()

                    // replace keypad
                    textField.onscreenKeyboard = TextField.OnscreenKeyboard { }
                    val keypad = main.keypad
                    keypad.scrollIn(main.stage)


                    // scroll down if needed
                    val scrollPane = main.multiplicationTable!!.parent as ScrollPane
                    val pos = actor.localToAscendantCoordinates(multiplicationTable, Vector2())
                    scrollPane.scrollTo(pos.x, pos.y - keypad.height, 1f, 1f)

                    // filter for text input
                    val digitInput = TextField.TextFieldFilter { textField, c ->
                        c.isDigit() || (c.equals('-') && !textField.text.contains('-') && textField.cursorPosition == 0)
                    }
                    textField.textFieldFilter = digitInput

                    var textChanged = false
                    // change value in matrix
                    textField.addListener(object : ChangeListener() {
                        override fun changed(event: ChangeEvent?, _actor: Actor?) {
                            textChanged = true
                            if (textField.text.length > 0) {
                                actor.setText(textField.text)
                                multiplicationTable.notifyChangeListeners()
                            }
                            try { // determine if text is integer
                                notIntegerLock = false
                                textField.text.toInt()

                                gameLogic.updateAnswers()
                            } catch (e: NumberFormatException) {
                                notIntegerLock = true
                            }
                        }
                    })


                    // remove text field when click elsewhere
                    main.stage.addListener(object : ClickListener() {
                        override fun clicked(event: InputEvent?, x: Float, y: Float) {

                            fun removeIfNecessary() {
                                val hitActor = main.stage.hit(x, y, false)

                                if (hitActor == textField) return
                                if (keypad.contains(x, y)) return
                                if (notIntegerLock) return // do not allow to close if not integer

                                try {
                                    if (!gameLogic.isComplete() && textChanged)
                                        gameLogic.updateAnswerAlternatives()

                                    textField.remove()
                                    main.stage.removeListener(this)
                                    multiplicationTable.notifyChangeListeners()


                                    if (hitActor !is TextField){
                                        keypad.scrollOut()
                                        scrollPane.scrollPercentY = 0f
                                    }

                                } catch (e: IllegalStateException) {
                                    Main.log("Trouble removing text field:")
                                    //Main.log(e)
                                    throw e
                                }
                            }

                            Gdx.app.postRunnable { removeIfNecessary() }

                        }
                    })
                }
            })
        }
    }

    private fun newGameMessage() {
        multiplicationTable!!.clearListeners()
        Gdx.app.postRunnable {
            main!!.showMessage("Touch anywhere for new game", "", {
                main!!.init(true, false)
            })
        }
    }

    private fun parentheses(i: Int): String {
        if (i >= 0) return i.toString()
        return "($i)"
    }

}