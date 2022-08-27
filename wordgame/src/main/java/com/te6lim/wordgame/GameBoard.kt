package com.te6lim.wordgame

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.te6lim.wordgame.WordGame.Companion.MAX_TRIAL
import com.te6lim.wordgame.WordGame.Companion.WORD_LENGTH
import kotlin.math.roundToInt

class GameBoard @JvmOverloads
constructor(context: Context, attributeSet: AttributeSet? = null) : ViewGroup(context, attributeSet) {
    private val attributeArray = context.theme
        .obtainStyledAttributes(attributeSet, R.styleable.GameBoard, 0, 0)

    enum class ColorType {
        CORRECT, MISPLACED, WRONG, FRAME
    }

    enum class GuessFlag {
        CORRECT, INCORRECT, INCOMPLETE,
    }

    private var cellWidth = 0.0f
    private val margin = resources.getDimension(R.dimen.margin)

    private var squareGroupWidth = 0.0f
    private var squareGroupHeight = 0.0f


    private val attrRow = attributeArray.getInt(R.styleable.GameBoard_row, 1)
    private val attrCol = attributeArray.getInt(R.styleable.GameBoard_col, 1)

    private val point = PointF(0f, 0f)

    private var correctColor = Color.rgb(107, 170, 100)
    private var misplacedColor = Color.rgb(201, 180, 87)
    private var wrongColor = Color.rgb(120, 124, 127)

    private var frameColor = Color.rgb(206, 206, 206)

    private var charPosition = 0
    private var turn = 0

    private var submitted = false

    private var game: WordGame? = null

    private var squareGroups = arrayListOf<SquareGroup>()

    private var guessFlag = GuessFlag.INCORRECT

    init {
        generateLetters()
    }

    fun setUpWithWordGame(g: WordGame) {
        game = g
    }

    private fun setNewSquaresInRow(guessInfo: WordGame.GuessInfo?) {
        val index = guessInfo?.trial ?: 0
        val group: SquareGroup = newSquareGroup()
        if (index < squareGroups.size && guessInfo != null) {
            removeViewAt(index)
            squareGroups[index] = group
            addView(squareGroups[index], index)
        } else {
            squareGroups.add(group)
            addView(group)
        }
        for (c in 0 until attrCol) {
            val square = newSquare(getCharForColumn(c, guessInfo), guessInfo)
            group.addToSquareGroup(square)
        }
    }

    private fun generateLetters() {
        for (i in 0 until attrRow) setNewSquaresInRow(null)
    }

    private fun getCharForColumn(c: Int, guessInfo: WordGame.GuessInfo?): Char {
        return guessInfo?.let {
            if (c < it.guessWord.length) it.guessWord[c] else '\u0000'
        } ?: '\u0000'
    }

    private fun newSquareGroup(): SquareGroup {
        return SquareGroup(context, attrCol, object : MotherBoardInterface {
            override fun squareWidth() = cellWidth
            override fun squareGroupWidth(): Float {
                return squareGroupWidth
            }

            override fun squareGroupHeight(): Float {
                return squareGroupHeight
            }

            override fun getRowCount(): Int {
                return attrRow
            }

            override fun getColumnCount(): Int {
                return attrCol
            }
        })
    }

    private fun newSquare(letter: Char, guessInfo: WordGame.GuessInfo?): Square {
        return Square(context, letter.uppercaseChar(), object : MotherBoardInterface {
            override fun getInfo(): WordGame.GuessInfo? {
                return guessInfo
            }

            override fun squareWidth(): Float {
                return cellWidth
            }

            override fun getRowCount(): Int {
                return attrRow
            }

            override fun getColumnCount(): Int {
                return attrCol
            }

            override fun squareGroupWidth(): Float {
                return squareGroupWidth
            }

            override fun squareGroupHeight(): Float {
                return squareGroupHeight
            }

            override fun getColor(type: ColorType): Int {
                return when (type) {
                    ColorType.CORRECT -> correctColor
                    ColorType.MISPLACED -> misplacedColor
                    ColorType.WRONG -> wrongColor
                    ColorType.FRAME -> frameColor
                }
            }

            override fun submittedStatus(): Boolean {
                return submitted
            }
        })
    }

    private fun PointF.calculateCoordinate(r: Int) {
        x = margin / 2f
        y = if (r == 0) margin / 2f else ((r * squareGroupHeight) + (margin / 2))
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
            heightSize = if (attrRow >= attrCol)
                widthSize + ((widthSize / attrCol) * (attrRow - attrCol))
            else ((widthSize / attrCol) * attrRow)
        }

        setMeasuredDimension(widthSize, heightSize)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        squareGroupWidth = (w) - margin
        squareGroupHeight = (w - margin) / attrCol.toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        for ((i, s) in squareGroups.withIndex()) {
            point.calculateCoordinate(i)
            s.layout(
                (point.x).roundToInt(), point.y.roundToInt(),
                (squareGroupWidth + (margin / 2f)).roundToInt(),
                (((i + 1) * squareGroupHeight) + (margin / 2f)).roundToInt()
            )
        }
    }

    fun setCharacter(char: Char) {
        game?.addLetter(char)
        submitted = false
        if (turn in 0 until MAX_TRIAL && charPosition in 0 until WORD_LENGTH) {
            squareGroups[turn].squares[charPosition++].letter = char
        }
    }

    fun clearLastCharacter() {
        game?.removeLastLetter()
        if (turn < MAX_TRIAL && charPosition > 0) {
            squareGroups[turn].squares[--charPosition].letter = '\u0000'
        }
    }

    fun submitLatestGuess() {
        submitted = true

        game?.getLatestGuess()?.let {

            if (it.guessWord.length < WORD_LENGTH) guessFlag = GuessFlag.INCOMPLETE
            else if (it.guessWord.length == WORD_LENGTH) guessFlag = GuessFlag.INCORRECT

            when (guessFlag) {
                GuessFlag.INCORRECT -> {
                    charPosition = 0
                    turn = it.trial + 1
                    setNewSquaresInRow(it)
                    if (it.isCorrect()) {
                        guessFlag = GuessFlag.CORRECT
                        disableInput()
                    }
                }

                GuessFlag.INCOMPLETE -> {
                    squareGroups[it.trial].animateSquareGroup()
                }

                GuessFlag.CORRECT -> {
                }
            }
        }
    }

    private fun disableInput() {
        turn = -1
        charPosition = -1
    }

    fun restoreGuesses(guessList: List<WordGame.GuessInfo>) {}

    private class SquareGroup(
        context: Context, private val columnSize: Int, private val listener: MotherBoardInterface
    ) : ViewGroup(context) {

        private val point = PointF(0f, 0f)

        val squares = arrayListOf<Square>()

        private var cellWidth = 0.0f

        private lateinit var translateRight: PropertyValuesHolder
        private lateinit var translateLeft: PropertyValuesHolder
        private lateinit var animatorRight: ObjectAnimator
        private lateinit var animatorLeft: ObjectAnimator
        private lateinit var animSet: AnimatorSet

        fun addToSquareGroup(square: Square) {
            squares.add(square)
            addView(square)
        }

        private fun PointF.calculateCoordinate(col: Int) {
            x = if (col == 0) 0f else col * cellWidth
            y = 0f
        }

        private fun right(col: Int): Float {
            return (col + 1) * cellWidth
        }

        private fun bottom(): Float {
            return cellWidth
        }

        override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
            super.onSizeChanged(w, h, oldw, oldh)
            cellWidth = listener.squareGroupWidth() / listener.getColumnCount()
            translateRight = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, cellWidth / 10)
            translateLeft = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, -(cellWidth / 10))
            animatorRight = ObjectAnimator.ofPropertyValuesHolder(
                this, translateRight
            ).apply {
                repeatMode = ObjectAnimator.REVERSE
                repeatCount = 1
                duration = 30
            }

            animatorLeft = ObjectAnimator.ofPropertyValuesHolder(this, translateLeft).apply {
                repeatMode = ObjectAnimator.REVERSE
                repeatCount = 1
                duration = 30
            }

            animSet = AnimatorSet().apply {
                playSequentially(animatorRight, animatorLeft)
            }
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
        }

        override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
            for ((i, s) in squares.withIndex()) {
                point.calculateCoordinate(i)
                s.layout(
                    point.x.roundToInt(), point.y.roundToInt(), right(i).roundToInt(), bottom()
                        .roundToInt()
                )
            }
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            setMeasuredDimension(
                (listener.squareGroupWidth()).roundToInt(), (listener.squareGroupHeight()).roundToInt()
            )
        }

        fun animateSquareGroup() {
            animSet.apply {
                end()
                start()
            }
        }
    }

    private class Square(
        context: Context, char: Char = '\u0000',
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

        private var cellWidth = 0.0f

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = listener.getColor(ColorType.FRAME)
            style = Paint.Style.STROKE
            strokeWidth = stroke
            isAntiAlias = true
        }


        private fun PointF.calculateTextPosition() {
            y = (cellWidth * 0.65f)
            x = (cellWidth * 0.5f)
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            setMeasuredDimension((cellWidth).roundToInt(), (cellWidth).roundToInt())
        }

        override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
            super.onSizeChanged(w, h, oldw, oldh)
            cellWidth = listener.squareGroupWidth() / listener.getColumnCount()
        }

        override fun onDraw(canvas: Canvas) {
            paint.apply {
                style = Paint.Style.STROKE
                paint.style = Paint.Style.STROKE
            }

            if (listener.getInfo()?.isMisplaced(letter) == true) {
                paint.style = Paint.Style.FILL
                paint.color = if (listener.getInfo()?.unUsedCharacters?.contains(letter) == true)
                    listener.getColor(ColorType.MISPLACED) else listener.getColor(ColorType.WRONG)
                canvas.drawRect(
                    stroke, stroke, cellWidth - stroke, cellWidth - stroke, paint
                )
            } else {
                if (listener.getInfo()?.isRight(letter) == true) {
                    paint.style = Paint.Style.FILL
                    paint.color = if (listener.getInfo()?.unUsedCharacters?.contains(letter) == true)
                        listener.getColor(ColorType.CORRECT) else listener.getColor(ColorType.WRONG)
                    canvas.drawRect(
                        stroke, stroke, cellWidth - stroke, cellWidth - stroke, paint
                    )
                } else {
                    if (listener.getInfo()?.isWrong(letter) == true) {
                        paint.style = Paint.Style.FILL
                        paint.color = listener.getColor(ColorType.WRONG)
                        canvas.drawRect(
                            stroke, stroke, cellWidth - stroke, cellWidth - stroke, paint
                        )
                    } else {
                        paint.style = Paint.Style.STROKE
                        paint.color = listener.getColor(ColorType.FRAME)
                        canvas.drawRect(
                            stroke, stroke, cellWidth - stroke, cellWidth - stroke, paint
                        )
                    }
                }
            }
            listener.getInfo()?.unUsedCharacters?.remove(letter)
            point.calculateTextPosition()
            paint.apply {
                color = if (listener.submittedStatus()) textColorWhite else textColorBlack
                style = Paint.Style.FILL
                textAlign = Paint.Align.CENTER
                textSize = cellWidth / 2f
                typeface = Typeface.create("", Typeface.BOLD)
            }
            canvas.drawText(letter.toString(), point.x, point.y, paint)
        }
    }

    interface MotherBoardInterface {

        fun getRowCount(): Int {
            return 0
        }

        fun getColumnCount(): Int {
            return 0
        }

        fun getInfo(): WordGame.GuessInfo? {
            return null
        }

        fun squareWidth(): Float {
            return 0.0f
        }

        fun squareGroupWidth(): Float {
            return 0.0f
        }

        fun squareGroupHeight(): Float {
            return 0.0f
        }

        fun getColor(type: ColorType): Int {
            return Color.rgb(0, 0, 0)
        }

        fun submittedStatus(): Boolean {
            return false
        }
    }
}