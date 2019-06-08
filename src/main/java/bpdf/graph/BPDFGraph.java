package bpdf.graph;

import bpdf.symbol.Equation;
import bpdf.symbol.Expression;
import bpdf.symbol.Product;
import bpdf.symbol.SystemSolver;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import java.util.Map;
import java.util.Set;

/**
 * The main BPDGgraph class. Captures the actors and edges of the graph
 * and provides analyses for liveness, boundedness and consistency. It
 * finds and holds the graph cycles (minor bug in that). Finally,
 * produces the scheduling constraints and sets up the slotted schedule
 * dynamic scheduler.
 */
public class BPDFGraph {
    /** Empty Constructor */
    public BPDFGraph() { }

    /** Copy Constructor */
    public BPDFGraph(BPDFGraph g) {
        addActors(g.getActors());
        addEdges(g.getEdges());
    }

    /**
     * Constructor using parser with file.
     * * @param file The graph file.
     */
    public BPDFGraph(DslParser p) {
        // this(p.getActors(), (p.getEdges()));
        addActors(p.getActors());
        addEdges(p.getEdges());
        analyse();
        reportStatus();
    }

    /**
     * Adds an edge to the graph
     * Throws exception if the connected
     * actors do not exist in the graph
     * @param edge The edge to be added
     */
    public void addEdge(BPDFEdge edge) {
        if (!m_actorList.contains(edge.getProducer())) {
            throw new RuntimeException("Producer "+ edge.getProducer().getName() + " is missing");
        } else if (!m_actorList.contains(edge.getConsumer())) {
            throw new RuntimeException("Consumer "+ edge.getConsumer().getName() + " is missing");
        } else {
            m_edgeList.add(edge);
        }
    }

    /**
     * Adds a list of edges to the graph
     * Throws exception if the connected
     * actors do not exist in the graph
     * @param edges The list of edges to be added
     */
    public void addEdges(List<BPDFEdge> edges) {
        for (BPDFEdge edge : edges) {
            addEdge(edge);
        }
    }

    /**
     * Adds an actor to the graph.
     * Updates the modifier map.
     * @param The actor to be added
     */
    public void addActor(BPDFActor actor) {
        m_actorList.add(actor);
        if (actor.isModifier()) {
            List<String> tempParamList = actor.getBoolParam();
            for (String param : tempParamList) {
                if (m_modifiers.containsKey(param)) {
                    throw new RuntimeException("Booleam Parameter " + param +
                                               " has 2 modifiers: "+ actor.getName() + " and " +
                                               m_modifiers.get(param).getName());
                }
                m_modifiers.put(param, actor);
            }
        }
    }

    /**
     * Adds a list of actors to the graph
     * @param The list of actors to be added
     */
    public void addActors(List<BPDFActor> actors) {
        for (BPDFActor actor : actors) {
            addActor(actor);
        }
    }


    public void setIntValues(Map<String, Integer> map) {
        for (BPDFActor actor : m_actorList) {
            actor.setParamValues(map);
        }
    }

    /**
     * Returns the list of edges
     * @return The list of edges
     */
    public List<BPDFEdge> getEdges() {
        return m_edgeList;
    }

    /**
     * Returns the list of actors
     * @return The list of actors
     */
    public List<BPDFActor> getActors() {
        return m_actorList;
    }

    /**
     * Returns the repetition vector
     * @return The repetition vector
     */
    public Map<String, Expression> getVector() {
        return m_repVector;
    }

    /**
     * Returns the list of modifiers
     * @return The list of modifiers
     */
    public Map<String, BPDFActor> getModifiers() {
        return m_modifiers;
    }

    /**
     * Returns all the outgoing edges of a given actor
     * @param The given actor
     * @return The list of the outgoing edges
     */
    public List<BPDFEdge> getOutEdges(BPDFActor a) {
        List<BPDFEdge> outEdges = new ArrayList<BPDFEdge>();
        for (BPDFEdge edge : m_edgeList) {
            if (edge.getProducer() == a) {
                outEdges.add(edge);
            }
        }
        return outEdges;
    }

    /**
     * Returns all the incoming edges of a given actor
     * @param The given actor
     * @return The list of the incoming edges
     */
    public List<BPDFEdge> getInEdges(BPDFActor a) {
        List<BPDFEdge> inEdges = new ArrayList<BPDFEdge>();
        for (BPDFEdge edge : m_edgeList) {
            if (edge.getConsumer() == a) {
                inEdges.add(edge);
            }
        }
        return inEdges;
    }

