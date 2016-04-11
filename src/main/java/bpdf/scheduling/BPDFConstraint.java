// BPDFConstraint.java
package bpdf.scheduling;

import bpdf.graph.*;
import bpdf.symbol.*;

import java.util.Set;
import java.util.Map;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * Main constraint class. Captures a constraint wit ha left and right actors, 
 * where the left is the dependent actor and the right is the actor imposing the
 * constraint. There are also the left and right function. For simplicity the
 * left function is always set to i. The constraint also uses the guard that
 * disables / enables the constraint.
 */
public class BPDFConstraint
{
/******************************************************************************
 ** PRIVATE PARAMETERS
 ******************************************************************************/

    /**
     * Left Actor
     */
    private BPDFActor lActor;

    /**
     * Right Actor
     */
    private BPDFActor rActor;

    /**
     * Right Function's Expression, The left function is considered to always
     * be set to "i".
     */
    private Expression rFunc;

    /**
     * Boolean guard enabling / disabling the constraint
     * Defaults to true (no guard)
     */
    private BooleanExpression guard = new BooleanValue(true);

    /**
     * Set holding the names of the boolean parameters used in the guard of
     * this constraint.
     */
    private Set<String> params = new HashSet<String>();

    /**
     * Map that holds the reading periods of each boolean parameter.
     */
    private Map periodMap = new HashMap<String, Product>();

    /**
     * Map that holds the values of each boolean parameter.
     */
    private Map<String, List<Boolean>> valueMap 
                                    = new HashMap<String, List<Boolean>>();

    /**
     * Shifting the boolean values in case of a user constraint that does that.
     * E.g. E(i+1) > D(i) becomes E(i) > D(i-1) with shift 1.
     */
    private int shift = 0;

/******************************************************************************
 ** CONSTRUCTORS
 ******************************************************************************/

    /**
     * Main Constructor
     * Needs the left and right actor as well as the right function.
     * @param leftActor The left actor
     * @param rightActor The right actor
     * @param rightFunc The right Function as Expression
     */
    public BPDFConstraint (BPDFActor leftActor, 
        BPDFActor rightActor,  Expression rightFunc)
    {
        lActor = leftActor;
        rActor = rightActor;
        rFunc = rightFunc;
    }

/******************************************************************************
 ** EVALUATION
 ******************************************************************************/

    /**
     * Evaluates the constraints and returns the number of firings
     * of the right actor needed for the left actor to be enabled.
     * Returns 0 if the constraint is disabled (e.g. the guard is false)
     * Returns -1 if the guard is not set
     * @param i The next firing of the left actor (ith firing)
     * @return The firings needed of the right actor
     */
    public int evaluate(int i)
    {
        int st = i + shift - 1;
        if (isSet(st))
        {
            if (!evaluateGuard(st))
                return -1;
            else
            {
                if (guard.getValue())
                {
                    int slot = rFunc.evaluate("i",i).getNumber();
                    if (slot < 0)
                        return 0;
                    else
                        return slot;
                }
                else
                    return 0;
            }
        }
        else
        { // if not set return the value anyway
            // Ugly - used in the case of user constraints.
            int slot = rFunc.evaluate("i",i).getNumber();
            if (slot < 0)
                return 0;
            else
                return slot;
        }
    }

    /**
     * Evaluates the left and right expressions by fixing values to the
     * parameters.
     * @param param The parameter to be set.
     * @param n The value to be set.
     */
    public void evaluateParam(String param, int n)
    {
        rFunc = rFunc.evaluate(param,n);
        
        for(String p : params)
        {
            Product period = (Product) periodMap.get(p);
            Product evalPeriod = period.evaluate(param,n);
            periodMap.put(p,evalPeriod);
        }
    }

    /**
     * Evaluates the boolean guard based on the stored boolean values.
     * @param status The number of the current firing.
     * @return True if the guard is set to a specific boolean value
     */
    public boolean evaluateGuard(int status)
    {
        for (String p : params)
        {
            List<Boolean> values = (List<Boolean>) valueMap.get(p);
            guard.setValue(p,(Boolean) values.get(status));
        }

        if (guard.isSet())
            return true;            
        else
            return false;
    }


