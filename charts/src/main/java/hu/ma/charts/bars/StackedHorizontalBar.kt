package hu.ma.charts.bars

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.unit.dp
import hu.ma.charts.bars.data.StackedBarItem

internal typealias EntryPathFactory = (entry: EntryDrawShape, size: Size) -> Path

internal val EntrySpacing = 2.dp

@Composable
internal fun DrawStackedBar(
  entries: List<StackedBarItem>,
  widthPx: Float,
  entryPathFactory: EntryPathFactory = { entry, size -> createBarEntryShape(entry, size) },
) {
  val total = remember(entries) { entries.sumByDouble { it.value.toDouble() }.toFloat() }
  val width = with(AmbientDensity.current) { widthPx.toDp() }
  val spacingPx = with(AmbientDensity.current) { EntrySpacing.toIntPx() }

  val totalSpacing = (entries.size - 1) * spacingPx

  val values = remember(entries) {
    entries.map {
      it.copy(value = (widthPx - totalSpacing) * it.value / total)
    }
  }

  Row(
    modifier = Modifier.width(width),
    horizontalArrangement = Arrangement.spacedBy(EntrySpacing)
  ) {
    values.forEachIndexed { idx, item ->
      val shape = when {
        idx == 0 && values.size == 1 -> EntryDrawShape.Single
        idx == 0 -> EntryDrawShape.First
        idx == values.lastIndex -> EntryDrawShape.Last
        else -> EntryDrawShape.Middle
      }

      Box(
        modifier = Modifier
          .width(with(AmbientDensity.current) { item.value.toDp() })
          .height(8.dp)
          .drawBehind {
            drawPath(entryPathFactory(shape, size), item.color)
          }
      )
    }
  }
}
