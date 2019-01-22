package genaku.com.flexfsm;

import com.genaku.flexfsm.FSM;
import com.genaku.flexfsm.State;
import com.genaku.flexfsm.StateGroup;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class Sample2 {

    private FSM<A, String> a;

    private enum A {
        INIT, SHOW, SAVE, HIDE
    }

    @SuppressWarnings("unchecked")
    private <E extends Object> FSM<A, E> createA() {
        ArrayList<State<A, E>> states = new ArrayList();
        states.add(new State<A, E>(A.INIT) {
            public void enter() {
                init();
                next(A.SHOW);
            }
        });
        states.add(new State<A, E>(A.SHOW) {
            public void enter() {
                show();
            }

            public void handleEvent() {
                if (getEvent().equals("SaveEvent")) next(A.SAVE);
            }
        });
        states.add(new State<A, E>(A.SAVE) {
            public void enter() {
                save();
                next(A.HIDE);
            }
        });
        states.add(new State<A, E>(A.HIDE) {
            public void enter() {
                hide();
            }

            public void handleEvent() {
                if (getEvent().equals("ShowEvent")) next(A.SHOW);
            }
        });
        states.add(new StateGroup<A, E>(A.SHOW, A.SAVE) {
            public void handleEvent() {
                if (getEvent().equals("HideEvent")) next(A.HIDE);
            }
        });

        return new FSM(states);
    }

    @Before
    public void setUp() {
        log = "";
        a = createA();
    }

    private String log;

    @Test
    public void testSample() {
        a.handleEvent("SaveEvent");
        a.handleEvent("ShowEvent");
        a.handleEvent("HideEvent");
        Assert.assertEquals("init-show-save-hide-show-hide", log);
    }

    private void init() {
        log("init");
    }

    private void show() {
        log("show");
    }

    private void hide() {
        log("hide");
    }

    private void save() {
        log("save");
    }

    private void log(String msg) {
        if (log != null && log.length() > 0)
            log += "-";
        log += msg;
    }
}
