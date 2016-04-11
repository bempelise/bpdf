// CycleDetector.java
package bpdf.graph;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Iterator;

public class CycleDetector
{
/******************************************************************************
 ** PRIVATE PARAMETERS
 ******************************************************************************/

    /**
     * The graph to be analyzed
     */
    private BPDFGraph graph;

    /** 
     * The cycles found in the graph
     */
    private List<BPDFGraph> cycleList = new ArrayList<BPDFGraph>();

    /**
     * Counter used in the algorithm
     */
    private int index = 1;

/******************************************************************************
 ** CONSTRUCTORS
 ******************************************************************************/

    public CycleDetector(BPDFGraph g)
    {
        graph = new BPDFGraph(g);
        modifiedTarjanAlgorithm();
    }

/******************************************************************************
 ** ALGORITHM
 ******************************************************************************/

    /**
     * A modified version of Tarjan's strongly connected component 
     * algorithm that can find nested cycles as well
     */
    private void modifiedTarjanAlgorithm()
    {
        Iterator iActors = graph.getActors().iterator();

        while (iActors.hasNext())
        {
            BPDFActor v = (BPDFActor) iActors.next();
            LinkedList<BPDFActor> nodeList = new LinkedList<BPDFActor>();
            if (v.index == 0)
            {
                v.index = index + 1;
                nodeList.push(v);
                dfs(v,nodeList);
            }
        }
    }

    /**
     * Depth First Search. If an actor is revisited a cycle is detected
     * @param v The starting actor
     * @param nodeList The list of previously visited actors
     */
    private void dfs(BPDFActor v, List nodeList)
    {
        List successors = graph.getSuccessors(v);
     
        // Consider successors of v
        Iterator iSuccessors = successors.iterator();
        while (iSuccessors.hasNext())
        {
            BPDFActor w = (BPDFActor) iSuccessors.next();

            // Copy node list
            LinkedList copyNodeList = new LinkedList<BPDFActor>();
            Iterator iNodeList = nodeList.iterator();
            while (iNodeList.hasNext())
            {
                BPDFActor actor = (BPDFActor) iNodeList.next();
                copyNodeList.add(actor);
            }

            if (!nodeList.contains(w))
            {// Successor w has not yet been visited; recurse on it
                // If no index set, set
                if (w.index == 0)
                    w.index = v.index + 1;

                // if parent index less, recurse otherwise ignore
                if (v.index < w.index)
                {
                    copyNodeList.push(w);
                    dfs(w,copyNodeList);
                }
            }
            else
            {
            // Successor w is in stack nodeList and hence in the current SCC
            // Set the global index to the maximum value after each cycle
                if (v.index > index)
                    index = v.index;
                getCycle(w,copyNodeList);
            }
        }
    }

    /**
     * It returns the cycle as a graph by popping elements off the list until 
     * the revisited actor is found
     */
    private void getCycle(BPDFActor v, LinkedList s)
    {

        BPDFGraph cycle = new BPDFGraph();
        BPDFActor w = (BPDFActor) s.pop();
        cycle.addActor(w);
        
        // Get actors
        while (w.getName() != v.getName())
        {
            w = (BPDFActor) s.pop();
            cycle.addActor(w);
        }

        // Get edges
        List cycleActorList = cycle.getActors();
        Iterator iActors = cycleActorList.iterator();
        while (iActors.hasNext())
        {
            BPDFActor actor = (BPDFActor) iActors.next();
            List edges = graph.getOutEdges(actor);
            Iterator iEdges = edges.iterator();
            while(iEdges.hasNext())
            {
                BPDFEdge edge = (BPDFEdge) iEdges.next();
                if (cycleActorList.contains(edge.getConsumer()))
                {
                    cycle.addEdge(edge);
                }
            }
        }
        cycleList.add(cycle);
        // System.out.println ("Cycle: ");
        // cycle.printGraph();
    }

/******************************************************************************
 ** GETTERS
 ******************************************************************************/

    public List<BPDFGraph> getCycles()
    {
        return cycleList;
    }
}