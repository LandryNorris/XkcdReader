import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import io.github.landrynorris.xkcd.components.RootComponent
import io.github.landrynorris.xkcd.database.ComicDatabase
import io.github.landrynorris.xkcd.database.ComicDatabaseDriverFactory

@Composable fun MainView() {
    val context = LocalContext.current
    val driverFactory = remember { ComicDatabaseDriverFactory(context) }
    val database = remember { ComicDatabase(driverFactory.createDriver()) }
    val component = remember { RootComponent(database) }
    App(component)
}
