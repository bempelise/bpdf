package bpdf.graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class CycleDetector {
    /** The graph to be analyzed */
    private BPDFGraph m_graph;
    /** The cycles found in the graph */
    private List<BPDFGraph> m_cycleList = new ArrayList<BPDFGraph>();
    /** Counter used in the algorithm */
    private int m_index = 1;

    public CycleDetector(BPDFGraph g) {
        m_graph = new BPDFGraph(g);
        modifiedTarjanAlgorithm();
    }

    /**
     * A modified version of Tarjan's strongly connected component
     * algorithm that can find nested cycles as well
     */
    private void modifiedTarjanAlgorithm() {
        Iterator<BPDFActor> iActors = m_graph.getActors().iterator();

        while (iActors.hasNext()) {
            BPDFActor v = iActors.next();
            LinkedList<BPDFActor> nodeList = new LinkedList<BPDFActor>();
            if (v.index == 0) {
                v.index = m_index + 1;
                nodeList.push(v);
                dfs(v, nodeList);
            }
        }
    }

    /**
     * Depth First Search. If an actor is revisited a cycle is detected
     * @param v The starting actor
     * @param nodeList The list of previously visited actors
     */
    private void dfs(BPDFActor v, List<BPDFActor> nodeList) {
        List<BPDFActor> successors = m_graph.getSuccessors(v);

        // Consider successors of v
        Iterator<BPDFActor> iSuccessors = successors.iterator();
        while (iSuccessors.hasNext()) {
            BPDFActor w = iSuccessors.next();

            // Copy node list
            LinkedList<BPDFActor> copyNodeList = new LinkedList<BPDFActor>();
            Iterator<BPDFActor> iNodeList = nodeList.iterator();
            while (iNodeList.hasNext()) {
                BPDFActor actor = iNodeList.next();
                copyNodeList.add(actor);
            }

            if (!nodeList.contains(w)) {
                // Successor w has not yet been visited; recurse on it
                // If no index set, set
                if (w.index == 0) {
                    w.index = v.index + 1;
                }

                // if parent index less, recurse otherwise ignore
                if (v.index < w.index) {
                    copyNodeList.push(w);
                    dfs(w, copyNodeList);
                }
            } else {
                // Successor w is in stack nodeList and hence in the current SCC
                // Set the global index to the maximum value after each cycle
                if (v.index > m_index) {
                    m_index = v.index;
                }
                getCycle(w, copyNodeList);
            }
        }
    }

    /**
     * It returns the cycle as a graph by popping elements off the list until
     * the revisited actor is found
     */
    private void getCycle(BPDFActor v, LinkedList<BPDFActor> s) {
        BPDFGraph cycle = new BPDFGraph();
        BPDFActor w = s.pop();
        cycle.addActor(w);

        // Get actors
        while (w.getName() != v.getName()) {
            w = s.pop();
            cycle.addActor(w);
        }

        // Get edges
        List<BPDFActor> cycleActorList = cycle.getActors();
        Iterator<BPDFActor> iActors = cycleActorList.iterator();
        while (iActors.hasNext()) {
            BPDFActor actor = iActors.next();
            List<BPDFEdge> edges = m_graph.getOutEdges(actor);
            Iterator<BPDFEdge> iEdges = edges.iterator();
            while (iEdges.hasNext()) {
                BPDFEdge edge = iEdges.next();
                if (cycleActorList.contains(edge.getConsumer())) {
                    cycle.addEdge(edge);
                }
            }
        }
        m_cycleList.add(cycle);
    }

    public List<BPDFGraph> getCycles() {
        return m_cycleList;
    }
}
