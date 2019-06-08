package bpdf.Symbol;

public interface Operand {
    /**
     * @return a numeric value of the expression
     */
    double getValue();

    /**
     * @return a string representation of the expression
     */
    String getExpression();

    /**
     * @return true if the expression is an atomic expression
     */
    boolean isLeaf();
}
