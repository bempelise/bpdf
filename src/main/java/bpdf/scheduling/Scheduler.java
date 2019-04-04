package bpdf.scheduling;

import bpdf.graph.BPDFActor;
import bpdf.graph.BPDFEdge;
import bpdf.graph.BPDFGraph;
import bpdf.symbol.BooleanComposite;
import bpdf.symbol.Expression;
import bpdf.symbol.Fraction;
import bpdf.symbol.Product;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * The dynamic scheduler. Gets as input a set of constraints, a set of
 * actors along with their repetition vector, values for the Integer
 * parameters for the current iteration and produced a slotted parallel
 * ASAP schedule of a single iteration.
 * @author Vagelis Bebelis
 */
public class Scheduler {
    /** Map of lists of boolean values. */
    public Map<String, String> m_boolMap = new HashMap<String, String>();
    /** Map of indexes to current boolean values. */
    public Map<String, Integer> m_boolIndx = new HashMap<String, Integer>();
    /** BPDF graph. */
    private BPDFGraph m_graph = new BPDFGraph();
    /** List of data dependencies on actors. */
    private List<BPDFConstraint> m_dataConstraints = new ArrayList<BPDFConstraint>();
    /** Used to produce random boolean values. */
    private Random m_rand = new Random();

    /** List of constraints on actors. */
    protected List<BPDFConstraint> p_constraints = new ArrayList<BPDFConstraint>();
    /** List of actors to be scheduled */
    protected List<BPDFActor> p_actorList = new ArrayList<BPDFActor>();
    /** Repetition vector. Holds actor name, actor solution. */
    protected Map<String, Expression> p_repVector = new HashMap<String, Expression>();
    /** Status vector. Holds actor name, times fired. */
    protected Map<String, Integer> p_stVector = new HashMap<String, Integer>();
    /** Slot counter. */
    protected int p_slot = 0;
    /** The total time used by the schedule. */
    protected int p_totalTime = 0;
    /** Keeps the maximum time of the current slot. */
    protected int p_slotMax = 0;

    Scheduler() { }

    /**
     * Constructor with BPDF graph,
     * map of values of integer parameters and
     * map of values of boolean parameters.
     */
    Scheduler(BPDFGraph g,
              Map<String, Integer> intMap,
              Map<String, String> boolMap) {
        m_graph = g;
        p_actorList = m_graph.getActors();
        p_repVector = m_graph.getVector();
        generateDataDependencies();
        generateMuDependencies();
        m_graph.setIntValues(intMap);
        setBoolValues(boolMap);
        setIntValues(intMap);
        setIndxValues();
    }

    /**
     * Produces the next boolean value of the given boolean parameter.
     * @param param The name of the boolean parameter.
     */
    boolean nextValue(String param) {
        boolean boolValue;
        int indx = m_boolIndx.get(param);
        String values = m_boolMap.get(param);
        char charValue =  values.charAt(indx);

        // get boolean value
        if (charValue == '0') {
            boolValue = false;
        } else if (charValue == '1') {
            boolValue = true;
        } else if (charValue == '*') {
            boolValue = m_rand.nextBoolean();
        } else {
            throw new RuntimeException("Invalid Boolean Value");
        }

        // update indexes
        indx++;
        if (indx == values.length()) {
            indx = 0;
        }
        m_boolIndx.put(param, indx);

        return boolValue;
    }

    /**
     * Set values to the Integer parameters.
     * @param map A map with the pairs of Integer parameters
     * and their values.
     */
    public void setIntValues(Map<String, Integer> map) {
        Iterator<Map.Entry<String, Integer>> iMap = map.entrySet().iterator();

        while (iMap.hasNext()) {
            Map.Entry<String, Integer> tempEntry = iMap.next();
            String param = tempEntry.getKey();
            int value = tempEntry.getValue();

            // Populate Constraints
            for (BPDFConstraint cons : p_constraints) {
                cons.evaluateParam(param, value);
            }

            // Populate Repetition Vector
            Iterator<Map.Entry<String, Expression>> rMap = p_repVector.entrySet().iterator();

            while (rMap.hasNext()) {
                Map.Entry<String, Expression> tEntry = rMap.next();
                String actor = tEntry.getKey();
                Expression sol = tEntry.getValue();
                sol = sol.evaluate(param, value);
                p_repVector.put(actor, sol);
            }
        }
    }

    /**
     * Set values to the Boolean parameters.
     * If value is set to '*', the Boolean will get random values.
     * @param map A map with the pairs of Boolean parameters
     * and their values.
     */
    public void setBoolValues(Map<String,String> boolVal) {
        Iterator<Map.Entry<String, String>> iMap = boolVal.entrySet().iterator();

        while (iMap.hasNext()) {
            Map.Entry<String, String> tempEntry = iMap.next();
            String key = tempEntry.getKey();
            String value = tempEntry.getValue();
            m_boolMap.put(key, value);
        }
    }

    /**
     * Initialize values to the indexes of the Boolean parameters.
     */
    public void setIndxValues() {
        Set<String> keys = m_boolMap.keySet();

        for (String s : keys) {
            m_boolIndx.put(s, 0);
        }
    }

