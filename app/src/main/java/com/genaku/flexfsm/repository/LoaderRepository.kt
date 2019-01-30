package com.genaku.flexfsm.repository

import com.genaku.flexfsm.domain.loader.interfaces.ILoaderRepository

class LoaderRepository: ILoaderRepository {
    override fun loadConfig(): String {
        Thread.sleep(5000)
        return "config"
    }

    override fun loadData(config: String): String {
        Thread.sleep(7000)
        return "data"
    }
}