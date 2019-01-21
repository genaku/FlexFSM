package genaku.com.flexfsm;

import com.genaku.flexfsm.FSM;
import com.genaku.flexfsm.State;
import com.genaku.flexfsm.StateGroup;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FSMTestEventHooks {
	private FSM<A, String> a;

	private enum A {
		ONE, TWO, THREE
	};

	@SuppressWarnings("unchecked")
	private <E extends Object> FSM<A, E> createA0() {
		return new FSM<A, E>(

		new State<A, E>
		(A.ONE) {
			public void handleEvent() { next(A.TWO); }
		}, 
		new State<A, E>
		(A.TWO) {
			public void handleEvent() { next(A.THREE); }
		},
		new State<A, E>
		(A.THREE) {
			public void handleEvent() { next(A.ONE); }
		},
		new StateGroup<A, E>
		(A.ONE, A.TWO),
		new StateGroup<A, E>
		(A.TWO, A.THREE)
		)
		{
			@Override
			protected void onFSMEvent(final FsmEvent e, final State<A, E> state) {
				String logMsg = e.name();
				if (state != null) {
					logMsg += "(" + state.toString() + ")";
				}
				log(logMsg);
			}
		};
	}

	private String log;

	@Before
	public void setUp() throws Exception {
		log = "";
		a = createA0();
	}

	@Test
	public void testFSM() {
		Assert.assertEquals(
				"BEFORE_INIT-AFTER_INIT"
				+ "-BEFORE_STATE_ENTER(StateGroup for [ONE, TWO])" 
				+ "-AFTER_STATE_ENTER(StateGroup for [ONE, TWO])"
				+ "-BEFORE_STATE_ENTER(ONE[0])-AFTER_STATE_ENTER(ONE[0])",
				log);
		log = "";

		a.handleEvent();
		Assert.assertEquals(
				"BEFORE_HANDLE_EVENT" 
				+ "-BEFORE_STATE_HANDLE_EVENT(StateGroup for [ONE, TWO])" 
				+ "-AFTER_STATE_HANDLE_EVENT(StateGroup for [ONE, TWO])" 
				+ "-BEFORE_STATE_HANDLE_EVENT(ONE[0])" 
				+ "-AFTER_STATE_HANDLE_EVENT(ONE[0])" 
				+ "-BEFORE_TRANSITION" 
				+ "-BEFORE_STATE_EXIT(ONE[0])-AFTER_STATE_EXIT(ONE[0])"
				+ "-BEFORE_SWITCH_STATE-AFTER_SWITCH_STATE"
				+ "-BEFORE_STATE_ENTER(StateGroup for [TWO, THREE])" 
				+ "-AFTER_STATE_ENTER(StateGroup for [TWO, THREE])" 
				+ "-BEFORE_STATE_ENTER(TWO[1])-AFTER_STATE_ENTER(TWO[1])" 
				+ "-AFTER_TRANSITION-AFTER_HANDLE_EVENT",
				log);
		log = "";

		a.handleEvent();
		Assert.assertEquals(
				"BEFORE_HANDLE_EVENT" 
				+ "-BEFORE_STATE_HANDLE_EVENT(StateGroup for [ONE, TWO])" 
				+ "-AFTER_STATE_HANDLE_EVENT(StateGroup for [ONE, TWO])" 
				+ "-BEFORE_STATE_HANDLE_EVENT(StateGroup for [TWO, THREE])" 
				+ "-AFTER_STATE_HANDLE_EVENT(StateGroup for [TWO, THREE])" 
				+ "-BEFORE_STATE_HANDLE_EVENT(TWO[1])" 
				+ "-AFTER_STATE_HANDLE_EVENT(TWO[1])" 
				+ "-BEFORE_TRANSITION-BEFORE_STATE_EXIT(TWO[1])" 
				+ "-AFTER_STATE_EXIT(TWO[1])" 
				+ "-BEFORE_STATE_EXIT(StateGroup for [ONE, TWO])" 
				+ "-AFTER_STATE_EXIT(StateGroup for [ONE, TWO])"
				+ "-BEFORE_SWITCH_STATE-AFTER_SWITCH_STATE"
				+ "-BEFORE_STATE_ENTER(THREE[2])-AFTER_STATE_ENTER(THREE[2])" 
				+ "-AFTER_TRANSITION-AFTER_HANDLE_EVENT",
				log);
		log = "";

		a.handleEvent();
		Assert.assertEquals(
				"BEFORE_HANDLE_EVENT" 
				+ "-BEFORE_STATE_HANDLE_EVENT(StateGroup for [TWO, THREE])" 
				+ "-AFTER_STATE_HANDLE_EVENT(StateGroup for [TWO, THREE])" 
				+ "-BEFORE_STATE_HANDLE_EVENT(THREE[2])" 
				+ "-AFTER_STATE_HANDLE_EVENT(THREE[2])" 
				+ "-BEFORE_TRANSITION-BEFORE_STATE_EXIT(THREE[2])" 
				+ "-AFTER_STATE_EXIT(THREE[2])" 
				+ "-BEFORE_STATE_EXIT(StateGroup for [TWO, THREE])" 
				+ "-AFTER_STATE_EXIT(StateGroup for [TWO, THREE])"
				+ "-BEFORE_SWITCH_STATE-AFTER_SWITCH_STATE"
				+ "-BEFORE_STATE_ENTER(StateGroup for [ONE, TWO])" 
				+ "-AFTER_STATE_ENTER(StateGroup for [ONE, TWO])" 
				+ "-BEFORE_STATE_ENTER(ONE[0])-AFTER_STATE_ENTER(ONE[0])" 
				+ "-AFTER_TRANSITION-AFTER_HANDLE_EVENT",
				log);
		log = "";
	}
	
	private void log(final String msg) {
		if (log != null && log.length() > 0)
			log += "-";
		log += msg;
	}
}
