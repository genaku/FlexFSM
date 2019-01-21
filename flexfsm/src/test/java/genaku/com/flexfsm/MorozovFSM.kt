package genaku.com.flexfsm

import com.genaku.flexfsm.FSM
import com.genaku.flexfsm.State

class MorozovFSM<STATES : Enum<*>, EVENT>(vararg statesArray: State<STATES, EVENT>) : FSM<STATES, EVENT>(*statesArray) {
    val prevStateM: State<STATES, EVENT>?
        get() = super.prevState
}