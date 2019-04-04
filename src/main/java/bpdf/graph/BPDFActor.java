package bpdf.graph;

import bpdf.symbol.BooleanComposite;
import bpdf.symbol.Product;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The main BPDF actor. Captures all the incoming / outgoing edges along with
 * their rates. Some auxiliary indexes are used for some algorithms in the
  * BPDFgraph class.
 * @author Vagelis Bebelis
 */
public class BPDFActor {
    /** Actor's name */
    private String m_name;
    /** Outgoing EdgeList */
    private List<BPDFEdge> m_outEdges = new ArrayList<BPDFEdge>();
    /** Incoming EdgeList */
    private List<BPDFEdge> m_inEdges = new ArrayList<BPDFEdge>();
    /** Boolean parameters the actor modifies */
    private Map<String, Product> m_modify = new HashMap<String, Product>();
    /** Boolean parameter periods */
    private Map<String, Product> m_readingPeriod = new HashMap<String,Product>();
    /** Time to complete a whole firing  */
    private int m_timing = 0;
    /** Remaining time to finish firing */
    private int m_remainTime = 0;
    /** Used in cycle(SCC) detection algorithm */
    public int index = 0;

    /** * Empty constructor */
    public BPDFActor() { }

    /**
     * Constructor that sets the actor's name
     * @param name The actor's name
     */
    public BPDFActor(String name) {
        m_name = name;
    }

    /**
     * Adds an edge to either the incoming or the outgoing edge list
     * @param edge The edge to be added
     */
    public void addEdge(BPDFEdge edge) {
        if (m_name.equals(edge.getConsumer().getName())) {
            m_inEdges.add(edge);
        } else if (m_name.equals(edge.getProducer().getName())) {
            m_outEdges.add(edge);
        } else {
            throw new RuntimeException("Invalid edge for this actor");
        }
    }

    /**
     * Adds a list of edges to the lists of the actor
     * @param edges The list of edges to be added
     */
    public void addEdges(List<BPDFEdge> edges) {
        for (BPDFEdge edge : edges) {
            addEdge(edge);
        }
    }

