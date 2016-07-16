package org.ajm.laforkids

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Array

/**
 * Recursively get all children.
 */
fun getAllChildren(table: Group): Array<Actor> {
    fun getAllChildren(table: Group, found: Array<Actor>): Array<Actor> {
        for (child in table.children) {
            found.add(child)
            if (child is Group)
                getAllChildren(child, found)
        }
        return found
    }

    return getAllChildren(table, Array<Actor>())
}

fun getTextWidth(font: BitmapFont, text: String): Float {
    val layout = GlyphLayout()
    layout.setText(font, text)
    val width = layout.width
    return width
}