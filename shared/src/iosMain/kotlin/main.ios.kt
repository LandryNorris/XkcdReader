import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import io.github.landrynorris.xkcd.components.RootComponent
import io.github.landrynorris.xkcd.database.ComicDatabase
import io.github.landrynorris.xkcd.database.ComicDatabaseDriverFactory

fun MainViewController() = ComposeUIViewController {
    val driverFactory = remember { ComicDatabaseDriverFactory() }
    val database = remember { ComicDatabase(driverFactory.createDriver()) }
    val component = remember { RootComponent(database) }
    App(component)
}