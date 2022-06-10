import org.chocosolver.solver.constraints.Propagator;
import org.chocosolver.solver.constraints.PropagatorPriority;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.events.IntEventType;
import org.chocosolver.util.ESat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class IntervallesPropagator extends Propagator<IntVar> {

    IntVar dij;
    ArrayList<BornesLbUb> lesintervalles;
    boolean removeInterval;

    public IntervallesPropagator(IntVar dij, ArrayList<BornesLbUb> lesintervalles) {

        super(new IntVar[]{dij}, PropagatorPriority.UNARY, false);
        this.dij = dij;
        this.lesintervalles = lesintervalles;

    }

    @Override
    public void propagate(int i) throws ContradictionException {

        for (int j = 0; j < lesintervalles.size(); j++) {
            removeInterval = dij.removeInterval(lesintervalles.get(j).lb, lesintervalles.get(j).ub, this);
        }


    }


    public ESat isEntailed() {
        if (removeInterval = false) {

            return ESat.FALSE;
        } else if (removeInterval = true) {

            return ESat.TRUE;
        } else {

            return ESat.UNDEFINED;
        }
    }
