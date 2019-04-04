package bpdf.scheduling;

import bpdf.graph.BPDFActor;
import bpdf.graph.BPDFGraph;
import bpdf.symbol.Expression;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The dynamic scheduler. Gets as input a set of constraints, a set of actors
 * along with their repetition vector, values for the Integer parameters for
 * the current iteration and produced a slotted parallel ASAP schedule of a
 * single iteration.
 * @author Vagelis Bebelis
 */
public class SlottedScheduler extends Scheduler {
    /** *  Constructor */
    public SlottedScheduler() { }

    public SlottedScheduler(BPDFGraph g,
                            Map<String, Integer> intMap,
                            Map<String, String> boolMap) {
        super(g, intMap, boolMap);
    }

    /**
     * Executes one iteration of the graph in slots.
     */
    @Override
    public int getSchedule() {
        // Initialize
        Set<String> keySet = p_repVector.keySet();
        for (String key : keySet) {
            p_stVector.put(key, 0);
        }

        // First slot
        List<BPDFActor> fireables = new ArrayList<BPDFActor>();
        fireables = getSlot();
        getTiming(fireables);
        updateBoolean(fireables);
        p_slot++;
        updateStatus(fireables);
        printSlot(fireables);

        // Rest of the slots
        while (!fireables.isEmpty()) {
            fireables = new ArrayList<BPDFActor>();
            fireables = getSlot();
            getTiming(fireables);
            updateBoolean(fireables);
            p_slot++;
            updateStatus(fireables);
            printSlot(fireables);
        }
        return p_totalTime;
    }

    /**
     * Schedules a slot. Evaluates constraints and returns a list of
     * fireable actors.
     * @return The list of fireable actors in the current slot.
     */
    private List<BPDFActor> getSlot() {
        List<BPDFActor> fireables = new ArrayList<BPDFActor>();

        for (BPDFActor actor : p_actorList) {
            String name = actor.getName();
            List<BPDFConstraint> actorCons = getConstraints(actor);
            int status = p_stVector.get(name);
            Expression solution = p_repVector.get(name);
            int sol = solution.getNumber();

            if (status == sol) {
                continue;
            }
            if (status >= sol) {
                throw new RuntimeException("Actor has been fired over the solution!");
            }

            boolean fire = true;
            for (BPDFConstraint cons : actorCons) {
                BPDFActor right = cons.getDepActor();
                int rightFired = (int) p_stVector.get(right.getName());
                int leftFire = status + 1;
                int neededFirings = cons.evaluate(leftFire);

                // if guard not set OR not enough firings
                if ((neededFirings < 0) || (rightFired < neededFirings)) {
                    fire = false;
                    break;
                }
            }
            if (fire) {
                fireables.add(actor);
            }
        }
        return fireables;
    }

    /**
     * Updates the status vector by increasing the firings of each actor
     * that will fire in the current slot.
     * @param The list of the actors to be fired (and therefore printed)
     * in the slot.
     */
    private void updateStatus(List<BPDFActor> fireables) {
        for (BPDFActor actor : fireables) {
            String name = actor.getName();
            int status = p_stVector.get(name);
            p_stVector.put(name, status + 1);
        }
    }

    /**
     * Updates the boolean values of the parameters if a modifier is eligible
     * to do so. All the users receive copies according to their reading
     * periods.
     * @param fireables The actors eligible to fire
     */
    private void updateBoolean(List<BPDFActor> fireables) {
        List<String> paramToModify = new ArrayList<String>();

        // Find which parameters will change values
        for (BPDFActor actor : fireables) {
            if (actor.isModifier()) {
                List<String> params = new ArrayList<String>();
                int st = p_stVector.get(actor.getName());
                params = actor.canModify(st);
                paramToModify.addAll(params);
            }
        }

        // Propagate the new values to all the users.
        for (String param : paramToModify) {
            boolean value = nextValue(param);
            for (BPDFConstraint cons : p_constraints) {
                cons.setParam(param, value);
            }
        }
    }

    /**
     * Calculates the maximum time on the current slot and keeps track of the
     * total time.
     * @param fireables The list of actors eligible to fire.
     */
    private void getTiming(List<BPDFActor> fireables) {
        p_slotMax = getMaxTime(fireables);
        p_totalTime += p_slotMax;
    }

    private int getMaxTime(List<BPDFActor> fireables) {
        int max = 0;
        for (BPDFActor actor : fireables) {
            String name = actor.getName();
            int status = (int) p_stVector.get(name);
            // Check if the actor is disconnected
            boolean active = false;
            for (BPDFConstraint cons : getDataConstraints(actor)) {
                if (cons.isSet(status)) {
                    if (cons.getGuardValue(status)) {
                        active = true;
                        break;
                    }
                }
            }
            if (active) {
                max = Math.max(actor.getTime(), max);
            } else {
                max = Math.max(10, max);
            }
        }
        return max;
    }

    /**
     * Prints the given list of actors in a slotted manner.
     * @param fireables The list of actors eligible to fire in the current slot.
     */
    private void printSlot(List<BPDFActor> fireables) {
        if (!fireables.isEmpty()) {
            System.out.print(p_slot + ": ");
            for (BPDFActor actor : fireables) {
                System.out.print(actor.getName() + " | ");
            }
            System.out.println("");
        }
    }
}