    /**
     * Returns the boolean value that will be used for the given firing.
     * @param status The given firing
     * @return The boolean value that the guard takes.
     */
    public boolean getGuardValue(int status)
    {
        int st = status + shift;
        if (evaluateGuard(st))
            return guard.getValue();
        else
            throw new RuntimeException("Guard is not set");
    }

/******************************************************************************
 ** BOOLEAN GUARDS
 ******************************************************************************/

    /**
     * Adds a guard to the constraint. The guard is given as a string
     * @param g The guard as a string.
     */
    public void setGuard(String g)
    {
        guard = new BooleanComposite(g);
        params = guard.getParam();
        for (String p : params)
        {
            periodMap.put(p,new Product(1));
            valueMap.put(p,new ArrayList<Boolean>());
        }
    }

    /**
     * Adds a guard to the constraint. The guard is given as a Boolean 
     * Expression.
     * @param expr The Boolean Expression
     */
    public void setGuard(BooleanExpression expr)
    {
        guard = expr;
        params = guard.getParam();
        for (String p : params)
        {
            periodMap.put(p,new Product(1));
            valueMap.put(p,new ArrayList<Boolean>());
        }
    }

    /**
     * Returns the edge's gaurd as a Boolean Expression.
     */
    public BooleanExpression getGuard()
    {
        return guard;
    }

    /**
     * Sets the value of a boolean parameter to value.
     * @param param The boolean parameter.
     * @param The boolean value.
     */
    public void setParam(String param, boolean value)
    {
        if (params.contains(param))
        {
            Product period = (Product) periodMap.get(param);
            List<Boolean> values = (List<Boolean>) valueMap.get(param);
            for (int i = 0; i < period.getNumber(); i++)
            {
                values.add(value);
            }
            valueMap.put(param,values);
        }
    }

    /**
     * Checks whether the boolean variables composing the boolean guard have
     * been assigned values for the given firing
     * @param status The given firing
     * @return True if all boolean parameters are set.
     */
    public boolean isSet(int status)
    {
        if (status < 0) return false;
        for (String p : params)
        {
            List<Boolean> values = (List<Boolean>) valueMap.get(p);
            int size = values.size();
            if (size <= status)
                return false;
        }
        return true;
    }

    /**
     * Sets the reading period for the given boolean parameter.
     * @param param The name of the boolean parameter.
     * @param The reading period of the boolean parameter.
     */
    public void setReadingPeriod(String param, Product period)
    {
        if (params.contains(param))
            periodMap.put(param,period);
    }

    /**
     * Returns a set of all the boolean parameters used by the constraint's
     * boolean guard.
     * @return The set with the boolean parameters.
     */
    public Set<String> getParam()
    {
        return params;
    }

/******************************************************************************
 ** ACTOR FUNCTIONS
 ******************************************************************************/

    /**
     * Returns true of the constraint is imposed on the given actor.
     * @param actor Actor to be checked if is the dependent
     * @return True if the constraint is imposed on the given actor.
     */
    public boolean isConstraintOn (BPDFActor actor)
    {
        return (lActor.getName().equals(actor.getName()));
    }

    /**
     * Returns true of the constraint uses the given actor (on either end).
     * @param actor Actor to be checked if is used by the constraint.
     * @return True If the constraint uses the given actor.
     */
    public boolean contains(BPDFActor actor)
    {
        String name = actor.getName();
        String lname = lActor.getName();
        String rname = rActor.getName();
        return ((lname.equals(name)) || (rname.equals(name)));
    }

    /**
     * Returns the actor that imposes this constraint (Right actor).
     * @return The constraining actor.
     */
    public BPDFActor getDepActor()
    {
        return rActor;
    }

    /**
     * Returns the actor that is constrained (left actor).
     * @return The constrained actor.
     */
    public BPDFActor getConActor()
    {
        return lActor;
    }

    /**
     * Set the shift value. Used for user constraints, see shift variable.
     * @param n The shifting value.
     */
    public void setShift(int n)
    {
        shift = n;
    }

/******************************************************************************
 ** AUXILIARY
 ******************************************************************************/

    /**
     * Auxiliary function that returns the constraint as a string for debugging.
     * @return THe constraint as a string.
     */
    public String getString()
    {
        String res = lActor.getName() + "[i]" + " > " 
            + rActor.getName() + "[" + rFunc.getString() + "]";
        return res;
    }
}