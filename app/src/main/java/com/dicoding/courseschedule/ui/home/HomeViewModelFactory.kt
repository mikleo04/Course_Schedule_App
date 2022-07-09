package com.dicoding.courseschedule.ui.home

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.courseschedule.data.DataRepository
import java.lang.reflect.InvocationTargetException

class HomeViewModelFactory(private val repository: DataRepository?): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        try {
            return modelClass.getConstructor(DataRepository::class.java).newInstance(repository)
        } catch (e: InstantiationException) {
            throw RuntimeException("Can't create an instance of $modelClass", e)
        } catch (e: IllegalAccessException) {
            throw RuntimeException("Can't create an instance of $modelClass", e)
        } catch (e: NoSuchMethodException) {
            throw RuntimeException("Can't create an instance of $modelClass", e)
        } catch (e: InvocationTargetException) {
            throw RuntimeException("Can't create an instance of $modelClass", e)
        }
    }

    companion object {
        fun createTheFactory(activity: Activity): HomeViewModelFactory {
            val context = activity.applicationContext
                ?: throw IllegalStateException("Not yet attached to Application")
            return HomeViewModelFactory(DataRepository.getInstance(context))
        }
    }
}