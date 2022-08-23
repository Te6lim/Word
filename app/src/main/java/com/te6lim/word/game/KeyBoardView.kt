package com.te6lim.word.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import kotlin.math.roundToInt

class KeyBoardView @JvmOverloads constructor(
    context: Context, private val attributeSet: AttributeSet? = null
) : ViewGroup(context, attributeSet) {

    enum class KeyType {
        TOP, MIDDLE, BOTTOM
    }

    private var keyWidth = 0.0f
    private var keyHeight = 0.0f

    private var secondRowConst = 0.0f
    private var thirdRowConst = 0.0f

    private val topChars = listOf('Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P')
    private val middleChars = listOf('A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L')
    private val bottomChars = listOf('#', 'Z', 'X', 'C', 'V', 'B', 'N', 'M', '*')

    private val topKeys = mutableListOf<KeyView>()
    private val middleKeys = mutableListOf<KeyView>()
    private val bottomKeys = mutableListOf<KeyView>()

    private val point = PointF(0f, 0f)

    init {
        for (c in topChars) topKeys.add(KeyView(KeyType.TOP).apply { addView(this) })
        for (c in middleChars) middleKeys.add(KeyView(KeyType.MIDDLE).apply { addView(this) })
        for (c in bottomChars) bottomKeys.add(KeyView(KeyType.BOTTOM).apply { addView(this) })
    }

    private val keyColor = Color.rgb(211, 214, 219)

    private fun PointF.computeXYForKey(type: KeyType, position: Int) {
        when (type) {
            KeyType.TOP -> {
                x = (position * keyWidth)
                y = 0f
            }
            KeyType.MIDDLE -> {
                x = secondRowConst + (position * keyWidth)
                y = keyHeight
            }

            KeyType.BOTTOM -> {
                x = thirdRowConst + (position * keyWidth)
                y = 2 * keyHeight
            }
        }
    }

    private fun right(type: KeyType, pos: Int): Float {
        when (type) {
            KeyType.TOP -> {
                return ((pos * keyWidth) + keyWidth)
            }

            KeyType.MIDDLE -> {
                return secondRowConst + (pos * keyWidth) + keyWidth
            }

            KeyType.BOTTOM -> {
                return thirdRowConst + (pos * keyWidth) + keyWidth
            }
        }
    }

    private fun bottom(type: KeyType): Float {
        when (type) {
            KeyType.TOP -> {
                return keyHeight
            }

            KeyType.MIDDLE -> {
                return 2 * keyHeight
            }

            KeyType.BOTTOM -> {
                return 3 * keyHeight
            }
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for ((i, k) in topKeys.withIndex()) {
            point.computeXYForKey(KeyType.TOP, i)
            topKeys[i].layout(
                point.x.toInt(), point.y.toInt(),
                right(KeyType.TOP, i).toInt(), bottom(KeyType.TOP).toInt()
            )
        }

        for ((i, k) in middleKeys.withIndex()) {
            point.computeXYForKey(KeyType.MIDDLE, i)
            middleKeys[i].layout(
                point.x.toInt(), point.y.toInt(),
                right(KeyType.MIDDLE, i).toInt(), bottom(KeyType.MIDDLE).toInt()
            )
        }

        for (i in 1 until bottomKeys.size - 1) {
            point.computeXYForKey(KeyType.BOTTOM, i - 1)
            bottomKeys[i].layout(
                point.x.toInt(), point.y.toInt(), right(KeyType.BOTTOM, i - 1).toInt(), bottom(KeyType.BOTTOM)
                    .toInt()
            )
        }
        point.apply {
            x = 0f
            y = 2 * keyHeight
            bottomKeys[0].layout(
                point.x.toInt(), point.y.toInt(), (keyWidth + secondRowConst).toInt(), (3 * keyHeight).toInt()
            )
        }

        point.apply {
            x = thirdRowConst + ((bottomKeys.size - 2) * keyWidth)
            y = 2 * keyHeight
            bottomKeys[bottomKeys.size - 1].layout(
                point.x.toInt(), point.y.toInt(), (x + (thirdRowConst)).toInt(), (3 * keyHeight).toInt()
            )
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)

        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        var heightSize = MeasureSpec.getSize(heightMeasureSpec)

        keyHeight = (widthSize / 10f) * 1.65f
        keyWidth = widthSize / 10f
        secondRowConst = keyWidth * 0.5f
        thirdRowConst = secondRowConst + keyWidth

        if (heightMode == MeasureSpec.AT_MOST) {
            heightSize = (keyHeight * 3).roundToInt()
        }
        setMeasuredDimension(widthSize, heightSize)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }

    inner class KeyView(type: KeyType) : View(context) {

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            setMeasuredDimension(keyWidth.roundToInt(), keyHeight.roundToInt())
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
        }
    }
}