package bpdf.Symbol;

import java.util.List;

public interface Operation {

    /**
     * Returns a numeric value of the operation performed on the given operands.
     * @param operands the list of operands
     * @return a numeric value of the operation
     */
    double calculate(List<Operand> operands);

    /**
     * Returns a string representation of the operation performed on the given operands.
     * @param operands operands the list of operands
     * @return a string representation of the operation
     */
    String compose(List<Operand> operands);

    /**
     * Returns a string representation of the operator
     * @return string representation of the operator
     */
    String getOperator();
}
