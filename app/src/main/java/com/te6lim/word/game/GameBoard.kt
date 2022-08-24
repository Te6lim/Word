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

    enum class ColorType {
        CORRECT, MISPLACED, WRONG, FRAME
    }

    private var cellWidth = 0.0f


    private val attrRow = attributeArray.getInt(R.styleable.GameBoard_row, 1)
    private val attrCol = attributeArray.getInt(R.styleable.GameBoard_col, 1)

    private val point = PointF(0f, 0f)

    private var correctColor = Color.rgb(107, 170, 100)
    private var misplacedColor = Color.rgb(201, 180, 87)
    private var wrongColor = Color.rgb(120, 124, 127)

    private var frameColor = Color.rgb(206, 206, 206)

    var guesses = listOf<WordGame.GuessInfo>()
        set(value) {
            if (value.isNotEmpty()) {
                field = value
                removeAllViews()
                generateLetters()
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
            squareGroups.add(SquareGroup(context, attrCol, object : MotherBoardInterface {
                override fun squareWidth() = cellWidth
            }))
            for (j in 0 until attrCol) {
                letter = if (i < guesses.size) {
                    if (j < guesses[i].characterArray.size) guesses[i].characterArray[j]
                    else '\u0000'
                } else '\u0000'
                squareGroups[i].addToSquareGroup(
                    Square(context, i, letter.uppercaseChar(), object : MotherBoardInterface {
                        override fun getInfo(row: Int): WordGame.GuessInfo? {
                            if (row < guesses.size) return guesses[row]
                            return null
                        }

                        override fun squareWidth(): Float {
                            return cellWidth
                        }

                        override fun getColor(type: ColorType): Int {
                            return when (type) {
                                ColorType.CORRECT -> correctColor
                                ColorType.MISPLACED -> misplacedColor
                                ColorType.WRONG -> wrongColor
                                ColorType.FRAME -> frameColor
                            }
                        }

                    })
                )
            }
            addView(squareGroups[i])
        }
    }

    private fun PointF.calculateCoordinate(r: Int) {
        x = 0f
        y = if (r == 0) 0f else (r) * cellWidth
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
                point.x.toInt(), point.y.roundToInt(), (attrCol * cellWidth).roundToInt(),
                ((i + 1) * cellWidth).roundToInt()
            )
        }
    }

    private class Square(
        context: Context, private val row: Int, char: Char = '\u0000',
        private val listener: MotherBoardInterface
    ) : View(context) {

        var letter: Char = char
            set(value) {
                field = value
                invalidate()
            }

        private val point = PointF(0f, 0f)

        private var textColorWhite = Color.rgb(255, 255, 255)
        private var textColorBlack = Color.rgb(0, 0, 0)

        private val density = resources.displayMetrics.density

        private val stroke = density * 2

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = listener.getColor(ColorType.FRAME)
            style = Paint.Style.STROKE
            strokeWidth = stroke
            isAntiAlias = true
        }


        private fun PointF.calculateTextPosition() {
            y = (listener.squareWidth() * 0.65f)
            x = (listener.squareWidth() * 0.5f)
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            setMeasuredDimension((listener.squareWidth()).roundToInt(), (listener.squareWidth()).roundToInt())
        }

        override fun onDraw(canvas: Canvas) {
            paint.apply {
                style = Paint.Style.STROKE
                paint.style = Paint.Style.STROKE
            }

            val cellWidth = listener.squareWidth()

            if (listener.getInfo(row)?.isMisplaced(letter) == true) {
                paint.style = Paint.Style.FILL
                paint.color = listener.getColor(ColorType.MISPLACED)
                canvas.drawRect(stroke, stroke, cellWidth - stroke, cellWidth - stroke, paint)
            } else {
                if (listener.getInfo(row)?.isRight(letter) == true) {
                    paint.style = Paint.Style.FILL
                    paint.color = listener.getColor(ColorType.CORRECT)
                    canvas.drawRect(stroke, stroke, cellWidth - stroke, cellWidth - stroke, paint)
                } else {
                    if (listener.getInfo(row)?.isWrong(letter) == true) {
                        paint.style = Paint.Style.FILL
                        paint.color = listener.getColor(ColorType.WRONG)
                        canvas.drawRect(stroke, stroke, cellWidth - stroke, cellWidth - stroke, paint)
                    } else {
                        paint.style = Paint.Style.STROKE
                        paint.color = listener.getColor(ColorType.FRAME)
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

    private class SquareGroup(
        context: Context, private val columnSize: Int, private val listener: MotherBoardInterface
    ) : ViewGroup(context) {

        private val point = PointF(0f, 0f)

        private val squares = arrayListOf<Square>()

        fun addToSquareGroup(square: Square) {
            squares.add(square)
            addView(square)
        }

        private fun PointF.calculateCoordinate(col: Int) {
            x = if (col == 0) 0f else col * listener.squareWidth()
            y = 0f
        }

        private fun right(col: Int): Float {
            return (col + 1) * listener.squareWidth()
        }

        private fun bottom(): Float {
            return listener.squareWidth()
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
        }

        override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
            for ((i, s) in squares.withIndex()) {
                point.calculateCoordinate(i)
                s.layout(
                    point.x.roundToInt(), point.y.roundToInt(), right(i).roundToInt(), bottom().roundToInt()
                )
            }
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            setMeasuredDimension(
                (listener.squareWidth() * columnSize).toInt(), listener.squareWidth().toInt()
            )

        }
    }

    interface MotherBoardInterface {

        fun getInfo(row: Int): WordGame.GuessInfo? {
            return null
        }

        fun squareWidth(): Float {
            return 0.0f
        }

        fun getColor(type: ColorType): Int {
            return Color.rgb(0, 0, 0)
        }
    }
}