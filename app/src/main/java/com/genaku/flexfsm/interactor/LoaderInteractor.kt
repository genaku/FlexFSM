package com.genaku.flexfsm.interactor

import com.genaku.flexfsm.domain.ILoaderInteractor
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

@ObsoleteCoroutinesApi
class LoaderInteractor(private val useCase: ILoaderInteractor) :
    ILoaderInteractor {

    override fun startLoad() {
        runBlocking {
            start()
        }
    }

    private fun start() = GlobalScope.async {
        useCase.startLoad()
    }

    override fun cancel() {
        runBlocking {
            cancelTask()
        }
    }

    private fun cancelTask() = GlobalScope.async {
        useCase.cancel()
    }
}