    /**
     * Returns the constraints imposed on a given actor.
     * @param The actor under consideration.
     * @return The list of constraints imposed on the actor.
     */
    public List<BPDFConstraint> getConstraints(BPDFActor actor) {
        List<BPDFConstraint> actorCons =  new ArrayList<BPDFConstraint>();

        for (BPDFConstraint cons : p_constraints) {
            if (cons.isConstraintOn(actor)) {
                actorCons.add(cons);
            }
        }
        return actorCons;
    }

    /**
     * Returns the constraints imposed on a given actor.
     * @param actor The actor under consideration.
     * @return The list of constraints imposed on the actor.
     */
    public List<BPDFConstraint> getAllConstraints(BPDFActor actor) {
        List<BPDFConstraint> actorCons =  new ArrayList<BPDFConstraint>();

        for (BPDFConstraint cons : p_constraints) {
            if (cons.contains(actor)) {
                actorCons.add(cons);
            }
        }
        return actorCons;
    }

    /**
     * Returns the data dependencies imposed on a give actor.
     * @param actor The actor under consideration.
     * @return The list of constraints imposed on the actor.
     */
    public List<BPDFConstraint> getDataConstraints(BPDFActor actor) {
        List<BPDFConstraint> actorCons =  new ArrayList<BPDFConstraint>();

        for (BPDFConstraint cons : m_dataConstraints) {
            if (cons.contains(actor)) {
                actorCons.add(cons);
            }
        }
        return actorCons;
    }

    private void generateDataDependencies() {
        List<BPDFEdge> edgeList = m_graph.getEdges();

        for (BPDFEdge edge : edgeList) {
            BPDFActor leftActor = edge.getConsumer();
            BPDFActor rightActor = edge.getProducer();
            Product rateIn = edge.getRateIn();
            Product rateOut = edge.getRateOut();
            Product tokens = edge.getTokens();
            Product negTokens = new Product("-" + tokens.getString());
            String guard = edge.getGuard();

            // Data Dependency
            Expression f = rateOut.multiply(new Product("i")).add(negTokens).divide(rateIn).ceiling();
            BPDFConstraint dataDependency = new BPDFConstraint (leftActor, rightActor, f);
            if (!guard.isEmpty()) {
                dataDependency.setGuard(guard);
                BooleanComposite tmp = new BooleanComposite(guard);
                Set<String> paramSet = tmp.getParam();
                for (String s : paramSet) {
                    dataDependency.setReadingPeriod(s,
                        leftActor.getReadingPeriod(s));
                }
            }
            p_constraints.add(dataDependency);
            m_dataConstraints.add(dataDependency);
        }
    }

    private void generateMuDependencies() {
        Map<String, BPDFActor> modifiers = m_graph.getModifiers();
        List<String> boolParams = new ArrayList<String>();
        boolParams.addAll(modifiers.keySet());

        for (String param : boolParams) {
            List<BPDFActor> users = m_graph.getUsers(param);
            BPDFActor modifier = modifiers.get(param);
            Product wPeriod = modifier.getPeriod(param);
            Product mSol = (Product) p_repVector.get(modifier.getName());
            Product freq = mSol.divide(wPeriod).getProduct();

            for (BPDFActor actor : users) {
                if (!actor.getName().equals(modifier.getName())) {
                    Product uSol = (Product) p_repVector.get(actor.getName());
                    Product rPeriod = uSol.divide(freq).getProduct();
                    Product negWPeriod = new Product("-" + wPeriod.getString());
                    Expression aux = new Fraction(new Product("i"), rPeriod);
                    Expression f = wPeriod.multiply(aux.ceiling()).add(new Product (1)).add(negWPeriod);
                    BPDFConstraint muDependency = new BPDFConstraint(actor, modifier, f);
                    p_constraints.add(muDependency);
                }
            }
        }
    }

    public void addConstraints(List<BPDFConstraint> cons) {
        for (BPDFConstraint constraint : cons) {
            BPDFActor constrainee = constraint.getConActor();
            Set<String> params = constraint.getParam();

            for (String p : params) {
                Product period = constrainee.getReadingPeriod(p);
                if (period == null) {
                    Product rPeriod = setupReadingPeriod(constrainee, p);
                    constrainee.setReadingPeriod(p, rPeriod);
                    constraint.setReadingPeriod(p, rPeriod);
                }
            }
        }
        p_constraints.addAll(cons);
    }

    private Product setupReadingPeriod(BPDFActor actor, String param) {
        Map<String, BPDFActor> modifiers = m_graph.getModifiers();
        BPDFActor modifier = modifiers.get(param);
        Product wPeriod = modifier.getPeriod(param);
        Product modSol = (Product) p_repVector.get(modifier.getName());
        Expression freq = modSol.divide(wPeriod);

        Product solution = (Product) p_repVector.get(actor.getName());
        Expression res = solution.divide(freq);
        if (!res.isProduct()) {
            throw new RuntimeException("Invalid User Constraint");
        }
        return res.getProduct();
    }

    /**
     * Executes one iteration of the graph in slots.
     */
    public int getSchedule() {
        return 0;
    }
}
