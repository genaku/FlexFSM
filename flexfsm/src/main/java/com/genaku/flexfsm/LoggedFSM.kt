package com.genaku.flexfsm

import java.util.*

class LoggedFSM<STATE_ID : Enum<*>, EVENT>(val name: String, states: List<State<STATE_ID, EVENT>>) : ObservableFSM<STATE_ID, EVENT>(states) {

    // {{ Properties

    var loggedEvents: EnumSet<FSM.FsmEvent> = EnumSet.noneOf(FSM.FsmEvent::class.java)
        private set

    private var logHandler: LogHandler? = null

    private var logMsg: String = ""

    fun setLoggedEvents(vararg logEvents: FSM.FsmEvent) {
        loggedEvents.clear()
        for (event in logEvents) {
            loggedEvents.add(event)
        }
    }

    fun setLogFull() {
        loggedEvents = EnumSet.allOf(FSM.FsmEvent::class.java)
    }

    fun setLogStandard() {
        setLoggedEvents(
            FSM.FsmEvent.AFTER_SWITCH_STATE,
            FSM.FsmEvent.BEFORE_HANDLE_EVENT, FSM.FsmEvent.BEFORE_STATE_ENTER,
            FSM.FsmEvent.BEFORE_STATE_HANDLE_EVENT,
            FSM.FsmEvent.BEFORE_STATE_EXIT
        )
    }

    fun setLogTransitions() {
        setLoggedEvents(FSM.FsmEvent.AFTER_SWITCH_STATE)
    }

    fun setLogCompact() {
        setLoggedEvents(FSM.FsmEvent.BEFORE_HANDLE_EVENT, FSM.FsmEvent.AFTER_SWITCH_STATE)
    }

    fun setLogHandler(handler: LogHandler) {
        logHandler = handler
    }

    fun removeLogHandler(handler: LogHandler) {
        logHandler = null
    }

    fun getLogMsg(): String {
        return logMsg
    }

    // }}

    override fun onFSMEvent(fsmEvent: FSM.FsmEvent, state: State<STATE_ID, EVENT>?) {
        val logBuilder = StringBuilder()
        logBuilder.append(name)
        logBuilder.append("[")
        val currentState = this.currentState
        if (currentState != null)
            logBuilder.append(currentState.id!!.name)
        else
            logBuilder.append("state unknown")
        logBuilder.append("]: ")
        if (logHandler != null && fsmEvent in loggedEvents) {
            when (fsmEvent) {
                FSM.FsmEvent.BEFORE_INIT, FSM.FsmEvent.AFTER_INIT -> logBuilder.append(fsmEvent.name)
                FSM.FsmEvent.BEFORE_HANDLE_EVENT, FSM.FsmEvent.AFTER_HANDLE_EVENT -> {
                    logBuilder.append(fsmEvent.name)
                    appendEventInfo(logBuilder)
                }
                FSM.FsmEvent.BEFORE_TRANSITION, FSM.FsmEvent.BEFORE_SWITCH_STATE -> {
                    logBuilder.append(fsmEvent.name)
                    appendTransitionInfo(logBuilder, currentState, nextState)
                }
                FSM.FsmEvent.AFTER_TRANSITION, FSM.FsmEvent.AFTER_SWITCH_STATE -> {
                    logBuilder.append(fsmEvent.name)
                    appendTransitionInfo(logBuilder, prevState, currentState)
                }
                FSM.FsmEvent.BEFORE_STATE_HANDLE_EVENT, FSM.FsmEvent.AFTER_STATE_HANDLE_EVENT -> {
                    logBuilder.append(fsmEvent.name)
                    appendStateInfo(logBuilder, state)
                    appendEventInfo(logBuilder)
                }
                FSM.FsmEvent.BEFORE_STATE_ENTER, FSM.FsmEvent.AFTER_STATE_ENTER, FSM.FsmEvent.BEFORE_STATE_EXIT, FSM.FsmEvent.AFTER_STATE_EXIT -> {
                    logBuilder.append(fsmEvent.name)
                    appendStateInfo(logBuilder, state)
                }
            }
            logMsg = logBuilder.toString()
            logHandler?.callback(logMsg)
        }

        super.onFSMEvent(fsmEvent, state)
    }

    private fun appendStateInfo(logBuilder: StringBuilder, state: State<STATE_ID, EVENT>?) {
        logBuilder.append(" (")
        if (state?.id != null)
            logBuilder.append(state.id?.name)
        else
            logBuilder.append(state.toString())
        logBuilder.append(")")
    }

    private fun appendTransitionInfo(logBuilder: StringBuilder, from: State<STATE_ID, EVENT>?, to: State<STATE_ID, EVENT>?) {
        logBuilder.append(" (")
        logBuilder.append(from?.id?.name)
        logBuilder.append("->")
        logBuilder.append(to?.id?.name)
        logBuilder.append(")")
    }

    private fun appendEventInfo(logBuilder: StringBuilder) {
        event?.apply {
            logBuilder.append(" (")
            logBuilder.append(this.toString())
            logBuilder.append(")")
        }
    }

    interface LogHandler {
        fun callback(logMsg: String)
    }
}
