package io.github.landrynorris.xkcd.repositories

import io.github.landrynorris.xkcd.database.Comic
import io.github.landrynorris.xkcd.database.ComicDatabase
import io.github.landrynorris.xkcd.model.XkcdModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class ComicRepository(database: ComicDatabase, private val httpClient: HttpClient) {
    private val comicQueries = database.comicQueries

    suspend fun cacheAllComicMetadata() {
        val maxSaved = comicQueries.maxSavedNumber { it?.toInt() ?: 0 }.executeAsOne()
        val latest = getLatestComicFromWeb()

        for(i in maxSaved+1 .. latest.num) {
            if(i == 404) continue //comic 404 doesn't exist

            val model = getComicFromWeb(i)

            val comic = Comic(model.num.toLong(), model.title, model.img, model.transcript,
                model.alt, false)

            comicQueries.saveComic(comic)
        }
    }

    suspend fun getComic(number: Int): XkcdModel {
        return comicQueries.getComic(number.toLong()).executeAsOneOrNull()?.toXkcdModel()
            ?: getComicFromWeb(number)
    }

    suspend fun getLatestComic(): XkcdModel {
        return getLatestComicFromWeb()
    }

    private fun getComicsMatching(text: String, limit: Int) =
        comicQueries.searchComic(text, limit.toLong()).executeAsList()

    private suspend fun getComicFromWeb(number: Int): XkcdModel =
        httpClient.get("https://xkcd.com/$number/info.0.json").body()

    private suspend fun getLatestComicFromWeb(): XkcdModel =
        httpClient.get("https://xkcd.com/info.0.json").body()

    private fun Comic.toXkcdModel(): XkcdModel {
        return XkcdModel(title, img, transcript, alt, number.toInt())
    }
}