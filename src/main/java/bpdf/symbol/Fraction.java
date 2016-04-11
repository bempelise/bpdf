// Fraction.java
package bpdf.symbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.lang.Math;

/**
 * Fraction expressions
 * @author Vagelis Bebelis
 */
public class Fraction extends Expression 
{
/******************************************************************************
 ** PRIVATE PARAMETERS
 ******************************************************************************/

    /** 
     * The fraction's numerator
     */
    private Product _num = new Product(1);

    /** 
     * The fraction's denominator
     */     
    private Product _denom = new Product(1);

    /** 
     * Guards indicating whether a floor or ceiling is imposed on the expression
     */
    private boolean ceiling = false;
    private boolean floor = false;

/******************************************************************************
 ** CONSTRUCTORS
 ******************************************************************************/

    /** 
     * Creates a 1/1 fraction
     */
    public Fraction()
    {
    }

    /** 
     * Creates a prod/1 fraction
     * @param prod The numerator
     */
    public Fraction(Product prod)
    {
        _num = new Product(prod);
        normalize();
    }

    /** 
     * Creates a num/denum fraction
     * @param num Numerator
     * @param denom Denominator
     */
    public Fraction(Product num,Product denom)
    {
        _num = new Product(num);
        _denom = new Product(denom);
        normalize();
    }

    /** 
     * Creates a num/denum fraction
     * @param num Numerator
     * @param denom Denominator
     */
    public Fraction(Expression num, Expression denom)
    {
        if ((!num.isFraction()) || (!denom.isFraction())) throw new 
            RuntimeException("Cannot create Fraction from Polynomial");
        Fraction numFrac = num.getFraction();
        Fraction denomFrac = denom.getFraction();
        _num = numFrac.getNum().multiply(denomFrac.getDenom()).getProduct();
        _denom = numFrac.getDenom().multiply(denomFrac.getNum()).getProduct();
        normalize();
    }

/******************************************************************************
 ** EXPRESSION METHODS - MATH FUNCTIONS
 ******************************************************************************/

    /**
     * Adds the Fraction to an expression
     * Returns the resulting polynomial
     * @param expr The expression to add with
     * @return The sum of the expression with the product
     */
    public Expression add(Expression expr)
    {
        return expr.getPolynomial().add(this);
    }

    /** 
     * Multiplies this with an expression
     * Returns the result as a fraction
     * @param expr The expression to multiply with
     * @return The product of the fraction by the expression
     */
    public Expression multiply(Expression expr)
    {

        if (expr instanceof Polynomial)
        {
            Polynomial poly = (Polynomial) expr;
            return poly.multiply(this);
        }
        else if (expr instanceof CompositeProduct)
        {
            return new CompositeProduct (this,expr);
        }
        else if (expr instanceof Fraction)
        {            
            Fraction frac = (Fraction) expr;
            if ((frac.hasCeiling() || frac.hasFloor()) 
                && (!frac.isProduct()))
            {
                return new CompositeProduct(this,frac);
            }
            else if ((this.hasCeiling() || this.hasFloor())
                && (!this.isProduct()))
            {
                return new CompositeProduct(frac,this);
            }
            else
            {
                Fraction resFrac = new Fraction
                (
                    _num.multiply(frac.getNum()).getProduct(),
                    _denom.multiply(frac.getDenom()).getProduct()
                );
                if (resFrac.isProduct())
                    return resFrac.getProduct();
                return resFrac;
            }
        }
        else
        {
            if (this.hasCeiling() || this.hasFloor())
            {
                return new CompositeProduct(expr,this);
            }
            else
            {
                Fraction resFrac = new Fraction
                (
                    _num.multiply(expr.getFraction().getNum()).getProduct(),
                    _denom.multiply(expr.getFraction().getDenom()).getProduct()
                );
                return resFrac;
            }
        }
    }

