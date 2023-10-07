package io.github.landrynorris.xkcd.screens

import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.landrynorris.xkcd.components.ComicLogic
import io.github.landrynorris.xkcd.components.ComicState
import io.github.landrynorris.xkcd.components.SearchLogic
import io.github.landrynorris.xkcd.model.XkcdModel
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
fun ComicScreen(logic: ComicLogic) {
    val state by logic.state.collectAsState()
    ComicColumn(state, logic)

    LaunchedEffect(Unit) {
        logic.loadLatest()
    }
}

@Composable
fun ComicColumn(state: ComicState, logic: ComicLogic) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        SearchUi(logic.searchComponent) { logic.loadComic(it.num) }
        Box(modifier = Modifier.weight(1f).clipToBounds()) {
            if(state.imageUrl != null) {
                ComicViewPane(state.zoomScale, state.panOffset, logic::onPanZoom) {
                    KamelImage(asyncPainterResource(state.imageUrl), state.transcript,
                        onLoading = { CircularProgressIndicator() },
                        alignment = Alignment.Center)
                }
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
        Text(state.title, fontSize = 24.sp)
        Text(state.altText, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(5.dp))

        Row {
            Button(onClick = logic::loadFirst) { Text("<<") }
            if(state.number > 1) Button(onClick = logic::loadPrevious) { Text("<") }
            Button(logic::loadRandom) { Text("random") }
            if(state.number < state.newest) Button(onClick = logic::loadNext) { Text(">") }
            Button(onClick = logic::loadLatest) { Text(">>") }
        }
        Spacer(modifier = Modifier.height(5.dp))
        val urlLauncher = LocalUriHandler.current
        TextButton(onClick = { logic.explain(urlLauncher) }) { Text("explain") }
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
fun ComicViewPane(scale: Float, offset: Offset, onPanZoom: (Float, Offset) -> Unit,
                  content: @Composable () -> Unit) {
    val state = rememberTransformableState { zoomChange, panChange, _ ->
        val newScale = (scale * zoomChange).coerceAtLeast(1.0f)
        val newOffset = offset + panChange
        onPanZoom(newScale, newOffset)
    }

    Box(modifier = Modifier.graphicsLayer(
        scaleX = scale, scaleY = scale,
        translationX = offset.x, translationY = offset.y)
        .transformable(state)) {
        content()
    }
}

@Composable
fun SearchUi(logic: SearchLogic, onResultSelected: (XkcdModel) -> Unit) {
    val state by logic.state.collectAsState()

    SearchBar(state.searchText, logic::searchTextUpdated, state.isExpanded,
        logic::onExpandedChanged, onResultSelected, state.results)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchBar(searchText: String, onSearchTextChanged: (String) -> Unit,
              isExpanded: Boolean, onExpandedChanged: (Boolean) -> Unit,
              onResultSelected: (XkcdModel) -> Unit,
              results: List<XkcdModel>) {
    ExposedDropdownMenuBox(isExpanded, onExpandedChanged) {
        TextField(searchText, onSearchTextChanged)
        ExposedDropdownMenu(isExpanded, { onExpandedChanged(false) }) {
            results.forEach {
                DropdownMenuItem(
                    onClick = {
                        onResultSelected(it)
                        onExpandedChanged(false)
                    }) {
                    Text("${it.num}: ${it.title}")
                }
            }
        }
    }
}
