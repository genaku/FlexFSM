package com.genaku.flexfsm

class FSMBuilder<STATE_ID : Enum<*>, EVENT> {

    private val statesArray: ArrayList<State<STATE_ID, EVENT>> = ArrayList()

    fun add(state: State<STATE_ID, EVENT>) = this.apply {
        statesArray.add(state)
    }

    fun build(): FSM<STATE_ID, EVENT> =
        FSM(statesArray)

    fun buildObservable(): ObservableFSM<STATE_ID, EVENT> =
        ObservableFSM(statesArray)

    fun buildLogged(name: String): LoggedFSM<STATE_ID, EVENT> =
        LoggedFSM(name, statesArray)
}