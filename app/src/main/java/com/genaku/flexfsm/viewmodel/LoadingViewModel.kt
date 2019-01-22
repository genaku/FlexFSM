package com.genaku.flexfsm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.genaku.flexfsm.domain.ILoaderInteractor
import com.genaku.flexfsm.domain.ILoaderPresenter
import com.genaku.flexfsm.domain.IRepository
import com.genaku.flexfsm.domain.LoaderUseCase
import com.genaku.flexfsm.interactor.LoaderInteractor

class LoadingViewModel(repository: IRepository) : ViewModel(), ILoaderPresenter {

    val progressEvent = MutableLiveData<String>()
    val dataEvent = MutableLiveData<String>()
    val hideEvent = MutableLiveData<Boolean>()

    private val useCase = LoaderUseCase(
        presenter = this,
        repository = repository
    )

    val interactor: ILoaderInteractor = LoaderInteractor(useCase)

    override fun init() {
        dataEvent.postValue("start")
    }

    override fun hide() {
        hideEvent.postValue(true)
    }

    override fun show(data: String) {
        dataEvent.postValue(data)
    }

    override fun showProgress(current: Int, total: Int, description: String) {
        progressEvent.postValue("Progress: $current/$total $description")
    }
}