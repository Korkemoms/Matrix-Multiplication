package org.ajm.laforkids

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align

/**
 * Interface for adjusting the settings defined in GameLogic.
 */
class SettingsInterface : ScrollPane {

    /** Stuff to do when ok is clicked. */
    var onOk = Runnable { }

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

        // create all the text fields for input
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

                s.saveSettingsForever()

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