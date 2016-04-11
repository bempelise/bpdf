// BPDFSchedule.java

package bpdf.graph;

import bpdf.symbol.*;

import java.util.List;
import java.util.LinkedList;

/**
 * The class capturing a simple sequential schedule. The class is readily
 * compatible with Ptolemy.
 * @author Vagelis Bebelis
 */
public class BPDFSchedule
{
/******************************************************************************
 ** PRIVATE PARAMETERS
 ******************************************************************************/

    /**
     * A list containing the sequence of firings consisting the schedule.
     */
    private LinkedList<BPDFFiring> schedule = new LinkedList<BPDFFiring>();

/******************************************************************************
 ** ADD / REMOVE / GET FIRINGS
 ******************************************************************************/

    /*
     * Adds a firing or a series of firings of an actor at the end of the list.
     * @param name The name of the actor to be fired.
     * @param times The number of times the firing repeats sequentially.
     */
    public void addFiring(String name, Product times)
    {
        BPDFFiring newFiring = new BPDFFiring(name,times);
        schedule.addLast(newFiring);
    }

    /**
     * Returns the next firing  of the schedule.
     * @param The next firing of the schedule or null if the list is empty.
     */
    public BPDFFiring getFiring()
    {
        if (schedule.isEmpty())
            return null;
        else
            return schedule.getFirst();
    }

    /**
     * Returns the list of the firings currently in the schedule.
     */
    public List<BPDFFiring> getScheduleList()
    {
        return schedule;
    }

/******************************************************************************
 ** AUXILIARY
 ******************************************************************************/

    /**
     * Prints the sequential schedule currently stored in the list.
     * Auxiliary method used for debugging.
     */
    public void printSchedule()
    {
        System.out.println("Schedule: ");
        for (int i = 0; i < schedule.size(); i++)
        {
            BPDFFiring tempFiring = schedule.get(i);
            System.out.println(tempFiring.getName() + "^(" 
                + tempFiring.getTimes().getString() + ")");
        }
    }
}