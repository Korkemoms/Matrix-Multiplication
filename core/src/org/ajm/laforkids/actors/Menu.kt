package org.ajm.laforkids.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align

/**
 * Button that displays a menu when clicked.
 */
class Menu : Label {
    val nextLabel: Label
    val helpLabel: Label
    val settingsLabel: Label
    private var table = Table()


    // settings
    var interpolationTime = 1f
    var interpolationMethod = Interpolation.linear
    var menuBackgroundColor = Color(Color.GRAY)

    /** Remove all listeners from the buttons(labels) in the dropdown menu.*/
    fun clearMenuItemListeners() {
        nextLabel.clearListeners()
        helpLabel.clearListeners()
        settingsLabel.clearListeners()
    }

    /** Hide the dropdown menu. Does not hide the button for the menu. */
    fun hideMenu() {
        table.remove()
    }

    /**
     * Convenience function for setting the color of all the labels at once.
     */
    fun setTextColor(color: Color) {
        nextLabel.style.fontColor.set(color)
        helpLabel.style.fontColor.set(color)
        settingsLabel.style.fontColor.set(color)

    }

    constructor(stage: Stage, skin: Skin) : super("Menu", skin) {
        nextLabel = Label("Next", skin)
        nextLabel.setAlignment(Align.left)

        helpLabel = Label("Help", skin)
        helpLabel.setAlignment(Align.left)

        settingsLabel = Label("Settings", skin)
        settingsLabel.setAlignment(Align.left)

        // add dropdown functionality
        addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                if (stage.actors.contains(table, true)) return // already visible
                stage.actors.removeValue(table, true)

                // prepare the dropdown menu
                table = Table()
                table.background = skin.getDrawable("dot")
                table.color.set(menuBackgroundColor)

                // determine size
                var width = nextLabel.prefWidth
                width = Math.max(helpLabel.prefWidth, width)
                width = Math.max(settingsLabel.prefWidth, width)
                width *= 1.25f
                val height = nextLabel.prefHeight * 1.25f

                // add labels and finalize layout
                table.add(nextLabel).height(height).width(width).row()
                table.add(helpLabel).height(height).width(width).row()
                table.add(settingsLabel).height(height).width(width).row()
                table.pack()

                stage.addActor(table)


                // drop down gradually
                fun animate() = { lerp: Float ->
                    val pos = localToStageCoordinates(Vector2())
                    table.setPosition(pos.x - table.width * (1 - lerp), pos.y - table.height)
                }
                org.ajm.laforkids.animate(animate(), interpolationMethod, interpolationTime)

                // add functionality that hides the dropdown menu when player clicks somewhere else
                stage.addListener(object : ClickListener() {
                    override fun clicked(event: InputEvent?, x: Float, y: Float) {
                        val pos = Vector2(0f, stage.height - table.height) // final position after animation
                        val rectangle = Rectangle(pos.x, pos.y, table.width, table.height)
                        val hit = rectangle.contains(x, y)

                        if (!hit)
                            hideMenu()
                    }
                })
            }
        })
    }
}