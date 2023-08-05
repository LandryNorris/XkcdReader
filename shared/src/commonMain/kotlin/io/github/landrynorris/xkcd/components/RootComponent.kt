package io.github.landrynorris.xkcd.components

import io.github.landrynorris.xkcd.database.ComicDatabase
import io.github.landrynorris.xkcd.repositories.ComicRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class RootComponent(comicDatabase: ComicDatabase) {
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }
    private val repository = ComicRepository(comicDatabase, httpClient).also {
        CoroutineScope(Dispatchers.Default).launch {
            it.cacheAllComicMetadata()
        }
    }
    val currentComponent = ComicComponent(repository)
}