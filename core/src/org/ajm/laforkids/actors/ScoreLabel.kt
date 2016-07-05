package org.ajm.laforkids.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Align

/**
 * Label for showing an integer score.
 */
class ScoreLabel : Label {

    var interpolationMethod: Interpolation = Interpolation.pow4Out
    var interpolationTime = 0.75f

    var score = 0
        set(value) {
            previousDisplayedScore = displayedScore
            field = value
            scoreSetAtTime = System.currentTimeMillis()

        }
    private var previousDisplayedScore = 0
    private var displayedScore = 0
    private var scoreSetAtTime = System.currentTimeMillis()

    constructor(skin: Skin, score: Int) : super("000000000", skin) {
        setAlignment(Align.center)

        previousDisplayedScore = score
        displayedScore = score
        this.score = score
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        if (score != displayedScore) Gdx.graphics.requestRendering()

        val alpha = MathUtils.clamp((System.currentTimeMillis() - scoreSetAtTime) / 1000f, 0f, interpolationTime) / interpolationTime
        val lerp = interpolationMethod.apply(alpha)

        // update score
        displayedScore = MathUtils.lerp(previousDisplayedScore.toFloat(), score.toFloat(), lerp).toInt()
        setText(displayedScore.toString())

        super.draw(batch, parentAlpha)

    }
}