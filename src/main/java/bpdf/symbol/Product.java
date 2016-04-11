// Product.java
package bpdf.symbol;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Product expressions
 * @author Vagelis Bebelis
 */
public class Product extends Expression 
{
/******************************************************************************
 ** PRIVATE PARAMETERS
 ******************************************************************************/

    /** 
     * A power map holding all the symbols of the expression
     * along with their powers.
     */
    private HashMap<String, Integer> _map = new HashMap<String, Integer>();

    /**
     * The numeric part of the product
     */
    private int _numeric = 1;

    /**
     * Sign of the product (positive / negative)
     */
    private boolean _sign = true;

/******************************************************************************
 ** CONSTRUCTORS
 ******************************************************************************/

    /** 
     * Creates a new product from a given string.
     * @param string The input string.
     */
    public Product(String string) 
    {
        parseString(string);
    }

    /**
     * Creates a new product from a given numeric
     * @param numeric The input numeric
     */
    public Product(int numeric)
    {
        if (numeric > 0)
            _numeric *= numeric;
        else
        {
            _numeric *= numeric*(-1);
            _sign = !_sign;
        }
    }

    /**
     * Creates a new Product from a given Product (copy)
     * @param prod The given Product
     */
    public Product(Product prod)
    {
        this(prod.getMap(),prod.getNumeric(),prod.getSign());
    }

    /**
     * Creates a new product from a given power map.
     * @param map The given power map
     */
    public Product(HashMap<String, Integer> map) 
    {
        parseString(mapToString(map));
    }

    /**
     * Creates a new product from a given power map and numeric
     * @param map The given power map
     * @param numeric The given numeric
     */
    public Product(HashMap<String, Integer> map, int numeric) 
    {
        if (numeric > 0)
        {
            _numeric *= numeric;
        }
        else
        {
            _numeric *= numeric*(-1);
            _sign = !_sign;

        }
        parseString(mapToString(map));
    }

    /**
     * Creates a new product from a given power map, numeric and sign
     * @param map The given power map
     * @param numeric The given numeric
     * @param sign The given sign
     */
    public Product(HashMap<String, Integer> map, int numeric, boolean sign)
    {
        _sign = sign;
        if (numeric > 0)
        {
            _numeric *= numeric;
        }
        else
        {
            _numeric *= numeric*(-1);
            _sign = !_sign;

        }
        parseString(mapToString(map));
    }

/******************************************************************************
 ** EXPRESSION METHODS - MATH FUNCTIONS
 ******************************************************************************/

    /**
     * Adds the product to an expression
     * @param expr The expression to add with
     * @return The resulting Polynomial
     */
    public Expression add(Expression expr)
    {
        if (expr instanceof Polynomial)
        {
            Polynomial poly = expr.getPolynomial();
            poly.add(this);
            return poly;
        }
        else
        {
            ArrayList<Expression> resList = new ArrayList<Expression>();
            resList.add(this);
            resList.add(expr);
            Polynomial poly = new Polynomial(resList);
            return poly;
        }
        
        // Expression resExpr = expr.getPolynomial().add(this);
        // return resExpr;
    }
    
    /** 
     * Multiplies the Product with another Expression
     * @param expr The Expression to be multiplied with
     * @return The resulting Expression
     */
    public Expression multiply(Expression expr)
    {
        if (expr instanceof Product)
        {
            Product prod = (Product) expr;
            
            HashMap<String, Integer> resMap = 
                MapManipulator.addMaps(_map, prod.getMap());
            int resNumeric = _numeric * prod.getNumeric();
            boolean resSign = !(_sign ^ prod.getSign());
            
            Product resProd = new Product(resMap,resNumeric,resSign);
            return resProd;
        } 
        else
            return expr.multiply(this);
    }

    /** 
     * Divides this Product with the given Expression
     * @param expr The denominator
     * @return The resulting Expression
     */
    public Expression divide (Expression expr) 
    {
        // Check if divide by 0
        if (expr.isZero()) throw new RuntimeException("Cannot divide by 0!");
        // Check if divide by 1
        if (expr.isUnit()) return this;

        if (expr instanceof Product)
        {
            Product prod = (Product) expr;         
            Fraction resFrac = new Fraction(this, prod);
            return resFrac;
        } 
        else if (expr instanceof Fraction)
        {
            Fraction frac = (Fraction) expr;            
            Fraction resFrac = new Fraction(
                frac.getDenom().multiply(this).getProduct(),
                frac.getNum());
            return resFrac;
        } 
        else // It's Polynomial
        {
            throw new RuntimeException("Cannot divide by a Polynomial");
        } 
    }

