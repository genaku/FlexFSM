package genaku.com.flexfsm;

import com.genaku.flexfsm.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.EnumSet;

public class FSMTestObservableFSM {
    private ObservableFSM<A, String> a;

    private enum A {
        ONE, TWO, THREE
    }

    @SuppressWarnings("unchecked")
    private <E extends Object> ObservableFSM<A, E> createA0() {
        return new FSMBuilder<A, E>()
                .add(new State<A, E>(A.ONE) {
                    public void handleEvent() {
                        next(A.TWO);
                    }
                })
                .add(new State<A, E>(A.TWO) {
                    public void handleEvent() {
                        next(A.THREE);
                    }
                })
                .add(new State<A, E>(A.THREE) {
                    public void handleEvent() {
                        next(A.ONE);
                    }
                })
                .add(new StateGroup<A, E>(A.ONE, A.TWO))
                .add(new StateGroup<A, E>(A.TWO, A.THREE)
                )
                .buildObservable();
    }

    private String log;

    @Before
    public void setUp() throws Exception {
        log = "";
        a = createA0();
    }

    @Test
    public void testFSM() {
        ObservableFSM.FsmObserver mon;
        mon = new ObservableFSM.FsmObserver() {
            @SuppressWarnings("unchecked")
            public void onEvent(final ObservableFSM fsm, final FSM.FsmEvent fsmEvent,
                                final State state) {
                String logMsg = fsmEvent.name();
                if (state != null) {
                    logMsg += "(" + state.toString() + ")";
                }
                log(logMsg);
            }
        };
        a.addObserver(mon, EnumSet.allOf(FSM.FsmEvent.class));

        a.handleEvent();
        Assert.assertEquals("BEFORE_HANDLE_EVENT"
                + "-BEFORE_STATE_HANDLE_EVENT(StateGroup for [ONE, TWO])"
                + "-AFTER_STATE_HANDLE_EVENT(StateGroup for [ONE, TWO])"
                + "-BEFORE_STATE_HANDLE_EVENT(ONE[0])"
                + "-AFTER_STATE_HANDLE_EVENT(ONE[0])"
                + "-BEFORE_TRANSITION"
                + "-BEFORE_STATE_EXIT(ONE[0])"
                + "-AFTER_STATE_EXIT(ONE[0])"
                + "-BEFORE_SWITCH_STATE"
                + "-AFTER_SWITCH_STATE"
                + "-BEFORE_STATE_ENTER(StateGroup for [TWO, THREE])"
                + "-AFTER_STATE_ENTER(StateGroup for [TWO, THREE])"
                + "-BEFORE_STATE_ENTER(TWO[1])"
                + "-AFTER_STATE_ENTER(TWO[1])"
                + "-AFTER_TRANSITION"
                + "-AFTER_HANDLE_EVENT", log);
        log = "";

        a.removeObserver(mon);
        a.addObserver(mon, FSM.FsmEvent.AFTER_TRANSITION,
                FSM.FsmEvent.BEFORE_STATE_ENTER,
                FSM.FsmEvent.BEFORE_STATE_EXIT);
        a.handleEvent();
        Assert.assertEquals("BEFORE_STATE_EXIT(TWO[1])"
                + "-BEFORE_STATE_EXIT(StateGroup for [ONE, TWO])"
                + "-BEFORE_STATE_ENTER(THREE[2])-AFTER_TRANSITION", log);
        log = "";
        a.handleEvent();
        Assert.assertEquals("BEFORE_STATE_EXIT(THREE[2])"
                + "-BEFORE_STATE_EXIT(StateGroup for [TWO, THREE])"
                + "-BEFORE_STATE_ENTER(StateGroup for [ONE, TWO])"
                + "-BEFORE_STATE_ENTER(ONE[0])-AFTER_TRANSITION", log);
        log = "";
    }

    private void log(final String msg) {
        if (log != null && log.length() > 0)
            log += "-";
        log += msg;
    }
}
