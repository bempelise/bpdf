// Polynomial.java
package bpdf.symbol;

import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Polynomial expressions
 * @author Vagelis Bebelis
 */
public class Polynomial extends Expression
{

/******************************************************************************
 ** PRIVATE PARAMETERS
 ******************************************************************************/
    
    /**
     * A list holding the polynomial's fractions
     */
    private ArrayList<Expression> _expList = new ArrayList<Expression>();

    /** 
     * Guards indicating whether a floor or ceiling is imposed on the expression
     */
    private boolean ceiling = false;
    private boolean floor = false;

/******************************************************************************
 ** CONSTRUCTORS
 ******************************************************************************/
       
    /**
     * Creates an empty Polynomial
     */
    public Polynomial()
    {
    }
        
    /**
     * Creates a polynomial with a single expression
     * @param expr
     */
    public Polynomial(Expression expr)
    {
        if (expr instanceof CompositeProduct)
        {
            throw new RuntimeException(
                "Construction of Simple out of Composite");
        }

        if (expr instanceof Polynomial)
        {
            Polynomial poly = (Polynomial) expr;
            _expList.addAll(poly.getList());
        }
       _expList.add(expr);
       normalize();
    }
        
    /**
     * Creates a polynomial using the given list of Expressions
     * @param expr
     */
    public Polynomial(ArrayList<Expression> expr)
    {
        for (Expression expression : expr)
        {
            if (expression instanceof Polynomial)
            {
                Polynomial poly = (Polynomial) expression;
                _expList.addAll(poly.getList());
            }
            else
            _expList.add(expression);        
        }
        normalize();
    }
        
/******************************************************************************
 ** EXPRESSION METHODS - MATH FUNCTIONS
 ******************************************************************************/

    /**
     * Adds this polynomial to another expression.
     * Returns the sum as a new polynomial
     * @param expr Expression to add with
     * @return The resulting sum 
     */
    public Expression add (Expression expr)
    {
        ArrayList<Expression> resList = new ArrayList<Expression>();
        resList.addAll(_expList);
        resList.addAll(expr.getPolynomial().getList());
        return new Polynomial(resList);
    }
    
    /**
     * Multiplies this polynomial with a polynomial
     * Returns the product as a new polynomial
     * @param poly Polynomial to multiply with
     * @return The product of the two polynomials
     */
    public Expression multiply (Expression expr)
    {
        Polynomial poly = expr.getPolynomial();
        ArrayList<Expression> resList = new ArrayList<Expression>();
        
        for (int i = 0; i < _expList.size(); i++)
        {
            for (int j = 0; j < poly.getList().size(); j++)
            {
                Expression currFrac = 
                    _expList.get(i).multiply(poly.getList().get(j));
                resList.add(currFrac);
            }
        }
        return new Polynomial(resList);
    }
    
    /** 
     * Divides this Polynomial with the given Expression
     * @param expr The denominator
     * @return An Expression with the result of the division
     */
    public Expression divide (Expression expr)
    {
        if (expr instanceof Polynomial)
            throw new RuntimeException("Cannot divide by a polymomial");
        else
        {
            ArrayList<Expression> resList = new ArrayList<Expression>();
            for (int i = 0; i < this.getList().size(); i++)
            {
                Expression currFrac = _expList.get(i).divide(expr);
                resList.add(currFrac);
            }
            return new Polynomial(resList);
        }
    }
    
