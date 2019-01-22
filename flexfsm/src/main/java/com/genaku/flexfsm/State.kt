package com.genaku.flexfsm

import java.util.*

open class State<STATE_ID : Enum<*>, EVENT>(open var id: STATE_ID?) : IState {

    var nextStateId: STATE_ID? = null
        protected set

    fun clearNextState() {
        nextStateId = null
    }

    // {{ Dependencies

    lateinit var fsm: FSM<STATE_ID, EVENT>

    private var stateGroups: MutableList<StateGroup<STATE_ID, EVENT>>? = null

    fun getStateGroups(): List<StateGroup<STATE_ID, EVENT>>? {
        return stateGroups
    }

    fun addStateGroup(stateGroup: StateGroup<STATE_ID, EVENT>) {
        if (stateGroups == null)
            stateGroups = ArrayList(1)
        stateGroups?.add(stateGroup)
    }

    // }}

    val event: EVENT?
        get() = fsm.event

    fun next(nextStateId: STATE_ID) {
        this.nextStateId = nextStateId
    }

    fun handleEventInternal() {
        handleEvent()
    }

    open fun handleEvent() {}

    fun exitInternal() {
        exit()
    }

    fun enterInternal() {
        enter()
    }

    open fun exit() {}

    open fun enter() {}

    override fun toString(): String = "${id?.name}[${id?.ordinal}]"
}
