package com.genaku.flexfsm.domain

import com.genaku.flexfsm.IState

interface IProgressState : IState {
    var description: String
}
