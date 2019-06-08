package bpdf.Symbol;

import java.util.Arrays;
import java.util.List;

public class CompoundOperand implements Operand {

    /**
     * Creates a compound expression.
     * @param operation the specified operation
     * @param operands The specified operands. The amount of operands must exactly
     * match the arity of the operation.
     */
    public CompoundOperand(Operation operation, Operand ... operands) {
        this.operands = Arrays.asList(operands);
        this.operation = operation;
    }

    /**
     * The Operands which this expression is compound of
     */
    final private List<Operand> operands;

    /**
     * The operation on operands.
     */
    final private Operation operation;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExpression() {
        return this.operation.compose(this.operands);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getValue() {
        return this.operation.calculate(this.operands);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        // ....
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        // ....
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLeaf() {
        return false;
    }
}
