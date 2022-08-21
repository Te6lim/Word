package com.te6lim.word.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
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

    private var correctColor = R.color.correctLetter
    private var misplacedColor = R.color.misplacedLetter
    private var wrongColor = R.color.wrongLetter
    private var textColor = R.color.white

    var guesses = listOf<WordGame.GuessInfo>()
        set(value) {
            field = value
            invalidate()
        }

    private val squares = arrayListOf<ArrayList<Square>>()

    init {
        for (i in 0 until row) {
            squares.add(arrayListOf())
            for (j in 0 until col) {
                squares[i].add(Square(context, i, j))
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
        squares[row][col].performDraw(canvas, row, col)
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
        gap = (cellWidth / col.toFloat()) / 4
        smallWidth = cellWidth - (gap * 2)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (i in 0 until row) {
            for (j in 0 until col) {
                drawSquare(i, j, canvas = canvas)
            }
        }
    }

    inner class Square
    @JvmOverloads constructor(
        context: Context, private val row: Int, private val col: Int, attributeSet: AttributeSet? = null
    ) : View(context, attributeSet) {

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = ContextCompat.getColor(context, R.color.strokeColor)
            style = Paint.Style.STROKE
            strokeWidth = context.resources.getDimension(R.dimen.strokeWidth)
            isAntiAlias = true
        }

        private fun PointF.calculateCoordinate(row: Int, col: Int) {
            y = if (row == 0) gap else (row * cellWidth) + (gap)
            x = if (col == 0) gap else (col * cellWidth) + (gap)
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            setMeasuredDimension(smallWidth.toInt(), smallWidth.toInt())
        }

        override fun onDraw(canvas: Canvas) {
            canvas.drawRect(point.x, point.y, right(col), bottom(row), paint)
        }

        fun performDraw(canvas: Canvas, row: Int, col: Int) {
            point.calculateCoordinate(row, col)
            draw(canvas)

        }
    }
}