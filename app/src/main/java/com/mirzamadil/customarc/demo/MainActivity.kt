package com.mirzamadil.customarc.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.mirzamadil.customarc.ArcProgressView

private data class ColorPalette(val name: String, val colors: List<Color>)

private val colorPalettes = listOf(
    ColorPalette("Sunset", listOf(Color(0xFFE5484D), Color(0xFF9C4FD6), Color(0xFF4C6FFF))),
    ColorPalette("Ocean", listOf(Color(0xFF00BFA5), Color(0xFF00ACC1), Color(0xFF3F51B5))),
    ColorPalette("Forest", listOf(Color(0xFF8BC34A), Color(0xFF4CAF50), Color(0xFF2E7D32))),
    ColorPalette("Amber", listOf(Color(0xFFFFCA28), Color(0xFFFFA000), Color(0xFFE65100))),
)

private data class FontOption(val name: String, val fontFamily: FontFamily?)

private val fontOptions = listOf(
    FontOption("Default", null),
    FontOption("Serif", FontFamily.Serif),
    FontOption("Monospace", FontFamily.Monospace),
    FontOption("Cursive", FontFamily.Cursive),
)

private val horizontalListProgress = listOf(10f, 25f, 40f, 55f, 70f, 85f, 100f)

private data class CardColorOption(val name: String, val color: Color)

private val cardColorOptions = listOf(
    CardColorOption("Surface", Color(0xFFEDE7F6)),
    CardColorOption("White", Color(0xFFFFFFFF)),
    CardColorOption("Mint", Color(0xFFE0F5EC)),
    CardColorOption("Peach", Color(0xFFFFE9DC)),
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    DemoScreen()
                }
            }
        }
    }
}

@Composable
private fun DemoScreen() {
    var progress by remember { mutableFloatStateOf(40f) }
    var deviceOffline by remember { mutableStateOf(true) }
    var paletteIndex by remember { mutableIntStateOf(0) }
    var fontIndex by remember { mutableIntStateOf(0) }
    var cardColorIndex by remember { mutableIntStateOf(0) }
    var cardHeight by remember { mutableFloatStateOf(220f) }
    val segmentColors = colorPalettes[paletteIndex].colors
    val fontFamily = fontOptions[fontIndex].fontFamily
    val cardColor = cardColorOptions[cardColorIndex].color

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            GaugeCard(color = cardColor) {
                ArcProgressView(
                    progress = progress,
                    segmentColors = segmentColors,
                    fontFamily = fontFamily,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(cardHeight.dp),
                )
            }

            GaugeCard(color = cardColor) {
                ArcProgressView(
                    progress = progress,
                    segmentColors = segmentColors,
                    fontFamily = fontFamily,
                    label = "Battery",
                    statusText = if (deviceOffline) "Device offline" else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(cardHeight.dp),
                )
            }

            Text(text = "Progress: ${progress.toInt()}%")

            Slider(
                value = progress,
                onValueChange = { progress = it },
                valueRange = 0f..100f,
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = "Device offline")
                Switch(checked = deviceOffline, onCheckedChange = { deviceOffline = it })
            }

            Text(text = "Color palette")

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                colorPalettes.forEachIndexed { index, palette ->
                    ColorSwatch(
                        colors = palette.colors,
                        selected = index == paletteIndex,
                        onClick = { paletteIndex = index },
                    )
                }
            }

            Text(text = "Typography")

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                fontOptions.forEachIndexed { index, option ->
                    FilterChip(
                        selected = index == fontIndex,
                        onClick = { fontIndex = index },
                        label = { Text(text = option.name) },
                    )
                }
            }

            Text(text = "Card color")

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                cardColorOptions.forEachIndexed { index, option ->
                    SolidSwatch(
                        color = option.color,
                        selected = index == cardColorIndex,
                        onClick = { cardColorIndex = index },
                    )
                }
            }

            Text(text = "Card size: ${cardHeight.toInt()}dp")

            Slider(
                value = cardHeight,
                onValueChange = { cardHeight = it },
                valueRange = 140f..360f,
            )

            Text(text = "Horizontal list")

            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val itemSpacing = 12.dp
                val visibleCount = 3
                val cardWidth = (maxWidth - itemSpacing * (visibleCount - 1)) / visibleCount

                LazyRow(horizontalArrangement = Arrangement.spacedBy(itemSpacing)) {
                    itemsIndexed(horizontalListProgress) { index, itemProgress ->
                        GaugeCard(
                            modifier = Modifier.width(cardWidth),
                            contentPadding = PaddingValues(vertical = 10.dp, horizontal = 6.dp),
                        ) {
                            ArcProgressView(
                                progress = itemProgress,
                                segmentColors = colorPalettes[index % colorPalettes.size].colors,
                                fontFamily = fontFamily,
                                segmentLength = 12.dp,
                                segmentThickness = 4.dp,
                                valueStyle = MaterialTheme.typography.titleLarge,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorSwatch(colors: List<Color>, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Brush.sweepGradient(colors))
            .border(
                width = if (selected) 3.dp else 0.dp,
                color = MaterialTheme.colorScheme.onSurface,
                shape = CircleShape,
            )
            .clickable(onClick = onClick),
    )
}

@Composable
private fun SolidSwatch(color: Color, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(color)
            .border(
                width = if (selected) 3.dp else 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (selected) 1f else 0.2f),
                shape = CircleShape,
            )
            .clickable(onClick = onClick),
    )
}

@Composable
private fun GaugeCard(
    modifier: Modifier = Modifier.fillMaxWidth(),
    color: Color = MaterialTheme.colorScheme.surface,
    contentPadding: PaddingValues = PaddingValues(vertical = 12.dp),
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = color,
        tonalElevation = 1.dp,
    ) {
        Column(modifier = Modifier.padding(contentPadding)) {
            content()
        }
    }
}
