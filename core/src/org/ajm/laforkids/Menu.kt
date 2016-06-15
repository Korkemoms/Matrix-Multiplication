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

/**
 * Button that displays a menu when clicked.
 */
class Menu : Label {
    val nextLabel: Label
    val helpLabel: Label
    val settingsLabel: Label
    private var table = Table()

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
        helpLabel = Label("Help", skin)
        settingsLabel = Label("Settings", skin)

        // add dropdown functionality
        addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {

                // prepare the dropdown menu
                table = Table()
                table.background = skin.getDrawable("dot")
                table.color = Color(0.9f,0.9f,0.9f,0.95f)
                table.add(nextLabel).pad(nextLabel.height*0.5f).row()
                table.add(helpLabel).pad(nextLabel.height*0.5f).row()
                table.add(settingsLabel).pad(nextLabel.height*0.5f).row()
                table.pack()

                stage.addActor(table)
                table.setPosition(0f, stage.height - table.height)

                // add functionality that hides the dropdown menu when player clicks somewhere else
                stage.addListener (object : ClickListener() {
                    override fun clicked(event: InputEvent?, x: Float, y: Float) {
                        val pos = table.localToStageCoordinates(Vector2(0f, 0f))
                        val rectangle = Rectangle(pos.x, pos.y, table.width, table.height)
                        val hit = rectangle.contains(x, y)

                        if (!hit) hideMenu()
                    }
                })
            }
        })
    }
}