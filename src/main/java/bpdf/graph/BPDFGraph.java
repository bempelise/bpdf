// BPDFGraph.java

package bpdf.graph;

// bpdf
import bpdf.symbol.*;
import bpdf.scheduling.*;

// io
import java.io.File;

// util
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

/**
 * The main BPDGgraph class. Captures the actors and edges of the graph 
 * and provides analyses for liveness, boundedness and consistency. It 
 * finds and holds the graph cycles (minor bug in that). Finally, 
 * produces the scheduling constraints and sets up the slotted schedule 
 * dynamic scheduler.
 * @author Vagelis Bebelis
 */
public class BPDFGraph
{
/***********************************************************************
 ** PRIVATE PARAMETERS
 ***********************************************************************/

    /** List of actors */
    private List<BPDFActor> _actorList = new ArrayList<BPDFActor>();

    /** List of edges */
    private List<BPDFEdge> _edgeList = new ArrayList<BPDFEdge>();

    /** List of modifiers */
    private Map<String, BPDFActor> _modifiers 
        = new HashMap<String, BPDFActor>();

    /** Repetition Vector */
    private Map _repVector;


/***********************************************************************
 ** CONSTRUCTORS
 ***********************************************************************/

    /** Empty Constructor */
    public BPDFGraph(){}

    /**
     * Constructor using parser with filepath.
     * @param path The path to graph file.
     */
    public BPDFGraph(String path)
    {
        this(new File(path));
    }

    /**
     * Constructor using parser with file.
     * * @param file The graph file.
     */
    public BPDFGraph(File file)
    {
        DslParser p = new DslParser(file);
        addActors(p.getActors());
        addEdges(p.getEdges());
    }

    /**
     *  Constructor initializing the actor list
     * @param actors The list of actors
     */
    public BPDFGraph(List<BPDFActor> actors)
    {
        _actorList.addAll(actors);
    }

    /**
     * Constructor copying a graph from another graph
     * @param g The graph to copy from
     */
    public BPDFGraph(BPDFGraph g)
    {
        addActors(g.getActors());
        addEdges(g.getEdges());
    }

/***********************************************************************
 ** TOPOLOGY MANIPULATION
 ***********************************************************************/

    /**
     * Adds an edge to the graph
     * Throws exception if the connected 
     * actors do not exist in the graph
     * @param edge The edge to be added
     */
    public void addEdge(BPDFEdge edge)
    {
        if (!_actorList.contains(edge.getProducer()))
           throw new RuntimeException ("Actor (producer)"  
                + edge.getProducer().getName() + " is missing");
        else if (!_actorList.contains(edge.getConsumer()))
           throw new RuntimeException ("Actor (consumer)" 
                + edge.getConsumer().getName()  + " is missing");
        else
            _edgeList.add(edge);
    }

    /**
     * Adds a list of edges to the graph
     * Throws exception if the connected 
     * actors do not exist in the graph
     * @param edges The list of edges to be added
     */
    public void addEdges(List<BPDFEdge> edges)
    {
        for (BPDFEdge edge : edges)
            addEdge(edge);
    }

    /**
     * Adds an actor to the graph. 
     * Updates the modifier map.
     * @param The actor to be added
     */
    public void addActor(BPDFActor actor)
    {
        _actorList.add(actor);
        if (actor.isModifier())
        {
            List<String> tempParamList = actor.getBoolParam();
            for (String param : tempParamList)
            {
                if (_modifiers.containsKey(param))
                    throw new RuntimeException("Booleam Parameter " 
                        + param + " has 2 modifiers: " 
                        + actor.getName() + " and "
                        + _modifiers.get(param).getName());
                _modifiers.put(param,actor);
            }
        }
    }

    /**
     * Adds a list of actors to the graph
     * @param The list of actors to be added
     */
    public void addActors(List<BPDFActor> actors)
    {
        for (BPDFActor actor : actors)
            addActor(actor);
    }


    public void setIntValues(Map<String, Integer> map)
    {
        for (BPDFActor actor : _actorList)
            actor.setParamValues(map);
    }

/******************************************************************************
 ** GETTERS
 ******************************************************************************/

    /**
     * Returns the list of edges
     * @return The list of edges
     */
    public List getEdges()
    {
        return _edgeList;
    }

    /**
     * Returns the list of actors
     * @return The list of actors
     */
    public List getActors()
    {
        return _actorList;
    }

