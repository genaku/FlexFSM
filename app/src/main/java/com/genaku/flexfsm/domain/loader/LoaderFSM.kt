package com.genaku.flexfsm.domain.loader

import com.genaku.flexfsm.FSMBuilder
import com.genaku.flexfsm.State
import com.genaku.flexfsm.StateGroup
import com.genaku.flexfsm.domain.progress.ProgressState

class LoaderFSM(
    init: () -> Unit,
    loadConfig: () -> Unit,
    loadData: () -> Unit,
    showData: () -> Unit,
    hide: () -> Unit
) {

    enum class States {
        INIT, LOAD_CONFIG, LOAD_DATA, SHOW, HIDE
    }

    enum class Events {
        CONFIG_LOADED, DATA_LOADED, SHOW, HIDE, FAILURE
    }

    val fsm = FSMBuilder<States, Events>()
        .add(object : State<States, Events>(
            States.INIT
        ) {
            override fun enter() {
                init()
                next(States.HIDE)
            }
        })
        .add(object : ProgressState<States, Events>(
            States.LOAD_CONFIG,
            "Configuration loading..."
        ) {
            override fun enter() {
                loadConfig()
            }

            override fun handleEvent() {
                when (event) {
                    Events.CONFIG_LOADED -> next(States.LOAD_DATA)
                    Events.HIDE -> next(States.INIT)
                    else -> {}
                }
            }
        })
        .add(object : ProgressState<States, Events>(
            States.LOAD_DATA,
            "Data loading..."
        ) {
            override fun enter() {
                loadData()
            }

            override fun handleEvent() {
                when (event) {
                    Events.DATA_LOADED -> next(States.SHOW)
                    Events.HIDE -> next(States.INIT)
                    else -> {}
                }
            }
        })
        .add(object : State<States, Events>(
            States.SHOW
        ) {
            override fun enter() {
                showData()
            }
            override fun handleEvent() {
                if (event == Events.SHOW) {
                    next(States.LOAD_CONFIG)
                }
            }
        })
        .add(object : State<States, Events>(
            States.HIDE
        ) {
            override fun enter() {
                hide()
            }

            override fun handleEvent() {
                if (event == Events.SHOW) {
                    next(States.LOAD_CONFIG)
                }
            }
        })
        .add(object : StateGroup<States, Events>(
            States.LOAD_CONFIG,
            States.LOAD_DATA
        ) {
            override fun handleEvent() {
                if (event == Events.FAILURE) {
                    next(States.HIDE)
                }
            }
        })
        .add(object : StateGroup<States, Events>(
            States.LOAD_CONFIG,
            States.LOAD_DATA,
            States.SHOW
        ) {
            override fun handleEvent() {
                if (event == Events.HIDE) {
                    next(States.HIDE)
                }
            }
        })
        .buildObservable()
}