package com.te6lim.word.game

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.te6lim.word.R

class GameBoard @JvmOverloads
constructor(context: Context, attributeSet: AttributeSet? = null) : View(context, attributeSet) {
    private val attributeArray = context.theme
        .obtainStyledAttributes(attributeSet, R.styleable.GameBoard, 0, 0)

    private var cellWidth = 0.0f
    private var smallWidth = 0.0f
    private var gap = 0.0f


    private val row = attributeArray.getInt(R.styleable.GameBoard_row, 1)
    private val col = attributeArray.getInt(R.styleable.GameBoard_col, 1)

    private val point = PointF(0f, 0f)

    private var correctColor = Color.rgb(107, 170, 100)
    private var misplacedColor = Color.rgb(201, 180, 87)
    private var wrongColor = Color.rgb(120, 124, 127)
    private var textColor = Color.rgb(255, 255, 255)

    var guesses = listOf<WordGame.GuessInfo>()
        set(value) {
            field = value
            generateLetters()
            invalidate()
        }

    private var squares = arrayListOf<ArrayList<Square>>()

    init {

    }

    private fun generateLetters() {
        squares = arrayListOf()
        var letter: Char
        for (i in 0 until row) {
            squares.add(arrayListOf())
            for (j in 0 until col) {
                letter = if (i < guesses.size)
                    guesses[i].characterArray[j]
                else '\u0000'
                squares[i].add(Square(context, i, j, letter.uppercaseChar()))
            }
        }
    }

    private fun right(pos: Int): Float {
        return ((pos * cellWidth) + smallWidth) + gap
    }

    private fun bottom(pos: Int): Float {
        return ((pos * cellWidth) + smallWidth) + gap
    }

    private fun drawSquare(row: Int, col: Int, canvas: Canvas) {
        squares[row][col].performDraw(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        var widthSize = MeasureSpec.getSize(widthMeasureSpec)

        if (widthMode == MeasureSpec.AT_MOST) {
            widthMode = MeasureSpec.EXACTLY
            widthSize = MeasureSpec.getSize(widthMode)
        }

        var heightSize = MeasureSpec.getSize(heightMeasureSpec)
        if (heightMode == MeasureSpec.AT_MOST) {
            heightSize = if (row >= col) widthSize + ((widthSize / col) * (row - col))
            else (widthSize / col) * row
        }

        setMeasuredDimension(widthSize, heightSize)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        cellWidth = width / col.toFloat()
        gap = (cellWidth / col.toFloat()) / 4f
        smallWidth = cellWidth - (gap * 2)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (i in 0 until row) {
            for (j in 0 until col) drawSquare(i, j, canvas = canvas)
        }
    }

    inner class Square(
        context: Context, private val row: Int, private val col: Int, private val letter: Char = '\u0000'
    ) : View(context) {

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(120, 124, 127)
            style = Paint.Style.STROKE
            strokeWidth = context.resources.getDimension(R.dimen.strokeWidth)
            isAntiAlias = true
        }

        private fun PointF.calculateCoordinate() {
            y = if (row == 0) gap else (row * cellWidth) + (gap)
            x = if (col == 0) gap else (col * cellWidth) + (gap)
        }

        private fun PointF.calculateTextPosition() {
            y = if (row == 0) cellWidth * 0.65f else ((row + 1) * cellWidth) - (cellWidth * 0.35f)
            x = if (col == 0) cellWidth / 2f else ((col + 1) * cellWidth) - (cellWidth / 2f)
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            setMeasuredDimension(smallWidth.toInt(), smallWidth.toInt())
        }

        override fun onDraw(canvas: Canvas) {
            point.calculateCoordinate()
            paint.apply {
                style = Paint.Style.STROKE
            }
            canvas.drawRect(point.x, point.y, right(col), bottom(row), paint)
            point.calculateTextPosition()
            paint.apply {
                style = Paint.Style.FILL
                textAlign = Paint.Align.CENTER
                textSize = smallWidth / 2f
                typeface = Typeface.create("", Typeface.BOLD)
            }
            canvas.drawText(letter.toString(), point.x, point.y, paint)
        }

        fun performDraw(canvas: Canvas) {
            draw(canvas)
        }
    }
}