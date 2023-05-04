package com.example.phoneapp.screens

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.phoneapp.routing.MyPhonesRouter
import com.example.phoneapp.routing.Screen
import com.example.phoneapp.viewmodel.MainViewModel
import com.example.phoneapp.R
import com.example.phoneapp.domain.model.ColorModel
import com.example.phoneapp.domain.model.NEW_PHONE_ID
import com.example.phoneapp.domain.model.PhoneModel
import com.example.phoneapp.domain.model.TagModel
import com.example.phoneapp.ui.components.PhoneColor
import com.example.phoneapp.util.fromHex
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@ExperimentalMaterialApi
@Composable
fun SavePhoneScreen(viewModel: MainViewModel) {
    val phoneEntry by viewModel.phoneEntry.observeAsState(PhoneModel())

    val colors: List<ColorModel> by viewModel.colors.observeAsState(listOf())

    val tags: List<TagModel> by viewModel.tags.observeAsState(listOf())

    val bottomDrawerState = rememberBottomDrawerState(BottomDrawerValue.Closed)

    val coroutineScope = rememberCoroutineScope()

    val movePhoneToTrashDialogShownState = rememberSaveable { mutableStateOf(false) }

    val check = rememberSaveable { mutableStateOf(false) }

    BackHandler {
        if (bottomDrawerState.isOpen) {
            coroutineScope.launch { bottomDrawerState.close() }
        } else {
            MyPhonesRouter.navigateTo(Screen.Phones)
        }
    }


    Scaffold(
        topBar = {
            val isEditingMode: Boolean = phoneEntry.id != NEW_PHONE_ID
            SavePhoneTopAppBar(
                isEditingMode = isEditingMode,
                onBackClick = { MyPhonesRouter.navigateTo(Screen.Phones) },
                onSavePhoneClick = { viewModel.savePhone(phoneEntry) },
                onOpenColorPickerClick = {
                    check.value = true
                    coroutineScope.launch { bottomDrawerState.open() }

                },
                onOpenTagPickerClick = {
                    check.value = false
                    coroutineScope.launch { bottomDrawerState.open()}

                },
                onDeletePhoneClick = {
                    movePhoneToTrashDialogShownState.value = true
                },

                )
        }
    ) {


        BottomDrawer(
            drawerState = bottomDrawerState,
            drawerContent = { if(check.value) {
                ColorPicker(
                    colors = colors,
                    onColorSelect = { color ->
                        viewModel.onPhoneEntryChange(phoneEntry.copy(color = color))
                    })}
                if(!check.value) {

                    TagPicker(
                        tags = tags,
                        onTagSelect = { tag ->
                            viewModel.onPhoneEntryChange(phoneEntry.copy(tag = tag))
                        })}


            }
        )



        {
            SavePhoneContent(
                phone= phoneEntry,
                onPhoneChange = { updatePhoneEntry ->
                    viewModel.onPhoneEntryChange(updatePhoneEntry)
                }
            )
        }

        if (movePhoneToTrashDialogShownState.value) {
            AlertDialog(
                onDismissRequest = {
                    movePhoneToTrashDialogShownState.value = false
                },
                title = {
                    Text("Move phone number to the trash?")
                },
                text = {
                    Text(
                        "Are you sure you want to " +
                                "move this phone number to the trash?"
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.movePhoneToTrash(phoneEntry)
                    }) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        movePhoneToTrashDialogShownState.value = false
                    }) {
                        Text("Dismiss")
                    }
                }
            )
        }


    }

}

