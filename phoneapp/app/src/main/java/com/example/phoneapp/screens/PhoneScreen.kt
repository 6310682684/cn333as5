package com.example.phoneapp.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import com.example.phoneapp.viewmodel.MainViewModel


import com.example.phoneapp.domain.model.PhoneModel
import com.example.phoneapp.ui.components.Phone


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@ExperimentalMaterialApi
@Composable
fun PhonesScreen(viewModel: MainViewModel) {
    val phones by viewModel.phonesNotInTrash.observeAsState(listOf())
    val scaffoldState: ScaffoldState = rememberScaffoldState()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                backgroundColor = Color.DarkGray,
                title = {
                    Text(
                        text = "Phone book App",
                        color = MaterialTheme.colors.onPrimary
                    )
                },
            )
        },

        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(
                backgroundColor = Color.Black,
                onClick = { viewModel.onCreateNewPhoneClick() },
                contentColor = MaterialTheme.colors.background,
                content = {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Phone Number Button"
                    )
                }
            )
        }
    ) {
        if (phones.isNotEmpty()) {
            PhonesList(
                phones = phones,
                onPhoneClick = { viewModel.onPhoneClick(it) }
            )
        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun PhonesList(
    phones: List<PhoneModel>,
    onPhoneClick: (PhoneModel) -> Unit
) {
    LazyColumn {
        items(count = phones.size) { noteIndex ->
            val phone = phones[noteIndex]
            Phone(
                phone = phone,
                onPhoneClick = onPhoneClick,
            )
        }
    }
}
