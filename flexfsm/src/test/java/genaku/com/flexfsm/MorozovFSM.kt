package genaku.com.flexfsm

import com.genaku.flexfsm.FSM
import com.genaku.flexfsm.State

class MorozovFSM<STATES : Enum<*>, EVENT>(states: List<State<STATES, EVENT>>) : FSM<STATES, EVENT>(states) {
    val prevStateM: State<STATES, EVENT>?
        get() = super.prevState
}