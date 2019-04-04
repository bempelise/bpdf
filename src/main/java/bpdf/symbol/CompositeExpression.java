package bpdf.symbol;

/**
 * Abstract class for composite symbolic expressions
 * @author Vagelis Bebelis
 */
public abstract class CompositeExpression extends Expression {

    public abstract Expression add(Expression expr);
    public abstract Expression multiply(Expression expr);
    public abstract Expression divide(Expression expr);
    public abstract Product gcd(Expression expr);
//     //public abstract Expression lcm(Expression expr);
    public abstract Expression evaluate(String str, Integer value);
    public abstract Expression ceiling();
    public abstract Expression floor();
//     public abstract boolean isEqualTo(Expression expr);
//     public abstract boolean isGreaterThan(Expression expr);
//     public abstract boolean isUnit();
//     public abstract boolean isZero();
//     public abstract boolean isProduct();
//     public abstract boolean isFraction();
//     public abstract boolean isNumber();
    public abstract boolean hasCeiling();
    public abstract boolean hasFloor();

    public abstract Product getProduct();
    public abstract Fraction getFraction();
    public abstract Polynomial getPolynomial();
    public abstract int getNumber();
    public abstract String getString();
    public abstract Expression getParam();
}
