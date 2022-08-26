package com.te6lim.keyboard

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import kotlin.math.roundToInt

class KeyBoardView @JvmOverloads constructor(
    context: Context, attributeSet: AttributeSet? = null
) : ViewGroup(context, attributeSet) {

    enum class KeyType {
        TOP, MIDDLE, BOTTOM
    }

    enum class SpecialKeys(value: Int) {
        ENTER(KeyEvent.KEYCODE_ENTER), DELETE(KeyEvent.KEYCODE_CLEAR)
    }

    private var keyWidth = 0.0f
    private var keyHeight = 0.0f

    private var secondRowConst = 0.0f
    private var thirdRowConst = 0.0f

    private val topChars = listOf("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P")
    private val middleChars = listOf("A", "S", "D", "F", "G", "H", "J", "K", "L")
    private val bottomChars = listOf("ENTER", "Z", "X", "C", "V", "B", "N", "M", "DEL")

    private val topKeys = mutableListOf<KeyView>()
    private val middleKeys = mutableListOf<KeyView>()
    private val bottomKeys = mutableListOf<KeyView>()

    private val point = PointF(0f, 0f)

    init {
        for (c in topChars) topKeys.add(KeyView(c).apply { addView(this) })
        for (c in middleChars) middleKeys.add(KeyView(c).apply { addView(this) })
        for ((i, c) in bottomChars.withIndex()) {
            bottomKeys.add(KeyView(c).apply { addView(this) })
        }
    }

    private val keyColor = Color.rgb(211, 214, 219)

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

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
        return when (type) {
            KeyType.TOP -> {
                ((pos * keyWidth) + keyWidth)
            }

            KeyType.MIDDLE -> {
                secondRowConst + (pos * keyWidth) + keyWidth
            }

            KeyType.BOTTOM -> {
                thirdRowConst + (pos * keyWidth) + keyWidth
            }
        }
    }

    private fun bottom(type: KeyType): Float {
        return when (type) {
            KeyType.TOP -> {
                keyHeight
            }

            KeyType.MIDDLE -> {
                2 * keyHeight
            }

            KeyType.BOTTOM -> {
                3 * keyHeight
            }
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for ((i, key) in topKeys.withIndex()) {
            point.computeXYForKey(KeyType.TOP, i)
            key.layout(
                point.x.toInt(), point.y.toInt(),
                right(KeyType.TOP, i).toInt(), bottom(KeyType.TOP).toInt()
            )
        }

        for ((i, k) in middleKeys.withIndex()) {
            point.computeXYForKey(KeyType.MIDDLE, i)
            k.layout(
                point.x.toInt(), point.y.toInt(),
                right(KeyType.MIDDLE, i).toInt(), bottom(KeyType.MIDDLE).toInt()
            )
        }

        for (i in 1 until bottomKeys.size - 1) {
            point.computeXYForKey(KeyType.BOTTOM, i - 1)
            bottomKeys[i].layout(
                point.x.toInt(), point.y.toInt(), right(KeyType.BOTTOM, i - 1).toInt(),
                bottom(KeyType.BOTTOM).toInt()
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

    var keyClickListener: OnKeyClickListener? = null

    fun setOnKeyClickListener(listener: OnKeyClickListener) {
        keyClickListener = listener
        for (k in topKeys) k.clickListener = listener
        for (k in middleKeys) k.clickListener = listener
        for (k in bottomKeys) k.clickListener = listener
    }

    inner class KeyView(private val char: String) : View(context) {

        private var gap = 0f
        private var corner = 0f
        private var textAccent = 0.0f

        var clickListener: OnKeyClickListener? = null

        init {
            isClickable = true
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            setMeasuredDimension(keyWidth.roundToInt(), keyHeight.roundToInt())
        }

        override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
            super.onSizeChanged(w, h, oldw, oldh)
            gap = keyWidth / 20
            corner = keyWidth / 5
            textAccent = (keyHeight * 0.5f) - ((paint.descent() + paint.ascent()) * 0.5f) + gap
        }

        override fun onDraw(canvas: Canvas) {
            paint.color = keyColor
            if (!isEnterKeyOrDelete()) {
                canvas.drawRoundRect(
                    gap, gap, keyWidth - gap, keyHeight - gap, corner, corner, paint
                )
            } else {
                canvas.drawRoundRect(
                    gap, gap, ((keyWidth + (keyWidth / 2)) - gap), keyHeight - gap, corner, corner, paint
                )
            }
            paint.apply {
                color = Color.BLACK
                textSize = keyWidth / 3
                textAlign = Paint.Align.CENTER
                typeface = Typeface.DEFAULT_BOLD
            }

            if (!isEnterKeyOrDelete()) {
                canvas.drawText(
                    char, keyWidth * 0.5f, textAccent, paint
                )
            } else {
                val pic = ContextCompat.getDrawable(context, R.drawable.ic_backspace)!!.toBitmap()
                if (isDeleteKey()) canvas.drawBitmap(
                    pic, (thirdRowConst - pic.width) / 2f,
                    ((thirdRowConst - pic.height) / 2f) + gap, paint
                )
                else canvas.drawText(char, (keyWidth + secondRowConst) * 0.5f, textAccent, paint)
            }
        }

        override fun performClick(): Boolean {
            super.performClick()
            contentDescription = "jksdf"
            if (char != bottomChars[0] && char != bottomChars[bottomChars.size - 1])
                clickListener?.onClick(char.toCharArray()[0])
            else {
                if (char == bottomChars[0]) clickListener?.onClick(SpecialKeys.ENTER)
                else clickListener?.onClick(SpecialKeys.DELETE)
            }
            return true
        }

        private fun isEnterKeyOrDelete(): Boolean {
            return char == "ENTER" || char == "DEL"
        }

        private fun isDeleteKey(): Boolean {
            return char == "DEL"
        }
    }

    interface OnKeyClickListener {
        fun onClick(char: Char)
        fun onClick(key: SpecialKeys)
    }
}