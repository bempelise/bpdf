package bpdf.symbol;

import java.util.HashSet;
import java.util.Set;

/**
 * Class for composite  symbolic boolean expressions. They can contain
 * two boolean expressions with an operator.
 * @author Vagelis Bebelis
 */
public class BooleanComposite extends BooleanExpression {
    /** RHS of the composite boolean expression. */
    private BooleanExpression m_right;
    /** LHS of the composite boolean expression. */
    private BooleanExpression m_left;
    /** Operator. */
    private char m_oper;
    /** Value of the boolean parameter. */
    private boolean m_value;
    /** The boolean parameter has taken a value */
    private boolean m_isSet = false;
    /** Not operator */
    private boolean m_not = false;

    public BooleanComposite(String param) {
        parseString(param);
    }

    /**
     * Parse the given String into simpler BooleanComposite and / or
     * BooleanValue expression until the whole string is captured hierarchically
     * by different boolean expressions.
     * Known bug: Still does not handle well parenthesis hierarchy.
     */
    private void parseString(String param) {
        if (param.charAt(0) == '!') {
            if (param.charAt(1) == '(') {
                m_not = true;
                parseString(param.substring(1));
            }
        }

        if (param.charAt(0) == '(') {
            int pClose = param.indexOf(')');
            if (pClose < 0) {
                throw new RuntimeException("Open parenthesis in " + param);
            }

            if ((pClose + 2) < param.length()) {
                m_right = new BooleanComposite(param.substring(1, pClose));
                m_oper = param.charAt(pClose + 1);
                m_left = new BooleanComposite(param.substring(pClose + 2));
            } else {
                parseString(param.substring(1, pClose));
            }

            if ((m_oper != '|') && (m_oper != ('&'))) {
                throw new RuntimeException("Invalid operator: " + m_oper);
            }
        } else {
            int orIndx = param.indexOf('|');
            int andIndx = param.indexOf('&');
            int maximum = Math.max(orIndx, andIndx);
            int minimum = Math.min(orIndx, andIndx);

            if (maximum < 0) {
                m_right = new BooleanValue(param);
                m_oper = '&';
                m_left = new BooleanValue(true);
            } else if (minimum < 0) {
                m_right = new BooleanValue(param.substring(0, maximum));
                m_oper = param.charAt(maximum);
                m_left = new BooleanComposite(param.substring(maximum + 1));
            } else {
                m_right = new BooleanValue(param.substring(0, minimum));
                m_oper = param.charAt(minimum);
                m_left = new BooleanComposite(param.substring(minimum + 1));
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
    public boolean setValue(String p, boolean bool) {
        boolean rightBool = m_right.setValue(p, bool);
        boolean leftBool = m_left.setValue(p, bool);
        if (m_left.isSet() && m_right.isSet()) {
            m_isSet = true;
        }
        return (rightBool | leftBool);
    }

    /**
     * Evaluates and returns the value of the boolean expression.
     * @return The boolean value of this boolean expression.
     */
    public boolean getValue() {
        if (m_isSet) {
            if (m_oper == '|') {
                return (m_left.getValue() | m_right.getValue());
            } else if (m_oper == '&') {
                return (m_left.getValue() & m_right.getValue());
            } else {
                throw new RuntimeException("Invalid operator has been set: " + m_oper);
            }
        } else {
            throw new RuntimeException ("Boolean value of " + m_left + " "
                                        + m_oper + " "+ m_right + " is not set");
        }
    }

    /**
     * Returns true if the boolean expression has all its parameters set.
     * @return True if the all boolean parameters are set.
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
        params.addAll(m_right.getParam());
        params.addAll(m_left.getParam());
        return params;
    }

    /**
     * Prints the boolean expression. Auxiliary function, used to debug.
     */
    public void print() {
        System.out.println(getString());
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
        res += "(" + m_left.getString() + " " + m_oper;
        res += " " + m_right.getString() + ")";
        return res;
    }
}
