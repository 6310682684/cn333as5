package com.example.phoneapp.domain.model

const val NEW_PHONE_ID = -1L

data class PhoneModel(
    val id: Long = NEW_PHONE_ID,
    val title: String = "",
    val content: String = "",
    val color: ColorModel = ColorModel.DEFAULT,
    val tag: TagModel = TagModel.DEFAULT
)