package com.example.phoneapp.domain.model

import com.example.phoneapp.database.TagDbModel

data class TagModel(
    val id: Long,
    val nameTag: String,
) {
    companion object {
        val DEFAULT = with(TagDbModel.DEFAULT_TAG) { TagModel(id, nameTag) }
    }
}