package io.github.landrynorris.xkcd.repositories

import io.github.landrynorris.xkcd.database.Comic
import io.github.landrynorris.xkcd.database.ComicDatabase
import io.github.landrynorris.xkcd.model.XkcdModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.isSuccess

class ComicRepository(database: ComicDatabase, private val httpClient: HttpClient) {
    private val comicQueries = database.comicQueries

    suspend fun cacheAllComicMetadata() {
        val maxSaved = comicQueries.maxSavedNumber { it?.toInt() ?: 0 }.executeAsOne()
        val latest = getLatestComicFromWeb()

        for(i in maxSaved+1 .. latest.num) {
            if(i == 404) continue //comic 404 doesn't exist

            val model = getComicFromWebOrNull(i)

            if(model != null) {
                val comic = Comic(model.num.toLong(), model.title, model.img, model.transcript,
                    model.alt, false)

                comicQueries.saveComic(comic)
            }
        }
    }

    suspend fun getComicOrNull(number: Int): XkcdModel? {
        return comicQueries.getComic(number.toLong()).executeAsOneOrNull()?.toXkcdModel()
            ?: getComicFromWebOrNull(number)
    }

    suspend fun getLatestComic(): XkcdModel {
        return getLatestComicFromWeb()
    }

    fun getComicsMatching(text: String, limit: Int) =
        comicQueries.searchComic(text, limit.toLong()).executeAsList().map { it.toXkcdModel() }

    private suspend fun getComicFromWebOrNull(number: Int): XkcdModel? {
        val response = httpClient.get("https://xkcd.com/$number/info.0.json")
        if(response.status.isSuccess()) {
            return response.body()
        }
        return null
    }

    private suspend fun getLatestComicFromWeb(): XkcdModel =
        httpClient.get("https://xkcd.com/info.0.json").body()

    private fun Comic.toXkcdModel(): XkcdModel {
        return XkcdModel(title, img, transcript, alt, number.toInt())
    }
}