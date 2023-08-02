package io.github.landrynorris.xkcd.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import io.github.landrynorris.xkcd.components.ComicLogic
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
fun ComicScreen(logic: ComicLogic) {
    LaunchedEffect(Unit) {
        logic.loadLatest()
    }

    val state by logic.state.collectAsState()
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        if(state.imageUrl != null) {
            KamelImage(asyncPainterResource(state.imageUrl!!), state.transcript, modifier = Modifier.weight(1f), alignment = Alignment.TopCenter)
        }
        Text(state.title, fontSize = 24.sp)
        Text(state.altText, fontSize = 18.sp)

        Row {
            Button(onClick = logic::loadFirst) { Text("<<") }
            if(state.number > 1) Button(onClick = logic::loadPrevious) { Text("<") }
            Button(logic::loadRandom) { Text("random") }
            if(state.number < state.newest) Button(onClick = logic::loadNext) { Text(">") }
            Button(onClick = logic::loadLatest) { Text(">>") }
        }
    }
}