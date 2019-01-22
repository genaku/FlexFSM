package genaku.com.flexfsm;

import com.genaku.flexfsm.State;
import com.genaku.flexfsm.StateGroup;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class FSMTestElementary {

    private MorozovFSM<A, String> a;

    private enum A {
        ONE, TWO
    }

    @SuppressWarnings("unchecked")
    private <E extends Object> MorozovFSM<A, E> createA0() {
        ArrayList<State<A, E>> states = new ArrayList();
        states.add(new State<A, E>(A.ONE) {
            public void enter() {
                log("enter1");
            }

            public void handleEvent() {
                log("handle1");
                if (getEvent().equals("E1")) next(A.TWO);
            }

            public void exit() {
                log("exit1");
            }
        });

        states.add(new State<A, E>(A.TWO) {
            public void enter() {
                log("enter2");
            }

            public void handleEvent() {
                log("handle2");
                if (getEvent().equals("E1")) next(A.ONE);
            }

            public void exit() {
                log("exit2");
            }
        });

        states.add(new StateGroup<A, E>(A.ONE, A.TWO));

        return new MorozovFSM(states);
    }

    private String log;

    @Before
    public void setUp() {
        log = "";
        a = createA0();
    }

    @Test
    public void testFSM() {
        Assert.assertEquals(a.getState(A.ONE), a.getCurrentState());
        Assert.assertNull(a.getEvent());
        Assert.assertNull(a.getPrevStateM());
        Assert.assertEquals(a.getCurrentState().toString(), a.toString());
        Assert.assertEquals(A.ONE, a.getCurrentState().getId());

        a.setEvent("E1");
        Assert.assertEquals("E1", a.getEvent());
        a.handleEvent();
        Assert.assertEquals(a.getState(A.TWO), a.getCurrentState());
        Assert.assertNull(a.getEvent());
        Assert.assertEquals(a.getState(A.ONE), a.getPrevStateM());

        Assert.assertEquals(a, a.getCurrentState().getFsm());
        Assert.assertTrue(a.getCurrentState().getStateGroups().get(0).toString().contains("ONE"));
        Assert.assertTrue(a.getCurrentState().getStateGroups().get(0).toString().contains("TWO"));
        Assert.assertTrue(a.getCurrentState().getStateGroups().get(0).getIncludedIDs().contains(A.ONE));
        Assert.assertTrue(a.getCurrentState().getStateGroups().get(0).getIncludedIDs().contains(A.TWO));
        Assert.assertEquals("TWO", a.getCurrentState().getId().name());
        Assert.assertEquals(A.TWO, a.getCurrentState().getId());
    }

    private void log(final String msg) {
        if (log != null && log.length() > 0)
            log += "-";
        log += msg;
    }
}
