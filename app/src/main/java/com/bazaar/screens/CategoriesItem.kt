package com.bazaar.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bazaar.R

@Composable
fun CategoryItem(data: ArrayList<String>, index: Int) {
    Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(10.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.food),
            contentDescription = "Image",
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
                .padding(15.dp)
        )
        Text(text = data[index], style = MaterialTheme.typography.bodyMedium, fontSize = 16.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryItemPreview() {
    val data: ArrayList<String> = arrayListOf()
    data.add("Groceries")
    data.add("Drinks")
    data.add("Snacks")
    data.add("Tobacco")
    data.add("Coffee")
    data.add("Tea")

    CategoryItem(data = data, 0)
}