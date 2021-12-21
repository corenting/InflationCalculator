package fr.corenting.convertisseureurofranc

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import fr.corenting.convertisseureurofranc.converters.ConverterAbstract
import java.lang.IllegalStateException


class ConverterViewModelFactory(private val initConverter: ConverterAbstract) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ConverterViewModel(initConverter) as T
    }
}

class ConverterViewModel(initConverter: ConverterAbstract) : ViewModel() {
    private val converter = MutableLiveData<ConverterAbstract>().apply {
        postValue(initConverter)
    }

    fun getConverter(): LiveData<ConverterAbstract> {
        return converter
    }

    fun setConverter(newConverter: ConverterAbstract) {
        converter.postValue(newConverter)
    }

    fun doConversion(yearOfOrigin: Int, yearOfResult: Int, amount: Float): Float {
        val currentConverter = converter.value
        if (currentConverter != null) {
            return currentConverter.convertFunction(
                yearOfOrigin,
                yearOfResult,
                amount
            )
        } else {
            throw IllegalStateException()
        }
    }
}
