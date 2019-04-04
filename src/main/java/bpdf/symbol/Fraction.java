package bpdf.symbol;

import java.util.HashMap;

/**
 * Fraction expressions
 * @author Vagelis Bebelis
 */
public class Fraction extends Expression {
    /** The fraction's numerator */
    private Product m_num = new Product(1);
    /** The fraction's denominator */
    private Product m_denom = new Product(1);
    /** Is under a ceiling operator */
    private boolean m_ceiling = false;
    /** Is under a floor operator */
    private boolean m_floor = false;

    /**
     * Creates a 1/1 fraction
     */
    public Fraction() { }

    /**
     * Creates a prod/1 fraction
     * @param prod The numerator
     */
    public Fraction(Product prod) {
        m_num = new Product(prod);
        normalize();
    }

    /**
     * Creates a num/denum fraction
     * @param num Numerator
     * @param denom Denominator
     */
    public Fraction(Product num, Product denom) {
        m_num = new Product(num);
        m_denom = new Product(denom);
        normalize();
    }

    /**
     * Creates a num/denum fraction
     * @param num Numerator
     * @param denom Denominator
     */
    public Fraction(Expression num, Expression denom) {
        if ((!num.isFraction()) || (!denom.isFraction())) {
            throw new RuntimeException("Cannot create Fraction from Polynomial");
        }
        Fraction numFrac = num.getFraction();
        Fraction denomFrac = denom.getFraction();
        m_num = numFrac.getNum().multiply(denomFrac.getDenom()).getProduct();
        m_denom = numFrac.getDenom().multiply(denomFrac.getNum()).getProduct();
        normalize();
    }

    /**
     * Adds the Fraction to an expression
     * Returns the resulting polynomial
     * @param expr The expression to add with
     * @return The sum of the expression with the product
     */
    public Expression add(Expression expr) {
        return expr.getPolynomial().add(this);
    }