@Composable
fun SavePhoneTopAppBar(
    isEditingMode: Boolean,
    onBackClick: () -> Unit,
    onSavePhoneClick: () -> Unit,
    onOpenColorPickerClick: () -> Unit,
    onOpenTagPickerClick: () -> Unit,
    onDeletePhoneClick: () -> Unit,
) {
    TopAppBar(
        backgroundColor = Color.DarkGray,
        title = {
            Text(
                text = "Person",
                color = MaterialTheme.colors.onPrimary
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back Button",
                    tint = MaterialTheme.colors.onPrimary
                )
            }
        },
        actions = {
            IconButton(onClick = onSavePhoneClick) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Save Phone number Button",
                    tint = MaterialTheme.colors.onPrimary
                )
            }
            IconButton(onClick = onOpenTagPickerClick,) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_color_lens_24),
                    contentDescription = "Save Phone number Button",
                    tint = MaterialTheme.colors.onPrimary
                )
            }

            IconButton(onClick = onOpenColorPickerClick) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_color_lens_24),
                    contentDescription = "Open Color Picker Button",
                    tint = MaterialTheme.colors.onPrimary
                )

            }

            if (isEditingMode) {
                IconButton(onClick = onDeletePhoneClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Phone number Button",
                        tint = MaterialTheme.colors.onPrimary
                    )
                }
            }
        }
    )
}

@Composable
private fun SavePhoneContent(
    phone: PhoneModel,
    onPhoneChange: (PhoneModel) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        LocalFocusManager.current
        ContentTextField(
            label = "Contract Name",
            text = phone.title,
        ) { newTitle ->
            onPhoneChange.invoke(phone.copy(title = newTitle))
        }

        ContentTextField(
            modifier = Modifier
                .heightIn(max = 240.dp)
                .padding(top = 16.dp),
            label = "Phone Number",
            text = phone.content,
        ) { newContent ->
            onPhoneChange.invoke(phone.copy(content = newContent))
        }

        PickedColor(color = phone.color)
        PickedTag(tag = phone.tag)
    }
}

@Composable
private fun ContentTextField(
    modifier: Modifier = Modifier,
    label: String,
    text: String,
    onTextChange: (String) -> Unit
) {
    TextField(
        value = text,
        onValueChange = onTextChange,
        label = { Text(label) },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = MaterialTheme.colors.surface
        )
    )
}

@Composable
private fun PickedColor(color: ColorModel) {
    Row(
        Modifier
            .padding(8.dp)
            .padding(top = 16.dp)
    ) {
        Text(
            text = "Picked Color",
            fontSize = 18.sp,
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        )
        PhoneColor(
            color = Color.fromHex(color.hex),
            size = 50.dp,
            border = 1.dp,
            modifier = Modifier.padding(4.dp)
        )
    }
}

@Composable
private fun ColorPicker(
    colors: List<ColorModel>,
    onColorSelect: (ColorModel) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Color Picker",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(colors.size) { itemIndex ->
                val color = colors[itemIndex]
                ColorItem(
                    color = color,
                    onColorSelect = onColorSelect
                )
            }
        }
    }
}


@Composable
fun ColorItem(
    color: ColorModel,
    onColorSelect: (ColorModel) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = {
                    onColorSelect(color)
                }
            )
    ) {
        PhoneColor(
            modifier = Modifier.padding(10.dp),
            color = Color.fromHex(color.hex),
            size = 80.dp,
            border = 2.dp
        )
        Text(
            text = color.name,
            fontSize = 22.sp,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .align(Alignment.CenterVertically)
        )
    }
}

@Composable
private fun TagPicker(
    tags: List<TagModel>,
    onTagSelect: (TagModel) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Tag Picker",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(tags.size) { itemIndex ->
                val tag = tags[itemIndex]
                TagItem(tag = tag, onTagSelect = onTagSelect)
            }
        }
    }
}

@Composable
fun TagItem(
    tag: TagModel,
    onTagSelect: (TagModel) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = {
                    onTagSelect(tag)
                }
            )
    ) {
        Text(
            text = tag.nameTag,
            fontSize = 18.sp,
            modifier = Modifier
                .padding(all = 6.dp)
                .align(Alignment.CenterVertically)
        )
    }
}

@Composable
private fun PickedTag(tag: TagModel) {
    Row(
        Modifier
            .padding(8.dp)
            .padding(top = 18.dp)
    ) {
        Text(
            text = "Picked Tag",
            fontSize = 18.sp,
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        )
        Text(
            text = tag.nameTag,
            fontSize = 18.sp,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .align(Alignment.CenterVertically)
        )
    }
}