package com.genaku.flexfsm.domain

import com.genaku.flexfsm.FSM
import com.genaku.flexfsm.ObservableFSM
import com.genaku.flexfsm.State
import com.genaku.flexfsm.domain.model.IProgressState
import com.genaku.flexfsm.domain.model.LoaderFSM

class LoaderUseCase(
    private val presenter: ILoaderPresenter,
    private val repository: IRepository
) : ILoaderInteractor, ObservableFSM.FsmObserver {

    private var config: String = ""

    private var data: String = ""

    override fun startLoad() {
        loaderFsm.handleEvent(LoaderFSM.Events.SHOW)
    }

    override fun cancel() {
        loaderFsm.handleEvent(LoaderFSM.Events.HIDE)
    }

    val loaderFsm: ObservableFSM<LoaderFSM.States, LoaderFSM.Events>

    init {
        loaderFsm = LoaderFSM(
            init = { initLoader() },
            loadConfig = { loadConfig() },
            loadData = { loadData() },
            showData = { presenter.show(data) },
            hide = { presenter.hide() }
        ).fsm
        loaderFsm.addObserver(this, FSM.FsmEvent.AFTER_SWITCH_STATE)
    }

    private fun initLoader() {
        config = ""
        data = ""
        presenter.init()
    }

    private fun loadConfig() {
        try {
            config = repository.loadConfig()
            loaderFsm.handleEvent(LoaderFSM.Events.CONFIG_LOADED)
        } catch (e: RepoException) {
            loaderFsm.handleEvent(LoaderFSM.Events.FAILURE)
        }
    }

    private fun loadData() {
        try {
            data = repository.loadData(config)
            loaderFsm.handleEvent(LoaderFSM.Events.DATA_LOADED)
        } catch (e: RepoException) {
            loaderFsm.handleEvent(LoaderFSM.Events.FAILURE)
        }
    }

    override fun onEvent(fsm: ObservableFSM<*, *>, fsmEvent: FSM.FsmEvent, state: State<*, *>?) {
        val newState = fsm.currentState
        if (newState is IProgressState) {
            val current = newState.id?.ordinal ?: 0+1
            val total = LoaderFSM.States.values().size
            presenter.showProgress(current, total, newState.description)
        }
    }
}