    /** 
     * Returns the Greater Common Divisor of the two expressions
     * @param expr The given expression
     * @return The GCD
     */
    public Product gcd(Expression expr)
    {
        if (expr instanceof Polynomial)
        {
            return expr.gcd(this);
        }
        else
        {
            Product prod = expr.getFraction().getNum();

            HashMap<String, Integer> resMap = 
                MapManipulator.gcdMaps(_map,prod.getMap());
            int resNumeric = 
                MapManipulator.gcdNum(_numeric,prod.getNumeric());

            return new Product(resMap,resNumeric);
        }
    }

    /**
     * Returns the Greater Common Divisor of a given list of products
     * @param prodList The list of products
     * @return The GCD oft he products as a product
     */
    public static Product gcdAll(List<Product> prodList)
    {
        if (prodList.isEmpty())
            throw new RuntimeException ("Empty Product list!");
        Product tmpGCD = prodList.get(0);
        for (int i = 1; i < prodList.size(); i++)
        {
            tmpGCD = tmpGCD.gcd(prodList.get(i));
        }
        return tmpGCD;
    }
    
    /** 
     * Returns an expression of the LCM between the two Products
     * @param prod The given Product
     * @return The LCM of the two Products
     */
    public Product lcm(Product prod)
    {
        return (Product) this.multiply(prod.divide(this.gcd(prod)));
    }

    /**
     *  Evaluates an expression for a specific value of a parameter
     * @param str The parameter to be evaluated
     * @param n The integer value used for the evaluation
     * @return The resulting expression
     */
    public Product evaluate(String p, Integer n)
    {
        HashMap<String, Integer> resMap = new HashMap<String, Integer>();
        resMap.putAll(_map);
        if (!resMap.containsKey(p))
            return new Product(resMap, _numeric, _sign);
        
        int power = resMap.get(p);
        resMap.remove(p);

        int resNumeric = this.getNumeric() * (int) Math.pow(n,power);
        if (!_sign)
            resNumeric = resNumeric*(-1);
        return new Product(resMap,resNumeric);

    }

    /**
     * Marks the expression to be under a ceiling
     * Case of Product - returns self
     */
    public Expression ceiling()
    {
        return this;
    }

    /**
     * Marks the expression to be under a floor
     * Case of Product - returns self
     */
    public Expression floor()
    {
        return this;
    }

    

/******************************************************************************
 ** EXPRESSION METHODS - PROPERTY CHECK
 ******************************************************************************/

    /** 
     * Compares this with another expression
     * @param expr The expression to compare with
     * @return True if they are equal, false otherwise
     */
    public boolean isEqualTo (Expression expr)
    {
        if (expr.isZero())
            return this.isZero();
        else if (this.isZero())
            return expr.isZero();
        return (expr.divide(this).isUnit());
    }

    /**
     * Compares if this is greater than the given expression
     * Throws exception if the expressions are incomparable
     * @param expr The expression to compare with
     * @return True if the current is greater false otherwise
     */
    public boolean isGreaterThan(Expression expr)
    {
        if (expr.isProduct())
        {
            Product prod = expr.getProduct();

            // Case Zeros
            if (this.isZero())
            {
                int numeric = prod.getNumeric();
                if (numeric < 0)
                    return true;
                else 
                    return false;
            }

            if (expr.isZero())
            {
                int numeric = this.getNumeric();
                if (numeric > 0)
                    return true;
                else 
                    return false;
            }

            // Simplification
            Product gcd = this.gcd(prod);
            prod = prod.divide(gcd).getProduct();
            Product current = this.divide(gcd).getProduct();
            Product prodParam = (Product) prod.getParam();
            Product thisParam = (Product) current.getParam();

            if (prodParam.isEqualTo(thisParam))
            {
                return (prod.getNumeric() < current.getNumeric());
            }
            else if (prod.isNumber())
            {
                if (current.isNumber())
                    return (prod.getNumeric() < current.getNumeric());
                else if (prod.getNumeric() <= current.getNumeric())
                    return true;
                else // we can't know
                    throw new RuntimeException("Cannot compare " 
                        + current.getString() + " with " + prod.getString());
            }
            else
            {
                return false;
            }
        }
        throw new RuntimeException("Cannot compare non- Products: "
            + this.getString() + " with " + expr.getString());
    }

    /** 
     * Returns true if the expression is equal to 1
     * @return The expression equals 1 or not
     */
    public boolean isUnit()
    {
        return ((_numeric == 1) && (_map.isEmpty()));
    }
    
    /** 
     * Returns true if the expression is equal to 0
     * @return The expression equals 0 or not
     */
    public boolean isZero()
    {
        return (_numeric == 0);
    }

    /**
     * Returns true if the expression can be captured as a Product
     * @return The expression can be a Product or not
     */
    public boolean isProduct()
    {
        return true;
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
        return (_map.isEmpty()); 
    }

