package org.ajm.laforkids

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align

/**
 * Interface for adjusting the settings defined in GameLogic.
 */
class Settings : ScrollPane {

    /** Stuff to do when ok is clicked. */
    var onOk = Runnable { }

    /** Stuff to do when cancel is clicked. */
    var onCancel = Runnable { }

    /**
     * Interface for adjusting the settings defined in GameLogic.
     */
    constructor(main: Main) : super(Table()) {

        val gl = main.gameLogic
        val skin = main.skin

        // filter for text input
        val digitInput = TextField.TextFieldFilter { textField, c -> c.isDigit() || c.equals('-') }

        // create all the text fields for input
        val minValue = TextField(gl.minValue.toString(), skin)
        minValue.textFieldFilter = digitInput
        val maxValue = TextField(gl.maxValue.toString(), skin)
        maxValue.textFieldFilter = digitInput

        val minRowsLeft = TextField(gl.minRowsLeft.toString(), skin)
        minRowsLeft.textFieldFilter = digitInput
        val maxRowsLeft = TextField(gl.maxRowsLeft.toString(), skin)
        maxRowsLeft.textFieldFilter = digitInput

        val minColumnsLeft = TextField(gl.minColumnsLeft.toString(), skin)
        minColumnsLeft.textFieldFilter = digitInput
        val maxColumnsLeft = TextField(gl.maxColumnsLeft.toString(), skin)
        maxColumnsLeft.textFieldFilter = digitInput

        val minColumnsRight = TextField(gl.minColumnsRight.toString(), skin)
        minColumnsRight.textFieldFilter = digitInput
        val maxColumnsRight = TextField(gl.maxColumnsRight.toString(), skin)
        maxColumnsRight.textFieldFilter = digitInput

        val answerAlternatives = TextField(gl.answerAlternatives.toString(), skin)
        answerAlternatives.textFieldFilter = digitInput
        val answerMaxError = TextField(gl.answerMaxError.toString(), skin)
        answerMaxError.textFieldFilter = digitInput

        // create all the labels
        val labelValue = Label("Value", skin)
        val labelRowsLeft = Label("Rows left", skin)
        val labelColumnsLeft = Label("Columns left", skin)
        val labelColumnsRight = Label("Columns right", skin)
        val labelAnswerAlternatives = Label("Answer alternatives", skin)
        val labelAnswerMaxError = Label("Alternative error", skin)


        // put labels and text fields in a table
        val pad = 3f
        val tableSettings = Table()

        tableSettings.add(labelValue).pad(pad).align(Align.left).row()
        var aux = Table()
        aux.add(minValue).pad(pad)
        aux.add(maxValue).pad(pad)
        tableSettings.add(aux).padBottom(pad * 5).align(Align.left).row()

        tableSettings.add(labelRowsLeft).pad(pad).align(Align.left).row()
        aux = Table()
        aux.add(minRowsLeft).pad(pad)
        aux.add(maxRowsLeft).pad(pad)
        tableSettings.add(aux).padBottom(pad * 5).align(Align.left).row()

        tableSettings.add(labelColumnsLeft).pad(pad).align(Align.left).row()
        aux = Table()
        aux.add(minColumnsLeft).pad(pad)
        aux.add(maxColumnsLeft).pad(pad)
        tableSettings.add(aux).padBottom(pad * 5).align(Align.left).row()

        tableSettings.add(labelColumnsRight).pad(pad).align(Align.left).row()
        aux = Table()
        aux.add(minColumnsRight).pad(pad)
        aux.add(maxColumnsRight).pad(pad)
        tableSettings.add(aux).padBottom(pad * 5).align(Align.left).row()

        tableSettings.add(labelAnswerAlternatives).pad(pad).align(Align.left).row()
        aux = Table()
        aux.add(answerAlternatives).pad(pad)
        tableSettings.add(aux).padBottom(pad * 5).align(Align.left).row()

        tableSettings.add(labelAnswerMaxError).pad(pad).align(Align.left).row()
        aux = Table()
        aux.add(answerMaxError).pad(pad)
        tableSettings.add(aux).padBottom(pad * 5).align(Align.left).row()

        // prepare ok and cancel buttons
        val ok = TextButton("Ok", skin)
        ok.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {

                // save settings
                gl.minValue = minValue.text.toInt()
                gl.maxValue = maxValue.text.toInt()
                gl.minRowsLeft = minRowsLeft.text.toInt()
                gl.maxRowsLeft = maxRowsLeft.text.toInt()
                gl.minColumnsLeft = minColumnsLeft.text.toInt()
                gl.maxColumnsLeft = maxColumnsLeft.text.toInt()
                gl.minColumnsRight = minColumnsRight.text.toInt()
                gl.maxColumnsRight = maxColumnsRight.text.toInt()
                gl.answerAlternatives = answerAlternatives.text.toInt()
                gl.answerMaxError = answerMaxError.text.toInt()

                gl.saveSettingsForever()

                onOk.run()
            }
        })

        val cancel = TextButton("Cancel", skin)
        cancel.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                onCancel.run()
            }
        })

        // ok and cancel button in their own table
        val tableButtons = Table()
        tableButtons.add(cancel).pad(pad * 10)
        tableButtons.add(ok).pad(pad * 10)

        // put it all in the main table
        val table = children.first() as Table
        table.add(tableSettings).row()
        table.add(tableButtons)
    }
}