    /** 
     * Returns an expression of the Greater Common Divisor
     * between this and the given expression
     * @param expr The given expression
     * @return The GCD
     */
    public Product gcd (Expression expr)
    {
        Polynomial poly = expr.getPolynomial();
        Product thisGCD;
        Product polyGCD;
        
        if (this.getList().size() < 2)
            thisGCD = _expList.get(0).getFraction().getNum();
        else
        {
            thisGCD = _expList.get(0).gcd(_expList.get(1));
            for (int i = 2;  i < _expList.size(); i++)
                thisGCD = thisGCD.gcd(_expList.get(i));
        }

        if (poly.getList().size() < 2)
            polyGCD = poly.getList().get(0).getFraction().getNum();
        else
        {
            polyGCD = poly.getList().get(0).gcd(this.getList().get(1));
            for (int i = 2;  i < poly.getList().size(); i++)
                polyGCD = polyGCD.gcd(poly.getList().get(i));
        }

        return thisGCD.gcd(polyGCD);
    }
    
    /**
     * TODO add an lcm function
     */

    /**
     *  Evaluates an expression for a specific value of the parameters
     * @param str The parameters to be evaluated
     * @param n The integer value used for the evaluation
     * @return The resulting expression
     */
    public Expression evaluate (String str, Integer n)
    {
        ArrayList<Expression> resList = new ArrayList<Expression>();

        for (int i = 0; i < _expList.size();i++)
            resList.add(_expList.get(i).evaluate(str,n));

        Polynomial resPoly = new Polynomial(resList);

        if (ceiling)
            if (resPoly.isFraction())
            {
                Product num = resPoly.getFraction().getNum();
                Product denom = resPoly.getFraction().getDenom();
                if (num.isNumber() && denom.isNumber())
                {
                    int ceiling = (int)
                        Math.ceil((double) num.getNumber() / 
                            (double) denom.getNumber());
                    Product resProd = new Product(ceiling);
                    return resProd;
                }
                else
                    return resPoly.ceiling();
            }
            else
                return resPoly.ceiling();
        else if (floor)
            if (resPoly.isFraction())
            {
                Product num = resPoly.getFraction().getNum();
                Product denom = resPoly.getFraction().getDenom();
                if (num.isNumber() && denom.isNumber())
                {   
                int floor = (int)
                    Math.floor((double) num.getNumber() /
                        (double) denom.getNumber());
                Product resProd = new Product(floor);
                return resProd;
                }
                else
                    return resPoly.floor();
            }
            else
                return resPoly.floor();
        else
            return resPoly;
    }

    /**
     * Marks the expression to be under a ceiling
     */
    public Expression ceiling()
    {
        if (this.isProduct())
            return this.getProduct();
        else
        {
            ceiling = true;
            return this;
        }
    }

    /**
     * Marks the expression to be under a floor
     */
    public Expression floor()
    {
        if (this.isProduct())
            return this.getProduct();
        else
        {
            floor = true;
            return this;
        }
    }

/******************************************************************************
 ** EXPRESSION METHODS - PROPERTY CHECK
 ******************************************************************************/
    
    /** 
     * Compares this with another expression
     * @param expr The expression to compare with
     * @return True if they are equal, false otherwise
     */
    public boolean isEqualTo(Expression expr)
    {
        if (expr instanceof Polynomial)
        {
            Polynomial poly = expr.getPolynomial();
            for (int i = 0; i < _expList.size();i++)
            {
                for (int j = 0; j < poly.getList().size(); j++)
                {
                    if (_expList.get(i).isEqualTo(poly.getList().get(j)))
                    {
                        poly.getList().remove(j);
                        break;
                    }
                }
            }
            if (poly.getList().isEmpty()) 
                return true;
            else
                return false;
        }
        else
        {
            Expression tmpExpr = this.divide(expr);
            if (tmpExpr.isUnit())
                return true;
            else
                return false;
        }
    }

    /**
     * Compares if this is greater than the given expression
     * Throws exception if the expressions are incomparable
     * @param expr The expression to compare with
     * @return True if the current is greater false otherwise
     */
    public boolean isGreaterThan(Expression expr)
    {
        if (this.isProduct())
        {
            this.getProduct().isGreaterThan(expr);
        }
        throw new RuntimeException("Cannot compare non- Products");
    }
    

