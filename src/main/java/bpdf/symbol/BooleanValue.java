// BooleanValue.java
package bpdf.symbol;

import java.util.Set;
import java.util.HashSet;

/**
 * Class for simple symbolic boolean expressions. They can contain a single
 * boolean value / parameter.
 * @author Vagelis Bebelis
 */
public class BooleanValue extends BooleanExpression
{
/******************************************************************************
 ** PRIVATE PARAMETERS
 ******************************************************************************/

    /*
     * The boolean parameter in string format. In case of always true / false
     * values the string is set to tt and ff equivalently.
     */
    private String param;

    /**
     * The value of the boolean parameter.
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

    /**
     * Main constructor based on a string. The '!' character is parsed for
     * negation. In case of tt ot ff, the parameter is set to the default value
     * of true and false equivalently.
     * @param p The string that is the name of the boolean parameter.
     */
    public BooleanValue(String p)
    {
        if (p.charAt(0) == '!')
        {
            param = p.substring(1);
            not = true;
        }
        else
            param = p;
        
        if (p.equals("tt"))
            setValue(param,true);
        else if (p.equals("ff"))
            setValue(param,false);
    }


    /**
     * Constructor creating a boolean constant of true or false. The param is
     * set to tt and ff equivalently. 
     * @param The boolean value of this expression.
     */
    public BooleanValue(boolean bool)
    {
        if (bool)
        {
            param = "tt";
            setValue(param,true);
        }
        else
        {
            param = "ff";
            setValue(param,false);
        }
    }

/******************************************************************************
 ** GET / SET BOOLEAN VALUE
 ******************************************************************************/

    /**
     * Method setting the value of the given boolean parameter to the given.
     * @param p The parameter name to be set
     * @param bool the boolean value to set
     * @return Returns true if this boolean value contained the given boolean
     * parameter and set its value. Returns false if the there was no effect.
     */
    public boolean setValue(String p, boolean bool)
    {
        if (param.equals(p))
        {
            value = bool;
            isSet = true;
            return true;
        }
        return false;
    }

    /**
     * Returns the value of the boolean expression.
     * @return The boolean value of this boolean expression.
     */
    public boolean getValue()
    {
        if (isSet)
            if (not)
                return !value;
            else
                return value;
        else
            throw new RuntimeException ("Boolean value of " 
                + param + " is not set");
    }

    /**
     * Returns true if the boolean parameter is set.
     * @return True if the boolean parameter is set.
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
        if ((!param.equals("tt")) && (!param.equals("ff")))
            params.add(param);
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
        if (not) System.out.print("!");
        System.out.println(param);
    }

    /**
     * Returns the boolean expression in String format. Used for debugging.
     * @return The current expression in String format.
     */
    public String getString()
    {
        String res = "";
        if (not) res += "!";
        res += param;
        return res;
    }
}