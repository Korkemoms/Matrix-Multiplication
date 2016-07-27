package org.ajm.laforkids.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Array
import org.ajm.laforkids.animate

/**
 * Onscreen keyboard for numbers.
 */
class Keypad : ScrollPane {

    private var animator: (lerp: Float) -> Unit = {}
    private var beganAnimation = System.currentTimeMillis()

    var interpolationMethod: Interpolation = Interpolation.pow3Out
    var interpolationTime = 0.5f

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
                    val char: Char
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

        touchable = Touchable.enabled

    }

    fun scrollIn(stage: Stage) {
        beganAnimation = System.currentTimeMillis()

        if (!stage.actors.contains(this, true)) {
            stage.addActor(this)
            y = -height
        }

        val begin = y
        val end = 0f
        animator = { lerp -> setPosition(0f, (1 - lerp) * begin + lerp * end) }

    }

    fun scrollOut(animationDelay: Float = 10f) {
        beganAnimation = System.currentTimeMillis()

        val begin = y
        val end = -height
        animator = { lerp ->
            setPosition(0f, (1 - lerp) * begin + lerp * end)
            if (lerp == 1f) Gdx.app.postRunnable { remove() }
        }
    }

    fun contains(x: Float, y: Float): Boolean {
        val rectangle = Rectangle(this.x, this.y, width, height)
        return rectangle.contains(x, y)
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        val seconds = (System.currentTimeMillis() - beganAnimation) / 1000f
        val alpha = MathUtils.clamp(seconds, 0f, interpolationTime) / interpolationTime
        val lerp = interpolationMethod.apply(alpha)

        animator.invoke(lerp)

        if (lerp < 1) Gdx.graphics.requestRendering()


        super.draw(batch, parentAlpha)
    }


}