    /** 
     * Divides this fraction with another expression
     * Returns the resulting expression
     * @param expr The expression to divide by
     * @return The result of the division
     */
    public Expression divide (Expression expr)
    {
        // Check if divide by 0
        if (expr.isZero()) throw new RuntimeException("Cannot divide by 0!");
        // Check if divide by 1
        if (expr.isUnit()) return this;

        if (expr instanceof Polynomial)
        {
            throw new RuntimeException("Cannot divide by a Polynomial");
        }
        else
        {
            Fraction resFrac = new Fraction
            (
                _num.multiply(expr.getFraction().getDenom()).getProduct(),
                _denom.multiply(expr.getFraction().getNum()).getProduct()
            );
            return resFrac;
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
        if (expr instanceof Polynomial)
        {
            return expr.gcd(this);
        }
        else
        {
            return _num.gcd(expr.getFraction().getNum());
        }
    }
    
    
    /**
     * TODO Add an lcm function
     */
    
    /**
     *  Evaluates an expression for a specific value of the parameters
     * @param str The parameters to be evaluated
     * @param n The integer value used for the evaluation
     * @return The resulting expression
     */
    public Expression evaluate(String str, Integer n)
    {
        Product evalNum = (Product) _num.evaluate(str,n);
        Product evalDenom = (Product) _denom.evaluate(str,n);
        Fraction resFrac = new Fraction(evalNum,evalDenom);

        if (ceiling)
            if (evalNum.isNumber() && evalDenom.isNumber())
            {
                int ceiling = (int) Math.ceil((double)evalNum.getNumber() / 
                    (double) evalDenom.getNumber());
                Product resProd = new Product(ceiling);
                return resProd;
            }
            else
                return resFrac.ceiling();
        else if (floor)
            if (evalNum.isNumber() && evalDenom.isNumber())
            {   
                int floor = (int) Math.floor((double) evalNum.getNumber() /
                    (double) evalDenom.getNumber());
                Product resProd = new Product(floor);
                return resProd;
            }
            else
                return resFrac.floor();
        else
            return resFrac;
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
     * Compares this with another expression for equality
     * @param expr The expression to compare with
     * @return True if they are equal, false otherwise
     */
    public boolean isEqualTo(Expression expr)
    {
        if (expr instanceof Polynomial)
        {
            return expr.isEqualTo(this);
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
     * Returns true if the fraction equals to 1
     * @return The fraction equals 1 or not
     */
    public boolean isUnit()
    {
        if ((_num.isUnit())&&(_denom.isUnit()))
            return true;
        return false;
    }
    
    /** 
     * Returns true if the expression is equal to 0
     * @return The expression equals 0 or not
     */
    public boolean isZero()
    {
        if (_num.isZero())
            return true;
        return false;
    }

    /**
     * Returns true if the expression can be captured as a Product
     * @return The expression can be a Product or not
     */
    public boolean isProduct()
    {
        if (_denom.isUnit())
            return true;
        return false;
    }

    /**
     * Returns true if the expression can be captured as a Fraction
     * @return The expression can be a Product or not
     */
    public boolean isFraction()
    {
        return true;
    }

    /**
     * Returns true if the expression can be captured as an integer
     * @return The expression can be an integer or not
     */
    public boolean isNumber()
    {
        if (!_denom.isNumber())
            return false;
        else if (!_num.isNumber())
            return false;
        else
        {
            int denom = getDenom().getNumber();
            int numer = getNum().getNumber();
            if (!(numer % denom == 0))
                return false;
            else 
                return true;
        }
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
     * @return The expression as Product if possible, null otherwise
     */
    public Product getProduct()
    {
        if (isProduct())
            return _num;
        return null;
    }

    /**
     * Returns the equivalent expression as Fraction if possible
     * @return The expression as Fraction if possible, null otherwise
     */
    public Fraction getFraction()
    {
        return this;
    }

    /**
     * Return the expression as a Polynomial
     */
    public Polynomial getPolynomial()
    {
        return new Polynomial(this);
    }

    /**
     * Return the expression as an integer
     */
    public int getNumber()
    {
        if (this.isNumber())
        {
            int denom = getDenom().getNumber();
            int numer = getNum().getNumber();
            return numer/denom;
        }
        else throw new RuntimeException("Fraction is NOT an integer");
    }

    /** 
     * Returns the fraction as a string
     * @return The string representation of the fraction
     */
    public String getString()
    {
        String res = _num.getString() + "/" + _denom.getString();
        
        if (ceiling)
            return ("ceil(" + res + ")");
        else if (floor)
            return ("floor(" + res + ")");
        else
            return res;
    }

    /**
     * Return the parametric part of the Expression
     * @return The resulting Expression
     */
    public Expression getParam()
    {
        Fraction param = new Fraction(_num.getParam(),_denom.getParam());
        return param;
    }


/******************************************************************************
 ** GETTERS
 ******************************************************************************/
    
    /** 
     * Returns the numerator
     * @return The Fraction's numerator
     */
    public Product getNum()
    {
        return _num;
    }

    /** 
     * Returns the denominator
     * @return The Fraction's denominator
     */
    public Product getDenom()
    {
        return _denom;
    }

/******************************************************************************
 ** PRIVATE METHODS
 ******************************************************************************/

    /** 
     * Factorizes the fraction if possible
     * If there is a GCD other than 1 between
     * numerator and denominator, both are simplified
     * by dividing by it.
     */
    private void normalize()
    {
        if (_denom.isZero())
        {
            throw new RuntimeException(
                "Denominator cannot be zero (division by zero)");
        }
        // Get the GCD of numerator and denominator
        HashMap<String, Integer> gcdMap =
            MapManipulator.gcdMaps(_num.getMap(),_denom.getMap());
        int gcdNumeric = 
            MapManipulator.gcdNum(_num.getNumeric(),_denom.getNumeric());

        // Get signs
        boolean sign = !(_num.getSign() ^ _denom.getSign());

        // Divide both with it
        Product newNum = new Product(
            MapManipulator.subMaps(_num.getMap(),gcdMap),
            _num.getNumeric()/gcdNumeric,sign);

        Product newDenom = new Product(
            MapManipulator.subMaps(_denom.getMap(),gcdMap),
            _denom.getNumeric()/gcdNumeric,true);
        
        _num = new Product(newNum);
        _denom = new Product(newDenom);
    }
}