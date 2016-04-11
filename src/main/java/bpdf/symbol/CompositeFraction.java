// CompositeFraction.java
package bpdf.symbol;

import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Composite product to support multiplications and divisions with ceilings
 * and floors
 * @author Vagelis Bebelis
 */
public class CompositeFraction extends CompositeExpression
{

/******************************************************************************
 ** PRIVATE PARAMETERS
 ******************************************************************************/
    
    /**
     * Numerator / Denominator of the composite fraction
     */
    private Expression _num;
    private Expression _denom;

/******************************************************************************
 ** CONSTRUCTORS
 ******************************************************************************/


    /**
     * Main Constructor
     */
    public CompositeFraction (Expression expr1, Expression expr2)
    {
        _num = expr1;
        _denom = expr2;
    }

/******************************************************************************
 ** GETTERS
 ******************************************************************************/

    public Expression getNum()
    {
        return _num;
    }

    public Expression getDenom()
    {
        return _denom;
    }

/******************************************************************************
 ** MATH FUNCTIONS
 ******************************************************************************/

    public Expression add(Expression expr)
    {
        return new CompositePolynomial(this,expr);
    }

    public Expression multiply(Expression expr)
    {
        return new CompositeProduct(this,expr);
    }

    public Expression divide (Expression expr)
    {
        return new CompositeFraction(this,expr);
    }

    public Product gcd (Expression expr)
    {
        return _num.gcd(expr);
    }

    public Expression evaluate(String str, Integer n)
    {
        Expression evalNum;
        Expression evalDenom;
        boolean ceilOrFloor = false;

        evalNum = _num.evaluate(str,n);
        if (evalNum.hasFloor() || evalNum.hasCeiling())
            ceilOrFloor = true;
        evalDenom = _denom.evaluate(str,n);
        if (evalDenom.hasFloor() || evalDenom.hasCeiling())
            ceilOrFloor = true;
        if (ceilOrFloor)
            return new CompositeProduct(evalNum, evalDenom);
        else
        {
            if (!(evalNum.isFraction() && evalDenom.isFraction()))
                return new Fraction(evalNum, evalDenom);
            else
                return new CompositeProduct(evalNum, evalDenom);
        }
    }

    public Expression ceiling ()
    {
        throw new RuntimeException ("Cannot get ceiling of composites");
    }

    public Expression floor ()
    {
        throw new RuntimeException ("Cannot get floot of composites");
    }

/******************************************************************************
 ** PROPERTY CHECK
 ******************************************************************************/

    public boolean isEqualTo(Expression expr)
    {
        return false;
    }

    public boolean isGreaterThan(Expression expr)
    {
        return false;
    }

    public boolean isUnit()
    {
        return false;
    }

    public boolean isZero()
    {
        return false;
    }

    public boolean isProduct()
    {
        return false;
    }

    public boolean isFraction()
    {
        return false;
    }

    public boolean isNumber()
    {
        return false;
    }

    public boolean hasCeiling()
    {
        return true;
    }

    public boolean hasFloor()
    {
        return true;
    }

/******************************************************************************
 ** TRANSFORMATIONS
 ******************************************************************************/

    public Product getProduct()
    {
        throw new RuntimeException("Composite Fractions cannot be products");
    }
    public Fraction getFraction()
    {
        throw new RuntimeException("Composite Fractions cannot be fractions");
    }

    public Polynomial getPolynomial()
    {
        throw new RuntimeException("Composite Fractions cannot be polynomials");
    }
    public int getNumber()
    {
        throw new RuntimeException("Composite Fractions cannot be numbers");
    }

    public String getString()
    {
        String res = _num.getString() + "/" + _denom.getString();
        return res;
    }
    
    public Expression getParam()
    {
        Expression numParam = _num.getParam();
        Expression denomParam = _denom.getParam();
        return new Fraction(numParam, denomParam);
    }
}
