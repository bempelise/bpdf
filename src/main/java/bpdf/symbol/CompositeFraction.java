package bpdf.symbol;

/**
 * Composite product to support multiplications and divisions with ceilings
 * and floors
 * @author Vagelis Bebelis
 */
public class CompositeFraction extends CompositeExpression {
    /** Numerator */
    private Expression m_num;
    /** Denominator */
    private Expression m_denom;

    /**
     * Main Constructor
     */
    public CompositeFraction(Expression expr1, Expression expr2) {
        m_num = expr1;
        m_denom = expr2;
    }

    public Expression getNum() {
        return m_num;
    }

    public Expression getDenom() {
        return m_denom;
    }

    public Expression add(Expression expr) {
        return new CompositePolynomial(this, expr);
    }

    public Expression multiply(Expression expr) {
        return new CompositeProduct(this, expr);
    }

    public Expression divide(Expression expr) {
        return new CompositeFraction(this, expr);
    }

    public Product gcd(Expression expr) {
        return m_num.gcd(expr);
    }

    public Expression evaluate(String str, Integer n) {
        Expression evalNum;
        Expression evalDenom;
        boolean ceilOrFloor = false;

        evalNum = m_num.evaluate(str, n);
        if (evalNum.hasFloor() || evalNum.hasCeiling()) {
            ceilOrFloor = true;
        }
        evalDenom = m_denom.evaluate(str, n);
        if (evalDenom.hasFloor() || evalDenom.hasCeiling()) {
            ceilOrFloor = true;
        }
        if (ceilOrFloor) {
            return new CompositeProduct(evalNum, evalDenom);
        } else {
            if (!(evalNum.isFraction() && evalDenom.isFraction())) {
                return new Fraction(evalNum, evalDenom);
            } else {
                return new CompositeProduct(evalNum, evalDenom);
            }
        }
    }

    public Expression ceiling() {
        throw new RuntimeException("Cannot get ceiling of composites");
    }

    public Expression floor() {
        throw new RuntimeException("Cannot get floot of composites");
    }

    public boolean isEqualTo(Expression expr) {
        return false;
    }

    public boolean isGreaterThan(Expression expr) {
        return false;
    }

    public boolean isUnit() {
        return false;
    }

    public boolean isZero() {
        return false;
    }

    public boolean isProduct() {
        return false;
    }

    public boolean isFraction() {
        return false;
    }

    public boolean isNumber() {
        return false;
    }

    public boolean hasCeiling() {
        return true;
    }

    public boolean hasFloor() {
        return true;
    }

    public Product getProduct() {
        throw new RuntimeException("Composite Fractions cannot be products");
    }
    public Fraction getFraction() {
        throw new RuntimeException("Composite Fractions cannot be fractions");
    }

    public Polynomial getPolynomial() {
        throw new RuntimeException("Composite Fractions cannot be polynomials");
    }

    public int getNumber() {
        throw new RuntimeException("Composite Fractions cannot be numbers");
    }

    public String getString() {
        String res = m_num.getString() + "/" + m_denom.getString();
        return res;
    }

    public Expression getParam() {
        Expression numParam = m_num.getParam();
        Expression denomParam = m_denom.getParam();
        return new Fraction(numParam, denomParam);
    }
}
