package com.genaku.flexfsm.domain.progress

import com.genaku.flexfsm.IState

interface IProgressState : IState {
    var description: String
}
