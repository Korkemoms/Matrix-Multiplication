package org.ajm.laforkids

import com.badlogic.gdx.graphics.Color
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
    var table = Table()

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

    /** Hide the button but not the dropdown menu. */
    fun hideButton() {
        isVisible = false
    }

    constructor(stage: Stage, skin: Skin) : super("Menu", skin) {

        nextLabel = Label("Next", skin)
        nextLabel.setAlignment(Align.center)

        helpLabel = Label("Help", skin)
        helpLabel.setAlignment(Align.center)

        settingsLabel = Label("Settings", skin)
        settingsLabel.setAlignment(Align.center)


        // add dropdown functionality
        addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                if (stage.actors.contains(table)) return

                // prepare the dropdown menu
                table = Table()
                table.background = skin.getDrawable("dot")
                table.color = Color(0.9f, 0.9f, 0.9f, 0.95f)

                var width = nextLabel.prefWidth
                width = Math.max(helpLabel.prefWidth, width)
                width = Math.max(settingsLabel.prefWidth, width)
                width *= 1.25f

                val height = nextLabel.prefHeight * 1.25f

                table.add(nextLabel).height(height).width(width).row()
                table.add(helpLabel).height(height).width(width).row()
                table.add(settingsLabel).height(height).width(width).row()

                table.pack()

                stage.addActor(table)
                table.setPosition(0f, stage.height - table.height)

                // add functionality that hides the dropdown menu when player clicks somewhere else
                stage.addListener(object : ClickListener() {
                    override fun clicked(event: InputEvent?, x: Float, y: Float) {
                        val pos = table.localToStageCoordinates(Vector2(0f, 0f))
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