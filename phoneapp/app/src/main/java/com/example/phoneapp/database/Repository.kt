package com.example.phoneapp.database

import android.provider.ContactsContract.CommonDataKinds.Phone
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.phoneapp.domain.model.ColorModel
import com.example.phoneapp.domain.model.PhoneModel
import com.example.phoneapp.domain.model.TagModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Repository(
    private val phoneDao: PhoneDao,
    private val colorDao: ColorDao,
    private val dbMapper: DbMapper,
    private val tagDao: TagDao,
) {

    private val phonesNotInTrashLiveData: MutableLiveData<List<PhoneModel>> by lazy {
        MutableLiveData<List<PhoneModel>>()
    }

    fun getAllPhonesNotInTrash(): LiveData<List<PhoneModel>> = phonesNotInTrashLiveData

    private val phonesInTrashLiveData: MutableLiveData<List<PhoneModel>> by lazy {
        MutableLiveData<List<PhoneModel>>()
    }

    init {
        initDatabase(this::updatePhonesLiveData)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun initDatabase(postInitAction: () -> Unit) {
        GlobalScope.launch {
            // Prepopulate colors
            val colors = ColorDbModel.DEFAULT_COLORS.toTypedArray()
            val dbColors = colorDao.getAllSync()
            if (dbColors.isEmpty()) {
                colorDao.insertAll(*colors)

            }

            val tags = TagDbModel.DEFAULT_TAGS.toTypedArray()
            val dbTags = tagDao.getAllSync()
            if (dbTags.isEmpty()) {
                tagDao.insertAll(*tags)

            }

            val phones = PhoneDbModel.DEFAULT_NOTES.toTypedArray()
            val dbPhones = phoneDao.getAllSync()
            if (dbPhones.isEmpty()) {
                phoneDao.insertAll(*phones)
            }

            postInitAction.invoke()
        }
    }

    private fun getAllPhonesDependingOnTrashStateSync(inTrash: Boolean): List<PhoneModel> {
        val colorDbModels: Map<Long, ColorDbModel> = colorDao.getAllSync().associateBy { it.id }
        val tagDbModels: Map<Long, TagDbModel> = tagDao.getAllSync().associateBy { it.id }
        val dbPhones: List<PhoneDbModel> =
            phoneDao.getAllSync().filter { it.isInTrash == inTrash }
        return dbMapper.mapPhones(dbPhones, colorDbModels, tagDbModels)
    }

    fun insertPhone(phone: PhoneModel) {
        phoneDao.insert(dbMapper.mapDbPhone(phone))
        updatePhonesLiveData()
    }

    fun movePhoneToTrash(phoneId: Long) {
        val dbPhone = phoneDao.findByIdSync(phoneId)
        val newDbPhone = dbPhone.copy(isInTrash = true)
        phoneDao.insert(newDbPhone)
        updatePhonesLiveData()
    }

    fun getAllColors(): LiveData<List<ColorModel>> =
        Transformations.map(colorDao.getAll()) { dbMapper.mapColors(it) }

    fun getAllTags(): LiveData<List<TagModel>> =
        Transformations.map(tagDao.getAll()) { dbMapper.mapTags(it) }

    private fun updatePhonesLiveData() {
        phonesNotInTrashLiveData.postValue(getAllPhonesDependingOnTrashStateSync(false))
        phonesInTrashLiveData.postValue(getAllPhonesDependingOnTrashStateSync(true))
    }
}