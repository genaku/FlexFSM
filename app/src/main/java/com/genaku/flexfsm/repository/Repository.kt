package com.genaku.flexfsm.repository

import com.genaku.flexfsm.domain.IRepository

class Repository: IRepository {
    override fun loadConfig(): String {
        Thread.sleep(5000)
        return "config"
    }

    override fun loadData(config: String): String {
        Thread.sleep(7000)
        return "data"
    }
}