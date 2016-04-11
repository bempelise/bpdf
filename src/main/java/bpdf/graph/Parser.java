// Parser.java

package bpdf.graph;

import java.util.Map;

public interface Parser {
    public BPDFGraph getGraph(String path);
    public Map<String,BPDFActor> getActorMap();
}