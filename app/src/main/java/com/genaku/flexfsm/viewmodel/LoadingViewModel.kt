package com.genaku.flexfsm.viewmodel

import androidx.lifecycle.ViewModel
import com.genaku.flexfsm.domain.ILoaderInteractor
import com.genaku.flexfsm.domain.ILoaderPresenter
import com.genaku.flexfsm.domain.IRepository
import com.genaku.flexfsm.domain.LoaderUseCase

class LoadingViewModel(repository: IRepository): ViewModel(), ILoaderPresenter {

    private val useCase = LoaderUseCase(
        presenter = this,
        repository = repository
    )

    val interactor: ILoaderInteractor = useCase

    override fun init() {
    }

    override fun hide() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun show(data: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showProgress(current: Int, total: Int, description: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}