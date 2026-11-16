package com.flipcoin.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class SelectableChip(
    val id: String,
    val label: String,
    val selected: Boolean,
)

/** A grid of toggleable chips, used wherever the user picks topics. */
@Composable
fun SelectableChipGrid(chips: List<SelectableChip>, onChipClick: (String) -> Unit, modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 140.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 16.dp),
        modifier = modifier,
    ) {
        items(chips, key = { it.id }) { chip ->
            FilterChip(
                selected = chip.selected,
                onClick = { onChipClick(chip.id) },
                label = { Text(chip.label) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
