package org.ajm.laforkids

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
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

fun isAscendant(actor: Actor, possibleParent: Group): Boolean {
    if (actor.parent == null) return false
    if (actor.parent == possibleParent) return true
    return isAscendant(actor.parent, possibleParent)
}

fun getTextWidth(font: BitmapFont, text: String): Float {
    val layout = GlyphLayout()
    layout.setText(font, text)
    val width = layout.width
    return width
}

/**
 * Calls the given function once every frame until the animation is done.
 */
fun animate(function: (lerp: Float) -> Unit,
            interpolationMethod: Interpolation,
            interpolationTime: Float) {

    val beganAnimation = System.currentTimeMillis()
    fun animate() {
        val seconds = (System.currentTimeMillis() - beganAnimation) / 1000f
        val alpha = MathUtils.clamp(seconds, 0f, interpolationTime) / interpolationTime
        val lerp = interpolationMethod.apply(alpha)

        function.invoke(lerp)

        if (alpha < 1f) Gdx.app.postRunnable { animate() }
    }
    animate()
}