    /**
     * Returns the set of boolean parameters
     * @return The set of boolean parameters
     */
    public Set<String> getBoolParamSet() {
        Set<String> params = new HashSet<String>();
        for (BPDFActor a : m_actorList) {
            params.addAll(a.getBoolParamSet());
        }
        return params;
    }

    /**
     * Returns the set of integer parameters
     * @return The set of integer parameters
     */
    public Set<String> getIntParamSet() {
        Set<String> params = new HashSet<String>();
        for (BPDFActor a : m_actorList) {
            params.addAll(a.getIntParamSet());
        }
        return params;
    }

    /**
     * Returns the list of actors that use the parameter
     * @param param The boolean parameter
     * @return The list of actors that use the parameter
     */
    public List<BPDFActor> getUsers(String param) {
        List<BPDFActor> users = new ArrayList<BPDFActor>();
        for (BPDFActor actor : m_actorList) {
            if (actor.uses(param)) {
                users.add(actor);
            }
        }
        return users;
    }

    /**
     *  Returns all the actors that a given actor is giving data to
     *  @param a    The given actor
     *  @return     A list of all the actors getting data from the given actor
     */
    public List<BPDFActor> getSuccessors(BPDFActor a) {
        List<BPDFActor> successors = new ArrayList<BPDFActor>();
        for (BPDFEdge edge : m_edgeList) {
            if (edge.getProducer().getName().equals(a.getName())) {
                successors.add(edge.getConsumer());
            }
        }
        return successors;
    }

    /**
     *  Runs all graphs analyses and updates the graph status
     */
    public void analyse() {
        m_isConsistent = checkConsistency();
        m_isLive = checkLiveness();
        m_isSafe = checkPeriodSafety();
    }

    /**
     *  @return True if the graph is consistent
     */
    public boolean isConsistent() {
        return m_isConsistent;
    }

    /**
     *  @return True if the graph is period safe
     */
    public boolean isSafe() {
        return m_isSafe;
    }

    /**
     *  @return True if the graph is live
     */
    public boolean isLive() {
        return m_isLive;
    }

    public void reportStatus() {
        String message = "Graph is%s %s";
        LOG.info(String.format(message, m_isConsistent ? "" : " not", "consistent"));
        LOG.info(String.format(message, m_isLive ? "" : " not", "live"));
        LOG.info(String.format(message, m_isSafe ? "" : " not", "safe"));
    }

    /**
     *  Consistency Analysis. Solves the balance equations and
     *  calculates the repetition vector.
     *  @return True if the graph is consistent
     */
    private boolean checkConsistency() {
        ArrayList<Equation> balanceEquations = new ArrayList<Equation>();
        for (BPDFEdge edge : m_edgeList) {
            balanceEquations.add(new Equation(edge.getRateIn(),
                                              edge.getProducer().getName(),
                                              edge.getRateOut(),
                                              edge.getConsumer().getName()));
        }

        ArrayList<String> actorNameList = new ArrayList<String>();
        for (BPDFActor actor : m_actorList) {
            actorNameList.add(actor.getName());
        }

        SystemSolver solver = new SystemSolver(actorNameList, balanceEquations);
        m_repVector = solver.getSolution();
        return (m_repVector != null);
    }

    /**
     * Liveness analysis.
     * Also finds a schedule for the graph.
     * First finds all directed cycles of the graph.
     * Clustering technique not supported yet.
     * @return True, if the graph is live.
     */
    private boolean checkLiveness() {
        CycleDetector cd = new CycleDetector(this);
        List<BPDFGraph> cycles = new ArrayList<BPDFGraph>();

        cycles = cd.getCycles();
        if (cycles.isEmpty()) {
            // is acyclic
            return true;
        } else if (hasSaturatedCycles(cycles)) {
            // has only saturated cycles
            return true;
        } else if (pslc()) {
            // PSLC found a schedule
            return true;
        }
        // TODO
        // else if (cluster()) {}
        return false;
    }