    /**
     * Multiplies this with an expression
     * Returns the result as a fraction
     * @param expr The expression to multiply with
     * @return The product of the fraction by the expression
     */
    public Expression multiply(Expression expr) {
        if (expr instanceof Polynomial) {
            Polynomial poly = (Polynomial) expr;
            return poly.multiply(this);
        } else if (expr instanceof CompositeProduct) {
            return new CompositeProduct(this, expr);
        } else if (expr instanceof Fraction) {
            Fraction frac = (Fraction) expr;
            if ((frac.hasCeiling() || frac.hasFloor()) && (!frac.isProduct())) {
                return new CompositeProduct(this, frac);
            } else if ((this.hasCeiling() || this.hasFloor()) && (!this.isProduct())) {
                return new CompositeProduct(frac, this);
            } else {
                Fraction resFrac = new Fraction(m_num.multiply(frac.getNum()).getProduct(),
                                                m_denom.multiply(frac.getDenom()).getProduct());
                if (resFrac.isProduct()) {
                    return resFrac.getProduct();
                }
                return resFrac;
            }
        } else {
            if (this.hasCeiling() || this.hasFloor()) {
                return new CompositeProduct(expr, this);
            } else {
                Fraction resFrac = new Fraction(m_num.multiply(expr.getFraction().getNum()).getProduct(),
                                                m_denom.multiply(expr.getFraction().getDenom()).getProduct());
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
    public Expression divide(Expression expr) {
        // Check if divide by 0
        if (expr.isZero()) {
            throw new RuntimeException("Cannot divide by 0!");
        }
        // Check if divide by 1
        if (expr.isUnit()) {
            return this;
        }

        if (expr instanceof Polynomial) {
            throw new RuntimeException("Cannot divide by a Polynomial");
        } else {
            Fraction resFrac = new Fraction(m_num.multiply(expr.getFraction().getDenom()).getProduct(),
                                            m_denom.multiply(expr.getFraction().getNum()).getProduct());
            return resFrac;
        }
    }

    /**
     * Returns an expression of the Greater Common Divisor
     * between this and the given expression
     * @param expr The given expression
     * @return The GCD
     */
    public Product gcd(Expression expr) {
        if (expr instanceof Polynomial) {
            return expr.gcd(this);
        }
        return m_num.gcd(expr.getFraction().getNum());
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
    public Expression evaluate(String str, Integer n) {
        Product evalNum = m_num.evaluate(str, n);
        Product evalDenom = m_denom.evaluate(str, n);
        Fraction resFrac = new Fraction(evalNum, evalDenom);

        if (m_ceiling) {
            if (evalNum.isNumber() && evalDenom.isNumber()) {
                int ceiling = (int) Math.ceil((double) evalNum.getNumber()
                                            / (double) evalDenom.getNumber());
                Product resProd = new Product(ceiling);
                return resProd;
            } else {
                return resFrac.ceiling();
            }
        } else if (m_floor) {
            if (evalNum.isNumber() && evalDenom.isNumber()) {
                int floor = (int) Math.floor((double) evalNum.getNumber()
                                           / (double) evalDenom.getNumber());
                Product resProd = new Product(floor);
                return resProd;
            } else {
                return resFrac.floor();
            }
        } else {
            return resFrac;
        }
    }

    /**
     * Marks the expression to be under a ceiling
     */
    public Expression ceiling() {
        if (this.isProduct()) {
            return this.getProduct();
        } else {
            m_ceiling = true;
            return this;
        }
    }

    /**
     * Marks the expression to be under a floor
     */
    public Expression floor() {
        if (this.isProduct()) {
            return this.getProduct();
        } else {
            m_floor = true;
            return this;
        }
    }

    /**
     * Compares this with another expression for equality
     * @param expr The expression to compare with
     * @return True if they are equal, false otherwise
     */
    public boolean isEqualTo(Expression expr) {
        if (expr instanceof Polynomial) {
            return expr.isEqualTo(this);
        } else {
            Expression tmpExpr = this.divide(expr);
            return (tmpExpr.isUnit());
        }
    }

    /**
     * Compares if this is greater than the given expression
     * Throws exception if the expressions are incomparable
     * @param expr The expression to compare with
     * @return True if the current is greater false otherwise
     */
    public boolean isGreaterThan(Expression expr) {
        if (this.isProduct()) {
            this.getProduct().isGreaterThan(expr);
        }
        throw new RuntimeException("Cannot compare non- Products");
    }

    /**
     * Returns true if the fraction equals to 1
     * @return The fraction equals 1 or not
     */
    public boolean isUnit() {
        if ((m_num.isUnit()) && (m_denom.isUnit())) {
            return true;
        }
        return false;
    }

    /**
     * Returns true if the expression is equal to 0
     * @return The expression equals 0 or not
     */
    public boolean isZero() {
        if (m_num.isZero()) {
            return true;
        }
        return false;
    }

    /**
     * Returns true if the expression can be captured as a Product
     * @return The expression can be a Product or not
     */
    public boolean isProduct() {
        if (m_denom.isUnit()) {
            return true;
        }
        return false;
    }

    /**
     * Returns true if the expression can be captured as a Fraction
     * @return The expression can be a Product or not
     */
    public boolean isFraction() {
        return true;
    }

    /**
     * Returns true if the expression can be captured as an integer
     * @return The expression can be an integer or not
     */
    public boolean isNumber() {
        if (!m_denom.isNumber() || !m_num.isNumber()) {
            return false;
        }
        int denom = getDenom().getNumber();
        int numer = getNum().getNumber();
        return (numer % denom == 0);
    }

    /**
     * Returns true if fraction is ceilinged
     */
    public boolean hasCeiling() {
        return m_ceiling;
    }

    /**
     * Returns true if fraction is floored
     */
    public boolean hasFloor() {
        return m_floor;
    }

    /**
     * Returns the equivalent expression as Product if possible
     * @return The expression as Product if possible, null otherwise
     */
    public Product getProduct() {
        if (isProduct()) {
            return m_num;
        }
        return null;
    }

    /**
     * Returns the equivalent expression as Fraction if possible
     * @return The expression as Fraction if possible, null otherwise
     */
    public Fraction getFraction() {
        return this;
    }

    /**
     * Return the expression as a Polynomial
     */
    public Polynomial getPolynomial() {
        return new Polynomial(this);
    }

    /**
     * Return the expression as an integer
     */
    public int getNumber() {
        if (this.isNumber()) {
            int denom = getDenom().getNumber();
            int numer = getNum().getNumber();
            return numer / denom;
        } else {
            throw new RuntimeException("Fraction is NOT an integer");
        }
    }

    /**
     * Returns the fraction as a string
     * @return The string representation of the fraction
     */
    public String getString() {
        String res = m_num.getString() + "/" + m_denom.getString();

        if (m_ceiling) {
            return ("ceil(" + res + ")");
        } else if (m_floor) {
            return ("floor(" + res + ")");
        } else {
            return res;
        }
    }

    /**
     * Return the parametric part of the Expression
     * @return The resulting Expression
     */
    public Expression getParam() {
        Fraction param = new Fraction(m_num.getParam(), m_denom.getParam());
        return param;
    }

    /**
     * Returns the numerator
     * @return The Fraction's numerator
     */
    public Product getNum() {
        return m_num;
    }

    /**
     * Returns the denominator
     * @return The Fraction's denominator
     */
    public Product getDenom() {
        return m_denom;
    }

    /**
     * Factorizes the fraction if possible
     * If there is a GCD other than 1 between
     * numerator and denominator, both are simplified
     * by dividing by it.
     */
    private void normalize() {
        if (m_denom.isZero()) {
            throw new RuntimeException(
                "Denominator cannot be zero (division by zero)");
        }
        // Get the GCD of numerator and denominator
        HashMap<String, Integer> gcdMap = MapManipulator.gcdMaps(m_num.getMap(), m_denom.getMap());
        int gcdNumeric = MapManipulator.gcdNum(m_num.getNumeric(), m_denom.getNumeric());

        // Get signs
        boolean sign = !(m_num.getSign() ^ m_denom.getSign());

        // Divide both with it
        Product newNum = new Product(
            MapManipulator.subMaps(m_num.getMap(), gcdMap),
            m_num.getNumeric() / gcdNumeric, sign);

        Product newDenom = new Product(
            MapManipulator.subMaps(m_denom.getMap(), gcdMap),
            m_denom.getNumeric() / gcdNumeric, true);

        m_num = new Product(newNum);
        m_denom = new Product(newDenom);
    }
}
