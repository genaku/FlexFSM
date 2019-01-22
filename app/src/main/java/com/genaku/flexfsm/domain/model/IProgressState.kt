package com.genaku.flexfsm.domain.model

import com.genaku.flexfsm.IState

interface IProgressState : IState {
    var description: String
}
