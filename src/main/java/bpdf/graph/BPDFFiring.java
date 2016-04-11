// BPDFFiring.java

package bpdf.graph;

import bpdf.symbol.*;

public class BPDFFiring
{
/******************************************************************************
 ** PRIVATE PARAMETERS
 ******************************************************************************/

    private String name;
    private Product times;

/******************************************************************************
 ** CONSTRUCTORS
 ******************************************************************************/

    public BPDFFiring(String n, Product t)
    {
        name = n;
        times = t;
    }

/******************************************************************************
 ** METHODS
 ******************************************************************************/
    
    public String getName()
    {
        return name;
    }

    public Product getTimes()
    {
        return times;
    }
}