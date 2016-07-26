package org.ajm.laforkids.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Align
import org.ajm.laforkids.getTextWidth

/**
 * Label for showing an integer score.
 */
class ScoreLabel : Label {

    // settings
    var interpolationMethod: Interpolation = Interpolation.pow4Out
    var interpolationTime = 0.75f

    private var diffLabel: Label? = null

    var score = 0
        set(value) {
            previousDisplayedScore = displayedScore
            val diff = value - score
            field = value
            scoreSetAtTime = System.currentTimeMillis()

            // count up or down the displayed score
            fun animate() = { lerp: Float ->
                displayedScore = MathUtils.lerp(previousDisplayedScore.toFloat(), score.toFloat(), lerp).toInt()
                setText(displayedScore.toString())
            }
            org.ajm.laforkids.animate(animate(), interpolationMethod, interpolationTime)

            // display the difference for a while, if there is one
            if (diff != 0 && stage != null) {
                diffLabel?.remove() // only one at a time

                // prepare label
                val sign = if (diff < 0) "" else "+"
                diffLabel = Label(sign + diff.toString(), style)
                val label = diffLabel as Label

                stage.addActor(diffLabel)
                val pos = localToStageCoordinates(Vector2())
                label.setPosition(pos.x + width - getTextWidth(style.font, score.toString()) - label.width - style.font.spaceWidth * 2f, pos.y)

                // prepare opacity animation
                val interpolationTime = interpolationTime * 4
                fun displayDiff() { // recursive
                    var alpha = MathUtils.clamp((System.currentTimeMillis() - scoreSetAtTime) / 1000f, 0f,
                            interpolationTime) / interpolationTime

                    // remove if done, otherwise continue animation
                    if (alpha == 1f) label.remove()
                    else Gdx.app.postRunnable { displayDiff() }

                    // animate
                    alpha *= 2f
                    if (alpha > 1) alpha = 2f - alpha
                    val lerp = Interpolation.sine.apply(interpolationMethod.apply(alpha))
                    label.color.a = lerp

                }
                displayDiff()
            }
        }

    // auxiliary
    private var previousDisplayedScore = 0
    private var displayedScore = 0
    private var scoreSetAtTime = System.currentTimeMillis()

    constructor(skin: Skin, score: Int) : super("000000000", skin) {
        setAlignment(Align.center)

        previousDisplayedScore = score
        displayedScore = score
        this.score = score
    }
}