package genaku.com.flexfsm;

import com.genaku.flexfsm.FSM;
import com.genaku.flexfsm.FSMException;
import com.genaku.flexfsm.State;
import com.genaku.flexfsm.StateGroup;
import org.junit.Test;

public class FSMTestExceptions {
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
		new StateGroup<A, E>
		(A.ONE, A.TWO)
		);
	}

	@SuppressWarnings("unchecked")
	private <E extends Object> FSM<A, E> createA1() {
		return new FSM<A, E>(

		new State<A, E>
		(A.ONE) {
			public void handleEvent() { next(A.TWO); }
		},
		new State<A, E>
		(A.TWO) {
			public void handleEvent() { next(A.THREE); }
		},
		new StateGroup<A, E>
		(A.ONE, A.TWO)
		);
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
