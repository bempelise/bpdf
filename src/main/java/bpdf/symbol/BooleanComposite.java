// BooleanComposite.java
package bpdf.symbol;

import java.util.Set;
import java.util.HashSet;

/**
 * Class for composite  symbolic boolean expressions. They can contain
 * two boolean expressions with an operator.
 * @author Vagelis Bebelis
 */
public class BooleanComposite extends BooleanExpression
{
/******************************************************************************
 ** PRIVATE PARAMETERS
 ******************************************************************************/

    /**
     * The right side of the composite boolean expression.
     */
    private BooleanExpression right;

    /*
     * The left part of the composite boolean expression.
     */
    private BooleanExpression left;

    /**
     * Character capturing the operator between the two boolean expressions.
     */
    private char oper;

    /**
     * The boolean value that this composite resolves to.
     */
    private boolean value;

    /**
     * Boolean value indicating whether the boolean parameter has taken a value
     * or not.
     */
    private boolean isSet = false;

    /**
     * Boolean value indicating whether the boolean value is negated or not.
     * Used to capture the boolean not operation.
     */
    private boolean not = false;

/******************************************************************************
 ** CONSTRUCTORS
 ******************************************************************************/

    public BooleanComposite(String param)
    {
        parseString(param);
    }

/******************************************************************************
 ** GET / SET BOOLEAN VALUE
 ******************************************************************************/

    /**
     * Parse the given String into simpler BooleanComposite and / or
     * BooleanValue expression until the whole string is captured hierarchically
     * by different boolean expressions.
     * Known bug: Still does not handle well parenthesis hierarchy.
     */
    private void parseString(String param)
    {
        if (param.charAt(0) == '!' )
        {
            if (param.charAt(1) == '(')
            {
                not = true;
                parseString(param.substring(1));
            }
        }

        if (param.charAt(0) == '(' )
        {
            int pClose = param.indexOf( ')' );

            if (pClose < 0) throw new RuntimeException("Open parenthesis " 
                    + "in " + param);
            
            if ((pClose + 2) < param.length())
            {
                right = new BooleanComposite(param.substring(1,pClose));
                oper = param.charAt(pClose + 1);
                left = new BooleanComposite(param.substring(pClose + 2));
            }
            else
            {
                parseString(param.substring(1,pClose));
            }

            if ((oper != '|') && (oper != ('&')))
                throw new RuntimeException("Invalid operator: " + oper);
        }
        else
        {
            int orIndx = param.indexOf('|');
            int andIndx = param.indexOf('&');
            int maximum = Math.max(orIndx,andIndx);
            int minimum = Math.min(orIndx,andIndx);
                    
            if (maximum < 0)
            {
                right = new BooleanValue(param);
                oper = '&';
                left = new BooleanValue(true);
            }
            else if (minimum < 0)
            {
                right = new BooleanValue(param.substring(0,maximum));
                oper = param.charAt(maximum);
                left = new BooleanComposite(param.substring(maximum + 1));
            }
            else
            {
                right = new BooleanValue(param.substring(0,minimum));
                oper = param.charAt(minimum);
                left = new BooleanComposite(param.substring(minimum + 1));
            }
        }
    }

    /**
     * Method setting the value of the given boolean parameter to the given.
     * The value propagates recursively to all the underlying BooleanComposite
     * and BooleanValue enclosed in the current boolean expression.
     * @param p The parameter name to be set
     * @param bool the boolean value to set
     * @return Returns true if this boolean value contained the given boolean
     * parameter and set its value. Returns false if the there was no effect.
     */
    public boolean setValue(String p, boolean bool)
    {
        boolean rightBool = right.setValue(p,bool);
        boolean leftBool = left.setValue(p,bool);
        if (left.isSet() && right.isSet())
            isSet = true;
        return (rightBool | leftBool);
    }

    /**
     * Evaluates and returns the value of the boolean expression.
     * @return The boolean value of this boolean expression.
     */
    public boolean getValue()
    {
        if (isSet)
        {
            if (oper == '|')
                return (left.getValue() | right.getValue());
            else if (oper == '&')
                return (left.getValue() & right.getValue());
            else
                throw new RuntimeException("Invalid operator has"
                + " been set: " + oper);
        }
        else
            throw new RuntimeException ("Boolean value of " 
                + left + " " 
                + oper + " " 
                + right + " is not set");
    }

    /**
     * Returns true if the boolean expression has all its parameters set.
     * @return True if the all boolean parameters are set.
     */
    public boolean isSet()
    {
        return isSet;
    }

    /**
     * Returns a set of string indicating the names of the boolean parameters
     * used in the current boolean expression.
     */
    public Set<String> getParam()
    {
        Set<String> params = new HashSet<String>();
        params.addAll(right.getParam());
        params.addAll(left.getParam());
        return params;
    }

/******************************************************************************
 ** AUXILIARY
 ******************************************************************************/

    /**
     * Prints the boolean expression. Auxiliary function, used to debug.
     */
    public void print()
    {
        System.out.println(getString());
    }

    /**
     * Returns the boolean expression in String format. Used for debugging.
     * @return The current expression in String format.
     */
    public String getString()
    {
        String res = "";
        if (not) res += "!";
        res += "("
            + left.getString() + " "
            + oper + " "
            + right.getString() + ")";
        return res;
    }
}