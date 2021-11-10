package it.francescobottino.android.randomizer

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import kotlinx.coroutines.delay

class RandomResultTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
): AppCompatTextView(context, attrs, defStyleAttr) {

    companion object {
        private const val DEFAULT_DURATION: Long = 1300L
        private const val DEFAULT_STEPS_NUM: Int = 20
    }

    suspend fun setAnimation(
        duration: Long = DEFAULT_DURATION,
        stepsNum: Int = DEFAULT_STEPS_NUM,
        onStep: (Int)->Int,
        onFinish: ()->Int) {

        var timeElapsed: Long = 0L
        var currentStep: Int = 0
        val stepDuration: Long = duration/stepsNum

        while(timeElapsed < duration) {
            text = onStep(currentStep++).toString()
            timeElapsed += stepDuration
            delay(stepDuration)
        }
        text = onFinish().toString()
    }
}