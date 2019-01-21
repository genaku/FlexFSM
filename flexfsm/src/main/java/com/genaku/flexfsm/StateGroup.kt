package com.genaku.flexfsm

import java.util.*

open class StateGroup<STATE_ID : Enum<*>, EVENT>(vararg includedIds: STATE_ID) : State<STATE_ID, EVENT>(null),
    IStateGroup {

    var includedIDs: MutableList<STATE_ID> = ArrayList(2)

    init {
        includedIDs.addAll(Arrays.asList(*includedIds))
    }

    private var stateName: String? = null

    override fun toString(): String {
        if (stateName == null) {
            val sb = StringBuilder("StateGroup for [")
            var firstToken = true
            for (stateId in includedIDs) {
                if (firstToken)
                    firstToken = false
                else
                    sb.append(", ")
                sb.append(stateId.name)
            }
            sb.append("]")
            stateName = sb.toString()
        }

        return stateName.toString()
    }
}