    /**
     * Period Safety check.
     * Checks whether the change periods of
     * all the boolean parameters are period safe.
     * @return True, if all periods are safe
     */
    private boolean checkPeriodSafety() {
        if (!m_isConsistent) {
            return false;
        }

        List<String> boolParams = new ArrayList<String>();
        boolParams.addAll(m_modifiers.keySet());

        for (String param : boolParams) {
            List<BPDFActor> users = getUsers(param);
            BPDFActor modifier = m_modifiers.get(param);
            Product period = modifier.getPeriod(param);
            Product modSol = (Product) m_repVector.get(modifier.getName());
            Expression freq = modSol.divide(period);

            if (!freq.isProduct()) {
                return false;
            }

            for (BPDFActor actor : users) {
                Product solution = (Product) m_repVector.get(actor.getName());
                Expression res = solution.divide(freq);
                if (!res.isProduct()) {
                    return false;
                }
                actor.setReadingPeriod(param, res.getProduct());
            }
        }
        return true;
    }

    /**
     * Returns true if the graph has all its cycles saturated
     * @return True if all cycles are saturated
     */
    private boolean hasSaturatedCycles(List<BPDFGraph> cycles) {
        for (BPDFGraph cycle : cycles) {
            boolean hasSat = false;
            List<BPDFEdge> eList = cycle.getEdges();
            for (BPDFEdge edge : eList) {
                BPDFActor cons = edge.getConsumer();
                Product consRate = edge.getRateOut();
                Product consSol = (Product) m_repVector.get(cons.getName());
                Product stored = edge.getTokens();
                Product needed = consSol.multiply(consRate).getProduct();
                if ((stored.isGreaterThan(needed)) || (stored.isEqualTo(needed))) {
                    hasSat = true;
                }
            }
            if (!hasSat) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns a schedule for the graph, null if not possible
     * @return True if a schedule was found
     */
    private boolean pslc() {
        Product zero = new Product(0);
        boolean progress;
        BPDFSchedule sched = new BPDFSchedule();
        Map<String, Expression> copyVector = new HashMap<String, Expression>();
        copyVector.putAll(m_repVector);

        while (hasMoreFirings(copyVector)) {
            progress = false;
            for (BPDFActor actor : m_actorList) {
                String actorName = actor.getName();
                Product firingsLeft = (Product) copyVector.get(actorName);
                if (firingsLeft.isZero()) {
                    continue;
                } else if (zero.isGreaterThan(firingsLeft)) {
                    throw new RuntimeException("Actor fired more times than in repetition vector");
                } else if (firingsLeft.isNumber()) {
                    if (actor.prefire()) {
                        progress = true;
                        Product one = new Product(1);
                        Product minusOne = new Product("-1");

                        sched.addFiring(actorName, new Product(one));
                        firingsLeft = firingsLeft.add(minusOne).getProduct();
                        copyVector.put(actorName, firingsLeft);
                        actor.fire();
                    }
                } else {
                    Product paramFirings = firingsLeft.getParam().getProduct();

                    if (actor.prefire(paramFirings)) {
                        progress = true;
                        sched.addFiring(actorName, paramFirings);
                        Product nParam = new Product("-" + paramFirings.getString());
                        firingsLeft = firingsLeft.add(nParam).getProduct();
                        copyVector.put(actorName, firingsLeft);
                        actor.fire(new Product(paramFirings));
                        break;
                    }
                }
            }
            if (!progress) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if the given firing vector has firings left
     * @param map The firing vector
     * @return True if the vector still has firings
     */
    private boolean hasMoreFirings(Map<String, Expression> map) {
        Iterator<Map.Entry<String, Expression>> iMap = map.entrySet().iterator();
        while (iMap.hasNext()) {
            Map.Entry<String, Expression> tempEntry = iMap.next();
            Expression tmpExpr = tempEntry.getValue();
            if (!tmpExpr.isZero()) {
                return true;
            }
        }
        return false;
    }

    private static final Logger LOG = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    /** List of actors */
    private List<BPDFActor> m_actorList = new ArrayList<BPDFActor>();
    /** List of edges */
    private List<BPDFEdge> m_edgeList = new ArrayList<BPDFEdge>();
    /** List of modifiers */
    private Map<String, BPDFActor> m_modifiers = new HashMap<String, BPDFActor>();
    /** Repetition Vector */
    private Map<String, Expression> m_repVector;
    /** Graph is Safe */
    private boolean m_isSafe = false;
    /** Graph is Live */
    private boolean m_isLive = false;
    /** Graph is Consistent */
    private boolean m_isConsistent = false;
}
