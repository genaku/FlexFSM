package com.genaku.flexfsm.domain

import com.genaku.flexfsm.State

open class ProgressState<STATE_ID : Enum<*>, EVENT>(
    override var id: STATE_ID?,
    override var description: String = ""
) : State<STATE_ID, EVENT>(id), IProgressState
