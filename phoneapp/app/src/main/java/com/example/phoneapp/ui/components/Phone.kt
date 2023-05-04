package com.example.phoneapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.phoneapp.domain.model.PhoneModel
import com.example.phoneapp.util.fromHex

@ExperimentalMaterialApi
@Composable
fun Phone(
    modifier: Modifier = Modifier,
    phone: PhoneModel,
    onPhoneClick: (PhoneModel) -> Unit = {},
) {

    Card(
        shape = RoundedCornerShape(4.dp),
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(),

        ) {
        ListItem(
            text = { Text(text = phone.title, maxLines = 1) },
            secondaryText = {
                Text(text = phone.content, maxLines = 1)
            },
            icon = {
                PhoneColor(
                    color = Color.fromHex(phone.color.hex),
                    size = 40.dp,
                    border = 1.dp
                )
            },
            modifier = Modifier.clickable {
                onPhoneClick.invoke(phone)
            }
        )
    }
}
