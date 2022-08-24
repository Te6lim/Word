package com.te6lim.word.game

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.te6lim.word.R
import kotlin.math.roundToInt

class GameBoard @JvmOverloads
constructor(context: Context, attributeSet: AttributeSet? = null) : ViewGroup(context, attributeSet) {
    private val attributeArray = context.theme
        .obtainStyledAttributes(attributeSet, R.styleable.GameBoard, 0, 0)

    private var cellWidth = 0.0f


    private val attrRow = attributeArray.getInt(R.styleable.GameBoard_row, 1)
    private val attrCol = attributeArray.getInt(R.styleable.GameBoard_col, 1)

    private val point = PointF(0f, 0f)

    private var correctColor = Color.rgb(107, 170, 100)
    private var misplacedColor = Color.rgb(201, 180, 87)
    private var wrongColor = Color.rgb(120, 124, 127)
    private var textColorWhite = Color.rgb(255, 255, 255)
    private var textColorBlack = Color.rgb(0, 0, 0)

    private var frameColor = Color.rgb(206, 206, 206)

    private val density = resources.displayMetrics.density

    private val stroke = density * 2

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = frameColor
        style = Paint.Style.STROKE
        strokeWidth = stroke
        isAntiAlias = true
    }

    var guesses = listOf<WordGame.GuessInfo>()
        set(value) {
            if (value.isNotEmpty() && value.size != field.size) {
                field = value
                removeAllViews()
                generateLetters()
                invalidate()
            }
        }

    private var squares = arrayListOf<ArrayList<Square>>()
    private var squareGroups = arrayListOf<SquareGroup>()

    init {
        generateLetters()
    }

    private fun generateLetters() {
        squares = arrayListOf()
        var letter: Char
        for (i in 0 until attrRow) {
            squares.add(arrayListOf())
            squareGroups.add(SquareGroup())
            for (j in 0 until attrCol) {
                letter = if (i < guesses.size) {
                    if (j < guesses[i].characterArray.size) guesses[i].characterArray[j]
                    else '\u0000'
                } else '\u0000'
                squareGroups[i].addToSquareGroup(Square(context, i, j, letter.uppercaseChar()))
            }
            addView(squareGroups[i])
        }
    }

    private fun PointF.calculateCoordinate(r: Int, c: Int) {
        y = if (r == 0) 0f else (r * cellWidth)
        x = if (c == 0) 0f else (c * cellWidth)
    }

    private fun PointF.calculateCoordinate(r: Int) {
        x = 0f
        y = if (r == 0) 0f else (r) * cellWidth
    }

    private fun right(pos: Int): Float {
        return ((pos * cellWidth) + cellWidth)
    }

    private fun bottom(pos: Int): Float {
        return ((pos * cellWidth) + cellWidth)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        var widthSize = MeasureSpec.getSize(widthMeasureSpec)

        if (widthMode == MeasureSpec.AT_MOST) {
            widthMode = MeasureSpec.getMode(MeasureSpec.EXACTLY)
            widthSize = MeasureSpec.getSize(widthMode)
        }

        var heightSize = MeasureSpec.getSize(heightMeasureSpec)
        if (heightMode == MeasureSpec.AT_MOST) {
            heightSize = if (attrRow >= attrCol) widthSize + ((widthSize / attrCol) * (attrRow - attrCol))
            else (widthSize / attrCol) * attrRow
        }

        setMeasuredDimension(widthSize, heightSize)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        cellWidth = width / attrCol.toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {

        for ((i, s) in squareGroups.withIndex()) {
            point.calculateCoordinate(i)
            s.layout(
                point.x.toInt(), point.y.toInt(), (attrCol * cellWidth).toInt(), ((i + 1) * cellWidth)
                    .toInt()
            )
        }
    }

    inner class Square(
        context: Context, private val row: Int, private val col: Int, char: Char = '\u0000'
    ) : View(context) {

        var letter: Char = char
            set(value) {
                field = value
                invalidate()
            }


        private fun PointF.calculateTextPosition() {
            y = (cellWidth * 0.65f)
            x = (cellWidth * 0.5f)
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            setMeasuredDimension((cellWidth).roundToInt(), (cellWidth).roundToInt())
        }

        override fun onDraw(canvas: Canvas) {
            point.calculateCoordinate(row, col)
            paint.apply {
                style = Paint.Style.STROKE
                paint.style = Paint.Style.STROKE
                paint.color = wrongColor
            }

            if (row < guesses.size && guesses[row].isMisplaced(letter)) {
                paint.style = Paint.Style.FILL
                paint.color = misplacedColor
                canvas.drawRect(stroke, stroke, cellWidth - stroke, cellWidth - stroke, paint)
            } else {
                if (row < guesses.size && guesses[row].isRight(letter)) {
                    paint.style = Paint.Style.FILL
                    paint.color = correctColor
                    canvas.drawRect(stroke, stroke, cellWidth - stroke, cellWidth - stroke, paint)
                } else {
                    if (row < guesses.size && guesses[row].isWrong(letter)) {
                        paint.style = Paint.Style.FILL
                        paint.color = wrongColor
                        canvas.drawRect(stroke, stroke, cellWidth - stroke, cellWidth - stroke, paint)
                    } else {
                        paint.style = Paint.Style.STROKE
                        paint.color = frameColor
                        canvas.drawRect(stroke, stroke, cellWidth - stroke, cellWidth - stroke, paint)
                    }
                }
            }
            point.calculateTextPosition()
            paint.apply {
                color = textColorBlack
                style = Paint.Style.FILL
                textAlign = Paint.Align.CENTER
                textSize = cellWidth / 2f
                typeface = Typeface.create("", Typeface.BOLD)
            }
            canvas.drawText(letter.toString(), point.x, point.y, paint)
        }
    }

    inner class SquareGroup() : ViewGroup(context) {

        private val squares = arrayListOf<Square>()

        fun addToSquareGroup(square: Square) {
            squares.add(square)
            addView(square)
        }

        override fun onDraw(canvas: Canvas) {

        }

        override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            setMeasuredDimension((cellWidth * attrCol).toInt(), cellWidth.toInt())

        }

        override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
            super.onSizeChanged(w, h, oldw, oldh)
        }
    }
}