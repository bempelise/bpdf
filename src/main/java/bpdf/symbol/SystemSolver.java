package bpdf.symbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SystemSolver {
    /** * Unknown factors */
    private ArrayList<String> m_factors;
    /** * System of equations */
    private ArrayList<Equation> m_equations;
    /** * Pairs of unknown factors and respective solutions */
    private HashMap<String, Expression> m_solutions;
    /** * Consistency flag */
    private boolean m_isConsistent = true;

    /**
     * Creates a new solver with a given list of unknown factors
     * and a given list of equations.
     * @param facts The list of factors
     * @param eqs The list of equations
     */
    public SystemSolver(ArrayList<String> facts, ArrayList<Equation> eqs) {
        m_factors = facts;
        m_equations = eqs;
        m_solutions = new HashMap<String, Expression>();
        solve();
    }

    /**
     *  Prints the solution of the equations
     */
    public void printSolution() {
        Iterator<Map.Entry<String, Expression>> iSol = m_solutions.entrySet().iterator();

        while (iSol.hasNext()) {
            Map.Entry<String, Expression> current = iSol.next();
            Fraction tempFrac = current.getValue().getFraction();

            if (tempFrac.isProduct()) {
                System.out.println(current.getKey() + " = " + tempFrac.getProduct().getString());
            } else {
                System.out.println(current.getKey() + " = " + tempFrac.getString());
            }
        }
    }

    /**
     * Returns a map with the unknown factors and their respective solutions
     * @return A map with the repetition vector
     */
    public HashMap<String, Expression> getSolution() {
        if (!m_isConsistent) {
            return null;
        }

        Iterator<Map.Entry<String, Expression>> iSol = m_solutions.entrySet().iterator();
        HashMap<String, Expression> solMap = new HashMap<String, Expression>();

        while (iSol.hasNext()) {
            Map.Entry<String, Expression> tempEntry = iSol.next();
            String key = tempEntry.getKey();
            Expression frac = tempEntry.getValue();
            solMap.put(key, frac.getProduct());
        }
        return solMap;
    }

    /**
     * Arbitrarily sets the first actor on the list to 1
     * and calls the recursive function checkSolution to solve the system
     */
    private void solve() {
        // Solve first
        String sol = m_factors.get(0);
        m_solutions.put(sol, new Fraction());
        checkSolution(sol);
        if (m_isConsistent) {
            normalSolution();
            printSolution();
        }
    }

    /**
     * Normalizes the solution Fractions so that all
     * denominators are equal to 1.
     */
    private void normalSolution() {
        Iterator<Map.Entry<String, Expression>> iSol = m_solutions.entrySet().iterator();

        Fraction tempFrac = iSol.next().getValue().getFraction();
        Product lcm = tempFrac.getDenom();

        while (iSol.hasNext()) {
            tempFrac = iSol.next().getValue().getFraction();
            lcm = lcm.lcm(tempFrac.getDenom());
        }

        iSol = m_solutions.entrySet().iterator();
        while (iSol.hasNext()) {
            Map.Entry<String, Expression> tempEntry = iSol.next();
            String key = tempEntry.getKey();
            Expression expr = tempEntry.getValue();
            m_solutions.put(key, expr.multiply(lcm));
        }
    }

    /**
     * Checks which equations can be solved with the newly found solution
     * If a new solution is found the function iterates recursively with
     * the new solution.
     * @param sol The newly found solution
     */
    private void checkSolution(String sol) {
        Iterator<Equation> iEq = m_equations.iterator();

        while (iEq.hasNext()) {
            Equation tempEq = iEq.next();

            if (tempEq.isSolved()) {
                continue;
            }

            if (tempEq.hasSolution(sol)) {
                Expression tempExpr = tempEq.solve(sol, m_solutions.get(sol));
                String otherSol = tempEq.getOther(sol);

                if (m_solutions.containsKey(otherSol)) {
                    Expression pastExpr = m_solutions.get(otherSol);
                    if (!pastExpr.isEqualTo(tempExpr)) {
                        m_isConsistent = false;
                        return;
                    }
                } else {
                    m_solutions.put(otherSol, tempExpr);
                    checkSolution(otherSol);
                }
            }
        }
    }
}
