package com.genaku.flexfsm.domain.loader.interfaces

interface ILoaderPresenter {
    fun init()
    fun hide()
    fun show(data: String)
    fun showProgress(current: Int, total: Int, description: String)
}