package bpdf.symbol;

public class Equation {
    /** The expression multiplied by the first actor */
    private Product m_expr1;

    /** The expression multiplied by the second actor */
    private Product m_expr2;

    /** The first unknown factor */
    private String m_sol1;

    /** The second unknown factor */
    private String m_sol2;

    /** The equation has been solved. */
    private boolean m_solved;

    /**
     * Create a new equation with given Expressions and factors.
     * @param exp1 The first product
     * @param str1 The first factor
     * @param exp2 The second product
     * @param str2 the second actor
     */
    public Equation(Product exp1, String str1, Product exp2, String str2) {
        m_expr1 = exp1;
        m_sol1 = str1;
        m_expr2 = exp2;
        m_sol2 = str2;
        m_solved = false;
    }

    /**
     * Returns true if the given factor is one of the
     * equation's unknown factors
     * @param sol The given factor
     * @return True if found among the equation's unknown factors
     */
    public boolean hasSolution(String sol) {
        if (m_sol1.equals(sol) || m_sol2.equals(sol)) {
            return true;
        }
        return false;
    }

    /**
     * Given the value of an unknown factor
     * solve the other
     * @param str The solved unknown factor
     * @param expr The value of the unknown factor
     * @return A fraction holding the solution for the remaining unknown factor
     */
    public Expression solve(String str, Expression solA) {
        if (m_sol1.equals(str)) {
            m_solved = true;
            return solA.multiply(m_expr1).divide(m_expr2);
        } else if (m_sol2.equals(str)) {
            m_solved = true;
            return solA.multiply(m_expr2).divide(m_expr1);
        } else {
            throw new RuntimeException("Non existing factor in the equation");
        }
    }

    /**
     * Return the other unknown factor of the equation
     * (Other than the one given)
     * @param sol One of the unknown factors of the equation
     * @return The other unknown factor of the equation
     */
    public String getOther(String sol) {
        if (m_sol1.equals(sol)) {
            return m_sol2;
        } else if (m_sol2.equals(sol)) {
            return m_sol1;
        } else {
            throw new RuntimeException("Non existing factor in the equation");
        }
    }

    /**
     * Return whether the unknown factors of the equations
     * are already solved
     * @return True if solved
     */
    public boolean isSolved() {
        return m_solved;
    }

    public String toString() {
        return (m_expr1.getString() + " * " + m_sol1 + " = "
                + m_expr2.getString() + " * " + m_sol2);
    }
}