    /**
     * Returns the repetition vector
     * @return The repetition vector 
     */
    public Map getVector()
    {
        return _repVector;
    }

    /**
     * Returns the list of modifiers
     * @return The list of modifiers
     */
    public Map<String, BPDFActor> getModifiers()
    {
        return _modifiers;
    } 

    /**
     * Returns all the outgoing edges of a given actor
     * @param The given actor
     * @return The list of the outgoing edges
     */
    public List getOutEdges(BPDFActor a)
    {
        List<BPDFEdge> outEdges = new ArrayList<BPDFEdge>();
        for (BPDFEdge edge : _edgeList)
        {
            if (edge.getProducer() == a) 
                outEdges.add(edge);
        }
        return outEdges;
    }

    /**
     * Returns all the incoming edges of a given actor
     * @param The given actor
     * @return The list of the incoming edges
     */
    public List getInEdges(BPDFActor a)
    {
        List<BPDFEdge> inEdges = new ArrayList<BPDFEdge>();
        for (BPDFEdge edge : _edgeList)
        {
            if (edge.getConsumer() == a)
                inEdges.add(edge);
        }
        return inEdges;
    }

    /**
     * Returns the set of boolean parameters
     * @return The set of boolean parameters 
     */
    public Set<String> getBoolParamSet()
    {
        Set<String> params = new HashSet<String>();
        for (BPDFActor a : _actorList)
        {
            params.addAll(a.getBoolParamSet());
        }
        return params;
    }

    /**
     * Returns the set of integer parameters
     * @return The set of integer parameters 
     */
    public Set<String> getIntParamSet()
    {
        Set<String> params = new HashSet<String>();
        for (BPDFActor a : _actorList)
        {
            params.addAll(a.getIntParamSet());
        }
        return params;
    }

    /**
     * Returns the list of actors that use the parameter
     * @param param The boolean parameter
     * @return The list of actors that use the parameter
     */
    public List<BPDFActor> getUsers(String param)
    {
        List<BPDFActor> users = new ArrayList<BPDFActor>();
        for (BPDFActor actor : _actorList)
        {
            if (actor.uses(param))
                users.add(actor);
        }
        return users;
    }

    /**
     * Returns all the actors that a given actor is giving data to
     * @param The given actor
     * @return A list of all the actors getting data from the given actor
     */
    public List getSuccessors(BPDFActor a)
    {
        List<BPDFActor> successors = new ArrayList<BPDFActor>();
        for (BPDFEdge edge : _edgeList)
        {
            if (edge.getProducer().getName().equals(a.getName()))
                successors.add(edge.getConsumer());
        }
        return successors;
    }

/***********************************************************************
 ** ANALYSES
 ***********************************************************************/

    /**
     * Consistency Analysis.
     * Solves the graph balance equations.
     * Calculates the repetition vector.
     * @return True if the graph is consistent
     */
    public boolean isConsistent()
    {
        ArrayList<Equation> balanceEquations = new ArrayList<Equation>();
        for (BPDFEdge edge : _edgeList)
        {
            balanceEquations.add( new Equation (
                edge.getRateIn() , edge.getProducer().getName(),
                edge.getRateOut() , edge.getConsumer().getName()));
        }

        ArrayList<String> actorNameList = new ArrayList<String>();
        for (BPDFActor actor : _actorList)
        {
            actorNameList.add(actor.getName());
        }

        SystemSolver solver = new SystemSolver(actorNameList,balanceEquations);
        _repVector = solver.getSolution();
        if (_repVector != null)
            return true;
        else
            return false;
    }

    /**
     * Liveness analysis. 
     * Also finds a schedule for the graph.
     * First finds all directed cycles of the graph.
     * Clustering technique not supported yet.
     * @return True, if the graph is live.
     */
    public boolean isLive()
    {
        CycleDetector cd = new CycleDetector(this);
        List<BPDFGraph> cycles = new ArrayList<BPDFGraph>();

        cycles = cd.getCycles();
        if (cycles.isEmpty())
        { // is acyclic
            return true;
        }
        else if (hasSaturatedCycles(cycles))
        { // has only saturated cycles
            return true;
        } 
        else if (pslc())
        { // PSLC found a schedule
            return true;
        }
        // else if (cluster())
        // { // TODO
        // }
        else
            return false;
    }