    /** 
     * Returns true if the expression is equal to 1
     * @return The expression equals 1 or not
     */
    public boolean isUnit() 
    {
        if (this.isFraction())
            return this.getFraction().isUnit();
        return false;
    }

    /** 
     * Returns true if the expression is equal to 0
     * @return The expression equals 0 or not
     */
    public boolean isZero()
    {
        if (_expList.size() == 0)
            return true;
        if (this.isFraction())
            return this.getFraction().isZero();
        return false;
    }

    /**
     * Returns true if the expression can be captured as a Product
     * @return The expression can be a Product or not
     */
    public boolean isProduct()
    {
        if (_expList.size() > 1)
            return false;
        else if (_expList.size() == 0)
            return true;
        else
            return _expList.get(0).isProduct();
    }

    /**
     * Returns true if the expression can be captured as a Fraction
     * @return The expression can be a Product or not
     */
    public boolean isFraction()
    {
        if (_expList.size() == 0)
            return true;
        if (_expList.size() != 1)
            return false;
        else
            return true;
    }

    /**
     * Returns true if the expression can be captured as an integer
     * @return The expression can be an integer or not
     */
    public boolean isNumber()
    {
        for (int i = 0; i < _expList.size(); i++)
        {
            if (!_expList.get(i).isNumber())
                return false;
        }
        return true;
    }

    /**
     * Returns true if fraction is ceilinged
     */
    public boolean hasCeiling()
    {
        return ceiling;
    }

    /**
     * Returns true if fraction is floored
     */
    public boolean hasFloor()
    {
        return floor;
    }


    
/******************************************************************************
 ** EXPRESSION METHODS - TRANSFORMATIONS
 ******************************************************************************/
    
    /** 
     * Returns the equivalent expression as Product if possible
     * @return The expression as Prodcut if possible, null otherwise
     */
    public Product getProduct()
    {
        if (this.isProduct())
        {
            if (_expList.size() == 0)
                return new Product(0);
            else
                return _expList.get(0).getProduct();
        }
        else
            throw new RuntimeException("Polynomial " + this.getString() 
                +  " is not a Product");
    }

    /**
     * Returns the equivalent expression as Fraction if possible
     * @return The expression as Fraction if possible, null otherwise
     */
    public Fraction getFraction()
    {
        if (this.isFraction())
        {
            if (_expList.size() == 0)
                return new Fraction(new Product(0),new Product(1));
            else
                return _expList.get(0).getFraction();
        }
        else
            throw new RuntimeException("Polynomial is NOT a Fraction");
    }

    /**
     * Return the expression as a Polynomial
     */
    public Polynomial getPolynomial()
    {
        return this;
    }

    /**
     * Return the polynomial as string
     * @return Polynomial in string format
     */
    public String getString()
    {
        String res;
        if (_expList.size() >= 1)
        {
            res = _expList.get(0).getString();
            for (int i = 1; i < _expList.size();i++ )
            {
                if (_expList.get(i).isProduct())
                {
                    if (_expList.get(i).getProduct().getSign())
                        res = res + "+" + _expList.get(i).getString();
                    else
                        res = res + _expList.get(i).getString();
                }
                else
                res = res + "+" + _expList.get(i).getString();
            }
                
        } 
        else 
            res = "";

        if (ceiling)
            return ("ceil(" + res + ")");
        else if (floor)
            return ("floor(" + res + ")");
        else
            return res;
        
    }

    /**
     * Returns the expression as an Integer
     */
    public int getNumber()
    {
        if (this.isNumber())
        {
            if (this.isZero()) return 0;
            if (_expList.size() > 1)
                throw new RuntimeException(
                    "If polynomial is number it should have a single fraction");
            return _expList.get(0).getNumber();
        }
        else throw new RuntimeException("Polynomial is NOT an integer");
    }