    /**
     * Returns true if fraction is ceilinged
     */
    public boolean hasCeiling()
    {
        return false;
    }

    /**
     * Returns true if fraction is floored
     */
    public boolean hasFloor()
    {
        return false;
    }


/******************************************************************************
 ** EXPRESSION METHODS - TRANSFORMATIONS
 ******************************************************************************/

    /** 
     * Returns the equivalent expression as Product if possible
     * @return The expression as Product if possible
     */
    public Product getProduct()
    {
        return this;
    }

    /**
     * Returns the equivalent expression as Fraction if possible
     * @return The expression as Fraction if possible
     */
    public Fraction getFraction()
    {
        return new Fraction(this);
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
     * @return The expression as integer if possible
     */
    public int getNumber()
    {
        if (this.isNumber()) 
        {
            if (getSign())
                return _numeric;
            else 
                return _numeric*(-1);
        }
        else throw new RuntimeException("Product: " + getString()
            + " is not an integer");
    }
    
    /** 
     * Return the expression's string.
     * @return The expression string.
     */
    public String getString() 
    {
        String resString = "";
        if (!_sign) resString += "-";
        if (this.isNumber())
        {
            resString += Integer.toString(_numeric);
            return resString;
        }
        if (_numeric == 1) 
        {
            resString += mapToString(_map);
            return resString;
        }
        resString += Integer.toString(_numeric) + '*' + mapToString(_map);
        return resString;
    }

    /**
     * Return the parametric part of the Expression
     * @return The resulting Expression
     */
    public Expression getParam()
    {
        return new Product(_map);
    }

/******************************************************************************
 ** GETTERS
 ******************************************************************************/

    /** 
     * Returns the power map
     * @return The power map
     */
    public HashMap<String, Integer> getMap() 
    {
        return _map;
    }

    /** 
     * Returns the numeric
     * @return The numeric
     */
    public int getNumeric() 
    {
        return _numeric;
    }

    /**
     * Returns the sign
     * @return The sign
     */
    public boolean getSign()
    {
        return _sign;
    }

    /**
     * Returns the list of the Product parameters
     * @return The list of a the product parameters
     */
    public List<String> getParamList() 
    {
        Set<String> paramSet = _map.keySet();
        List<String> paramList = new ArrayList<String>();
        paramList.addAll(paramSet);
        return paramList;
    }

    public Set<String> getParamSet()
    {
        return _map.keySet();
    }

/******************************************************************************
 ** PRIVATE METHODS
 ******************************************************************************/

    /** 
     * Generate the Product's map and numeric based on the input String
     */
    private void parseString(String string)
    {
        // Parse multiplications
        StringTokenizer tokenStar = new StringTokenizer(string, "*");
        while (tokenStar.hasMoreTokens()) 
        {
            // Parse powers
            StringTokenizer tokenExpo = 
                new StringTokenizer(tokenStar.nextToken(), "^");
            String value = tokenExpo.nextToken();
            int power = 1;

            // Get power
            if (tokenExpo.hasMoreTokens()) 
            {
                try 
                {
                    // Get numerical value of power
                    power = Integer.parseInt(tokenExpo.nextToken());
                    if (power < 0) 
                    {
                        throw new RuntimeException("Negative Exponent!");
                    }
                } 
                catch (NumberFormatException e) 
                {
                    throw new RuntimeException("Non numerical Exponential!");
                }
            }

            // Check sign
            if (value.startsWith("-"))
            {
                _sign = !_sign;
                value = value.substring(1);
            }

            // Check if value is numeric
            try
            {
                int parsedNumeric = Integer.parseInt(value);
                _numeric *= Math.pow(parsedNumeric,power);
            }
            catch (NumberFormatException e)
            {
                addFactor(value,power);
            }
        }
    }

    /** 
     * Add a new factor in the hash map
     * @param token The map key
     * @param power Its power
     */
    private void addFactor(String token, int power) 
    {
        if (_map.containsKey(token)) 
        {
            int current = _map.get(token);
            _map.put(token, current + power);
        } 
        else 
        {
            _map.put(token, power);
        }
    }

    /** 
     * Create the expression string from a given power map
     * @param map The given power map
     * @return The string representation of the map
     */
    private String mapToString(HashMap<String, Integer> map)
    {
        Iterator<Map.Entry<String, Integer>> iMap = map.entrySet().iterator();
        String tempStr = "";
        while (iMap.hasNext()) 
        {
            Map.Entry<String, Integer> tempEntry = iMap.next();
            String key = tempEntry.getKey();
            int value = tempEntry.getValue();
            tempStr = tempStr + key;
            if (value != 1) tempStr = tempStr + "^" + value;
            if (iMap.hasNext()) tempStr = tempStr + "*";
        }
        return tempStr;
    }
}