    /**
     * Period Safety check.
     * Checks whether the change periods of 
     * all the boolean parameters are period safe.
     * @return True, if all periods are safe
     */
    public boolean isSafe()
    {
        if (!isConsistent()) return false;

        List<String> boolParams = new ArrayList<String>();
        boolParams.addAll(_modifiers.keySet());

        for (String param : boolParams)
        {   
            List<BPDFActor> users = getUsers(param);
            
            BPDFActor modifier = _modifiers.get(param);
            Product period = modifier.getPeriod(param);
            Product modSol = (Product) _repVector.get(modifier.getName());
            Expression freq = modSol.divide(period);

            if (!freq.isProduct()) return false;

            for (BPDFActor actor : users)
            {   
                Product solution = (Product) _repVector.get(actor.getName());
                Expression res = solution.divide(freq);
                if (!res.isProduct()) return false;
                actor.setReadingPeriod(param,res.getProduct());
            }
        }
        return true;
    }

    /**
     * Runs all static analyses on the graph.
     * Checks for consistency, boundedness and liveness.
     * @return True, if all checks return true.
     */
    public boolean verifyGraph()
    {
        if (!isConsistent()) 
            return false;
        else if (!isLive())
            return false;
        else if (!isSafe()) 
            return false;
        return true;
    }

    /**
     * Returns true if the graph has all its cycles saturated
     * @return True if all cycles are saturated
     */
    private boolean hasSaturatedCycles(List<BPDFGraph> cycles)
    {        
        for (BPDFGraph cycle : cycles)
        {
            boolean hasSat = false;
            List<BPDFEdge> eList = cycle.getEdges();
            for(BPDFEdge edge : eList)
            {
                BPDFActor cons = edge.getConsumer();
                Product consRate = edge.getRateOut();
                Product consSol = (Product) _repVector.get(cons.getName());
                Product stored = edge.getTokens();
                Product needed = consSol.multiply(consRate).getProduct();
                if ((stored.isGreaterThan(needed))
                    || (stored.isEqualTo(needed))) 
                    hasSat = true;
            }
            if (!hasSat) return false;
        }
        return true;
    }

    /**
     * Returns a schedule for the graph, null if not possible
     * @return True if a schedule was found
     */
    private boolean pslc() 
    {
        Product zero = new Product(0);
        boolean progress;
        BPDFSchedule sched = new BPDFSchedule();
        Map copyVector = new HashMap<String, Expression>();
        copyVector.putAll(_repVector);

        while (hasMoreFirings(copyVector))
        {
            progress = false;
            for (BPDFActor actor : _actorList)
            {
                String actorName = actor.getName();
                Product firingsLeft = (Product) copyVector.get(actorName);
                if (firingsLeft.isZero()) continue;
                else if (zero.isGreaterThan(firingsLeft))
                    throw new RuntimeException("Actor fired more times "
                            + "than in repetition vector");
                else if (firingsLeft.isNumber())
                {
                    if (actor.prefire())
                    {
                        progress = true;
                        Product one = new Product(1);
                        Product minusOne = new Product("-1");

                        sched.addFiring(actorName,new Product(one));
                        firingsLeft = firingsLeft.add(minusOne).getProduct();
                        copyVector.put(actorName,firingsLeft);
                        actor.fire();
                    }
                }
                else
                {
                    Product paramFirings = firingsLeft.getParam().getProduct();

                    if (actor.prefire(paramFirings))
                    {
                        progress = true;
                        sched.addFiring(actorName,paramFirings);
                        Product nParam = new Product("-" + paramFirings.getString());
                        firingsLeft = firingsLeft.add(nParam).getProduct();
                        copyVector.put(actorName,firingsLeft);
                        actor.fire(new Product(paramFirings));
                        break;
                    }
                }
            }
            if (!progress) return false;
        }
        return true;
    }

    /**
     * Returns true if the given firing vector has firings left
     * @param map The firing vector
     * @return True if the vector still has firings
     */
    private boolean hasMoreFirings(Map<String, Expression> map)
    {
        Iterator<Map.Entry<String, Expression>> iMap 
            = map.entrySet().iterator();
        while (iMap.hasNext())
        {
            Map.Entry<String, Expression> tempEntry = iMap.next();
            Expression tmpExpr = tempEntry.getValue();
            if (!tmpExpr.isZero())
                return true;
        }
        return false;
    }
}