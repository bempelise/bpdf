package bpdf.symbol;

import java.util.Set;
import java.util.HashSet;

/**
 * Class for simple symbolic boolean expressions. They can contain a single
 * boolean value / parameter.
 * @author Vagelis Bebelis
 */
public class BooleanValue extends BooleanExpression {
    /** Parameter name. Always true (false) sets to 'tt' ('ff') */
    private String m_param;
    /** Value of the boolean parameter. */
    private boolean m_value;
    /** The boolean parameter has taken a value */
    private boolean m_isSet = false;
    /** Not operator */
    private boolean m_not = false;

    /**
     * Main constructor based on a string. The '!' character is parsed for
     * negation. In case of tt ot ff, the parameter is set to the default value
     * of true and false equivalently.
     * @param p The string that is the name of the boolean parameter.
     */
    public BooleanValue(String p) {
        if (p.charAt(0) == '!') {
            m_param = p.substring(1);
            m_not = true;
        } else {
            m_param = p;
        }

        if (p.equals("tt")) {
            setValue(m_param, true);
        } else if (p.equals("ff")) {
            setValue(m_param, false);
        }
    }

    /**
     * Constructor creating a boolean constant of true or false. The param is
     * set to tt and ff equivalently.
     * @param The boolean value of this expression.
     */
    public BooleanValue(boolean bool) {
        if (bool) {
            m_param = "tt";
            setValue(m_param, true);
        } else {
            m_param = "ff";
            setValue(m_param, false);
        }
    }

    /**
     * Method setting the value of the given boolean parameter to the given.
     * @param p The parameter name to be set
     * @param bool the boolean value to set
     * @return Returns true if this boolean value contained the given boolean
     * parameter and set its value. Returns false if the there was no effect.
     */
    public boolean setValue(String p, boolean bool) {
        if (m_param.equals(p)) {
            m_value = bool;
            m_isSet = true;
            return true;
        }
        return false;
    }

    /**
     * Returns the value of the boolean expression.
     * @return The boolean value of this boolean expression.
     */
    public boolean getValue() {
        if (m_isSet) {
            if (m_not) {
                return !m_value;
            }
            return m_value;
        } else {
            throw new RuntimeException ("Boolean value of " + m_param + " is not set");
        }
    }

    /**
     * Returns true if the boolean parameter is set.
     * @return True if the boolean parameter is set.
     */
    public boolean isSet() {
        return m_isSet;
    }

    /**
     * Returns a set of string indicating the names of the boolean parameters
     * used in the current boolean expression.
     */
    public Set<String> getParam() {
        Set<String> params = new HashSet<String>();
        if ((!m_param.equals("tt")) && (!m_param.equals("ff")))
            params.add(m_param);
        return params;
    }

    /**
     * Prints the boolean expression. Auxiliary function, used to debug.
     */
    public void print() {
        if (m_not) {
            System.out.print("!");
        }
        System.out.println(m_param);
    }

    /**
     * Returns the boolean expression in String format. Used for debugging.
     * @return The current expression in String format.
     */
    public String getString() {
        String res = "";
        if (m_not) {
            res += "!";
        }
        res += m_param;
        return res;
    }
}
