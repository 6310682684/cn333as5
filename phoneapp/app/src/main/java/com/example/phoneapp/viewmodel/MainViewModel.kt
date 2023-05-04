package com.example.phoneapp.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.phoneapp.database.AppDatabase
import com.example.phoneapp.database.DbMapper
import com.example.phoneapp.database.Repository
import com.example.phoneapp.domain.model.ColorModel
import com.example.phoneapp.domain.model.PhoneModel
import com.example.phoneapp.domain.model.TagModel
import com.example.phoneapp.routing.MyPhonesRouter
import com.example.phoneapp.routing.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(application: Application) : ViewModel() {
    val phonesNotInTrash: LiveData<List<PhoneModel>> by lazy {
        repository.getAllPhonesNotInTrash()
    }

    private var _phoneEntry = MutableLiveData(PhoneModel())

    val phoneEntry: LiveData<PhoneModel> = _phoneEntry

    val colors: LiveData<List<ColorModel>> by lazy {
        repository.getAllColors()
    }

    val tags: LiveData<List<TagModel>> by lazy {
        repository.getAllTags()
    }



    private val repository: Repository

    init {
        val db = AppDatabase.getInstance(application)
        repository = Repository(db.phoneDao(), db.colorDao(), DbMapper() , db.tagDao())
    }

    fun onCreateNewPhoneClick() {
        _phoneEntry.value = PhoneModel()
        MyPhonesRouter.navigateTo(Screen.SavePhone)
    }

    fun onPhoneClick(phone: PhoneModel) {
        _phoneEntry.value = phone
        MyPhonesRouter.navigateTo(Screen.SavePhone)
    }

    fun onPhoneEntryChange(phone: PhoneModel) {
        _phoneEntry.value = phone
    }

    fun savePhone(phone: PhoneModel) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.insertPhone(phone)

            withContext(Dispatchers.Main) {
                MyPhonesRouter.navigateTo(Screen.Phones)

                _phoneEntry.value = PhoneModel()
            }
        }
    }

    fun movePhoneToTrash(phone: PhoneModel) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.movePhoneToTrash(phone.id)

            withContext(Dispatchers.Main) {
                MyPhonesRouter.navigateTo(Screen.Phones)
            }
        }
    }
}