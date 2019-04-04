package bpdf.symbol;

import java.util.ArrayList;

/**
 * Composite product to support multiplications and divisions with ceilings
 * and floors
 * @author Vagelis Bebelis
 */
public class CompositePolynomial extends CompositeExpression {
    /** Polynomial Factors */
    private ArrayList<Expression> m_factors = new ArrayList<Expression>();

    public CompositePolynomial(Expression expr1, Expression expr2) {
        if (expr1 instanceof CompositePolynomial) {
            CompositePolynomial ep1 =  (CompositePolynomial) expr1;
            ArrayList<Expression> tmpList1 = ep1.getList();
            if (expr2 instanceof CompositePolynomial) {
                CompositePolynomial ep2 = (CompositePolynomial) expr2;
                ArrayList<Expression> tmpList2 = ep2.getList();
                tmpList1.addAll(tmpList2);
            } else {
                tmpList1.add(expr2);
            }
            m_factors.addAll(tmpList1);
        } else {
            if (expr2 instanceof CompositePolynomial) {
                CompositePolynomial ep2 = (CompositePolynomial) expr2;
                ArrayList<Expression> tmpList2 = ep2.getList();
                tmpList2.add(expr1);
                m_factors.addAll(tmpList2);
            } else {
                m_factors.add(expr1);
                m_factors.add(expr2);
            }
        }
    }

    /**
     * Constructor using an expression list
     */
    public CompositePolynomial(ArrayList<Expression> exprList) {
        m_factors.addAll(exprList);
    }

/******************************************************************************
 ** GETTERS
 ******************************************************************************/

    public ArrayList<Expression> getList() {
        return m_factors;
    }


/******************************************************************************
 ** MATH FUNCTIONS
 ******************************************************************************/

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
        Product gcd = new Product(1);
        for (Expression expression : m_factors) {
            gcd = gcd.gcd(expression);
        }
        return gcd.gcd(expr);
    }

    public Expression evaluate(String str, Integer n) {
        ArrayList<Expression> evalList = new ArrayList<Expression>();
        boolean ceilOrFloor = false;

        for (Expression expr : m_factors) {
            Expression evalExpr = expr.evaluate(str, n);
            if (evalExpr.hasFloor() || evalExpr.hasCeiling()) {
                ceilOrFloor = true;
            }
            evalList.add(evalExpr);
        }
        if (ceilOrFloor) {
            return new CompositePolynomial(evalList);
        } else {
            Expression poly = new Product(0);
            for (Expression expr : evalList) {
                poly = poly.add(expr);
            }
            return poly;
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
        throw new RuntimeException("Composite polynomials cannot be products");
    }
    public Fraction getFraction() {
        throw new RuntimeException("Composite polynomials cannot be fractions");
    }

    public Polynomial getPolynomial() {
        throw new RuntimeException("Composite polynomials cannot be polynomials");
    }
    public int getNumber() {
        throw new RuntimeException("Composite polynomials cannot be number");
    }

    public String getString() {
        String res = "";
        for (Expression expr : m_factors) {
            if (!res.equals("")) {
                if (expr.isProduct()) {
                    if (expr.getProduct().getSign()) {
                        res = res + "+" + expr.getString();
                    } else {
                        res = res + expr.getString();
                    }
                } else {
                    res = res + "+" + expr.getString();
                }
            } else {
                res = expr.getString();
            }
        }
        return res;
    }

    public Expression getParam() {
        Expression poly = new Product(0);
        for (Expression expr : m_factors) {
            Expression param = expr.getParam();
            poly = poly.add(param);
        }
        return poly;
    }
}
