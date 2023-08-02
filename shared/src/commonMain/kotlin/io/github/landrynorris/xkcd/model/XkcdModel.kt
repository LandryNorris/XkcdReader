package io.github.landrynorris.xkcd.model

import kotlinx.serialization.Serializable

@Serializable
data class XkcdModel(val title: String, val img: String, val transcript: String,
                     val alt: String, val num: Int)