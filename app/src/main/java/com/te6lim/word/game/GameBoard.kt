package com.te6lim.word.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.te6lim.word.R

class GameBoard @JvmOverloads
constructor(context: Context, attributeSet: AttributeSet? = null) : ViewGroup(context, attributeSet) {
    private val attributeArray = context.theme
        .obtainStyledAttributes(attributeSet, R.styleable.GameBoard, 0, 0)

    private var cellWidth = 0.0f


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
                addView(squares[i][j])
            }
        }
    }

    private fun PointF.calculateCoordinate(r: Int, c: Int) {
        y = if (r == 0) 0f else (r * cellWidth)
        x = if (c == 0) 0f else (c * cellWidth)
    }

    private fun right(pos: Int): Float {
        return ((pos * cellWidth) + cellWidth)
    }

    private fun bottom(pos: Int): Float {
        return ((pos * cellWidth) + cellWidth)
    }

    private fun drawSquare(row: Int, col: Int, canvas: Canvas) {
        squares[row][col].draw(canvas)
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
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (i in 0 until row) {
            //for (j in 0 until col) drawSquare(i, j, canvas = canvas)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        for (i in 0 until row) {
            for (j in 0 until col) {
                point.calculateCoordinate(i, j)
                squares[i][j].layout(point.x.toInt(), point.y.toInt(), right(j).toInt(), bottom(i).toInt())
            }
        }
    }

    inner class Square(
        context: Context, private val row: Int, private val col: Int, private val letter: Char = '\u0000'
    ) : View(context) {

        private val stroke = context.resources.getDimension(R.dimen.strokeWidth)

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(120, 124, 127)
            style = Paint.Style.STROKE
            strokeWidth = stroke
            isAntiAlias = true
        }

        private fun PointF.calculateTextPosition() {
            y = if (row == 0) cellWidth * 0.65f else ((row + 1) * cellWidth) - (cellWidth * 0.35f)
            x = if (col == 0) cellWidth / 2f else ((col + 1) * cellWidth) - (cellWidth / 2f)
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            setMeasuredDimension((cellWidth).toInt(), (cellWidth).toInt())
        }

        override fun onDraw(canvas: Canvas) {
            //point.calculateCoordinate()
            /*paint.apply {
                style = Paint.Style.STROKE
            }*/
            canvas.drawRect(stroke, stroke, cellWidth - stroke, cellWidth - stroke, paint)
            //point.calculateTextPosition()
            /*paint.apply {
                style = Paint.Style.FILL
                textAlign = Paint.Align.CENTER
                textSize = smallWidth / 2f
                typeface = Typeface.create("", Typeface.BOLD)
            }
            canvas.drawText(letter.toString(), point.x, point.y, paint)*/
        }
    }
}