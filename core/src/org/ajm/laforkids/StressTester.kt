package org.ajm.laforkids

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.math.Vector2
import java.util.*

/**
 * Clicks randomly really fast.
 * Revealed some weaknesses that are now fixed.
 */
class StressTester(val inputProcessor: InputProcessor) {
    private val random = Random()

    var active = false

    fun invoke() {
        if (!active) return

        Gdx.graphics.requestRendering()

        val pos = Vector2()
        val add = Vector2()
        try {

            for (i in 0 until 100) {
                // chose a position and a radius
                pos.set(random.nextFloat() * Gdx.graphics.width, random.nextFloat() * Gdx.graphics.height)
                val radius = random.nextFloat() * Math.min(Gdx.graphics.width, Gdx.graphics.height).toFloat()

                // click 10 times inside this radius
                for (j in 0 until 10) {
                    add.set(0f, radius)
                    add.rotate(random.nextFloat() * 360f)
                    pos.add(add)

                    inputProcessor.touchDown(pos.x.toInt(), pos.y.toInt(), 1, 1)
                    inputProcessor.touchUp(pos.x.toInt(), pos.y.toInt(), 1, 1)
                }
            }
        } catch (e: Exception) {
            Gdx.app.log("Stress Test", "Stress test crashed at x=$pos.")
            throw e
        }
    }
}