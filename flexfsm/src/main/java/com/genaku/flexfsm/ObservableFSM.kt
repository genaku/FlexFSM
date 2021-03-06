package com.genaku.flexfsm

import java.util.*
import java.util.concurrent.ConcurrentHashMap

open class ObservableFSM<STATE_ID : Enum<*>, EVENT>(states: List<State<STATE_ID, EVENT>>) :
    FSM<STATE_ID, EVENT>(states) {

    private val observers = ConcurrentHashMap<FsmObserver, EnumSet<FsmEvent>>(1)

    fun addObserver(observer: FsmObserver, events: EnumSet<FSM.FsmEvent>) {
        observers[observer] = events
    }

    fun addObserver(observer: FsmObserver, vararg events: FSM.FsmEvent) {
        val enumSet = EnumSet.noneOf(FSM.FsmEvent::class.java)
        Collections.addAll(enumSet, *events)
        addObserver(observer, enumSet)
    }

    fun removeObserver(observer: FsmObserver) {
        observers.remove(observer)
    }

    override fun onFSMEvent(fsmEvent: FSM.FsmEvent, state: State<STATE_ID, EVENT>?) {
        observers ?: return
        observers.keys.forEach { observer ->
            val events = observers[observer]
            if (events != null && fsmEvent in events) {
                observer.onEvent(this, fsmEvent, state)
            }
        }
    }

    interface FsmObserver {
        fun onEvent(fsm: ObservableFSM<*, *>, fsmEvent: FSM.FsmEvent, state: State<*, *>?)
    }
}
