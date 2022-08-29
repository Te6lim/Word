package com.te6lim.wordgame

import android.animation.*
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

    enum class GuessFlag {
        CORRECT, INCORRECT, INCOMPLETE,
    }

    private var cellWidth = 0.0f
    private val gap = resources.displayMetrics.density * 4f


    private val attrRow = attributeArray.getInt(R.styleable.GameBoard_row, 1)
    private val attrCol = attributeArray.getInt(R.styleable.GameBoard_col, 1)

    private val point = PointF(0f, 0f)

    var correctColor = Color.rgb(107, 170, 100)
        private set

    var misplacedColor = Color.rgb(201, 180, 87)
        private set

    var wrongColor = Color.rgb(120, 124, 127)
        private set

    private var frameColor = Color.rgb(206, 206, 206)

    private var charPosition = 0
    private var turn = 0

    private var submitted = false

    private var game: WordGame? = null

    private var squareGroups = arrayListOf<ArrayList<Square>>()

    private var guessFlag = GuessFlag.INCORRECT

    private var animList: List<List<AnimatorSet>>
    private var animGroup: List<AnimatorSet>

    private var drawCount: Int = 0
        set(value) {
            field = value % (attrCol - 1)
        }

    init {
        generateLetters()
        animList = squareGroups.buildAnimations()
        animGroup = getAnimGroup()
    }

    fun setUpWithWordGame(g: WordGame) {
        game = g
    }

    private fun pos(r: Int, col: Int): Int {
        return (r * attrCol) + col
    }

    private fun removeViews(squares: List<Square>) {
        for (c in squares) {
            removeView(c)
        }
    }

    private fun addViews(list: ArrayList<Square>, r: Int) {
        for (c in 0 until attrCol) {
            val p = pos(r, c)
            addView(list[c], p)
        }
    }

    private fun generateLetters() {
        for (r in 0 until attrRow) {
            setNewSquaresInRow(r, null)
        }
    }

    private fun setNewSquaresInRow(index: Int, guessInfo: WordGame.GuessInfo?) {
        val group: ArrayList<Square> = newSquareGroup(guessInfo)
        if (index < squareGroups.size && guessInfo != null) {
            removeViews(squareGroups[index])
            squareGroups[index] = group
            addViews(squareGroups[index], index)
        } else {
            squareGroups.add(group)
            addViews(group, index)
        }
    }

    private fun getCharForColumn(c: Int, guessInfo: WordGame.GuessInfo?): Char {
        return guessInfo?.let {
            if (c < it.guessWord.length) it.guessWord[c] else '\u0000'
        } ?: '\u0000'
    }

    private fun newSquareGroup(guessInfo: WordGame.GuessInfo?): ArrayList<Square> {
        val list = ArrayList<Square>()
        for (i in 0 until attrCol)
            list.add(newSquare(getCharForColumn(i, guessInfo), guessInfo))
        return list
    }

    private fun newSquare(letter: Char, guessInfo: WordGame.GuessInfo?): Square {
        return Square(context, letter.uppercaseChar(), object : MotherBoardInterface {
            override fun getInfo(): WordGame.GuessInfo? {
                return guessInfo
            }
        })
    }

    private fun PointF.computeXY(r: Int, c: Int) {
        x = if (c == 0) gap else (c * cellWidth) + ((c + 1) * gap)
        y = if (r == 0) gap else (r * cellWidth) + ((r + 1) * gap)
    }

    private fun right(r: Int, c: Int): Float {
        point.computeXY(r, c)
        return point.x + cellWidth
    }

    private fun bottom(r: Int, c: Int): Float {
        point.computeXY(r, c)
        return point.y + cellWidth
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
        cellWidth = (w - ((1 + attrCol) * gap)) / attrCol.toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        for (r in 0 until attrRow) {
            for (c in 0 until attrCol) {
                point.computeXY(r, c)
                squareGroups[r][c].layout(
                    point.x.roundToInt(), point.y.roundToInt(),
                    right(r, c).roundToInt(), bottom(r, c).roundToInt()
                )
            }
        }
    }

    fun setCharacter(char: Char) {
        submitted = false
        if (turn in 0 until MAX_TRIAL && charPosition in 0 until WORD_LENGTH) {
            game?.addLetter(char)
            squareGroups[turn][charPosition++].letter = char
        }
    }

    fun clearLastCharacter() {
        game?.removeLastLetter()
        if (turn < MAX_TRIAL && charPosition > 0) {
            squareGroups[turn][--charPosition].letter = '\u0000'
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
                    submitListener?.onSubmit(it.misplacedCharacters, it.wrongCharacters)
                    setNewSquaresInRow(it.trial, it)
                    if (it.isCorrect()) {
                        guessFlag = GuessFlag.CORRECT
                        disableInput()
                    }
                }

                GuessFlag.INCOMPLETE -> {
                    animGroup[it.trial].apply {
                        end()
                        start()
                        return@let
                    }
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

    private fun ArrayList<ArrayList<Square>>.buildAnimations(): List<List<AnimatorSet>> {
        val list = arrayListOf<ArrayList<AnimatorSet>>()

        for (r in 0 until attrRow) {
            list.add(arrayListOf())
            for (c in 0 until attrCol) {
                list[r].add(AnimatorSet().apply {
                    playSequentially(
                        this@buildAnimations[r][c].getTranslateRight(),
                        this@buildAnimations[r][c].getTranslateLeft()
                    )
                })
            }
        }
        return list
    }

    private fun getAnimGroup(): List<AnimatorSet> {
        val list = arrayListOf<AnimatorSet>()
        for (r in 0 until attrRow) {
            list.add(AnimatorSet().apply {
                playTogether(*animList[r].toTypedArray())
            })
        }
        return list
    }

    private var submitListener: SubmitListener? = null

    fun setOnGuessSubmittedListener(listener: SubmitListener) {
        submitListener = listener
    }

    inner class Square(
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

        private val stroke = resources.displayMetrics.density * 2

        private var cellWidth = 0.0f

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = frameColor
            style = Paint.Style.STROKE
            strokeWidth = stroke
            isAntiAlias = true
        }

        private val popAnimSet: AnimatorSet

        init {
            popAnimSet = AnimatorSet().apply {
                playTogether(getPopAnimX(), getPopAnimY())
            }
        }

        private fun getPopAnimX() = ObjectAnimator.ofPropertyValuesHolder(
            this, PropertyValuesHolder.ofFloat(SCALE_X, 1.2f)
        ).apply {
            duration = 20
            repeatMode = ObjectAnimator.REVERSE
            repeatCount = 1
        }

        private fun getPopAnimY() = ObjectAnimator.ofPropertyValuesHolder(
            this, PropertyValuesHolder.ofFloat(SCALE_Y, 1.2f)
        ).apply {
            duration = 20
            repeatMode = ObjectAnimator.REVERSE
            repeatCount = 1
        }


        private fun PointF.calculateTextPosition() {
            y = (cellWidth * 0.65f)
            x = (cellWidth * 0.5f)
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            setMeasuredDimension((cellWidth).roundToInt(), (cellWidth).roundToInt())
        }

        override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
            cellWidth = w.toFloat()
        }

        override fun onDraw(canvas: Canvas) {
            if (drawCount == 0) listener.getInfo()?.resetUnselectedCharacters()
            ++drawCount
            paint.apply {
                style = Paint.Style.STROKE
                paint.style = Paint.Style.STROKE
            }

            if (listener.getInfo()?.isMisplaced(letter) == true) {
                paint.style = Paint.Style.FILL
                paint.color = if (listener.getInfo()?.unUsedCharacters?.contains(letter) == true)
                    misplacedColor else wrongColor
                canvas.drawRect(
                    stroke, stroke, cellWidth - stroke, cellWidth - stroke, paint
                )
            } else {
                if (listener.getInfo()?.isRight(letter) == true) {
                    paint.style = Paint.Style.FILL
                    paint.color = if (listener.getInfo()?.unUsedCharacters?.contains(letter) == true)
                        correctColor else wrongColor
                    canvas.drawRect(
                        stroke, stroke, cellWidth - stroke, cellWidth - stroke, paint
                    )
                } else {
                    if (listener.getInfo()?.isWrong(letter) == true) {
                        paint.style = Paint.Style.FILL
                        paint.color = wrongColor
                        canvas.drawRect(
                            stroke, stroke, cellWidth - stroke, cellWidth - stroke, paint
                        )
                    } else {
                        paint.style = Paint.Style.STROKE
                        paint.color = if (letter == '\u0000') frameColor
                        else wrongColor
                        canvas.drawRect(
                            stroke, stroke, cellWidth - stroke, cellWidth - stroke, paint
                        )
                    }
                }
            }
            listener.getInfo()?.unUsedCharacters?.remove(letter)

            point.calculateTextPosition()
            paint.apply {
                color = if (submitted) textColorWhite else textColorBlack
                style = Paint.Style.FILL
                textAlign = Paint.Align.CENTER
                textSize = cellWidth / 2f
                typeface = Typeface.create("", Typeface.BOLD)
            }
            canvas.drawText(letter.toString(), point.x, point.y, paint)

            popAnimSet.apply {
                end()
                start()
            }
        }

        fun getTranslateRight(): ObjectAnimator {
            val translateRight = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, gap)
            return ObjectAnimator.ofPropertyValuesHolder(this, translateRight).apply {
                duration = 30
                repeatCount = 1
                repeatMode = ObjectAnimator.REVERSE
            }
        }

        fun getTranslateLeft(): ObjectAnimator {
            val translateLeft = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, -gap)
            return ObjectAnimator.ofPropertyValuesHolder(this, translateLeft).apply {
                duration = 30
                repeatCount = 1
                repeatMode = ObjectAnimator.REVERSE
            }
        }

    }

    interface MotherBoardInterface {
        fun getInfo(): WordGame.GuessInfo? {
            return null
        }
    }

    interface SubmitListener {
        fun onSubmit(misplacedChars: List<Char>, wrongChar: List<Char>)
    }
}