package genaku.com.flexfsm

import com.genaku.flexfsm.FSM
import com.genaku.flexfsm.State
import com.genaku.flexfsm.StateGroup
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class KotlinSample {

    private lateinit var a: FSM<A, String>

    private var log: String = ""

    private enum class A {
        INIT, SHOW, SAVE, HIDE
    }

    private fun <E : Any> createA(): FSM<A, E> = FSM(
            object : State<A, E>(A.INIT) {
                override fun enter() {
                    init()
                    next(A.SHOW)
                }
            },
            object : State<A, E>(A.SHOW) {
                override fun enter() {
                    show()
                }

                override fun handleEvent() {
                    if (event() == "SaveEvent") next(A.SAVE)
                }
            },
            object : State<A, E>(A.SAVE) {
                override fun enter() {
                    save()
                    next(A.HIDE)
                }
            },
            object : State<A, E>(A.HIDE) {
                override fun enter() {
                    hide()
                }

                override fun handleEvent() {
                    if (event() == "ShowEvent") next(A.SHOW)
                }
            },
            object : StateGroup<A, E>(A.SHOW, A.SAVE) {
                override fun handleEvent() {
                    if (event() == "HideEvent") next(A.HIDE)
                }
            }
    )

    @Before
    @Throws(Exception::class)
    fun setUp() {
        log = ""
        a = createA()
    }

    @Test
    fun testSample() {
        a.handleEvent("SaveEvent")
        a.handleEvent("ShowEvent")
        a.handleEvent("HideEvent")
        Assert.assertEquals("init-show-save-hide-show-hide", log)
    }

    private fun init() {
        log("init")
    }

    private fun show() {
        log("show")
    }

    private fun hide() {
        log("hide")
    }

    private fun save() {
        log("save")
    }

    private fun log(msg: String) {
        if (log.isNotEmpty())
            log += "-"
        log += msg
    }

}