    /**
     * Returns true if the actor can fire (enough tokens in its input ports).
     * @return True if the actor can fire once
     */
    public boolean prefire() {
        for (BPDFEdge edge : m_inEdges) {
            Product needed = edge.getRateOut();
            Product stored = edge.getTokens();
            if (needed.isGreaterThan(stored)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Fires the actor one time.
     */
    public void fire() {
        for (BPDFEdge edge : m_inEdges) {
            edge.removeTokens(edge.getRateOut());
        }

        for (BPDFEdge edge : m_outEdges) {
            edge.addTokens(edge.getRateIn());
        }
    }

    /**
     * Returns true if the actor can fire a given number of times.
     * @param times The number of times the actor should fire.
     * @return True if the actor can fire that many times.
     */
    public boolean prefire(Product times) {
        for (BPDFEdge edge : m_inEdges) {
            Product needed = edge.getRateOut().multiply(times).getProduct();
            Product stored = edge.getTokens();
            if (needed.isGreaterThan(stored)) {
                return false;
            }
        }
        return true;
    }


    /**
     * Fires the actors a given number of times.
     * @param times The number of times the actor should fire.
     */
    public void fire(Product times) {
        for (BPDFEdge edge : m_inEdges) {
            edge.removeTokens(edge.getRateOut().multiply(times).getProduct());
        }

        for (BPDFEdge edge : m_outEdges) {
            edge.addTokens(edge.getRateIn().multiply(times).getProduct());
        }
    }

    /**
     * Set specific values to the Integer parameters used by the actor.
     * @param map A map holding the pairs of integer parameters and values.
     */
    public void setParamValues(Map<String, Integer> map) {
        Iterator<Map.Entry<String, Integer>> iMap = map.entrySet().iterator();

        while (iMap.hasNext()) {
            Map.Entry<String, Integer> tempEntry = iMap.next();
            String param = tempEntry.getKey();
            int value = tempEntry.getValue();

            Set<String> params = m_modify.keySet();
            for (String p : params) {
                Product period = m_modify.get(p);
                Product evalPeriod = period.evaluate(param, value);
                m_modify.put(p, evalPeriod);
            }
        }
    }

    /**
     * Returns the actor's name
     * @return The actor's name
     */
    public String getName() {
        return m_name;
    }

    /**
     * Set this actor as a modifier of a boolean parameter
     * @param param The name of the parameter
     * @param period The change period of the parameter
     */
    public void setModifier(String param, Product period) {
        m_modify.put(param, period);
    }

    /**
     * Returns the change period of the given boolean parameter.
     * @param name The name of the boolean parameter.
     * @return The change periof of the boolean parameter.
     */
    public Product getPeriod(String name) {
        return m_modify.get(name);
    }

    /**
     * Returns whether he actor modifies any boolean parameter
     * @return True if the actor modifies a boolean parameter
     */
    public boolean isModifier() {
        return !m_modify.isEmpty();
    }

    /**
     * Returns the list of the parameters modified by the actor
     * @return The list of the parameters modified by the actor
     */
    public List<String> getBoolParam() {
        List<String> paramList = new ArrayList<String>();
        paramList.addAll(m_modify.keySet());
        return paramList;
    }

    /**
     * Returns the list of the boolean parameters it can modify in the
     * current firing of the actor slot.
     * @param i The current firing of the actor
     * @return The list of the parameters that can be modified in the
     * current firing.
     */
    public List<String> canModify(int i) {
        Set<String> params = m_modify.keySet();
        List<String> change = new ArrayList<String>();
        for (String param : params) {
            int period = m_modify.get(param).getNumber();
            if (i % period == 0) {
                change.add(param);
            }
        }
        return change;
    }

    /**
     *  Returns true if the actor modifies the given boolean parameter
     * @param param The name of the parameter
     * @return True, if the actor modifies the parameter
     */
    public boolean modifies(String param) {
        return m_modify.containsKey(param);
    }

/******************************************************************************
 ** USER METHODS
 ******************************************************************************/

    /**
     * Sets the reading period for the given boolean parameter.
     * @param name The name of the boolean parameter.
     * @param solution The value of the reading period.
     */
    public void setReadingPeriod(String name, Product solution) {
        m_readingPeriod.put(name, solution);
    }

    /**
     * Returns the reading period of the given boolean parameter.
     * Returns null if not set.
     * @param name The name of the boolean parameter.
     * @return The reading period.
     */
    public Product getReadingPeriod(String name) {
        if (m_readingPeriod.containsKey(name)) {
            return m_readingPeriod.get(name);
        } else {
            return null;
        }
    }

    /**
     * Returns true if the actor uses the parameter
     * (i.e. any of its adjacent edges have the parameter as a guard)
     * @param name The name of the parameter
     * @return True if the actor uses the parameter
     */
    public boolean uses(String name) {
        if (this.modifies(name)) {
            return true;
        }

        for (BPDFEdge edge : m_inEdges) {
            String guard = edge.getGuard();
            if (guard.equals("")) {
                continue;
            }
            BooleanComposite boolGuard = new BooleanComposite(guard);
            Set<String> boolParams = boolGuard.getParam();
            for (String s : boolParams) {
                if (s.equals(name)) {
                    return true;
                }
            }
        }

        for (BPDFEdge edge : m_outEdges) {
            String guard = edge.getGuard();
            if (guard.equals("")) {
                continue;
            }
            BooleanComposite boolGuard = new BooleanComposite(guard);
            Set<String> boolParams = boolGuard.getParam();
            for (String s : boolParams) {
                if (s.equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns the set of boolean parameters the actor uses
     * @return The set of boolean parameters the actor uses
     */
    public Set<String> getBoolParamSet() {
        Set<String> params = new HashSet<String>();

        for (BPDFEdge edge : m_inEdges) {
            String guard = edge.getGuard();
            if (guard.equals("")) {
                continue;
            }
            BooleanComposite boolGuard = new BooleanComposite(guard);
            params.addAll(boolGuard.getParam());
        }

        for (BPDFEdge edge : m_outEdges) {
            String guard = edge.getGuard();
            if (guard.equals("")) {
                continue;
            }
            BooleanComposite boolGuard = new BooleanComposite(guard);
            params.addAll(boolGuard.getParam());
        }
        return params;
    }

    /**
     * Returns the set of integer parameters the actor uses
     * @return The set of integer parameters the actor uses
     */
    public Set<String> getIntParamSet() {
        Set<String> params = new HashSet<String>();

        for (BPDFEdge edge : m_inEdges) {
            params.addAll(edge.getRateIn().getParamSet());
        }

        for (BPDFEdge edge : m_outEdges) {
            params.addAll(edge.getRateOut().getParamSet());
        }
        return params;
    }

    /**
     * Sets the average execution time.
     * @param time The amount of time units
     */
    public void setTime(int time) {
        m_timing = time;
        m_remainTime = time;
    }

    /**
     * Returns the average execution time of the actor.
     * @return The execution time
     */
    public int getTime() {
        return m_remainTime;
    }

    public int advanceTime(int advance) {
        m_remainTime = m_remainTime - advance;
        if (m_remainTime == 0) {
            m_remainTime = m_timing;
            return 0;
        } else {
            return m_remainTime;
        }
    }
}
