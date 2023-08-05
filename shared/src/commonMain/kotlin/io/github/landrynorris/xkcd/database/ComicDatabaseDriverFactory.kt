package io.github.landrynorris.xkcd.database

import app.cash.sqldelight.db.SqlDriver

expect class ComicDatabaseDriverFactory {
    fun createDriver(): SqlDriver
}
