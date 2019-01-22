package genaku.com.flexfsm;

import com.genaku.flexfsm.FSM;
import com.genaku.flexfsm.FSMException;
import com.genaku.flexfsm.State;
import com.genaku.flexfsm.StateGroup;
import org.junit.Test;

import java.util.ArrayList;

public class FSMTestExceptions {
    private FSM<A, String> a;

    private enum A {
        ONE, TWO, THREE
    }

    @SuppressWarnings("unchecked")
    private <E extends Object> FSM<A, E> createA0() {
        ArrayList<State<A, E>> states = new ArrayList();
        states.add(new State<A, E>(A.ONE) {
            public void handleEvent() {
                next(A.TWO);
            }
        });
        states.add(new StateGroup<A, E>(A.ONE, A.TWO));

        return new FSM<A, E>(states);
    }

    @SuppressWarnings("unchecked")
    private <E extends Object> FSM<A, E> createA1() {
        ArrayList<State<A, E>> states = new ArrayList();
        states.add(new State<A, E>(A.ONE) {
            public void handleEvent() {
                next(A.TWO);
            }
        });
        states.add(new State<A, E>(A.TWO) {
            public void handleEvent() {
                next(A.THREE);
            }
        });
        states.add(new StateGroup<A, E>(A.ONE, A.TWO));
        return new FSM(states);
    }

    @Test(expected = FSMException.class)
    public void testException1() {
        a = createA0();
    }

    @Test(expected = FSMException.class)
    public void testException2() {
        a = createA1();
        a.handleEvent();
        a.handleEvent();
    }
}
