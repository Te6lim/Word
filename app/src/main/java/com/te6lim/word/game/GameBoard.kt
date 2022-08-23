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
    private var textColor = Color.rgb(255, 255, 255)

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
            field = value
            removeAllViews()
            generateLetters()
            invalidate()
        }

    private var squares = arrayListOf<ArrayList<Square>>()

    init {
        generateLetters()
    }

    private fun generateLetters() {
        squares = arrayListOf()
        var letter: Char
        for (i in 0 until attrRow) {
            squares.add(arrayListOf())
            for (j in 0 until attrCol) {
                letter = if (i < guesses.size) guesses[i].characterArray[j]
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
        for (i in 0 until attrRow) {
            //for (j in 0 until col) drawSquare(i, j, canvas = canvas)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        for (i in 0 until attrRow) {
            for (j in 0 until attrCol) {
                point.calculateCoordinate(i, j)
                squares[i][j].layout(point.x.toInt(), point.y.toInt(), right(j).toInt(), bottom(i).toInt())
            }
        }
    }

    inner class Square(
        context: Context, private val row: Int, private val col: Int, private val letter: Char = '\u0000'
    ) : View(context) {

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
                color = textColor
                style = Paint.Style.FILL
                textAlign = Paint.Align.CENTER
                textSize = cellWidth / 2f
                typeface = Typeface.create("", Typeface.BOLD)
            }
            canvas.drawText(letter.toString(), point.x, point.y, paint)
        }
    }
}