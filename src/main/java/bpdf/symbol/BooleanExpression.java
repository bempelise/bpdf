// BooleanExpression.java
package bpdf.symbol;

import java.util.Set;

/**
 * Abstract class for symbolic boolean expressions. Instantiated
 * in subclasses BooleanComposite and BooleanValue.
 * @author Vagelis Bebelis
 */
public abstract class BooleanExpression
{
    public abstract boolean setValue(String param, boolean bool);
    public abstract boolean getValue();
    public abstract boolean isSet();
    public abstract void print();
    public abstract String getString();
    public abstract Set<String> getParam();
}