package com.bazaar.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bazaar.theme.PrimaryGreen

@Composable
fun MainScreenHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth(1f)
            .padding(8.dp)
    ) {
        Text("Bazaar", style = MaterialTheme.typography.headlineLarge, color = PrimaryGreen)
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenHeaderPreview() {
    MainScreenHeader()
}