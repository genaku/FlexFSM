package com.genaku.flexfsm.domain.loader.interfaces

import com.genaku.flexfsm.domain.RepoException

interface ILoaderRepository {
    @Throws (RepoException::class)
    fun loadConfig(): String

    @Throws (RepoException::class)
    fun loadData(config: String): String
}