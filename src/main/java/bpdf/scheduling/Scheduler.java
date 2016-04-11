// Scheduler.java
package bpdf.scheduling;

// bpdf
import bpdf.graph.*;
import bpdf.symbol.*;

// util
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

/**
 * The dynamic scheduler. Gets as input a set of constraints, a set of 
 * actors along with their repetition vector, values for the Integer 
 * parameters for the current iteration and produced a slotted parallel 
 * ASAP schedule of a single iteration.
 * @author Vagelis Bebelis
 */
public class Scheduler
{
/***********************************************************************
 ** PRIVATE PARAMETERS
 ***********************************************************************/

    /** Map of lists of boolean values. */
    public Map<String, String> _boolMap 
        = new HashMap<String,String>();

    /** Map of indexes to current boolean values. */
    public Map<String, Integer> _boolIndx 
        = new HashMap<String,Integer>();

    /** BPDF graph. */
    public BPDFGraph graph = new BPDFGraph();

    /** List of constraints on actors. */
    public List<BPDFConstraint> _constraints 
        = new ArrayList<BPDFConstraint>();

    /** List of data dependencies on actors. */
    public List<BPDFConstraint> _dataConstraints 
        = new ArrayList<BPDFConstraint>();

    /** List of actors to be scheduled */
    public List<BPDFActor> _actorList = new ArrayList<BPDFActor>();

    /** Repetition vector. Holds actor name, actor solution. */
    public Map _repVector = new HashMap<String, Expression>();

    /** Status vector. Holds actor name, times fired. */
    public Map _stVector = new HashMap<String, Integer>();

    /** Active vector. Holds actor name, active or not. */
    public Map _actVector = new HashMap<String, Boolean>();

    /** Used to produce random boolean values. */
    public Random _rand = new Random();

    /** Slot counter. */
    public int _slot = 0;

    /** The total time used by the schedule. */
    public int _totalTime = 0;

    /** Keeps the maximum time of the current slot. */
    public int _slotMax = 0;

    /** Boolean that keep track of schedule progress (to remove?) */
    public boolean _advance = false;

/***********************************************************************
 ** CONSTRUCTORS
 ***********************************************************************/

    Scheduler(){}

    /** 
     * Constructor with BPDF graph,
     * map of values of integer parameters and
     * map of values of boolean parameters.
     */
    Scheduler (BPDFGraph g, 
        Map<String, Integer> intMap, 
        Map<String,String> boolMap)
    {
        graph = g;
        _actorList = graph.getActors();
        _repVector = graph.getVector();
        generateDataDependencies();
        generateMuDependencies();
        graph.setIntValues(intMap);
        setBoolValues(boolMap);
        setIntValues(intMap);
        setIndxValues();
    }

/***********************************************************************
 ** METHODS
 ***********************************************************************/

    /**
     * Produces the next boolean value of the given boolean parameter.
     * @param param The name of the boolean parameter.
     */
    boolean nextValue(String param)
    {
        boolean boolValue;
        int indx = _boolIndx.get(param);
        String values = _boolMap.get(param);
        char charValue =  values.charAt(indx);

        // get boolean value
        if (charValue == '0') boolValue = false;
        else if (charValue == '1') boolValue = true;
        else if (charValue == '*') boolValue = _rand.nextBoolean();
        else throw new RuntimeException("Invalid Boolean Value");

        // update indexes
        indx++;
        if (indx == values.length()) indx = 0;
        _boolIndx.put(param,indx);

        return boolValue;
    }

    /**
     * Set values to the Integer parameters.
     * @param map A map with the pairs of Integer parameters 
     * and their values.
     */
    public void setIntValues(Map<String, Integer> map)
    {
        Iterator<Map.Entry<String,Integer>> iMap = 
            map.entrySet().iterator();

        while (iMap.hasNext())
        {
            Map.Entry<String,Integer> tempEntry = iMap.next();
            String param = tempEntry.getKey();
            int value = tempEntry.getValue();

            // Populate Constraints
            for (BPDFConstraint cons : _constraints)
            {
                cons.evaluateParam(param,value);
            }

            // Populate Repetition Vector
            Iterator<Map.Entry<String,Expression>> rMap =
                _repVector.entrySet().iterator();

            while (rMap.hasNext())
            {
                Map.Entry<String,Expression> tEntry = rMap.next();
                String actor = tEntry.getKey();
                Expression sol = tEntry.getValue();
                sol = sol.evaluate(param,value);
                _repVector.put(actor,sol);
            }
        }
    }

    /**
     * Set values to the Boolean parameters.
     * If value is set to '*', the Boolean will get random values.
     * @param map A map with the pairs of Boolean parameters 
     * and their values.
     */
    public void setBoolValues(Map<String,String> boolVal)
    {
        Iterator<Map.Entry<String, String>> iMap 
            = boolVal.entrySet().iterator();

        while (iMap.hasNext())
        {
            Map.Entry<String, String> tempEntry = iMap.next();
            String key = tempEntry.getKey();
            String value = tempEntry.getValue();
            _boolMap.put(key,value);
        }
    }

    /**
     * Initialize values to the indexes of the Boolean parameters.
     */
    public void setIndxValues()
    {
        Set<String> keys = _boolMap.keySet();

        for (String s : keys)
        {
            _boolIndx.put(s,0);
        }
    }

