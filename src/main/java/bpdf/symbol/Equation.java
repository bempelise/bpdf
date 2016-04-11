// Equation.java
package bpdf.symbol;

public class Equation 
{
/******************************************************************************
 ** PRIVATE PARAMETERS
 ******************************************************************************/

    /**
     * The expression multiplied by the first actor
     */
    private Product expr1;

    /**
     * The expression multiplied by the second actor
     */
    private Product expr2;

    /**
     * The first unknown factor
     */
    private String sol1;

    /**
     * The second unknown factor
     */
    private String sol2;

    /** 
     * Boolean value showing whether the unknown
     * factors of this equations have been solved or not
     */
    private boolean solved;

/******************************************************************************
 ** CONSTRUCTORS
 ******************************************************************************/

    /** 
     * Create a new equation with given Expressions and factors.
     * @param exp1 The first product
     * @param str1 The first factor
     * @param exp2 The second product
     * @param str2 the second actor
     */
    public Equation(Product exp1, String str1, Product exp2, String str2)
    {
        expr1 = exp1;
        sol1 = str1;
        expr2 = exp2;
        sol2 = str2;
        solved = false;
    }

/******************************************************************************
 ** PUBLIC METHODS
 ******************************************************************************/

    /** 
     * Returns true if the given factor is one of the
     * equation's unknown factors
     * @param sol The given factor
     * @return True if found among the equation's unknown factors
     */
    public boolean hasSolution(String sol)
    {
        if (sol1.equals(sol)) return true;
        if (sol2.equals(sol)) return true;
        return false;
    }

    /** 
     * Given the value of an unknown factor
     * solve the other
     * @param str The solved unknown factor
     * @param expr The value of the unknown factor
     * @return A fraction holding the solution for the remaining unknown factor
     */
    public Expression solve(String str, Expression solA)
    {
        if (sol1.equals(str)) 
        {
            solved = true;
            return solA.multiply(expr1).divide(expr2);
        } 
        else if (sol2.equals(str)) 
        {
            solved = true;
            return solA.multiply(expr2).divide(expr1);
        }
        else throw new RuntimeException("Non existing factor in the equation");
    }


    /** 
     * Return the other unknown factor of the equation
     * (Other than the one given)
     * @param sol One of the unknown factors of the equation
     * @return The other unknown factor of the equation
     */
    public String getOther(String sol)
    {
        if (sol1.equals(sol)) return sol2;
        else if (sol2.equals(sol)) return sol1;
        else throw new RuntimeException("Non existing factor in the equation");
    }

    /** 
     * Return whether the unknown factors of the equations
     * are already solved
     * @return True if solved
     */
    public boolean isSolved()
    {
        return solved;
    }

    public String toString()
    {
        return (expr1.getString() + " * " + sol1 + " = " +
                expr2.getString() + " * " + sol2);
    }
}