package com.te6lim.word

import com.te6lim.word.database.Word

fun List<String>.toWordList(): List<Word> {
    return map { Word(data = it) }
}