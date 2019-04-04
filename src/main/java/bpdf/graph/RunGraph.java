package bpdf.graph;

import bpdf.scheduling.BPDFConstraint;
import bpdf.symbol.Expression;
import bpdf.symbol.Product;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RunGraph {
    // # of iterations
    private int _iter;
    // Integer parameters
    private int _p;
    private int _q;
    Map<String, Integer> setup = new HashMap<String, Integer>();
    // Boolean parameters
    private List<Boolean> boolA = new ArrayList<Boolean>();
    private List<Boolean> boolB = new ArrayList<Boolean>();

    private Random rand = new Random();

    public RunGraph(int iter, int p, int q) {
        _iter = iter;
        _p = p;
        _q = q;
        setup.put("p", _p);
        setup.put("q", _q);
    }

    private void populateBoolean() {
        boolean value1;
        boolean value2;

        boolA.clear();
        boolB.clear();

        for (int i = 0; i < _p; i++) {
            value1 = rand.nextBoolean();
            value2 = rand.nextBoolean();
            while (!value1 && !value2) {
                value1 = rand.nextBoolean();
                value2 = rand.nextBoolean();
            }
            boolA.add(value1);
            boolB.add(value2);
        }
    }

    private List<BPDFConstraint> getMCLOOPConstraints(Map<String, BPDFActor> actorMap) {
        List<BPDFConstraint> cons = new ArrayList<BPDFConstraint>();

        BPDFActor actorMC = actorMap.get("MC");
        BPDFActor actorMBB = actorMap.get("MBB");
        BPDFActor actorIQIT = actorMap.get("IQIT");

        Product one = new Product(1);
        Product minusOne = new Product("-1");
        Product paramI = new Product("i");
        Product paramQ = new Product("q");
        Product paramMinusQ = new Product("-q");
        Expression poly = paramI.add(minusOne);

        Expression poly1 = poly.multiply(paramQ);
        BPDFConstraint uc1 = new BPDFConstraint(actorMC, actorMBB, poly1);
        uc1.setGuard("!a");
        uc1.setShift(1);
        cons.add(uc1);

        BPDFConstraint uc2 = new BPDFConstraint(actorMC, actorIQIT, poly1);
        uc2.setGuard("a");
        uc2.setShift(-1);
        cons.add(uc2);

        Expression poly2 = poly.multiply(paramQ).add(paramMinusQ).add(one);
        BPDFConstraint uc3 = new BPDFConstraint(actorMC, actorMBB, poly2);
        cons.add(uc3);
        return cons;
    }

    private List<BPDFConstraint> getBufferConstraints(Map<String, BPDFActor> actorMap) {
        List<BPDFConstraint> cons = new ArrayList<BPDFConstraint>();

        BPDFActor actorLOOP = actorMap.get("LOOP");
        BPDFActor actorMC = actorMap.get("MC");

        Product minusOne = new Product("-1");
        Product paramI = new Product("i");

        Expression poly3 = paramI.add(minusOne);
        BPDFConstraint uc4 = new BPDFConstraint(actorMC , actorLOOP, poly3);

        cons.add(uc4);
        return cons;
    }
}
