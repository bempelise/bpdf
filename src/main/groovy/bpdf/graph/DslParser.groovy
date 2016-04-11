// DslParser.groovy

package bpdf.graph

import bpdf.symbol.*
import bpdf.scheduling.*

class DslParser
{
    def binding = new Binding();
    def actorList = [];
    def edgeList = [];

    DslParser(String path)
    {
        this(new File(path));
    }

    DslParser(File file)
    {
        prepareParsing(binding);
        def shell = new GroovyShell(binding);
        shell.evaluate(file);

        binding.aMap.each
        {
            actorList += it.value
        }
        
        edgeList.addAll(binding.eList);
    }

    public List getActors()
    {
        return actorList;
    }

    public List getEdges()
    {
        return edgeList;
    }

    public Map getActorMap()
    {
        return binding.aMap;
    }

    /**
     * Creates the namespace to be considered when parsing the DSL.
     * Each function to be called should be defined here.
     */
    void prepareParsing (Binding binding)
    {

        // Data structures
        binding.aMap = [:] // Graph's actors
        binding.eList = [] // Graph's edges
        

        // Actors
        binding.actor = 
        {name ->
            BPDFActor a = new BPDFActor(name)
            binding.aMap[(name)] = a
        }

        // Edges
        binding.connect = 
        {actorA, rateA, actorB, rateB ->
            BPDFActor producer = binding.aMap[(String)actorA];
            BPDFActor consumer = binding.aMap[(String)actorB];
            BPDFEdge e = new BPDFEdge(
                producer, (String)rateA, 
                consumer, (String)rateB);
            producer.addEdge(e);
            consumer.addEdge(e);
            binding.eList += e;
        }

        // Initial tokens
        binding.setTokens = 
        {actorA, actorB, tokens ->
            def edge = findEdge((String) actorA,(String) actorB);
            edge.setTokens(tokens);
        }

        // Boolean Guard
        binding.setGuard = 
        {actorA, actorB, bool ->
            def edge = findEdge((String) actorA,(String) actorB);
            edge.setGuard(bool);
        }

        // Modifiers
        binding.setModifier = 
        {name, bool, period ->
            BPDFActor tmp = binding.aMap.get(name);
            tmp.setModifier(bool,new Product(period));
        }

        // Timing
        binding.timing = 
        {actor, time ->
            BPDFActor tmp = binding.aMap.get(actor);
            tmp.setTime(time);
        }
    }

    BPDFEdge findEdge(String actorA, String actorB)
    {
        def producerList = binding.eList.findAll
        {   
            it.getProducer().getName().equals(actorA);
        }

        def correctList = producerList.findAll
        {
            it.getConsumer().getName().equals(actorB);
        }

        def edge = correctList.get(0);
        return edge;
    }
}