// RunGraph.java

package bpdf.graph;

import bpdf.symbol.*;
import bpdf.scheduling.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

public class RunGraph
{
    // # of iterations
    private int _iter;
    // Integer parameters
    private int _p;
    private int _q;
    Map<String, Integer> setup = new HashMap<String, Integer>();
    // Boolean parameters
    private List<Boolean> boolA = new ArrayList<Boolean>();
    private List<Boolean> boolB = new ArrayList<Boolean>();

    private Random rand = new Random();


    public RunGraph(int iter, int p, int q)
    {
        _iter = iter;
        _p = p;
        _q = q;
        setup.put("p",_p);
        setup.put("q",_q);
    }

    /*public void runAll(String path)
    {
        String fullpath = "src/" + path + ".groovy";
        populateBoolean();
        double sumSVAN = 0;
        double sumNSVAN = 0;
        double sumSOPT = 0;
        double sumBUFF = 0;

        double sum1 = 0;
        double sum2 = 0;
        double sum3 = 0;
        double sum4 = 0;

        for (int i = 0; i < _iter; i++)
        {
            double sVan = runSlottedVanilla(new BPDFGraph(fullpath));
            double nsVan = runNonSlottedVanilla(new BPDFGraph(fullpath));
            double sOpt = runSlottedOptimized(new BPDFGraph(fullpath));
            double sBuff = runSlottedBuffer(new BPDFGraph(fullpath));

            double ratio1 = sVan / nsVan;
            double ratio2 = sOpt / nsVan;
            double ratio3 = sVan / sOpt;
            double ratio4 = sVan / sBuff;
            sum1 += ratio1;
            sum2 += ratio2;
            sum3 += ratio3;
            sum4 += ratio4;
            sumSVAN += sVan;
            sumNSVAN += nsVan;
            sumSOPT += sOpt;
            sumBUFF += sBuff;
        }

        double avg1 = sum1 / _iter;
        double avg2 = sum2 / _iter;
        double avg3 = sum3 / _iter;
        double avg4 = sum4 / _iter;
        double avgSVAN = sumSVAN / _iter;
        double avgNSVAN = sumNSVAN / _iter;
        double avgSOPT = sumSOPT / _iter;
        double avgBUFF = sumBUFF / _iter;

        String filename = path + "_" 
            + _iter + "_"
            + _p + "_" 
            + _q + ".txt";

        String results = "\n" + 
        "Avg. Van Sl / Non-Sl: \t"  + avg1 + "\n" +
        "Avg. Opt Sl / Non-Sl: \t"  + avg2 + "\n" +
        "Avg. Van / Opt:    \t\t"   + avg3 + "\n" +
        "Avg. Van / L.Buff: \t\t"   + avg4 + "\n" +
        "Avg. Van Sl:       \t\t\t" + avgSVAN + "\n" +
        "Avg. Non-Sl:       \t\t\t" + avgNSVAN + "\n" +
        "Avg. Opt Sl:       \t\t\t" + avgSOPT + "\n" +
        "Avg. LBuff:        \t\t\t" + avgBUFF + "\n";


        try
        {
            File file = new File(filename);
 
            //if file doesnt exists, then create it
            if(!file.exists())
            {
                file.createNewFile();
            }
 
            //true = append file
            FileWriter fileWritter = new FileWriter(file.getName(),true);
            BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
            bufferWritter.write(results);
            bufferWritter.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        System.out.println(results);
    }*/

    private void populateBoolean()
    {
        boolean value1;
        boolean value2;
        
        boolA.clear();
        boolB.clear();

        for (int i = 0; i < _p; i++)
        {
            value1 = rand.nextBoolean();
            value2 = rand.nextBoolean();
            while (!value1 && !value2)
            {
                value1 = rand.nextBoolean();
                value2 = rand.nextBoolean();
            }
            boolA.add(value1);
            boolB.add(value2);
        }
    }
/*
    public int runSlottedVanilla(BPDFGraph graph)
    {
        Scheduler sched = new ConstraintScheduler(boolA,boolB);
        if (!graph.verifyGraph())
            throw new RuntimeException("Graph could not be verified");
        return graph.execute(setup,sched);
    }

    public int runSlottedOptimized(BPDFGraph graph)
    {
        Scheduler sched = new ConstraintScheduler(boolA,boolB);
        if (!graph.verifyGraph())
            throw new RuntimeException("Graph could not be verified");
        Map<String, BPDFActor> actorMap = graph.getActorMap();
        graph.addConstraints(getMCLOOPConstraints(actorMap));
        return graph.execute(setup,sched);
    }

    public int runSlottedBuffer(BPDFGraph graph)
    {
        Scheduler sched = new ConstraintScheduler(boolA,boolB);
        if (!graph.verifyGraph())
            throw new RuntimeException("Graph could not be verified");
        Map<String, BPDFActor> actorMap = graph.getActorMap();
        graph.addConstraints(getBufferConstraints(actorMap));
        return graph.execute(setup,sched);
    }

    public int runNonSlottedVanilla(BPDFGraph graph)
    {
        Scheduler sched = new NonSlottedScheduler(boolA,boolB);
        if (!graph.verifyGraph())
            throw new RuntimeException("Graph could not be verified");
        return graph.execute(setup,sched);
    }*/

    private List<BPDFConstraint> getMCLOOPConstraints(
            Map<String, BPDFActor> actorMap)
    {
        List<BPDFConstraint> cons = new ArrayList<BPDFConstraint>();

        BPDFActor actorMC = (BPDFActor) actorMap.get("MC");
        BPDFActor actorMBB = (BPDFActor) actorMap.get("MBB");
        BPDFActor actorIQIT = (BPDFActor) actorMap.get("IQIT");
        
        Product one = new Product(1);
        Product minusOne = new Product("-1");
        Product paramI = new Product("i");
        Product paramQ = new Product("q");
        Product paramMinusQ = new Product("-q");
        Expression poly = paramI.add(minusOne);

        Expression poly1 = poly.multiply(paramQ);
        BPDFConstraint uc1 = new BPDFConstraint(actorMC,actorMBB,poly1);
        uc1.setGuard("!a");
        uc1.setShift(1);
        cons.add(uc1);

        BPDFConstraint uc2 = new BPDFConstraint(actorMC,actorIQIT,poly1);
        uc2.setGuard("a");
        uc2.setShift(-1);
        cons.add(uc2);

        Expression poly2 = poly.multiply(paramQ).add(paramMinusQ).add(one);
        BPDFConstraint uc3 = new BPDFConstraint(actorMC,actorMBB,poly2);
        cons.add(uc3);

        return cons;        
    }

    private List<BPDFConstraint> getBufferConstraints(
            Map<String, BPDFActor> actorMap)
    {
        List<BPDFConstraint> cons = new ArrayList<BPDFConstraint>();

        BPDFActor actorLOOP = (BPDFActor) actorMap.get("LOOP");
        BPDFActor actorMC = (BPDFActor) actorMap.get("MC");

        Product minusOne = new Product("-1");
        Product paramI = new Product("i");

        Expression poly3 = paramI.add(minusOne);
        BPDFConstraint uc4 = new BPDFConstraint(actorMC,actorLOOP,poly3);

        cons.add(uc4);
        return cons;
    }
}
