import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import io.github.landrynorris.xkcd.components.RootComponent
import io.github.landrynorris.xkcd.screens.ComicScreen

@Composable
fun App(component: RootComponent) {
    MaterialTheme {
        ComicScreen(component.currentComponent)
    }
}

