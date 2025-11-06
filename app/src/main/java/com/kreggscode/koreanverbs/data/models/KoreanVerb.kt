package com.kreggscode.koreanverbs.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KoreanVerb(
    val id: String,
    val category: String,
    val verb: String,
    @SerialName("verb_romanization")
    val verbRomanization: String,
    @SerialName("english_meaning")
    val englishMeaning: String,
    @SerialName("korean_sentence")
    val koreanSentence: String,
    @SerialName("korean_sentence_romanization")
    val koreanSentenceRomanization: String,
    @SerialName("english_sentence")
    val englishSentence: String
)