    /**
     * Returns the parametric part of the Expression
     * @return The resulting Expression
     */
    public Expression getParam()
    {
        return new Polynomial(getParamList());
    }

/******************************************************************************
 ** GETTERS
 ******************************************************************************/

    /**
     * Returns the list of fractions
     * @return The fraction list of the polynomial
     */
    public ArrayList<Expression> getList()
    {
        return _expList;
    }

    /**
     * Returns the parametric part of the list of expressions
     * @return The expression list of the polynomial
     */
    public ArrayList<Expression> getParamList()
    {
        ArrayList<Expression> paramList = new ArrayList<Expression>();
        for(int i = 0; i < _expList.size(); i++)
        {
            paramList.add(_expList.get(i).getParam());
        }
        return paramList;
    }

/******************************************************************************
 ** PRIVATE METHODS
 ******************************************************************************/
    
    /**
     * Normalizes the  _expList
     */
    private void normalize()
    {
        int numeric = 0;       
        if (_expList.size() > 1)
        {
            ArrayList<Expression> paramList = getParamList();
            ArrayList<Expression> resList = new ArrayList<Expression>();

            while (paramList.size() > 0)
            {
                ArrayList<Expression> fracListP = new ArrayList<Expression>();
                ArrayList<Expression> fracList = new ArrayList<Expression>();

                Expression baseFrac = paramList.get(0);
                fracListP.add(baseFrac);
                fracList.add(_expList.get(0));
                // Check if the fraction have common base
                for(int i = 1; i < paramList.size(); i++)
                {
                    Expression frac2 = paramList.get(i);

                    if (baseFrac.isEqualTo(frac2))
                    {
                        fracListP.add(frac2);
                        fracList.add(_expList.get(i));
                    }
                }
                
                // Remove the common fractions
                paramList.removeAll(fracListP);

                // Make homonyms and merge the fractions into a single one
                Expression newFrac1 = fracList.get(0);
                for (int i = 1; i < fracList.size(); i++)
                {
                    int numNum;
                    int numDenom;
                    Expression newFrac2 = fracList.get(i);

                    int newFrac1Num = 
                        newFrac1.getFraction().getNum().getNumeric();
                    if (!newFrac1.getFraction().getNum().getSign()) 
                        newFrac1Num = newFrac1Num*(-1);
                    
                    int newFrac2Num 
                        = newFrac2.getFraction().getNum().getNumeric();
                    if (!newFrac2.getFraction().getNum().getSign()) 
                        newFrac2Num = newFrac2Num*(-1);
                    
                    int newFrac1Denom
                        = newFrac1.getFraction().getDenom().getNumeric();
                    
                    int newFrac2Denom
                        = newFrac2.getFraction().getDenom().getNumeric();

                    numNum = newFrac1Num * newFrac2Denom +
                         newFrac2Num * newFrac1Denom;
                    numDenom = newFrac1Denom * newFrac2Denom;

                    Product numNumP = new Product(numNum);
                    Product numDenomP = new Product(numDenom);
                    newFrac1 = new Fraction
                    (
                        numNumP.multiply(baseFrac.getFraction().getNum()),
                        numDenomP.multiply(baseFrac.getFraction().getDenom())
                    );
                }
                // Remove all used fractions from the expression list
                _expList.removeAll(fracList); 
                // Add the homonym fraction to the results list
                resList.add(newFrac1);
            }
            // Update the expression list
            _expList.addAll(resList);
        }

        for (ListIterator<Expression> iter = 
            _expList.listIterator(_expList.size()); iter.hasPrevious();)
        {
            Expression prevFrac = iter.previous();
            // Remove zeros
            if (prevFrac.isZero())
                iter.remove();
            else if (prevFrac.isNumber())
            {   // Collect numerics in a single fraction
                numeric += prevFrac.getNumber();
                iter.remove();
            }            
        }

        // Add the collective numeric fraction 
        if (numeric != 0)
            _expList.add(new Product(numeric));
    }
}
