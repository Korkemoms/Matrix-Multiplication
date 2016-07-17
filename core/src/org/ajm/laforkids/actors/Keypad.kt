package org.ajm.laforkids.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Array

/**
 * Onscreen keyboard for numbers.
 */
class Keypad : ScrollPane {

    constructor(skin: Skin) : super(Table()) {

        val table = widget as Table

        // determine sizes
        val totalHeight = Gdx.graphics.height / 3f
        val totalWidth = Gdx.graphics.width.toFloat()
        var entryWidth = totalWidth / 3f
        var entryHeight = totalHeight / 4f
        val pad = Math.min(entryWidth, entryHeight) / 10f
        entryWidth -= pad * 2f
        entryHeight -= pad * 2f
        width = totalWidth
        height = totalHeight

        // prepare buttons
        val buttons = Array<String>()
        buttons.add("1")
        buttons.add("2")
        buttons.add("3")
        buttons.add("4")
        buttons.add("5")
        buttons.add("6")
        buttons.add("7")
        buttons.add("8")
        buttons.add("9")
        buttons.add("0")
        buttons.add("-")
        buttons.add("rem")

        // add listeners that forward the input
        var i = 0
        for (text in buttons) {
            val textButton = TextButton(text, skin)
            textButton.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    val backspace = text.equals("rem")
                    val char :Char
                    if (backspace)
                        char = 8.toChar()
                    else
                        char = text.first()

                    Gdx.input.inputProcessor.keyTyped(char)
                }
            })
            table.add(textButton).pad(pad).width(entryWidth).height(entryHeight)

            if (++i % 3 == 0)
                table.row()
        }

        // don't want touches going through
        touchable = Touchable.enabled

        // make it somewhat opaque
        style.background = skin.getDrawable("dot")
    }

    fun contains(x: Float, y: Float): Boolean {
        val rectangle = Rectangle(this.x, this.y, width, height)
        return rectangle.contains(x, y)
    }


}