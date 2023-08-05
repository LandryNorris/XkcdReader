package io.github.landrynorris.xkcd.components

import androidx.compose.ui.geometry.Offset
import io.github.landrynorris.xkcd.model.XkcdModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.random.Random

interface ComicLogic {
    val state: StateFlow<ComicState>

    fun loadComic(number: Int)
    fun loadLatest()
    fun loadPrevious()
    fun loadNext()
    fun loadFirst()

    fun loadRandom()

    fun onPanZoom(scale: Float, offset: Offset)
}

class ComicComponent: ComicLogic {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    private val context = CoroutineScope(Dispatchers.Default)

    override val state = MutableStateFlow(ComicState())

    override fun onPanZoom(scale: Float, offset: Offset) {
        state.update {
            it.copy(zoomScale = scale, panOffset = offset)
        }
    }

    override fun loadFirst() = loadComic(1)
    override fun loadNext() = loadComic(number = state.value.number+1)
    override fun loadPrevious() = loadComic(number = state.value.number-1)

    override fun loadRandom() = loadComic(Random.nextInt(state.value.newest)+1)

    override fun loadComic(number: Int) {
        context.launch {
            val model = client.get("https://xkcd.com/$number/info.0.json").body<XkcdModel>()

            state.update { it.copy(title = model.title, imageUrl = model.img,
                altText = model.alt, number = number, zoomScale = 1.0f, panOffset = Offset.Zero) }
        }
    }

    override fun loadLatest() {
        context.launch {
            val model = client.get("https://xkcd.com/info.0.json").body<XkcdModel>()

            state.update { it.copy(title = model.title, imageUrl = model.img,
                transcript = model.transcript, altText = model.alt,
                number = model.num, newest = model.num,
                zoomScale = 1.0f, panOffset = Offset.Zero) }
        }
    }
}

data class ComicState(val title: String = "", val imageUrl: String? = null,
                      val transcript: String = "",
                      val altText: String = "",
                      val number: Int = 0, val newest: Int = 0,
                      val zoomScale: Float = 1.0f, val panOffset: Offset = Offset.Zero)
