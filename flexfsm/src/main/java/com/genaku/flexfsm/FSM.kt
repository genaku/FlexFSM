package com.genaku.flexfsm

import java.util.*

open class FSM<STATE_ID : Enum<*>, EVENT>(states: List<State<STATE_ID, EVENT>>) {

    @Volatile
    var event: EVENT? = null

    @Volatile
    var currentState: State<STATE_ID, EVENT>? = null

    @Volatile
    protected var nextState: State<STATE_ID, EVENT>? = null

    @Volatile
    protected var prevState: State<STATE_ID, EVENT>? = null

    private val states = HashMap<STATE_ID, State<STATE_ID, EVENT>>()
    private val stateGroups = ArrayList<StateGroup<STATE_ID, EVENT>>(0)

    private val lock = Any()

    init {
        setup(states)
        start()
    }

    fun getState(stateId: STATE_ID): State<STATE_ID, EVENT>? = states[stateId]

    fun handleEvent(event: EVENT) = synchronized(lock) {
        this.event = event
        handleEvent()
    }

    fun handleEvent() = synchronized(lock) {
        onBeforeHandleEvent()

        handleState()
        doTransition()

        onAfterHandleEvent()
        this.event = null
    }

    override fun toString(): String = currentState.toString()

    protected fun setup(states: List<State<STATE_ID, EVENT>>) {
        onBeforeInit()
        for (state in states) {
            addState(state)
        }

        for (superState in stateGroups) {
            for (stateId in superState.includedIDs) {
                val state = getState(stateId)
                    ?: throw FSMException("Unconfigured state [$stateId] was included into state group")
                state.addStateGroup(superState)
            }
        }
        onAfterInit()
    }

    protected fun addState(state: State<STATE_ID, EVENT>) {
        state.fsm = this

        if (state is IStateGroup) {
            assert(!(state is StateGroup<STATE_ID, EVENT> && state in stateGroups))
            stateGroups.add(state as StateGroup<STATE_ID, EVENT>)
        } else {
            if (currentState == null) {
                currentState = state
            }
            assert(!states.containsValue(state))
            state.id?.apply {
                states[this] = state
            }
        }
    }

    protected fun start() = synchronized(lock) {
        enterState()
        doTransition()
    }

    protected fun doTransition() {
        var nextStateId: STATE_ID? = pickNextStateId()
        while (nextStateId != null) {
            val nextState = getState(nextStateId)
                ?: throw FSMException("Error trying to transit into unconfigured state [$nextStateId]")
            this.nextState = nextState
            changeState()
            nextStateId = pickNextStateId()
        }
    }

    protected fun pickNextStateId(): STATE_ID? {
        val superStates = currentState?.getStateGroups()
        if (superStates != null) {
            for (superState in superStates) {
                val nextStateFromSuper = superState.nextStateId
                if (nextStateFromSuper != null) {
                    return nextStateFromSuper
                }
            }
        }
        return currentState?.nextStateId
    }

    protected fun changeState() {
        onBeforeTransition()

        exitState()

        onBeforeSwitchState()
        prevState = currentState
        currentState = nextState
        onAfterSwitchState()

        enterState()
        nextState = null

        onAfterTransition()
    }

    protected fun enterState() {
        val superStates = currentState?.getStateGroups()
        if (superStates != null)
            for (superState in superStates) {
                if (prevState == null || prevState?.id !in superState.includedIDs) {
                    onBeforeStateEnter(superState)
                    superState.enterInternal()
                    onAfterStateEnter(superState)
                }
            }
        onBeforeStateEnter(currentState)
        currentState?.enterInternal()
        onAfterStateEnter(currentState)
    }

    protected fun handleState() {
        val superStates = currentState?.getStateGroups()
        if (superStates != null) {
            for (superState in superStates) {
                onBeforeStateHandleEvent(superState)
                superState.handleEventInternal()
                onAfterStateHandleEvent(superState)
            }
        }
        onBeforeStateHandleEvent(currentState)
        currentState?.handleEventInternal()
        onAfterStateHandleEvent(currentState)
    }

    protected fun exitState() {
        onBeforeStateExit(currentState)
        currentState?.exitInternal()
        onAfterStateExit(currentState)

        currentState?.clearNextState()
        val superStates = currentState?.getStateGroups()
        if (superStates != null) {
            for (superState in superStates) {
                if (nextState?.id !in superState.includedIDs) {
                    onBeforeStateExit(superState)
                    superState.exitInternal()
                    onAfterStateExit(superState)
                }
                superState.clearNextState()
            }
        }
    }

// {{ Event hooks

    enum class FsmEvent {
        BEFORE_INIT, AFTER_INIT, BEFORE_HANDLE_EVENT, AFTER_HANDLE_EVENT, //
        BEFORE_SWITCH_STATE, AFTER_SWITCH_STATE, //
        BEFORE_TRANSITION, AFTER_TRANSITION, BEFORE_STATE_ENTER, AFTER_STATE_ENTER, //
        BEFORE_STATE_HANDLE_EVENT, AFTER_STATE_HANDLE_EVENT, //
        BEFORE_STATE_EXIT, AFTER_STATE_EXIT
    }

    protected open fun onFSMEvent(fsmEvent: FsmEvent, state: State<STATE_ID, EVENT>?) {}

    protected fun onBeforeInit() {
        onFSMEvent(FsmEvent.BEFORE_INIT, null)
    }

    protected fun onAfterInit() {
        onFSMEvent(FsmEvent.AFTER_INIT, null)
    }

    protected fun onBeforeHandleEvent() {
        onFSMEvent(FsmEvent.BEFORE_HANDLE_EVENT, null)
    }

    protected fun onAfterHandleEvent() {
        onFSMEvent(FsmEvent.AFTER_HANDLE_EVENT, null)
    }

    protected fun onBeforeTransition() {
        onFSMEvent(FsmEvent.BEFORE_TRANSITION, null)
    }

    protected fun onAfterTransition() {
        onFSMEvent(FsmEvent.AFTER_TRANSITION, null)
    }

    protected fun onBeforeSwitchState() {
        onFSMEvent(FsmEvent.BEFORE_SWITCH_STATE, null)
    }

    protected fun onAfterSwitchState() {
        onFSMEvent(FsmEvent.AFTER_SWITCH_STATE, null)
    }

    protected fun onBeforeStateEnter(state: State<STATE_ID, EVENT>?) {
        onFSMEvent(FsmEvent.BEFORE_STATE_ENTER, state)
    }

    protected fun onAfterStateEnter(state: State<STATE_ID, EVENT>?) {
        onFSMEvent(FsmEvent.AFTER_STATE_ENTER, state)
    }

    protected fun onBeforeStateHandleEvent(state: State<STATE_ID, EVENT>?) {
        onFSMEvent(FsmEvent.BEFORE_STATE_HANDLE_EVENT, state)
    }

    protected fun onAfterStateHandleEvent(state: State<STATE_ID, EVENT>?) {
        onFSMEvent(FsmEvent.AFTER_STATE_HANDLE_EVENT, state)
    }

    protected fun onBeforeStateExit(state: State<STATE_ID, EVENT>?) {
        onFSMEvent(FsmEvent.BEFORE_STATE_EXIT, state)
    }

    protected fun onAfterStateExit(state: State<STATE_ID, EVENT>?) {
        onFSMEvent(FsmEvent.AFTER_STATE_EXIT, state)
    }

// }}
}