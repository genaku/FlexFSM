package com.genaku.flexfsm.domain

interface IRepository {
    @Throws (RepoException::class)
    fun loadConfig(): String

    @Throws (RepoException::class)
    fun loadData(config: String): String
}