    /** 
     * Returns the constraints imposed on a given actor.
     * @param The actor under consideration.
     * @return The list of constraints imposed on the actor.
     */
    public List<BPDFConstraint> getConstraints(BPDFActor actor)
    {
        List<BPDFConstraint> actorCons =  new ArrayList<BPDFConstraint>();

        for (BPDFConstraint cons : _constraints)
        {
            if (cons.isConstraintOn(actor))
                actorCons.add(cons);
        }
        return actorCons;
    }

    /** 
     * Returns the constraints imposed on a given actor.
     * @param actor The actor under consideration.
     * @return The list of constraints imposed on the actor.
     */
    public List<BPDFConstraint> getAllConstraints(BPDFActor actor)
    {
        List<BPDFConstraint> actorCons =  new ArrayList<BPDFConstraint>();

        for (BPDFConstraint cons : _constraints)
        {
            if (cons.contains(actor))
                actorCons.add(cons);
        }
        return actorCons;
    }

    /**
     * Returns the data dependencies imposed on a give actor.
     * @param actor The actor under consideration.
     * @return The list of constraints imposed on the actor.
     */
    public List<BPDFConstraint> getDataConstraints(BPDFActor actor)
    {
        List<BPDFConstraint> actorCons =  new ArrayList<BPDFConstraint>();

        for (BPDFConstraint cons : _dataConstraints)
        {
            if (cons.contains(actor))
                actorCons.add(cons);
        }
        return actorCons;
    }

/***********************************************************************
 ** GRAPH CONSTRAINTS
 ***********************************************************************/

    private void generateDataDependencies()
    {
        List<BPDFEdge> edgeList = graph.getEdges();

        for (BPDFEdge edge : edgeList)
        {
            BPDFActor leftActor = edge.getConsumer();
            BPDFActor rightActor = edge.getProducer();
            Product rateIn = edge.getRateIn();
            Product rateOut = edge.getRateOut();
            Product tokens = edge.getTokens();
            Product negTokens = new Product("-" + tokens.getString());
            String guard = edge.getGuard();

            // Data Dependency
            Expression f = rateOut.multiply(
                new Product("i")).add(negTokens).divide(rateIn).ceiling();
            BPDFConstraint dataDependency = 
                new BPDFConstraint (leftActor,rightActor,f);
            if (!guard.isEmpty())
            {
                dataDependency.setGuard(guard);
                BooleanComposite tmp = new BooleanComposite(guard);
                Set<String> paramSet = tmp.getParam();
                for (String s : paramSet)
                {
                    dataDependency.setReadingPeriod(s,
                        leftActor.getReadingPeriod(s));
                }
            }
            _constraints.add(dataDependency);
            _dataConstraints.add(dataDependency);
        }
    }

    private void generateMuDependencies()
    {
        Map<String, BPDFActor> modifiers = graph.getModifiers();
        List<String> boolParams = new ArrayList<String>();
        boolParams.addAll(modifiers.keySet());

        for (String param : boolParams)
        {
            List<BPDFActor> users = graph.getUsers(param);
            BPDFActor modifier = modifiers.get(param);
            Product wPeriod = modifier.getPeriod(param);
            Product mSol = (Product) _repVector.get(modifier.getName());
            Product freq = mSol.divide(wPeriod).getProduct();

            for (BPDFActor actor : users)
            {
                if (!actor.getName().equals(modifier.getName()))
                {
                    Product uSol 
                        = (Product) _repVector.get(actor.getName());
                    Product rPeriod = uSol.divide(freq).getProduct();
                    Product negWPeriod 
                        = new Product("-" + wPeriod.getString());
                    Expression aux 
                        = new Fraction(new Product("i"),rPeriod);
                    Expression f = wPeriod.multiply(aux.ceiling()).add(
                        new Product (1)).add(negWPeriod);
                    BPDFConstraint muDependency = 
                        new BPDFConstraint(actor,modifier,f);
                    _constraints.add(muDependency);
                }
            }
        }
    }

/***********************************************************************
 ** USER CONSTRAINTS
 ***********************************************************************/

    public void addConstraints(List<BPDFConstraint> cons)
    {
        for (BPDFConstraint constraint : cons)
        {
            BPDFActor constrainee = constraint.getConActor();
            Set<String> params = constraint.getParam();

            for (String p : params)
            {
                Product period = constrainee.getReadingPeriod(p);
                if (period == null)
                {
                    Product rPeriod = setupReadingPeriod(constrainee, p);
                    constrainee.setReadingPeriod(p,rPeriod);
                    constraint.setReadingPeriod(p,rPeriod);
                }
            }
        }
        _constraints.addAll(cons);
    }

    private Product setupReadingPeriod(BPDFActor actor, String param)
    {
        Map<String, BPDFActor> modifiers = graph.getModifiers();
        BPDFActor modifier = modifiers.get(param);
        Product wPeriod = modifier.getPeriod(param);
        Product modSol = (Product) _repVector.get(modifier.getName());
        Expression freq = modSol.divide(wPeriod);

        Product solution = (Product) _repVector.get(actor.getName());
        Expression res = solution.divide(freq);
        if (!res.isProduct()) throw new RuntimeException(
            "Invalid User Constraint");
        return res.getProduct();
    }

/***********************************************************************
 ** SCHEDULER
 ***********************************************************************/

    /**
     * Executes one iteration of the graph in slots.
     */
    public int getSchedule()
    {
        return 0;
    }

}