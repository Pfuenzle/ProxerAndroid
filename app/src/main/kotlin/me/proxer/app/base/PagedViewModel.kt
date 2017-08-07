package me.proxer.app.base

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.proxer.app.util.ErrorUtils
import me.proxer.library.entitiy.ProxerIdItem

/**
 * @author Ruben Gees
 */
abstract class PagedViewModel<T>(application: Application) : BaseViewModel<List<T>>(application) {

    val refreshError = MutableLiveData<ErrorUtils.ErrorAction?>()

    protected var hasReachedEnd = false
    protected var page = 0

    abstract protected val itemsOnPage: Int

    override fun load() {
        dataDisposable?.dispose()
        dataDisposable = dataSingle
                .doAfterSuccess { newData -> hasReachedEnd = newData.size < itemsOnPage }
                .map { newData ->
                    data.value.let { existingData ->
                        when (existingData) {
                            null -> newData
                            else -> when (page) {
                                0 -> newData + existingData.filter { item ->
                                    newData.find { oldItem -> areItemsTheSame(oldItem, item) } == null
                                }
                                else -> existingData.filter { item ->
                                    newData.find { oldItem -> areItemsTheSame(oldItem, item) } == null
                                } + newData
                            }
                        }
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    refreshError.value = null
                    error.value = null
                    isLoading.value = true
                }
                .doAfterSuccess { data.value?.size?.div(itemsOnPage) ?: 0 }
                .doAfterTerminate {
                    isLoading.value = false
                }
                .subscribe({
                    refreshError.value = null
                    error.value = null
                    data.value = it
                }, {
                    if (page == 0 && data.value?.size ?: 0 > 0) {
                        refreshError.value = ErrorUtils.handle(it)
                    } else {
                        error.value = ErrorUtils.handle(it)
                    }
                })
    }

    override fun loadIfPossible() {
        if (!hasReachedEnd) {
            super.loadIfPossible()
        }
    }

    override fun refresh() {
        page = 0

        load()
    }

    override fun reload() {
        refreshError.value = null
        hasReachedEnd = false
        page = 0

        super.reload()
    }

    open protected fun areItemsTheSame(old: T, new: T) = when {
        old is ProxerIdItem && new is ProxerIdItem -> old.id == new.id
        else -> old == new
    }
}
