package com.bazaar.screens

import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun CategoriesList(onClick: () -> Unit) {
    val data: ArrayList<String> = arrayListOf()
    data.add("Groceries")
    data.add("Drinks")
    data.add("Snacks")
    data.add("Tobacco")
    data.add("Coffee")
    data.add("Tea")

    // REPLACE ABOVE WITH ACTUAL DATA

    LazyRow(content = {
        items(data.size) { index ->
            CategoryItem(data, index = index)
        }
    })
}

@Preview(showBackground = true)
@Composable
fun CategoriesListPreview(){
    CategoriesList(onClick = {})
}