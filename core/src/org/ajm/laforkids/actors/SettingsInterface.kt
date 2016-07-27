package org.ajm.laforkids.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import org.ajm.laforkids.Main
import org.ajm.laforkids.animate
import org.ajm.laforkids.getAllChildren

/**
 * Interface for adjusting the settings defined in [Settings].
 */
class SettingsInterface : ScrollPane {

    /** Stuff to do when ok is clicked. */
    var onSaved = Runnable { }

    /** Stuff to do when cancel is clicked. */
    var onCancel = Runnable { }

    /**
     * Interface for adjusting the settings defined in GameLogic.
     */
    constructor(main: Main) : super(Table()) {

        val s = main.settings
        val skin = main.skin

        // filter for text input
        val digitInput = TextField.TextFieldFilter { textField, c -> c.isDigit() || c.equals('-') }

        // create the text fields for input
        val minValue = TextField(s.minValue.toString(), skin)
        minValue.textFieldFilter = digitInput
        val maxValue = TextField(s.maxValue.toString(), skin)
        maxValue.textFieldFilter = digitInput

        val minRowsLeft = TextField(s.minRowsLeft.toString(), skin)
        minRowsLeft.textFieldFilter = digitInput
        val maxRowsLeft = TextField(s.maxRowsLeft.toString(), skin)
        maxRowsLeft.textFieldFilter = digitInput

        val minColumnsLeft = TextField(s.minColumnsLeft.toString(), skin)
        minColumnsLeft.textFieldFilter = digitInput
        val maxColumnsLeft = TextField(s.maxColumnsLeft.toString(), skin)
        maxColumnsLeft.textFieldFilter = digitInput

        val minColumnsRight = TextField(s.minColumnsRight.toString(), skin)
        minColumnsRight.textFieldFilter = digitInput
        val maxColumnsRight = TextField(s.maxColumnsRight.toString(), skin)
        maxColumnsRight.textFieldFilter = digitInput

        val answerAlternatives = TextField(s.answerAlternatives.toString(), skin)
        answerAlternatives.textFieldFilter = digitInput
        val answerMaxError = TextField(s.answerMaxError.toString(), skin)
        answerMaxError.textFieldFilter = digitInput

        // create the labels
        val labelValue = Label("Value", skin)
        val labelRowsLeft = Label("Rows left", skin)
        val labelColumnsLeft = Label("Columns left", skin)
        val labelColumnsRight = Label("Columns right", skin)
        val labelAnswerAlternatives = Label("Answer alternatives", skin)
        val labelAnswerMaxError = Label("Alternative error", skin)


        // put labels and text fields in a table
        val pad = 3f
        val tableSettings = Table()

        var width = Math.min(Gdx.graphics.width / 3f, labelRowsLeft.width)

        var aux = Table()
        aux.add(Label("Min", skin)).width(width).pad(pad)
        aux.add(Label("Max", skin)).width(width).pad(pad)
        tableSettings.add(aux).padBottom(pad * 5).align(Align.left).row()

        tableSettings.add(labelValue).pad(pad).align(Align.left).row()
        aux = Table()
        aux.add(minValue).width(width).pad(pad)
        aux.add(maxValue).width(width).pad(pad)
        tableSettings.add(aux).padBottom(pad * 5).align(Align.left).row()

        tableSettings.add(labelRowsLeft).pad(pad).align(Align.left).row()
        aux = Table()
        aux.add(minRowsLeft).width(width).pad(pad)
        aux.add(maxRowsLeft).width(width).pad(pad)
        tableSettings.add(aux).padBottom(pad * 5).align(Align.left).row()

        tableSettings.add(labelColumnsLeft).pad(pad).align(Align.left).row()
        aux = Table()
        aux.add(minColumnsLeft).width(width).pad(pad)
        aux.add(maxColumnsLeft).width(width).pad(pad)
        tableSettings.add(aux).padBottom(pad * 5).align(Align.left).row()

        tableSettings.add(labelColumnsRight).pad(pad).align(Align.left).row()
        aux = Table()
        aux.add(minColumnsRight).width(width).pad(pad)
        aux.add(maxColumnsRight).width(width).pad(pad)
        tableSettings.add(aux).padBottom(pad * 5).align(Align.left).row()

        tableSettings.add(labelAnswerAlternatives).pad(pad).align(Align.left).row()
        aux = Table()
        aux.add(answerAlternatives).width(width).pad(pad)
        tableSettings.add(aux).padBottom(pad * 5).align(Align.left).row()

        tableSettings.add(labelAnswerMaxError).pad(pad).align(Align.left).row()
        aux = Table()
        aux.add(answerMaxError).width(width).pad(pad)
        tableSettings.add(aux).padBottom(pad * 5).align(Align.left).row()

        // replace onscreen keyboard

        for (actor in getAllChildren(tableSettings)) {
            if (actor !is TextField) continue
            actor.onscreenKeyboard = TextField.OnscreenKeyboard { }


            actor.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    actor.selectAll()


                    val keypad = main.keypad

                    Gdx.app.postRunnable { keypad.scrollIn(main.stage) }

                    // scroll down if needed
                    val scrollPane = this@SettingsInterface
                    val pos = actor.localToAscendantCoordinates(tableSettings.parent, Vector2())
                    scrollPane.scrollTo(pos.x, pos.y - keypad.height, 1f, 1f)

                    // listener for removing the keypad
                    stage.addListener(object : ClickListener() {
                        override fun clicked(event: InputEvent?, x: Float, y: Float) {
                            val hitActor = main.stage.hit(x, y, true)

                            if (hitActor == actor) return
                            if (keypad.contains(x, y)) return
                            if (hitActor is TextField) return

                            main.stage.removeListener(this)
                            keypad.scrollOut()

                            actor.clearSelection()

                        }
                    })
                }
            })
        }


        // prepare ok and cancel buttons
        val ok = TextButton("Save", skin)
        ok.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {

                // try to save settings
                s.minValue = minValue.text.toInt()
                s.maxValue = maxValue.text.toInt()
                s.minRowsLeft = minRowsLeft.text.toInt()
                s.maxRowsLeft = maxRowsLeft.text.toInt()
                s.minColumnsLeft = minColumnsLeft.text.toInt()
                s.maxColumnsLeft = maxColumnsLeft.text.toInt()
                s.minColumnsRight = minColumnsRight.text.toInt()
                s.maxColumnsRight = maxColumnsRight.text.toInt()
                s.answerAlternatives = answerAlternatives.text.toInt()
                s.answerMaxError = answerMaxError.text.toInt()

                val errors = s.dataInvariant()


                if (errors.size == 0) {
                    // all is ok
                    s.saveSettingsForever()
                    onSaved.run()
                } else {
                    // mark the illegal values
                    labelValue.setText("Value" + if (errors.contains(0, false)) "***" else "")
                    labelRowsLeft.setText("Rows left" + if (errors.contains(1, false)) "***" else "")
                    labelColumnsLeft.setText("Columns left" + if (errors.contains(2, false)) "***" else "")
                    labelColumnsRight.setText("Columns right" + if (errors.contains(3, false)) "***" else "")
                    labelAnswerAlternatives.setText("Answer alternatives" + if (errors.contains(4, false)) "***" else "")
                    labelAnswerMaxError.setText("Alternative error" + if (errors.contains(5, false)) "***" else "")

                }

            }
        })

        val cancel = TextButton("Cancel", skin)
        cancel.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                s.reload()
                onCancel.run()
            }
        })

        // ok and cancel button in their own table
        val tableButtons = Table()
        width = Math.max(width, cancel.width)
        tableButtons.add(cancel).width(width).pad(width * 0.1f)
        tableButtons.add(ok).width(width).pad(width * 0.1f)

        // put it all in the main table
        val table = children.first() as Table
        table.add(tableSettings).row()
        table.add(tableButtons)
        table.padBottom(Gdx.graphics.width / 3f)
    }

    fun setFontColor(color: Color) {
        for (actor in getAllChildren(this)) {
            if (actor is Label)
                actor.style.fontColor.set(color)
        }
    }


}